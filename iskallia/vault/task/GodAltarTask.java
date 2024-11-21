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
import iskallia.vault.world.data.PlayerReputationData;
import iskallia.vault.world.data.ServerVaults;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectIterator;
import it.unimi.dsi.fastutil.objects.Object2IntMap.Entry;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.HoverEvent.Action;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.TickEvent.Phase;

public class GodAltarTask extends Task {
   private UUID altarUuid;
   private UUID vaultUuid;
   private VaultGod god;
   private ResourceKey<Level> dimension;
   private BlockPos pos;
   protected ResourceLocation modifierCompletionPool;
   protected ResourceLocation modifierFailurePool;
   private TimedTask child;
   private boolean expired;
   private static final ResourceLocation MODIFIER_GROUP = VaultMod.id("god_altar");
   public static final Map<VaultGod, List<String>> FAILURE_MESSAGES = new HashMap<>();
   public static final Map<VaultGod, List<String>> COMPLETION_MESSAGES = new HashMap<>();

   public GodAltarTask() {
   }

   public GodAltarTask(
      GodAltarTileEntity entity,
      UUID altarUuid,
      UUID vaultUuid,
      long duration,
      ResourceLocation modifierCompletionPool,
      ResourceLocation modifierFailurePool,
      VaultGod god,
      Task child
   ) {
      this.altarUuid = altarUuid;
      this.vaultUuid = vaultUuid;
      this.god = god;
      this.dimension = entity.getLevel().dimension();
      this.pos = entity.getBlockPos();
      this.modifierCompletionPool = modifierCompletionPool;
      this.modifierFailurePool = modifierFailurePool;
      this.child = new TimedTask(duration).addChildren(new Task[]{child});
      this.expired = false;
   }

   public UUID getAltarUuid() {
      return this.altarUuid;
   }

   public VaultGod getGod() {
      return this.god;
   }

   public TimedTask getChild() {
      return this.child;
   }

   public boolean isExpired() {
      return this.expired;
   }

   @Override
   public Iterable<Task> getChildren() {
      return Collections.singleton(this.child);
   }

   @Override
   public boolean isCompleted() {
      return !this.expired;
   }

   @Override
   public void onAttach(TaskContext context) {
      CommonEvents.SERVER_TICK.at(Phase.END).register(this, event -> {
         if (!this.expired) {
            Vault vault = ServerVaults.get(this.vaultUuid).orElse(null);
            if (this.vaultUuid != null && vault == null) {
               this.onFail(null, context);
            } else {
               if (vault != null && context.getSource() instanceof EntityTaskSource entitySource) {
                  for (UUID uuid : entitySource.getUuids()) {
                     if (!vault.get(Vault.LISTENERS).contains(uuid)) {
                        this.onFail(vault, context);
                        return;
                     }
                  }
               }

               if (this.child.isCompleted() && this.child.streamDescendants().allMatch(Task::isCompleted)) {
                  this.onSucceed(vault, context);
               } else if (!this.child.isCompleted()) {
                  this.onFail(vault, context);
               }
            }
         }
      });
      super.onAttach(context);
   }

   public void onFail(Vault vault, TaskContext context) {
      this.expired = true;
      this.doCompletionEffects(vault, context, true);
   }

   public void onSucceed(Vault vault, TaskContext context) {
      this.expired = true;
      this.doCompletionEffects(vault, context, false);
      if (context.getSource() instanceof EntityTaskSource entitySource) {
         for (Player player : entitySource.getEntities(Player.class)) {
            PlayerReputationData.attemptFavour(player, this.god, context.getSource().getRandom());
            CharmHelper.getCharms(player).forEach(charm -> {
               if (charm.isUsable(player)) {
                  CharmItem.addUsedVault(charm.stack(), vault.get(Vault.ID));
               }
            });
         }
      }

      ServerLevel world = context.getServer().getLevel(this.dimension);
      if (world != null && world.getBlockEntity(this.pos) instanceof GodAltarTileEntity altar) {
         altar.placeReward(world, this.pos.above(), this.god);
      }
   }

