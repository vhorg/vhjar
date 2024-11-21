package iskallia.vault.core.world.storage;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.item.crystal.data.adapter.ISimpleAdapter;
import iskallia.vault.item.crystal.data.serializable.ISerializable;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.NumericTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.block.Rotation;
import org.jetbrains.annotations.Nullable;

public class BlockCuboid implements ISerializable<CompoundTag, JsonObject> {
   private final int minX;
   private final int minY;
   private final int minZ;
   private final int maxX;
   private final int maxY;
   private final int maxZ;

   protected BlockCuboid(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
      this.minX = minX;
      this.minY = minY;
      this.minZ = minZ;
      this.maxX = maxX;
      this.maxY = maxY;
      this.maxZ = maxZ;
   }

   public static BlockCuboid of(int minX, int minY, int minZ, int maxX, int maxY, int maxZ) {
      return new BlockCuboid(minX, minY, minZ, maxX, maxY, maxZ);
   }

   public static BlockCuboid of(BlockPos min, BlockPos max) {
      return of(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ());
   }

   public int getMinX() {
      return this.minX;
   }

   public int getMinY() {
      return this.minY;
   }

   public int getMinZ() {
      return this.minZ;
   }

   public int getMaxX() {
      return this.maxX;
   }

   public int getMaxY() {
      return this.maxY;
   }

   public int getMaxZ() {
      return this.maxZ;
   }

   public static BlockCuboid of(BlockPos pos) {
      return of(pos, pos);
   }

   public List<BlockCuboid> subtract(BlockCuboid other) {
      return List.of(this);
   }

   public Optional<BlockCuboid> combine(BlockCuboid other) {
      if (this.equals(other)) {
         return Optional.of(this);
      } else if (other.minX > this.maxX || this.minX > other.maxX) {
         return Optional.empty();
      } else if (other.minY > this.maxY || this.minY > other.maxY) {
         return Optional.empty();
      } else {
         return other.minZ <= this.maxZ && this.minX <= other.maxZ
            ? Optional.of(
               of(
                  Math.min(this.minX, other.minX),
                  Math.min(this.minY, other.minY),
                  Math.min(this.minZ, other.minZ),
                  Math.max(this.maxX, other.maxX),
                  Math.max(this.maxY, other.maxY),
                  Math.max(this.maxZ, other.maxZ)
               )
            )
            : Optional.empty();
      }
   }

   public static void add(List<BlockCuboid> cuboids, BlockCuboid other) {
      BlockCuboid added = other;
      int addedIndex = -1;
      boolean done = false;

      while (!done) {
         done = true;

         for (int i = cuboids.size() - 1; i >= 0; i--) {
            if (i != addedIndex) {
               BlockCuboid merged = cuboids.get(i).combine(added).orElse(null);
               if (merged != null) {
                  cuboids.set(i, merged);
                  done = false;
                  if (addedIndex >= 0) {
                     cuboids.remove(addedIndex);
                  }

                  added = merged;
                  addedIndex = i;
               }
            }
         }
      }

      if (addedIndex < 0) {
         cuboids.add(added);
      }
   }

   @Override
   public boolean equals(Object object) {
      if (this == object) {
         return true;
      } else if (object != null && this.getClass() == object.getClass()) {
         BlockCuboid other = (BlockCuboid)object;
         return this.minX == other.minX
            && this.minY == other.minY
            && this.minZ == other.minZ
            && this.maxX == other.maxX
            && this.maxY == other.maxY
            && this.maxZ == other.maxZ;
      } else {
         return false;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ);
   }

   public BlockCuboid offset(Vec3i offset) {
      return new BlockCuboid(
         this.minX + offset.getX(),
         this.minY + offset.getY(),
         this.minZ + offset.getZ(),
         this.maxX + offset.getX(),
         this.maxY + offset.getY(),
         this.maxZ + offset.getZ()
      );
   }

   public boolean contains(Vec3i point) {
      return this.minX <= point.getX()
         && this.maxX >= point.getX()
         && this.minY <= point.getY()
         && this.maxY >= point.getY()
         && this.minZ <= point.getZ()
         && this.maxZ >= point.getZ();
   }

   public BlockCuboid grow(int x, int y, int z) {
      return new BlockCuboid(this.minX - x, this.minY - y, this.minZ - z, this.maxX + x, this.maxY + y, this.maxZ + z);
   }

