package iskallia.vault.attribute;

import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.common.util.INBTSerializable;

public class CompoundAttribute<T extends INBTSerializable<CompoundNBT>> extends VAttribute.Instance<T> {
   protected Function<CompoundNBT, T> read;

   public CompoundAttribute(Function<CompoundNBT, T> read) {
      this.read = read;
   }

   public static <T extends INBTSerializable<CompoundNBT>> CompoundAttribute<T> of(Supplier<T> supplier) {
      return new CompoundAttribute<>(nbt -> {
         T value = supplier.get();
         value.deserializeNBT(nbt);
         return value;
      });
   }

   @Override
   public void write(CompoundNBT nbt) {
      if (this.getBaseValue() != null) {
         nbt.func_218657_a("BaseValue", this.getBaseValue().serializeNBT());
      }
   }

   @Override
   public void read(CompoundNBT nbt) {
      if (nbt.func_150297_b("BaseValue", 10)) {
         this.setBaseValue(this.read.apply(nbt.func_74775_l("BaseValue")));
      }
   }
}