   public void doCompletionEffects(Vault vault, TaskContext context, boolean failure) {
      int level = vault == null ? 0 : vault.getOptional(Vault.LEVEL).map(VaultLevel::get).orElse(0);
      List<VaultModifier<?>> modifiers = ModConfigs.VAULT_MODIFIER_POOLS
         .getRandom(failure ? this.modifierFailurePool : this.modifierCompletionPool, level, context.getSource().getRandom());
      Object2IntMap<VaultModifier<?>> groups = new Object2IntOpenHashMap();
      modifiers.forEach(modifier -> groups.put(modifier, groups.getOrDefault(modifier, 0) + 1));
      TextComponent suffix = new TextComponent("");
      ObjectIterator<Entry<VaultModifier<?>>> it1 = groups.object2IntEntrySet().iterator();

      while (it1.hasNext()) {
         Entry<VaultModifier<?>> entry = (Entry<VaultModifier<?>>)it1.next();
         suffix.append(((VaultModifier)entry.getKey()).getChatDisplayNameComponent(entry.getIntValue()));
         if (it1.hasNext()) {
            suffix.append(new TextComponent(", "));
         }
      }

      TextComponent prefix = new TextComponent("");
      Iterator<Entity> it2 = context.getSource() instanceof EntityTaskSource source ? source.getEntities(Entity.class).iterator() : Collections.emptyIterator();

      while (it2.hasNext()) {
         prefix.append(it2.next().getDisplayName());
         if (it2.hasNext()) {
            prefix.append(new TextComponent(", "));
         }
      }

      TextComponent text = new TextComponent("");
      if (!modifiers.isEmpty()) {
         text.append(prefix)
            .append(new TextComponent(" added ").withStyle(ChatFormatting.GRAY))
            .append(suffix)
            .append(new TextComponent(".").withStyle(ChatFormatting.GRAY));
      }

      Set<Player> notified = new HashSet<>();
      if (context.getSource() instanceof EntityTaskSource sourcex) {
         notified.addAll(sourcex.getEntities(Player.class));
      }

      if (vault != null) {
         groups.forEach(
            (modifier, count) -> vault.ifPresent(
               Vault.MODIFIERS,
               value -> {
                  if (failure) {
                     value.addModifier(modifier, count, true, context.getSource().getRandom());
                  } else {
                     for (Modifiers.Entry entry : value.getEntries()) {
                        if (MODIFIER_GROUP.equals(entry.get(Modifiers.Entry.CONTEXT).get(ModifierContext.GROUP))) {
                           entry.get(Modifiers.Entry.CONTEXT).setExpired();
                        }
                     }

                     value.addModifier(
                        modifier, count, true, context.getSource().getRandom(), modifierContext -> modifierContext.set(ModifierContext.GROUP, MODIFIER_GROUP)
                     );
                  }
               }
            )
         );

         for (Listener listener : vault.get(Vault.LISTENERS).getAll()) {
            listener.getPlayer().ifPresent(notified::add);
         }
      }

      List<String> messagePool = (failure ? FAILURE_MESSAGES : COMPLETION_MESSAGES).get(this.god);
      String message = messagePool.get(context.getSource().getRandom().nextInt(messagePool.size()));
      MutableComponent vgName = new TextComponent(this.god.getName()).withStyle(this.god.getChatColor());
      vgName.withStyle(style -> style.withHoverEvent(new HoverEvent(Action.SHOW_TEXT, this.god.getHoverChatComponent())));
      MutableComponent txt = new TextComponent("")
         .append(vgName)
         .append(new TextComponent(": ").withStyle(ChatFormatting.WHITE))
         .append(new TextComponent(message));

      for (Player other : notified) {
         if (failure) {
            other.level.playSound(null, other.getX(), other.getY(), other.getZ(), SoundEvents.BLAZE_DEATH, SoundSource.PLAYERS, 0.9F, 0.5F);
         } else {
            other.level.playSound(null, other.getX(), other.getY(), other.getZ(), SoundEvents.NOTE_BLOCK_BELL, SoundSource.PLAYERS, 0.9F, 1.2F);
         }

         other.displayClientMessage(txt, false);
         if (!modifiers.isEmpty()) {
            other.displayClientMessage(text, false);
         }
      }
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.UUID.writeBits(this.altarUuid, buffer);
      Adapters.UUID.writeBits(this.vaultUuid, buffer);
      Adapters.GOD_NAME.writeBits(this.god, buffer);
      Adapters.DIMENSION.writeBits(this.dimension, buffer);
      Adapters.BLOCK_POS.writeBits(this.pos, buffer);
      Adapters.IDENTIFIER.writeBits(this.modifierCompletionPool, buffer);
      Adapters.IDENTIFIER.writeBits(this.modifierFailurePool, buffer);
      Adapters.TASK.writeBits(this.child, buffer);
      Adapters.BOOLEAN.writeBits(this.expired, buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.altarUuid = Adapters.UUID.readBits(buffer).orElseThrow();
      this.vaultUuid = Adapters.UUID.readBits(buffer).orElseThrow();
      this.god = Adapters.GOD_NAME.readBits(buffer).orElseThrow();
      this.dimension = Adapters.DIMENSION.readBits(buffer).orElseThrow();
      this.pos = Adapters.BLOCK_POS.readBits(buffer).orElseThrow();
      this.modifierCompletionPool = Adapters.IDENTIFIER.readBits(buffer).orElseThrow();
      this.modifierFailurePool = Adapters.IDENTIFIER.readBits(buffer).orElseThrow();
      this.child = (TimedTask)Adapters.TASK.readBits(buffer).orElseThrow();
      this.expired = Adapters.BOOLEAN.readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.UUID.writeNbt(this.altarUuid).ifPresent(value -> nbt.put("altarUuid", value));
         Adapters.UUID.writeNbt(this.vaultUuid).ifPresent(value -> nbt.put("vaultUuid", value));
         Adapters.GOD_NAME.writeNbt(this.god).ifPresent(value -> nbt.put("god", value));
         Adapters.DIMENSION.writeNbt(this.dimension).ifPresent(value -> nbt.put("dimension", value));
         Adapters.BLOCK_POS.writeNbt(this.pos).ifPresent(value -> nbt.put("pos", value));
         Adapters.IDENTIFIER.writeNbt(this.modifierCompletionPool).ifPresent(value -> nbt.put("modifierCompletionPool", value));
         Adapters.IDENTIFIER.writeNbt(this.modifierFailurePool).ifPresent(value -> nbt.put("modifierFailurePool", value));
         Adapters.TASK.writeNbt(this.child).ifPresent(value -> nbt.put("child", value));
         Adapters.BOOLEAN.writeNbt(this.expired).ifPresent(value -> nbt.put("expired", value));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.altarUuid = Adapters.UUID.readNbt(nbt.get("altarUuid")).orElse(null);
      this.vaultUuid = Adapters.UUID.readNbt(nbt.get("vaultUuid")).orElse(null);
      this.god = Adapters.GOD_NAME.readNbt(nbt.get("god")).orElse(null);
      this.dimension = Adapters.DIMENSION.readNbt(nbt.get("dimension")).orElse(null);
      this.pos = Adapters.BLOCK_POS.readNbt(nbt.get("pos")).orElse(null);
      this.modifierCompletionPool = Adapters.IDENTIFIER.readNbt(nbt.get("modifierCompletionPool")).orElse(null);
      this.modifierFailurePool = Adapters.IDENTIFIER.readNbt(nbt.get("modifierFailurePool")).orElse(null);
      this.child = (TimedTask)Adapters.TASK.readNbt(nbt.get("child")).orElse(null);
      this.expired = Adapters.BOOLEAN.readNbt(nbt.get("expired")).orElse(false);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.UUID.writeJson(this.altarUuid).ifPresent(value -> json.add("altarUuid", value));
         Adapters.UUID.writeJson(this.vaultUuid).ifPresent(value -> json.add("vaultUuid", value));
         Adapters.GOD_NAME.writeJson(this.god).ifPresent(value -> json.add("god", value));
         Adapters.DIMENSION.writeJson(this.dimension).ifPresent(value -> json.add("dimension", value));
         Adapters.BLOCK_POS.writeJson(this.pos).ifPresent(value -> json.add("pos", value));
         Adapters.IDENTIFIER.writeJson(this.modifierCompletionPool).ifPresent(value -> json.add("modifierCompletionPool", value));
         Adapters.IDENTIFIER.writeJson(this.modifierFailurePool).ifPresent(value -> json.add("modifierFailurePool", value));
         Adapters.TASK.writeJson(this.child).ifPresent(value -> json.add("child", value));
         Adapters.BOOLEAN.writeJson(this.expired).ifPresent(value -> json.add("expired", value));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.altarUuid = Adapters.UUID.readJson(json.get("altarUuid")).orElse(null);
      this.vaultUuid = Adapters.UUID.readJson(json.get("vaultUuid")).orElse(null);
      this.god = Adapters.GOD_NAME.readJson(json.get("god")).orElse(null);
      this.dimension = Adapters.DIMENSION.readJson(json.get("dimension")).orElse(null);
      this.pos = Adapters.BLOCK_POS.readJson(json.get("pos")).orElse(null);
      this.modifierCompletionPool = Adapters.IDENTIFIER.readJson(json.get("modifierCompletionPool")).orElse(null);
      this.modifierFailurePool = Adapters.IDENTIFIER.readJson(json.get("modifierFailurePool")).orElse(null);
      this.child = (TimedTask)Adapters.TASK.readJson(json.get("child")).orElse(null);
      this.expired = Adapters.BOOLEAN.readJson(json.get("expired")).orElse(false);
   }

   static {
      FAILURE_MESSAGES.put(
         VaultGod.VELARA,
         Arrays.asList(
            "The harmony of the natural world eludes you.",
            "The beauty of nature thrives on resilience, but your actions have brought only discord.",
            "Your failures wither the very essence of life.",
            "The song of nature laments your inadequacy.",
            "You tarnish the purity of nature with your missteps."
         )
      );
      FAILURE_MESSAGES.put(
         VaultGod.TENOS,
         Arrays.asList(
            "Even the wisest can stumble on their path to enlightenment.",
            "Seek knowledge, learn from your failures, and you shall find your way.",
            "Failure is but a stepping stone on the road to enlightenment.",
            "Even the most brilliant minds face setbacks.",
            "In the pursuit of wisdom, missteps are bound to occur."
         )
      );
      FAILURE_MESSAGES.put(
         VaultGod.WENDARR,
         Arrays.asList(
            "A moment lost is a chance wasted.",
            "The sands of time never cease their relentless march.",
            "In the grand tapestry of existence, your actions are but threads.",
            "Time's embrace can be both cruel and kind.",
            "As the clock ticks, so do your choices shape your destiny."
         )
      );
      FAILURE_MESSAGES.put(
         VaultGod.IDONA,
         Arrays.asList(
            "Perhaps you should consider a different path.",
            "Weakness is a burden that few can afford.",
            "Your feeble attempt have left much to be desired.",
            "A hunter must be swift to adapt, or they are doomed to fail.",
            "No smiles will be cast upon those who crumble in the face of adversity."
         )
      );
      COMPLETION_MESSAGES.put(
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
      COMPLETION_MESSAGES.put(
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
      COMPLETION_MESSAGES.put(
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
      COMPLETION_MESSAGES.put(
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
