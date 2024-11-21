package iskallia.vault.world.data;

import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.world.storage.WorldZones;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.UpdateWorldZonesDataMessage;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
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
public class WorldZonesData extends SavedData {
   public static WorldZonesData CLIENT = new WorldZonesData();
   protected static final String DATA_NAME = "the_vault_Zones";
   private final Map<ResourceKey<Level>, WorldZones> zones = new HashMap<>();
   private boolean cleared = false;
   private final Set<ResourceKey<Level>> addedDimensions = new HashSet<>();
   private final Set<ResourceKey<Level>> removedDimensions = new HashSet<>();

   public Map<ResourceKey<Level>, WorldZones> getZones() {
      return this.zones;
   }

   public boolean isCleared() {
      return this.cleared;
   }

   public Set<ResourceKey<Level>> getAddedDimensions() {
      return this.addedDimensions;
   }

   public Set<ResourceKey<Level>> getRemovedDimensions() {
      return this.removedDimensions;
   }

   public void clear() {
      this.zones.clear();
      this.cleared = true;
      this.addedDimensions.clear();
      this.removedDimensions.clear();
   }

   public WorldZones getOrCreate(ResourceKey<Level> dimension) {
      WorldZones zones = this.zones.get(dimension);
      if (zones != null) {
         return zones;
      } else {
         zones = new WorldZones();
         this.zones.put(dimension, zones);
         this.addedDimensions.add(dimension);
         return zones;
      }
   }

   public void remove(ResourceKey<Level> dimension) {
      this.zones.remove(dimension);
      this.addedDimensions.remove(dimension);
      this.removedDimensions.add(dimension);
   }

   public void refreshDiff() {
      this.cleared = false;
      this.addedDimensions.clear();
      this.removedDimensions.clear();

      for (WorldZones zones : this.zones.values()) {
         zones.refreshDiff();
      }
   }

   public boolean isDirty() {
      return true;
   }

   public CompoundTag save(CompoundTag nbt) {
      ListTag list = new ListTag();
      this.zones.forEach((dimension, zones) -> {
         CompoundTag entry = new CompoundTag();
         Adapters.DIMENSION.writeNbt((ResourceKey<Level>)dimension).ifPresent(tag -> entry.put("dimension", tag));
         Adapters.WORLD_ZONES.writeNbt(zones).ifPresent(tag -> entry.put("zones", tag));
         list.add(entry);
      });
      nbt.put("zones", list);
      return nbt;
   }

   public void load(CompoundTag nbt) {
      ListTag list = nbt.getList("zones", 10);
      this.zones.clear();

      for (int i = 0; i < list.size(); i++) {
         CompoundTag entry = list.getCompound(i);
         this.zones.put(Adapters.DIMENSION.readNbt(entry.get("dimension")).orElseThrow(), Adapters.WORLD_ZONES.readNbt(entry.get("zones")).orElseThrow());
      }
   }

   @SubscribeEvent
   public static void onPlayerLoggedIn(PlayerLoggedInEvent event) {
      if (event.getPlayer() instanceof ServerPlayer player) {
         WorldZonesData data = get(player.getServer());
         ModNetwork.CHANNEL.sendTo(UpdateWorldZonesDataMessage.full(data), player.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
      }
   }

   @SubscribeEvent
   public static void onServerTick(ServerTickEvent event) {
      if (event.phase == Phase.END) {
         WorldZonesData data = get(ServerLifecycleHooks.getCurrentServer());
         UpdateWorldZonesDataMessage packet = UpdateWorldZonesDataMessage.diff(data);
         if (!packet.isEmpty()) {
            ModNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), packet);
            data.refreshDiff();
         }
      }
   }

   public static WorldZonesData get(Level world) {
      return world instanceof ServerLevel serverWorld ? get(serverWorld.getServer()) : CLIENT;
   }

   public static WorldZonesData get(MinecraftServer server) {
      return (WorldZonesData)server.overworld().getDataStorage().computeIfAbsent(nbt -> {
         WorldZonesData data = new WorldZonesData();
         data.load(nbt);
         return data;
      }, WorldZonesData::new, "the_vault_Zones");
   }
}
