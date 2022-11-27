package iskallia.vault.core.data.adapter;

import iskallia.vault.core.data.sync.context.SyncContext;
import iskallia.vault.core.net.BitBuffer;
import net.minecraft.core.BlockPos;

public class BlockPosAdapter extends Adapter<BlockPos> {
   public final int minX;
   public final int minY;
   public final int minZ;
   public final int maxX;
   public final int maxY;
   public final int maxZ;
   public final boolean nullable;

   public BlockPosAdapter(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, boolean nullable) {
      this.minX = minX;
      this.minY = minY;
      this.minZ = minZ;
      this.maxX = maxX;
      this.maxY = maxY;
      this.maxZ = maxZ;
      this.nullable = nullable;
   }

   public BlockPosAdapter x(int min, int max) {
      return new BlockPosAdapter(min, this.minY, this.minZ, max, this.maxY, this.maxZ, true);
   }

   public BlockPosAdapter y(int min, int max) {
      return new BlockPosAdapter(this.minX, min, this.minZ, max, this.maxY, this.maxZ, true);
   }

   public BlockPosAdapter z(int min, int max) {
      return new BlockPosAdapter(this.minX, this.minY, min, this.maxX, this.maxY, max, true);
   }

   public BlockPosAdapter min(int minX, int minY, int minZ) {
      return new BlockPosAdapter(minX, minY, minZ, this.maxX, this.maxY, this.maxZ, true);
   }

   public BlockPosAdapter max(int maxX, int maxY, int maxZ) {
      return new BlockPosAdapter(this.minX, this.minY, this.minZ, maxX, maxY, maxZ, true);
   }

   public BlockPosAdapter asNullable() {
      return this.nullable ? this : new BlockPosAdapter(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ, true);
   }

   public BlockPos validate(BlockPos value, SyncContext context) {
      if (!this.nullable && value == null) {
         throw new UnsupportedOperationException("Value cannot be null");
      } else if (value == null) {
         return null;
      } else if (value.getX() < this.minX || value.getX() > this.maxX) {
         throw new UnsupportedOperationException(
            String.format("X value in (%d, %d, %d) is not between %d and %d", value.getX(), value.getY(), value.getZ(), this.minX, this.maxX)
         );
      } else if (value.getY() < this.minY || value.getY() > this.maxY) {
         throw new UnsupportedOperationException(
            String.format("Y value in (%d, %d, %d) is not between %d and %d", value.getX(), value.getY(), value.getZ(), this.minY, this.maxY)
         );
      } else if (value.getZ() >= this.minZ && value.getZ() <= this.maxZ) {
         return value;
      } else {
         throw new UnsupportedOperationException(
            String.format("Z value in (%d, %d, %d) is not between %d and %d", value.getX(), value.getY(), value.getZ(), this.minZ, this.maxZ)
         );
      }
   }

   public void writeValue(BitBuffer buffer, SyncContext context, BlockPos value) {
      if (this.nullable) {
         buffer.writeBoolean(value == null);
      }

      if (value != null) {
         buffer.writeIntBounded(value.getX(), this.minX, this.maxX);
         buffer.writeIntBounded(value.getY(), this.minY, this.maxY);
         buffer.writeIntBounded(value.getZ(), this.minZ, this.maxZ);
      }
   }

   public BlockPos readValue(BitBuffer buffer, SyncContext context, BlockPos value) {
      return this.nullable && buffer.readBoolean()
         ? null
         : new BlockPos(buffer.readIntBounded(this.minX, this.maxX), buffer.readIntBounded(this.minY, this.maxY), buffer.readIntBounded(this.minZ, this.maxZ));
   }
}
