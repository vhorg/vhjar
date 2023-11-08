package iskallia.vault.task;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.VaultLevel;
import iskallia.vault.core.vault.influence.VaultGod;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.task.source.EntityTaskSource;
import iskallia.vault.task.source.TaskSource;
import iskallia.vault.world.data.GodAltarData;
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
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.player.Player;

public class FailGodAltarTask extends Task {
   public static final Map<VaultGod, List<String>> MESSAGES = new HashMap<>();
   private UUID vaultUuid;
   private UUID playerUuid;
   private UUID altarUuid;
   private ResourceLocation modifierPool;

   public FailGodAltarTask() {
   }

   public FailGodAltarTask(UUID vaultUuid, UUID playerUuid, UUID altarUuid, ResourceLocation modifierPool) {
      this.vaultUuid = vaultUuid;
      this.playerUuid = playerUuid;
      this.altarUuid = altarUuid;
      this.modifierPool = modifierPool;
   }

   @Override
   public boolean isCompleted(TaskSource source) {
      return false;
   }

   @Override
   public void onTick(TaskSource source) {
      super.onTick(source);
      if (this.vaultUuid != null) {
         Vault vault = ServerVaults.get(this.vaultUuid).orElse(null);
         if (vault == null || !vault.get(Vault.LISTENERS).contains(this.playerUuid)) {
            this.onStop(source);
         }
      }
   }

   @Override
   public void onStop(TaskSource source) {
      VaultGod god = GodAltarData.get(this.altarUuid).map(GodAltarData.Entry::getGod).orElse(null);
      if (god != null) {
         GodAltarData.remove(this.altarUuid);
         ServerPlayer player = null;
         if (source instanceof EntityTaskSource entitySource) {
            Set<ServerPlayer> entities = entitySource.getEntities(ServerPlayer.class);
            if (!entities.isEmpty()) {
               player = entities.iterator().next();
            }
         }

         if (player != null) {
            Vault vault = ServerVaults.get(this.vaultUuid).orElse(null);
            int level = vault == null ? 0 : vault.getOptional(Vault.LEVEL).map(VaultLevel::get).orElse(0);
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
               text.append(player.getDisplayName())
                  .append(new TextComponent(" added ").withStyle(ChatFormatting.GRAY))
                  .append(suffix)
                  .append(new TextComponent(".").withStyle(ChatFormatting.GRAY));
            }

            Set<Player> notified = new HashSet<>();
            notified.add(player);
            if (vault != null) {
               groups.forEach((modifier, count) -> vault.ifPresent(Vault.MODIFIERS, value -> value.addModifier(modifier, count, true, source.getRandom())));

               for (Listener listener : vault.get(Vault.LISTENERS).getAll()) {
                  listener.getPlayer().ifPresent(notified::add);
               }
            }

            String message = MESSAGES.get(god).get(source.getRandom().nextInt(MESSAGES.get(god).size()));
            MutableComponent vgName = new TextComponent(god.getName()).withStyle(god.getChatColor());
            vgName.withStyle(style -> style.withHoverEvent(new HoverEvent(Action.SHOW_TEXT, god.getHoverChatComponent())));
            MutableComponent txt = new TextComponent("")
               .append(vgName)
               .append(new TextComponent(": ").withStyle(ChatFormatting.WHITE))
               .append(new TextComponent(message));

            for (Player other : notified) {
               other.level.playSound(null, other.getX(), other.getY(), other.getZ(), SoundEvents.BLAZE_DEATH, SoundSource.PLAYERS, 0.9F, 0.5F);
               other.displayClientMessage(txt, false);
               if (!modifiers.isEmpty()) {
                  other.displayClientMessage(text, false);
               }
            }
         }
      }
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      super.writeBits(buffer);
      Adapters.UUID.writeBits(this.vaultUuid, buffer);
      Adapters.UUID.writeBits(this.playerUuid, buffer);
      Adapters.UUID.writeBits(this.altarUuid, buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      super.readBits(buffer);
      this.vaultUuid = Adapters.UUID.readBits(buffer).orElse(null);
      this.playerUuid = Adapters.UUID.readBits(buffer).orElse(null);
      this.altarUuid = Adapters.UUID.readBits(buffer).orElse(null);
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return super.writeNbt().map(nbt -> {
         Adapters.UUID.writeNbt(this.vaultUuid).ifPresent(value -> nbt.put("vaultUuid", value));
         Adapters.UUID.writeNbt(this.playerUuid).ifPresent(value -> nbt.put("playerUuid", value));
         Adapters.UUID.writeNbt(this.altarUuid).ifPresent(value -> nbt.put("altarUuid", value));
         return (CompoundTag)nbt;
      });
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      super.readNbt(nbt);
      this.vaultUuid = Adapters.UUID.readNbt(nbt.get("vaultUuid")).orElse(null);
      this.playerUuid = Adapters.UUID.readNbt(nbt.get("playerUuid")).orElse(null);
      this.altarUuid = Adapters.UUID.readNbt(nbt.get("altarUuid")).orElse(null);
   }

   @Override
   public Optional<JsonObject> writeJson() {
      return super.writeJson().map(json -> {
         Adapters.UUID.writeJson(this.vaultUuid).ifPresent(value -> json.add("vaultUuid", value));
         Adapters.UUID.writeJson(this.playerUuid).ifPresent(value -> json.add("playerUuid", value));
         Adapters.UUID.writeJson(this.altarUuid).ifPresent(value -> json.add("altarUuid", value));
         return (JsonObject)json;
      });
   }

   @Override
   public void readJson(JsonObject json) {
      super.readJson(json);
      this.vaultUuid = Adapters.UUID.readJson(json.get("vaultUuid")).orElse(null);
      this.playerUuid = Adapters.UUID.readJson(json.get("playerUuid")).orElse(null);
      this.altarUuid = Adapters.UUID.readJson(json.get("altarUuid")).orElse(null);
   }

   static {
      MESSAGES.put(
         VaultGod.VELARA,
         Arrays.asList(
            "The harmony of the natural world eludes you.",
            "The beauty of nature thrives on resilience, but your actions have brought only discord.",
            "Your failures wither the very essence of life.",
            "The song of nature laments your inadequacy.",
            "You tarnish the purity of nature with your missteps."
         )
      );
      MESSAGES.put(
         VaultGod.TENOS,
         Arrays.asList(
            "Even the wisest can stumble on their path to enlightenment.",
            "Seek knowledge, learn from your failures, and you shall find your way.",
            "Failure is but a stepping stone on the road to enlightenment.",
            "Even the most brilliant minds face setbacks.",
            "In the pursuit of wisdom, missteps are bound to occur."
         )
      );
      MESSAGES.put(
         VaultGod.WENDARR,
         Arrays.asList(
            "A moment lost is a chance wasted.",
            "The sands of time never cease their relentless march.",
            "In the grand tapestry of existence, your actions are but threads.",
            "Time's embrace can be both cruel and kind.",
            "As the clock ticks, so do your choices shape your destiny."
         )
      );
      MESSAGES.put(
         VaultGod.IDONA,
         Arrays.asList(
            "Perhaps you should consider a different path.",
            "Weakness is a burden that few can afford.",
            "Your feeble attempt have left much to be desired.",
            "A hunter must be swift to adapt, or they are doomed to fail.",
            "No smiles will be cast upon those who crumble in the face of adversity."
         )
      );
   }
}
