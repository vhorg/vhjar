package iskallia.vault.world.raid;

import iskallia.vault.attribute.VAttribute;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public class RaidProperties implements INBTSerializable<CompoundTag> {
   protected CompoundTag data = new CompoundTag();

   public CompoundTag getData() {
      return this.data;
   }

   public <T, I extends VAttribute.Instance<T>> Optional<I> get(VAttribute<T, I> attribute) {
      return attribute.get(this.getData());
   }

   public <T, I extends VAttribute.Instance<T>> Optional<T> getBase(VAttribute<T, I> attribute) {
      return attribute.get(this.getData()).map(VAttribute.Instance::getBaseValue);
   }

   public <T, I extends VAttribute.Instance<T>> T getValue(VAttribute<T, I> attribute) {
      return attribute.get(this.getData()).map(VAttribute.Instance::getBaseValue).get();
   }

   public <T, I extends VAttribute.Instance<T>> boolean exists(VAttribute<T, I> attribute) {
      return attribute.exists(this.getData());
   }

   public <T, I extends VAttribute.Instance<T>> I getOrDefault(VAttribute<T, I> attribute, T value) {
      return attribute.getOrDefault(this.getData(), value);
   }

   public <T, I extends VAttribute.Instance<T>> I getOrDefault(VAttribute<T, I> attribute, Supplier<T> value) {
      return attribute.getOrDefault(this.getData(), value);
   }

   public <T, I extends VAttribute.Instance<T>> T getBaseOrDefault(VAttribute<T, I> attribute, T value) {
      return attribute.getOrDefault(this.getData(), value).getBaseValue();
   }

   public <T, I extends VAttribute.Instance<T>> T getBaseOrDefault(VAttribute<T, I> attribute, Supplier<T> value) {
      return attribute.getOrDefault(this.getData(), value).getBaseValue();
   }

   public <T, I extends VAttribute.Instance<T>> I getOrCreate(VAttribute<T, I> attribute, T value) {
      return attribute.getOrCreate(this.getData(), value);
   }

   public <T, I extends VAttribute.Instance<T>> I getOrCreate(VAttribute<T, I> attribute, Supplier<T> value) {
      return attribute.getOrCreate(this.getData(), value);
   }

   public <T, I extends VAttribute.Instance<T>> I create(VAttribute<T, I> attribute, T value) {
      return attribute.create(this.getData(), value);
   }

   public <T, I extends VAttribute.Instance<T>> I create(VAttribute<T, I> attribute, Supplier<T> value) {
      return attribute.create(this.getData(), value);
   }

   public CompoundTag serializeNBT() {
      return this.data;
   }

   public void deserializeNBT(CompoundTag nbt) {
      this.data = nbt;
   }
}
