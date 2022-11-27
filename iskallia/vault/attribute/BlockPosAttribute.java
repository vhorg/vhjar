package iskallia.vault.attribute;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;

public class BlockPosAttribute extends VAttribute.Instance<BlockPos> {
   @Override
   public void write(CompoundTag nbt) {
      if (this.getBaseValue() != null) {
         nbt.putIntArray("BaseValue", new int[]{this.getBaseValue().getX(), this.getBaseValue().getY(), this.getBaseValue().getZ()});
      }
   }

   @Override
   public void read(CompoundTag nbt) {
      if (nbt.contains("BaseValue", 11)) {
         int[] pos = nbt.getIntArray("BaseValue");
         this.setBaseValue(new BlockPos(pos[0], pos[1], pos[2]));
      }
   }
}
