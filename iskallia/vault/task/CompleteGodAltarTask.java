package iskallia.vault.task;

import com.google.gson.JsonObject;
import iskallia.vault.VaultMod;
import iskallia.vault.block.base.GodAltarTileEntity;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.vault.Modifiers;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.VaultLevel;
import iskallia.vault.core.vault.influence.VaultGod;
import iskallia.vault.core.vault.modifier.spi.ModifierContext;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.gear.charm.CharmHelper;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.item.gear.CharmItem;
import iskallia.vault.task.source.EntityTaskSource;
import iskallia.vault.task.source.TaskSource;
import iskallia.vault.world.data.GodAltarData;
import iskallia.vault.world.data.PlayerReputationData;
import iskallia.vault.world.data.ServerVaults;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.HoverEvent.Action;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

public class CompleteGodAltarTask extends Task {
   public static final Map<VaultGod, List<String>> MESSAGES = new HashMap<>();
   private static final ResourceLocation GROUP = VaultMod.id("god_altar");
   private UUID uuid;
   private ResourceLocation modifierPool;
   private boolean completed;

   public CompleteGodAltarTask() {
   }

   public CompleteGodAltarTask(UUID uuid, ResourceLocation modifierPool) {
      this.uuid = uuid;
      this.modifierPool = modifierPool;
      this.completed = false;
   }

   @Override
   public boolean isCompleted(TaskSource source) {
      return this.completed;
   }

   @Override
   public void onAttach(TaskSource source) {
      CommonEvents.PLAYER_INTERACT
         .register(
            this,
            event -> {
               if (!event.getWorld().isClientSide() && !this.completed) {
                  if (source instanceof EntityTaskSource entitySource && entitySource.matches(event.getPlayer())) {
                     if (event.getWorld().getBlockEntity(event.getPos()) instanceof GodAltarTileEntity altar && this.uuid.equals(altar.getUuid())) {
                        VaultGod god = GodAltarData.get(this.uuid).map(GodAltarData.Entry::getGod).orElse(null);
                        if (god != null) {
                           PlayerReputationData.attemptFavour(event.getPlayer(), god, entitySource.getRandom());
                           this.completed = true;
                           GodAltarData.remove(this.uuid);
                           Vault vault = ServerVaults.get(event.getWorld()).orElse(null);
                           int level = vault == null ? 0 : vault.getOptional(Vault.LEVEL).map(VaultLevel::get).orElse(0);
                           CharmHelper.getCharms(event.getPlayer()).forEach(charm -> {
                              if (vault != null && charm.isUsable(event.getPlayer())) {
                                 CharmItem.addUsedVault(charm.stack(), vault.get(Vault.ID));
                              }
                           });
                           List<VaultModifier<?>> modifiers = ModConfigs.VAULT_MODIFIER_POOLS.getRandom(this.modifierPool, level, source.getRandom());
                           Object2IntMap<VaultModifier<?>> groups = new Object2IntOpenHashMap();
                           modifiers.forEach(modifier -> groups.put(modifier, groups.getOrDefault(modifier, 0) + 1));
                           ObjectIterator<Entry<VaultModifier<?>>> it = groups.object2IntEntrySet().iterator();
                           TextComponent suffix = new TextComponent("");

                           while (it.hasNext()) {
                              Entry<VaultModifier<?>> entry = (Entry<VaultModifier<?>>)it.next();
                              suffix.append(((VaultModifier)entry.getKey()).getChatDisplayNameComponent(entry.getIntValue()));
                              if (it.hasNext()) {
                                 suffix.append(new TextComponent(", "));
                              }
                           }

                           TextComponent text = new TextComponent("");
                           if (!modifiers.isEmpty()) {
                              text.append(event.getPlayer().getDisplayName())
                                 .append(new TextComponent(" added ").withStyle(ChatFormatting.GRAY))
                                 .append(suffix)
                                 .append(new TextComponent(".").withStyle(ChatFormatting.GRAY));
                           }

                           Set<Player> notified = new HashSet<>();
                           notified.add(event.getPlayer());
                           if (vault != null) {
                              groups.forEach(
                                 (modifier, count) -> vault.ifPresent(
                                    Vault.MODIFIERS,
                                    value -> {
                                       for (Modifiers.Entry entry : value.getEntries()) {
                                          if (GROUP.equals(entry.get(Modifiers.Entry.CONTEXT).get(ModifierContext.GROUP))) {
                                             entry.get(Modifiers.Entry.CONTEXT).setExpired();
                                          }
                                       }

                                       value.addModifier(
                                          modifier, count, true, source.getRandom(), context -> context.set(ModifierContext.GROUP, VaultMod.id("god_altar"))
                                       );
                                    }
                                 )
                              );

                              for (Listener listener : vault.get(Vault.LISTENERS).getAll()) {
                                 listener.getPlayer().ifPresent(notified::add);
                              }
                           }

                           String message = MESSAGES.get(god).get(source.getRandom().nextInt(MESSAGES.get(god).size()));
                           MutableComponent vgName = new TextComponent(god.getName()).withStyle(god.getChatColor());
                           vgName.withStyle(style -> style.withHoverEvent(new HoverEvent(Action.SHOW_TEXT, god.getHoverChatComponent())));
                           MutableComponent txt = new TextComponent("");
                           txt.append(vgName).append(new TextComponent(": ").withStyle(ChatFormatting.WHITE)).append(new TextComponent(message));

                           for (Player other : notified) {
                              event.getWorld()
                                 .playSound(null, other.getX(), other.getY(), other.getZ(), SoundEvents.NOTE_BLOCK_BELL, SoundSource.PLAYERS, 0.9F, 1.2F);
                              other.displayClientMessage(txt, false);
                              if (!modifiers.isEmpty()) {
                                 other.displayClientMessage(text, false);
                              }
                           }
                        }
                     }
                  }
               }
            }
         );
      super.onAttach(source);
   }

