package iskallia.vault.attribute;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.math.MutableBoundingBox;

public class BoundingBoxAttribute extends VAttribute.Instance<MutableBoundingBox> {
   @Override
   public void write(CompoundNBT nbt) {
      if (this.getBaseValue() != null) {
         nbt.func_218657_a("BaseValue", this.getBaseValue().func_151535_h());
      }
   }

   @Override
   public void read(CompoundNBT nbt) {
      if (nbt.func_150297_b("BaseValue", 11)) {
         this.setBaseValue(new MutableBoundingBox(nbt.func_74759_k("BaseValue")));
      }
   }
}
