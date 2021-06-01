package iskallia.vault.item.gear.attribute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.INBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.util.INBTSerializable;

public class ItemAttribute<T, I extends ItemAttribute.Instance<T>> {
   private final ResourceLocation id;
   private final Supplier<I> instance;
   private final List<ItemAttribute<T, I>> modifiers;

   public ItemAttribute(ResourceLocation id, Supplier<I> instance, ItemAttribute<T, I>... modifiers) {
      this.id = id;
      this.instance = instance;
      this.modifiers = new ArrayList<>(Arrays.asList(modifiers));
   }

   public ResourceLocation getId() {
      return this.id;
   }

   protected String getTagKey() {
      return "Attributes";
   }

   public Optional<I> get(ItemStack stack) {
      CompoundNBT nbt = stack.func_179543_a("Vault");
      if (nbt != null && nbt.func_150297_b(this.getTagKey(), 9)) {
         for (INBT element : nbt.func_150295_c(this.getTagKey(), 10)) {
            CompoundNBT tag = (CompoundNBT)element;
            if (tag.func_74779_i("Id").equals(this.getId().toString())) {
               I instance = this.instance.get();
               instance.parent = this;
               instance.delegate = tag;
               instance.read(tag);
               return Optional.of(instance);
            }
         }

         return Optional.empty();
      } else {
         return Optional.empty();
      }
   }

   public boolean exists(ItemStack stack) {
      return this.get(stack).isPresent();
   }

   public I getOrDefault(ItemStack stack, T value) {
      return this.getOrDefault(stack, () -> value);
   }

   public I getOrDefault(ItemStack stack, Random random, ItemAttribute.Instance.Generator<T> generator) {
      return this.getOrDefault(stack, () -> generator.generate(stack, random));
   }

   public I getOrDefault(ItemStack stack, Supplier<T> value) {
      return this.get(stack).orElse((I)this.instance.get().setBaseValue(value.get()));
   }

   public I getOrCreate(ItemStack stack, T value) {
      return this.getOrCreate(stack, () -> value);
   }

   public I getOrCreate(ItemStack stack, Random random, ItemAttribute.Instance.Generator<T> generator) {
      return this.getOrCreate(stack, () -> generator.generate(stack, random));
   }

   public I getOrCreate(ItemStack stack, Supplier<T> value) {
      return this.get(stack).orElseGet(() -> this.create(stack, value));
   }

   public I create(ItemStack stack, T value) {
      return this.create(stack, () -> value);
   }

   public I create(ItemStack stack, Random random, ItemAttribute.Instance.Generator<T> generator) {
      return this.create(stack, () -> generator.generate(stack, random));
   }

   public I create(ItemStack stack, Supplier<T> value) {
      CompoundNBT nbt = stack.func_190925_c("Vault");
      if (!nbt.func_150297_b(this.getTagKey(), 9)) {
         nbt.func_218657_a(this.getTagKey(), new ListNBT());
      }

      ListNBT attributesList = nbt.func_150295_c(this.getTagKey(), 10);
      CompoundNBT attributeNBT = attributesList.stream()
         .map(element -> (CompoundNBT)element)
         .filter(tag -> tag.func_74779_i("Id").equals(this.getId().toString()))
         .findFirst()
         .orElseGet(() -> {
            CompoundNBT tag = new CompoundNBT();
            attributesList.add(tag);
            return tag;
         });
      I instance = this.instance.get();
      instance.parent = this;
      instance.delegate = attributeNBT;
      instance.setBaseValue(value.get());
      return instance;
   }

   public abstract static class Instance<T> implements INBTSerializable<CompoundNBT>, ItemAttribute.Modifier<T> {
      protected ItemAttribute<T, ? extends ItemAttribute.Instance<T>> parent;
      protected T baseValue;
      private ItemAttribute.Modifier<T> modifier;
      protected CompoundNBT delegate;

      protected Instance() {
      }

      protected Instance(ItemAttribute.Modifier<T> modifier) {
         this.modifier = modifier;
      }

      public final CompoundNBT serializeNBT() {
         CompoundNBT nbt = new CompoundNBT();
         nbt.func_74778_a("Id", this.parent.id.toString());
         this.write(nbt);
         return nbt;
      }

      public final void deserializeNBT(CompoundNBT nbt) {
         this.read(nbt);
      }

      public abstract void write(CompoundNBT var1);

      public abstract void read(CompoundNBT var1);

      public T getBaseValue() {
         return this.baseValue;
      }

      public ItemAttribute.Instance<T> setBaseValue(T baseValue) {
         this.baseValue = baseValue;
         this.updateNBT();
         return this;
      }

      public T getValue(ItemStack stack) {
         T value = this.getBaseValue();
         if (this.parent == null) {
            return value;
         } else {
            for (ItemAttribute<T, ? extends ItemAttribute.Instance<T>> modifier : this.parent.modifiers) {
               Optional<? extends ItemAttribute.Instance<T>> instance = modifier.get(stack);
               if (instance.isPresent()) {
                  value = instance.get().apply(stack, (ItemAttribute.Instance<T>)instance.get(), value);
               }
            }

            return value;
         }
      }

      @Override
      public T apply(ItemStack stack, ItemAttribute.Instance<T> parent, T value) {
         return this.modifier == null ? value : this.modifier.apply(stack, parent, value);
      }

      protected void updateNBT() {
         if (this.delegate != null) {
            CompoundNBT nbt = this.serializeNBT();

            for (String key : nbt.func_150296_c()) {
               INBT value = nbt.func_74781_a(key);
               if (value != null) {
                  this.delegate.func_218657_a(key, value);
               }
            }
         }
      }

      @FunctionalInterface
      public interface Generator<T> {
         T generate(ItemStack var1, Random var2);
      }
   }

   @FunctionalInterface
   public interface Modifier<T> {
      T apply(ItemStack var1, ItemAttribute.Instance<T> var2, T var3);
   }
}
