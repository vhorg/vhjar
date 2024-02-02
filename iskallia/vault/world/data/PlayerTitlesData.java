package iskallia.vault.world.data;

import iskallia.vault.config.PlayerTitlesConfig;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.item.crystal.data.serializable.INbtSerializable;
import iskallia.vault.nbt.VMapNBT;
import iskallia.vault.network.message.UpdateTitlesDataMessage;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraftforge.event.TickEvent.Phase;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.server.ServerLifecycleHooks;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public class PlayerTitlesData extends SavedData {
   protected static final String DATA_NAME = "the_vault_PlayerCustomNames";
   public static PlayerTitlesData CLIENT = new PlayerTitlesData();
   public VMapNBT<UUID, PlayerTitlesData.Entry> entries = VMapNBT.ofUUID(PlayerTitlesData.Entry::new);

   public void putAll(Map<UUID, PlayerTitlesData.Entry> entries) {
      this.entries.putAll(entries);
   }

   public static void setCustomName(Player player, String id, PlayerTitlesConfig.Affix affix) {
      setCustomName(player.getUUID(), id, affix, player.level.isClientSide());
   }

   public static void setCustomName(UUID uuid, String id, PlayerTitlesConfig.Affix affix, boolean isClient) {
      VMapNBT<UUID, PlayerTitlesData.Entry> entries = isClient ? CLIENT.entries : get().entries;
      PlayerTitlesData.Entry entry = entries.computeIfAbsent(uuid, _uuid -> new PlayerTitlesData.Entry());
      if (affix == PlayerTitlesConfig.Affix.PREFIX) {
         entry.setPrefix(id);
      } else if (affix == PlayerTitlesConfig.Affix.SUFFIX) {
         entry.setSuffix(id);
      }

      entry.setChanged(true);
   }

   public static Optional<MutableComponent> getCustomName(Player player, Component name, PlayerTitlesData.Type type) {
      return getCustomName(player.getUUID(), name, type, player.level.isClientSide());
   }

   public static Optional<MutableComponent> getCustomName(UUID uuid, Component name, PlayerTitlesData.Type type, boolean isClient) {
      VMapNBT<UUID, PlayerTitlesData.Entry> entries = isClient ? CLIENT.entries : get().entries;
      return entries.computeIfAbsent(uuid, _uuid -> new PlayerTitlesData.Entry()).getCustomName(name, type);
   }

   @SubscribeEvent
   public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
      if (event.getPlayer() instanceof ServerPlayer player) {
         ModNetwork.CHANNEL.sendTo(new UpdateTitlesDataMessage(get().entries), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
      }
   }

   @SubscribeEvent
   public static void onServerTick(ServerTickEvent event) {
      if (event.phase == Phase.END) {
         Map<UUID, PlayerTitlesData.Entry> changed = new HashMap<>();
         get().entries.forEach((uuid, entry) -> {
            if (entry.changed) {
               changed.put(uuid, entry);
               entry.setChanged(false);
            }
         });
         if (!changed.isEmpty()) {
            ModNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), new UpdateTitlesDataMessage(changed));
         }
      }
   }

   public boolean isDirty() {
      return true;
   }

   private static PlayerTitlesData load(CompoundTag nbt) {
      PlayerTitlesData data = new PlayerTitlesData();
      data.entries.deserializeNBT(nbt.getList("entries", 10));
      return data;
   }

   @Nonnull
   public CompoundTag save(CompoundTag nbt) {
      nbt.put("entries", this.entries.serializeNBT());
      return nbt;
   }

   public static PlayerTitlesData get() {
      return (PlayerTitlesData)ServerLifecycleHooks.getCurrentServer()
         .overworld()
         .getDataStorage()
         .computeIfAbsent(PlayerTitlesData::load, PlayerTitlesData::new, "the_vault_PlayerCustomNames");
   }

   public static class Entry implements INbtSerializable<CompoundTag> {
      private String prefix;
      private String suffix;
      private boolean changed;

      public Entry() {
         this(null, null);
      }

      public Entry(String prefix, String suffix) {
         this.prefix = prefix;
         this.suffix = suffix;
         this.changed = true;
      }

      public Optional<String> getPrefix() {
         return Optional.ofNullable(this.prefix);
      }

      public Optional<String> getSuffix() {
         return Optional.ofNullable(this.suffix);
      }

      public boolean isChanged() {
         return this.changed;
      }

      public void setPrefix(String prefix) {
         this.prefix = prefix;
         this.setChanged(true);
      }

      public void setSuffix(String suffix) {
         this.suffix = suffix;
         this.setChanged(true);
      }

      public void setChanged(boolean changed) {
         this.changed = changed;
      }

      public Optional<MutableComponent> getCustomName(Component name, PlayerTitlesData.Type type) {
         MutableComponent prefix = ModConfigs.PLAYER_TITLES
            .get(PlayerTitlesConfig.Affix.PREFIX, this.prefix)
            .map(title -> title.getDisplay(type))
            .flatMap(PlayerTitlesConfig.Display::getComponent)
            .orElse(null);
         MutableComponent suffix = ModConfigs.PLAYER_TITLES
            .get(PlayerTitlesConfig.Affix.SUFFIX, this.suffix)
            .map(title -> title.getDisplay(type))
            .flatMap(PlayerTitlesConfig.Display::getComponent)
            .orElse(null);
         if (prefix == null && suffix == null) {
            return Optional.empty();
         } else {
            TextComponent component = new TextComponent("");
            if (prefix != null) {
               component.append(prefix);
            }

            component.append(name);
            if (suffix != null) {
               component.append(suffix);
            }

            return Optional.of(component);
         }
      }

      @Override
      public Optional<CompoundTag> writeNbt() {
         CompoundTag nbt = new CompoundTag();
         Adapters.UTF_8.writeNbt(this.prefix).ifPresent(tag -> nbt.put("Prefix", tag));
         Adapters.UTF_8.writeNbt(this.suffix).ifPresent(tag -> nbt.put("Suffix", tag));
         return Optional.of(nbt);
      }

      public void readNbt(CompoundTag nbt) {
         this.prefix = Adapters.UTF_8.readNbt(nbt.get("Prefix")).orElse(null);
         this.suffix = Adapters.UTF_8.readNbt(nbt.get("Suffix")).orElse(null);
      }

      public PlayerTitlesData.Entry copy() {
         PlayerTitlesData.Entry entry = new PlayerTitlesData.Entry();
         entry.prefix = this.prefix;
         entry.suffix = this.suffix;
         entry.changed = this.changed;
         return entry;
      }
   }

   public static enum Type {
      CHAT,
      TAB_LIST;
   }
}
