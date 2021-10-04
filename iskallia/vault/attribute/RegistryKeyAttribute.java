package iskallia.vault.attribute;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;

public class RegistryKeyAttribute<T> extends VAttribute.Instance<RegistryKey<T>> {
   @Override
   public void write(CompoundNBT nbt) {
      if (this.getBaseValue() != null) {
         CompoundNBT valueNBT = new CompoundNBT();
         valueNBT.func_74778_a("Parent", this.getBaseValue().getRegistryName().toString());
         valueNBT.func_74778_a("Identifier", this.getBaseValue().func_240901_a_().toString());
         nbt.func_218657_a("BaseValue", valueNBT);
      }
   }

   @Override
   public void read(CompoundNBT nbt) {
      if (nbt.func_150297_b("BaseValue", 10)) {
         CompoundNBT valueNBT = nbt.func_74775_l("BaseValue");
         this.setBaseValue(
            RegistryKey.func_240903_a_(
               RegistryKey.func_240904_a_(new ResourceLocation(valueNBT.func_74779_i("Parent"))), new ResourceLocation(valueNBT.func_74779_i("Identifier"))
            )
         );
      }
   }
}
