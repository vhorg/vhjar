package iskallia.vault.attribute;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class IdentifierAttribute extends VAttribute.Instance<ResourceLocation> {
   @Override
   public void write(CompoundNBT nbt) {
      if (this.getBaseValue() != null) {
         nbt.func_74778_a("BaseValue", this.getBaseValue().toString());
      }
   }

   @Override
   public void read(CompoundNBT nbt) {
      if (nbt.func_150297_b("BaseValue", 8)) {
         this.setBaseValue(new ResourceLocation(nbt.func_74779_i("BaseValue")));
      }
   }
}
