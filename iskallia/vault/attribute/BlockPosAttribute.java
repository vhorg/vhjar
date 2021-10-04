package iskallia.vault.attribute;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.BlockPos;

public class BlockPosAttribute extends VAttribute.Instance<BlockPos> {
   @Override
   public void write(CompoundNBT nbt) {
      if (this.getBaseValue() != null) {
         nbt.func_74783_a("BaseValue", new int[]{this.getBaseValue().func_177958_n(), this.getBaseValue().func_177956_o(), this.getBaseValue().func_177952_p()});
      }
   }

   @Override
   public void read(CompoundNBT nbt) {
      if (nbt.func_150297_b("BaseValue", 11)) {
         int[] pos = nbt.func_74759_k("BaseValue");
         this.setBaseValue(new BlockPos(pos[0], pos[1], pos[2]));
      }
   }
}