   @Override
   public void onDetach() {
      CommonEvents.PLAYER_INTERACT.release(this);
      super.onDetach();
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.UUID.writeBits(this.uuid, buffer);
      Adapters.IDENTIFIER.writeBits(this.modifierPool, buffer);
      Adapters.BOOLEAN.writeBits(this.completed, buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.uuid = Adapters.UUID.readBits(buffer).orElseThrow();
      this.modifierPool = Adapters.IDENTIFIER.readBits(buffer).orElse(null);
      this.completed = Adapters.BOOLEAN.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.UUID.writeNbt(this.uuid).ifPresent(value -> nbt.put("uuid", value));
         Adapters.IDENTIFIER.writeNbt(this.modifierPool).ifPresent(value -> nbt.put("modifierPool", value));
         Adapters.BOOLEAN.writeNbt(this.completed).ifPresent(value -> nbt.put("completed", value));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.uuid = Adapters.UUID.readNbt(nbt.get("uuid")).orElse(null);
      this.modifierPool = Adapters.IDENTIFIER.readNbt(nbt.get("modifierPool")).orElse(null);
      this.completed = Adapters.BOOLEAN.readNbt(nbt.get("completed")).orElse(false);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.UUID.writeJson(this.uuid).ifPresent(value -> json.add("uuid", value));
         Adapters.IDENTIFIER.writeJson(this.modifierPool).ifPresent(value -> json.add("modifierPool", value));
         Adapters.BOOLEAN.writeJson(this.completed).ifPresent(value -> json.add("completed", value));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.uuid = Adapters.UUID.readJson(json.get("uuid")).orElse(null);
      this.modifierPool = Adapters.IDENTIFIER.readJson(json.get("modifierPool")).orElse(null);
      this.completed = Adapters.BOOLEAN.readJson(json.get("completed")).orElse(false);
   }

   static {
      MESSAGES.put(
         VaultGod.VELARA,
         Arrays.asList(
            "Our domain's ground will carve a path.",
            "Tread upon our domain with care and it will respond in kind.",
            "May your desire blossom into a wildfire.",
            "Creation bends to our will.",
            "The soil whispers secrets of those who've walked before.",
            "Beneath the surface, our whispers guide fate.",
            "Our heartbeats sync with the world's tremors.",
            "Our reach extends beyond the visible horizon."
         )
      );
      MESSAGES.put(
         VaultGod.TENOS,
         Arrays.asList(
            "May foresight guide your step.",
            "Careful planning and strategy may lead you.",
            "A set choice; followed through and flawlessly executed.",
            "Chance's hand may favour your goals.",
            "In the dance of fate and strategy, precision reigns.",
            "To foresee is to command the threads of destiny.",
            "Tread lightly on the line between choice and predestination.",
            "To conquer fate, first understand its riddles."
         )
      );
      MESSAGES.put(
         VaultGod.WENDARR,
         Arrays.asList(
            "Seize the opportunity.",
            "A single instant, stretched to infinity.",
            "Your future glows golden with possibility.",
            "Hasten and value every passing moment.",
            "With every heartbeat, an epoch is born.",
            "The sands of time are fleeting; grasp them.",
            "The dance of time is swift; miss not its rhythm.",
            "The pendulum of time swings but once for each moment."
         )
      );
      MESSAGES.put(
         VaultGod.IDONA,
         Arrays.asList(
            "Enforce your path through obstacles.",
            "Our vigor may aid your conquest.",
            "Cherish this mote of my might.",
            "A tempest incarnate.",
            "Rise, like the waves, unyielding and fierce.",
            "Forge ahead with the fury of storms.",
            "With relentless force, mold your path.",
            "Harness my wrath to awaken the dormant."
         )
      );
   }
}
