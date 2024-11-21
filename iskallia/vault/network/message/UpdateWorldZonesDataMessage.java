package iskallia.vault.network.message;

import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.world.storage.WorldZone;
import iskallia.vault.core.world.storage.WorldZones;
import iskallia.vault.world.data.WorldZonesData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent.Context;

public class UpdateWorldZonesDataMessage {
   private final boolean reset;
   private final Collection<ResourceKey<Level>> addedDimensions;
   private final Collection<ResourceKey<Level>> removedDimensions;
   private final Map<ResourceKey<Level>, Map<Integer, WorldZone>> addedZones;
   private final Map<ResourceKey<Level>, Collection<Integer>> removedZones;

   private UpdateWorldZonesDataMessage() {
      this.reset = false;
      this.addedDimensions = new ArrayList<>();
      this.removedDimensions = new ArrayList<>();
      this.addedZones = new HashMap<>();
      this.removedZones = new HashMap<>();
   }

   private UpdateWorldZonesDataMessage(
      boolean reset,
      Collection<ResourceKey<Level>> addedDimensions,
      Collection<ResourceKey<Level>> removedDimensions,
      Map<ResourceKey<Level>, Map<Integer, WorldZone>> addedZones,
      Map<ResourceKey<Level>, Collection<Integer>> removedZones
   ) {
      this.reset = reset;
      this.addedDimensions = addedDimensions;
      this.removedDimensions = removedDimensions;
      this.addedZones = addedZones;
      this.removedZones = removedZones;
   }

   public static UpdateWorldZonesDataMessage full(WorldZonesData data) {
      Map<ResourceKey<Level>, Map<Integer, WorldZone>> added = new HashMap<>();
      data.getZones().forEach((dimension, zones) -> {
         Map<Integer, WorldZone> map = added.computeIfAbsent((ResourceKey<Level>)dimension, key -> new HashMap<>());
         map.putAll(zones.getAll());
      });
      return new UpdateWorldZonesDataMessage(true, data.getZones().keySet(), new ArrayList<>(), added, new HashMap<>());
   }

   public static UpdateWorldZonesDataMessage diff(WorldZonesData data) {
      Map<ResourceKey<Level>, Map<Integer, WorldZone>> added = new HashMap<>();
      Map<ResourceKey<Level>, Collection<Integer>> removed = new HashMap<>();
      data.getZones().forEach((dimension, zones) -> {
         if (!zones.getAddedZones().isEmpty() || !zones.getRemovedZones().isEmpty()) {
            Map<Integer, WorldZone> addedMap = added.computeIfAbsent((ResourceKey<Level>)dimension, key -> new HashMap<>());
            Collection<Integer> removedMap = removed.computeIfAbsent((ResourceKey<Level>)dimension, key -> new ArrayList<>());
            zones.getAddedZones().forEach(index -> zones.get(index).ifPresent(zone -> addedMap.put(index, zone)));
            removedMap.addAll(zones.getRemovedZones());
         }
      });
      return new UpdateWorldZonesDataMessage(data.isCleared(), data.getAddedDimensions(), data.getRemovedDimensions(), added, removed);
   }

   public boolean isEmpty() {
      return !this.reset && this.addedDimensions.isEmpty() && this.removedDimensions.isEmpty() && this.addedZones.isEmpty() && this.removedZones.isEmpty();
   }

   public static void encode(UpdateWorldZonesDataMessage message, FriendlyByteBuf buffer) {
      buffer.writeBoolean(message.reset);
      buffer.writeCollection(message.addedDimensions, (buf, dimension) -> Adapters.DIMENSION.writeBytes(dimension, buffer));
      buffer.writeCollection(message.removedDimensions, (buf, dimension) -> Adapters.DIMENSION.writeBytes(dimension, buffer));
      buffer.writeMap(
         message.addedZones,
         (buf, dimension) -> Adapters.DIMENSION.writeBytes(dimension, buffer),
         (buf, zones) -> buffer.writeMap(zones, FriendlyByteBuf::writeVarInt, (buf2, zone) -> {
            CompoundTag nbt = (CompoundTag)Adapters.WORLD_ZONE.writeNbt(zone).orElseThrow();
            buf2.writeNbt(nbt);
         })
      );
      buffer.writeMap(
         message.removedZones,
         (buf, dimension) -> Adapters.DIMENSION.writeBytes(dimension, buffer),
         (buf, zones) -> buffer.writeCollection(zones, FriendlyByteBuf::writeVarInt)
      );
   }

   public static UpdateWorldZonesDataMessage decode(FriendlyByteBuf buffer) {
      boolean reset = buffer.readBoolean();
      List<ResourceKey<Level>> addedDimensions = (List<ResourceKey<Level>>)buffer.readCollection(
         ArrayList::new, buf -> Adapters.DIMENSION.readBytes(buffer).orElseThrow()
      );
      List<ResourceKey<Level>> removedDimensions = (List<ResourceKey<Level>>)buffer.readCollection(
         ArrayList::new, buf -> Adapters.DIMENSION.readBytes(buffer).orElseThrow()
      );
      Map<ResourceKey<Level>, Map<Integer, WorldZone>> addedZones = buffer.readMap(
         HashMap::new, buf -> Adapters.DIMENSION.readBytes(buffer).orElseThrow(), buf -> buffer.readMap(HashMap::new, FriendlyByteBuf::readVarInt, buf2 -> {
            CompoundTag nbt = buf2.readNbt();
            return Adapters.WORLD_ZONE.readNbt(nbt).orElseThrow();
         })
      );
      Map<ResourceKey<Level>, Collection<Integer>> removedZones = buffer.readMap(
         HashMap::new, buf -> Adapters.DIMENSION.readBytes(buffer).orElseThrow(), buf -> buf.readList(FriendlyByteBuf::readVarInt)
      );
      return new UpdateWorldZonesDataMessage(reset, addedDimensions, removedDimensions, addedZones, removedZones);
   }

   public static void handle(UpdateWorldZonesDataMessage message, Supplier<Context> contextSupplier) {
      Context context = contextSupplier.get();
      context.enqueueWork(() -> {
         if (message.reset) {
            WorldZonesData.CLIENT.clear();
         }

         message.removedDimensions.forEach(dimension -> WorldZonesData.CLIENT.getZones().remove(dimension));
         message.addedDimensions.forEach(dimension -> WorldZonesData.CLIENT.getZones().put((ResourceKey<Level>)dimension, new WorldZones()));
         message.removedZones.forEach((dimension, indices) -> {
            WorldZones zones = WorldZonesData.CLIENT.getZones().get(dimension);
            if (zones != null) {
               indices.forEach(zones::remove);
            }
         });
         message.addedZones.forEach((dimension, added) -> {
            WorldZones zones = WorldZonesData.CLIENT.getZones().computeIfAbsent((ResourceKey<Level>)dimension, key -> new WorldZones());
            added.forEach(zones::add);
         });
         WorldZonesData.CLIENT.refreshDiff();
      });
      context.setPacketHandled(true);
   }
}
