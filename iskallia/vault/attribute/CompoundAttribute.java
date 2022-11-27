package iskallia.vault.attribute;

import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public class CompoundAttribute<T extends INBTSerializable<CompoundTag>> extends VAttribute.Instance<T> {
   protected Function<CompoundTag, T> read;

   public CompoundAttribute(Function<CompoundTag, T> read) {
      this.read = read;
   }

   public static <T extends INBTSerializable<CompoundTag>> CompoundAttribute<T> of(Supplier<T> supplier) {
      return new CompoundAttribute<>(nbt -> {
         T value = supplier.get();
         value.deserializeNBT(nbt);
         return value;
      });
   }

   @Override
   public void write(CompoundTag nbt) {
      if (this.getBaseValue() != null) {
         nbt.put("BaseValue", this.getBaseValue().serializeNBT());
      }
   }

   @Override
   public void read(CompoundTag nbt) {
      if (nbt.contains("BaseValue", 10)) {
         this.setBaseValue(this.read.apply(nbt.getCompound("BaseValue")));
      }
   }
}