   public BlockCuboid rotate(Rotation rotation, BlockPos pivot, boolean centered) {
      int termX;
      int termZ;
      switch (rotation) {
         case COUNTERCLOCKWISE_90:
            termX = pivot.getX() - pivot.getZ() - (centered ? 0 : 1);
            termZ = pivot.getX() + pivot.getZ();
            break;
         case CLOCKWISE_90:
            termX = pivot.getX() + pivot.getZ();
            termZ = pivot.getZ() - pivot.getX() - (centered ? 0 : 1);
            break;
         case CLOCKWISE_180:
            termX = pivot.getX() + pivot.getX() - (centered ? 0 : 1);
            termZ = pivot.getZ() + pivot.getZ() - (centered ? 0 : 1);
            break;
         default:
            termX = 0;
            termZ = 0;
      }
      BlockPos min = switch (rotation) {
         case COUNTERCLOCKWISE_90 -> new BlockPos(termX + this.minZ, this.minY, termZ - this.minX);
         case CLOCKWISE_90 -> new BlockPos(termX - this.minZ, this.minY, termZ + this.minX);
         case CLOCKWISE_180 -> new BlockPos(termX - this.minX, this.minY, termZ - this.minZ);
         default -> new BlockPos(this.minX, this.minY, this.minZ);
      };

      BlockPos max = switch (rotation) {
         case COUNTERCLOCKWISE_90 -> new BlockPos(termX + this.maxZ, this.maxY, termZ - this.maxX);
         case CLOCKWISE_90 -> new BlockPos(termX - this.maxZ, this.maxY, termZ + this.maxX);
         case CLOCKWISE_180 -> new BlockPos(termX - this.maxX, this.maxY, termZ - this.maxZ);
         default -> new BlockPos(this.maxX, this.maxY, this.maxZ);
      };
      return new BlockCuboid(
         Math.min(min.getX(), max.getX()),
         min.getY(),
         Math.min(min.getZ(), max.getZ()),
         Math.max(min.getX(), max.getX()),
         max.getY(),
         Math.max(min.getZ(), max.getZ())
      );
   }

   public static class Adapter implements ISimpleAdapter<BlockCuboid, Tag, JsonElement> {
      public void writeBits(@Nullable BlockCuboid value, BitBuffer buffer) {
         Adapters.BOOLEAN.writeBits(value == null, buffer);
         if (value != null) {
            Adapters.INT_SEGMENTED_7.writeBits(Integer.valueOf(value.minX), buffer);
            Adapters.INT_SEGMENTED_7.writeBits(Integer.valueOf(value.minY), buffer);
            Adapters.INT_SEGMENTED_7.writeBits(Integer.valueOf(value.minZ), buffer);
            Adapters.INT_SEGMENTED_7.writeBits(Integer.valueOf(value.maxX - value.minX), buffer);
            Adapters.INT_SEGMENTED_7.writeBits(Integer.valueOf(value.maxY - value.minY), buffer);
            Adapters.INT_SEGMENTED_7.writeBits(Integer.valueOf(value.maxZ - value.minZ), buffer);
         }
      }

      @Override
      public Optional<BlockCuboid> readBits(BitBuffer buffer) {
         if (Adapters.BOOLEAN.readBits(buffer).orElseThrow()) {
            return Optional.empty();
         } else {
            int minX = Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();
            int minY = Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();
            int minZ = Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();
            int maxX = minX + Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();
            int maxY = minY + Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();
            int maxZ = minZ + Adapters.INT_SEGMENTED_7.readBits(buffer).orElseThrow();
            return Optional.of(new BlockCuboid(minX, minY, minZ, maxX, maxY, maxZ));
         }
      }

      public Optional<Tag> writeNbt(@Nullable BlockCuboid value) {
         if (value == null) {
            return Optional.empty();
         } else {
            ListTag array = new ListTag();
            array.add(LongTag.valueOf(BlockPos.asLong(value.minX, value.minY, value.minZ)));
            array.add(LongTag.valueOf(BlockPos.asLong(value.maxX, value.maxY, value.maxZ)));
            return Optional.of(array);
         }
      }

      @Override
      public Optional<BlockCuboid> readNbt(@Nullable Tag nbt) {
         if (nbt instanceof ListTag list && list.size() == 2 && list.get(0) instanceof NumericTag) {
            long min = ((NumericTag)list.get(0)).getAsLong();
            long max = ((NumericTag)list.get(1)).getAsLong();
            return Optional.of(BlockCuboid.of(BlockPos.of(min), BlockPos.of(max)));
         } else if (nbt instanceof CompoundTag compound && compound.getAllKeys().size() == 2) {
            CollectionTag<?> min = compound.get("min") instanceof CollectionTag<?> tag ? tag : null;
            CollectionTag<?> max = compound.get("max") instanceof CollectionTag<?> tagx ? tagx : null;
            return min != null && max != null && min.size() == 3 && max.size() == 3
               ? Optional.of(
                  BlockCuboid.of(
                     Adapters.INT.readNbt((Tag)min.get(0)).orElseThrow(),
                     Adapters.INT.readNbt((Tag)min.get(1)).orElseThrow(),
                     Adapters.INT.readNbt((Tag)min.get(2)).orElseThrow(),
                     Adapters.INT.readNbt((Tag)max.get(0)).orElseThrow(),
                     Adapters.INT.readNbt((Tag)max.get(1)).orElseThrow(),
                     Adapters.INT.readNbt((Tag)max.get(2)).orElseThrow()
                  )
               )
               : Optional.empty();
         } else {
            return Optional.empty();
         }
      }
   }
}
