package iskallia.vault.core.data.adapter.vault;

import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.item.crystal.data.adapter.IBitAdapter;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import org.jetbrains.annotations.Nullable;

@Deprecated
public class LegacyBlockPosAdapter implements IBitAdapter<BlockPos, Object> {
   public final int minX;
   public final int minY;
   public final int minZ;
   public final int maxX;
   public final int maxY;
   public final int maxZ;
   public final boolean nullable;

   public LegacyBlockPosAdapter(int minX, int minY, int minZ, int maxX, int maxY, int maxZ, boolean nullable) {
      this.minX = minX;
      this.minY = minY;
      this.minZ = minZ;
      this.maxX = maxX;
      this.maxY = maxY;
      this.maxZ = maxZ;
      this.nullable = nullable;
   }

   public static LegacyBlockPosAdapter create() {
      return new LegacyBlockPosAdapter(-29999999, -63, -29999999, 30000000, 320, 30000000, false);
   }

   public LegacyBlockPosAdapter x(int min, int max) {
      return new LegacyBlockPosAdapter(min, this.minY, this.minZ, max, this.maxY, this.maxZ, true);
   }

   public LegacyBlockPosAdapter y(int min, int max) {
      return new LegacyBlockPosAdapter(this.minX, min, this.minZ, max, this.maxY, this.maxZ, true);
   }

   public LegacyBlockPosAdapter z(int min, int max) {
      return new LegacyBlockPosAdapter(this.minX, this.minY, min, this.maxX, this.maxY, max, true);
   }

   public LegacyBlockPosAdapter min(int minX, int minY, int minZ) {
      return new LegacyBlockPosAdapter(minX, minY, minZ, this.maxX, this.maxY, this.maxZ, true);
   }

   public LegacyBlockPosAdapter max(int maxX, int maxY, int maxZ) {
      return new LegacyBlockPosAdapter(this.minX, this.minY, this.minZ, maxX, maxY, maxZ, true);
   }

   public LegacyBlockPosAdapter asNullable() {
      return new LegacyBlockPosAdapter(this.minX, this.minY, this.minZ, this.maxX, this.maxY, this.maxZ, true);
   }

   public void writeBits(@Nullable BlockPos value, BitBuffer buffer, Object context) {
      if (this.nullable) {
         buffer.writeBoolean(value == null);
      }

      if (value != null) {
         buffer.writeIntBounded(value.getX(), this.minX, this.maxX);
         buffer.writeIntBounded(value.getY(), this.minY, this.maxY);
         buffer.writeIntBounded(value.getZ(), this.minZ, this.maxZ);
      }
   }

   @Override
   public Optional<BlockPos> readBits(BitBuffer buffer, Object context) {
      return this.nullable && buffer.readBoolean()
         ? Optional.empty()
         : Optional.of(
            new BlockPos(buffer.readIntBounded(this.minX, this.maxX), buffer.readIntBounded(this.minY, this.maxY), buffer.readIntBounded(this.minZ, this.maxZ))
         );
   }
}
