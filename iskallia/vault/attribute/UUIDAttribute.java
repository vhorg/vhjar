package iskallia.vault.attribute;

import java.util.UUID;
import net.minecraft.nbt.CompoundNBT;

public class UUIDAttribute extends VAttribute.Instance<UUID> {
   @Override
   public void write(CompoundNBT nbt) {
      if (this.getBaseValue() != null) {
         nbt.func_74778_a("BaseValue", this.getBaseValue().toString());
      }
   }

   @Override
   public void read(CompoundNBT nbt) {
      if (nbt.func_150297_b("BaseValue", 8)) {
         this.setBaseValue(UUID.fromString(nbt.func_74779_i("BaseValue")));
      }
   }
}
