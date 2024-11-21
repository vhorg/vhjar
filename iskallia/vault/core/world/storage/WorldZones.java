package iskallia.vault.core.world.storage;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.ints.IntIterator;
import it.unimi.dsi.fastutil.ints.IntOpenHashSet;
import it.unimi.dsi.fastutil.ints.IntSet;
import it.unimi.dsi.fastutil.longs.Long2ObjectMap;
import it.unimi.dsi.fastutil.longs.Long2ObjectOpenHashMap;
import it.unimi.dsi.fastutil.longs.LongIterator;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.world.level.ChunkPos;

public class WorldZones implements ISerializable<CompoundTag, JsonObject> {
   private Long2ObjectMap<IntSet> chunkToZones = new Long2ObjectOpenHashMap();
   private Int2ObjectMap<WorldZone> indexToZone = new Int2ObjectOpenHashMap();
   private int index;
   private IntSet addedZones = new IntOpenHashSet();
   private IntSet removedZones = new IntOpenHashSet();

   public Int2ObjectMap<WorldZone> getAll() {
      return this.indexToZone;
   }

   public IntSet getAddedZones() {
      return this.addedZones;
   }

   public IntSet getRemovedZones() {
      return this.removedZones;
   }

   public Optional<WorldZone> get(int zoneIndex) {
      return Optional.ofNullable((WorldZone)this.indexToZone.get(zoneIndex));
   }

   public List<WorldZone> get(BlockPos pos) {
      return this.get(pos.getX(), pos.getY(), pos.getZ());
   }

   public List<WorldZone> get(int x, int y, int z) {
      long chunk = ChunkPos.asLong(x >> 4, z >> 4);
      List<WorldZone> zones = new ArrayList<>();
      IntSet affected = (IntSet)this.chunkToZones.get(chunk);
      if (affected == null) {
         return zones;
      } else {
         IntIterator var8 = affected.iterator();

         while (var8.hasNext()) {
            int index = (Integer)var8.next();
            WorldZone zone = (WorldZone)this.indexToZone.get(index);
            if (zone != null && zone.contains(new BlockPos(x, y, z))) {
               zones.add(zone);
            }
         }

         return zones;
      }
   }

   public int add(int index, WorldZone zone) {
      this.remove(index);
      zone.setLocked(true);
      this.indexToZone.put(index, zone);
      LongIterator var3 = zone.getChunks().iterator();

      while (var3.hasNext()) {
         long chunk = (Long)var3.next();
         ((IntSet)this.chunkToZones.computeIfAbsent(chunk, l -> new IntOpenHashSet())).add(index);
      }

      this.removedZones.remove(index);
      this.addedZones.add(index);
      return index;
   }

   public int add(WorldZone zone) {
      return this.add(this.index++, zone);
   }

   public void remove(int zoneIndex) {
      WorldZone region = (WorldZone)this.indexToZone.remove(zoneIndex);
      if (region != null) {
         region.getChunks().iterator().forEachRemaining(chunk -> {
            IntSet chunkRegions = (IntSet)this.chunkToZones.get(chunk);
            if (chunkRegions != null) {
               chunkRegions.remove(zoneIndex);
               if (chunkRegions.isEmpty()) {
                  this.chunkToZones.remove(chunk);
               }
            }
         });
         this.addedZones.remove(zoneIndex);
         this.removedZones.add(zoneIndex);
      }
   }

   public void refreshDiff() {
      this.addedZones.clear();
      this.removedZones.clear();
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      Adapters.INT_SEGMENTED_7.writeBits(Integer.valueOf(this.indexToZone.size()), buffer);
      this.indexToZone.forEach((index, zone) -> {
         Adapters.INT_SEGMENTED_7.writeBits(index, buffer);
         Adapters.WORLD_ZONE.writeBits(zone, buffer);
      });
   }

   @Override
   public void readBits(BitBuffer buffer) {
      int size = Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();
      this.chunkToZones.clear();
      this.indexToZone.clear();

      for (int i = 0; i < size; i++) {
         this.indexToZone.put(Adapters.INT_SEGMENTED_3.readBits(buffer).orElseThrow(), Adapters.WORLD_ZONE.readBits(buffer).orElseThrow());
      }

      this.indexToZone.forEach((index, zone) -> {
         LongIterator var3x = zone.getChunks().iterator();

         while (var3x.hasNext()) {
            long chunk = (Long)var3x.next();
            ((IntSet)this.chunkToZones.computeIfAbsent(chunk, l -> new IntOpenHashSet())).add(index);
         }
      });
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return Optional.of(new CompoundTag()).map(nbt -> {
         ListTag list = new ListTag();
         this.indexToZone.forEach((index, zone) -> {
            CompoundTag entry = new CompoundTag();
            Adapters.INT.writeNbt(index).ifPresent(tag -> entry.put("index", tag));
            Adapters.WORLD_ZONE.writeNbt(zone).ifPresent(tag -> entry.put("zone", tag));
            list.add(entry);
         });
         nbt.put("zones", list);
         Adapters.INT.writeNbt(Integer.valueOf(this.index)).ifPresent(tag -> nbt.put("index", tag));
         return (CompoundTag)nbt;
      });
   }

   public void readNbt(CompoundTag nbt) {
      this.chunkToZones.clear();
      this.indexToZone.clear();
      ListTag list = nbt.getList("zones", 10);

      for (int i = 0; i < list.size(); i++) {
         CompoundTag entry = list.getCompound(i);
         this.indexToZone.put(Adapters.INT.readNbt(entry.get("index")).orElseThrow(), Adapters.WORLD_ZONE.readNbt(entry.getCompound("zone")).orElseThrow());
      }

      this.indexToZone.forEach((index, zone) -> {
         LongIterator var3x = zone.getChunks().iterator();

         while (var3x.hasNext()) {
            long chunk = (Long)var3x.next();
            ((IntSet)this.chunkToZones.computeIfAbsent(chunk, l -> new IntOpenHashSet())).add(index);
         }
      });
   }
}
