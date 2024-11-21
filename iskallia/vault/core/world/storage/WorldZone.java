package iskallia.vault.core.world.storage;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import it.unimi.dsi.fastutil.longs.LongOpenHashSet;
import it.unimi.dsi.fastutil.longs.LongSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.ChunkPos;

public class WorldZone implements ISerializable<CompoundTag, JsonObject> {
   private final LongSet chunks = new LongOpenHashSet();
   private final List<BlockCuboid> area = new ArrayList<>();
   private boolean locked;
   private Boolean modify;

   public boolean isLocked() {
      return this.locked;
   }

   public void setLocked(boolean locked) {
      this.locked = locked;
   }

   public LongSet getChunks() {
      return this.chunks;
   }

   public boolean contains(BlockPos pos) {
      for (BlockCuboid cuboid : this.area) {
         if (cuboid.contains(pos)) {
            return true;
         }
      }

      return false;
   }

   public WorldZone add(BlockCuboid cuboid) {
      BlockCuboid.add(this.area, cuboid);

      for (int x = cuboid.getMinX(); x <= cuboid.getMaxX(); x += 16 - Math.floorMod(x, 16)) {
         for (int z = cuboid.getMinZ(); z <= cuboid.getMaxZ(); z += 16 - Math.floorMod(z, 16)) {
            this.chunks.add(ChunkPos.asLong(x >> 4, z >> 4));
         }
      }

      return this;
   }

   public void subtract(BlockCuboid cuboid) {
      throw new UnsupportedOperationException();
   }

   public Boolean canModify() {
      return this.modify;
   }

   public WorldZone setModify(Boolean modify) {
      this.modify = modify;
      return this;
   }

   @Override
   public void writeBits(BitBuffer buffer) {
      Adapters.INT_SEGMENTED_7.writeBits(Integer.valueOf(this.area.size()), buffer);

      for (BlockCuboid cuboid : this.area) {
         Adapters.BLOCK_CUBOID.writeBits(cuboid, buffer);
      }

      Adapters.BOOLEAN.writeBits(this.locked, buffer);
      Adapters.BOOLEAN.asNullable().writeBits(this.locked, buffer);
   }

   @Override
   public void readBits(BitBuffer buffer) {
      int size = Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();
      this.chunks.clear();
      this.area.clear();

      for (int i = 0; i < size; i++) {
         this.add(Adapters.BLOCK_CUBOID.readBits(buffer).orElseThrow());
      }

      this.locked = Adapters.BOOLEAN.readBits(buffer).orElseThrow();
      this.locked = Adapters.BOOLEAN.asNullable().readBits(buffer).orElseThrow();
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      return Optional.of(new CompoundTag()).map(nbt -> {
         ListTag list = new ListTag();

         for (BlockCuboid cuboid : this.area) {
            Adapters.BLOCK_CUBOID.writeNbt(cuboid).ifPresent(list::add);
         }

         nbt.put("area", list);
         Adapters.BOOLEAN.writeNbt(this.locked).ifPresent(tag -> nbt.put("locked", tag));
         Adapters.BOOLEAN.asNullable().writeNbt(this.modify).ifPresent(tag -> nbt.put("modify", tag));
         return (CompoundTag)nbt;
      });
   }

   public void readNbt(CompoundTag nbt) {
      this.area.clear();
      Tag var3 = nbt.get("area");
      if (var3 instanceof ListTag) {
         for (Tag tag : (ListTag)var3) {
            Adapters.BLOCK_CUBOID.readNbt(tag).ifPresent(this::add);
         }
      }

      this.locked = Adapters.BOOLEAN.readNbt(nbt.get("locked")).orElse(false);
      this.modify = Adapters.BOOLEAN.asNullable().readNbt(nbt.get("modify")).orElse(null);
   }
}
