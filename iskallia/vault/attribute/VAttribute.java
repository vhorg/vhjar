package iskallia.vault.attribute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Supplier;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.util.INBTSerializable;

public class VAttribute<T, I extends VAttribute.Instance<T>> {
   private final ResourceLocation id;
   private final Supplier<I> instance;
   private final List<VAttribute<T, I>> modifiers;

   public VAttribute(ResourceLocation id, Supplier<I> instance) {
      this(id, instance);
   }

   public VAttribute(ResourceLocation id, Supplier<I> instance, VAttribute<T, I>... modifiers) {
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

   public Optional<I> get(CompoundTag nbt) {
      if (nbt != null && nbt.contains(this.getTagKey(), 9)) {
         for (Tag element : nbt.getList(this.getTagKey(), 10)) {
            CompoundTag tag = (CompoundTag)element;
            if (tag.getString("Id").equals(this.getId().toString())) {
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

   public boolean exists(CompoundTag nbt) {
      return this.get(nbt).isPresent();
   }

   public I getOrDefault(CompoundTag nbt, T value) {
      return this.getOrDefault(nbt, () -> value);
   }

   public I getOrDefault(CompoundTag nbt, Supplier<T> value) {
      return this.get(nbt).orElse((I)this.instance.get().setBaseValue(value.get()));
   }

   public I getOrCreate(CompoundTag nbt, T value) {
      return this.getOrCreate(nbt, () -> value);
   }

   public I getOrCreate(CompoundTag nbt, Supplier<T> value) {
      return this.get(nbt).orElseGet(() -> this.create(nbt, value));
   }

   public I create(CompoundTag nbt, T value) {
      return this.create(nbt, () -> value);
   }

   public I create(CompoundTag nbt, Supplier<T> value) {
      if (!nbt.contains(this.getTagKey(), 9)) {
         nbt.put(this.getTagKey(), new ListTag());
      }

      ListTag attributesList = nbt.getList(this.getTagKey(), 10);
      CompoundTag attributeNBT = attributesList.stream()
         .map(element -> (CompoundTag)element)
         .filter(tag -> tag.getString("Id").equals(this.getId().toString()))
         .findFirst()
         .orElseGet(() -> {
            CompoundTag tag = new CompoundTag();
            attributesList.add(tag);
            return tag;
         });
      I instance = this.instance.get();
      instance.parent = this;
      instance.delegate = attributeNBT;
      instance.setBaseValue(value.get());
      return instance;
   }

   public Optional<I> get(ItemStack stack) {
      CompoundTag nbt = stack.getTagElement("Vault");
      if (nbt != null && nbt.contains(this.getTagKey(), 9)) {
         for (Tag element : nbt.getList(this.getTagKey(), 10)) {
            CompoundTag tag = (CompoundTag)element;
            if (tag.getString("Id").equals(this.getId().toString())) {
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

   public Optional<T> getBase(ItemStack stack) {
      return this.get(stack).map(VAttribute.Instance::getBaseValue);
   }

   public Optional<T> getValue(ItemStack stack) {
      return this.get(stack).map(attribute -> attribute.getValue(stack));
   }

   public boolean exists(ItemStack stack) {
      return this.get(stack).isPresent();
   }

   public I getOrDefault(ItemStack stack, T value) {
      return this.getOrDefault(stack, () -> value);
   }

   public I getOrDefault(ItemStack stack, Random random, VAttribute.Instance.Generator<T> generator) {
      return this.getOrDefault(stack, () -> generator.generate(stack, random));
   }

   public I getOrDefault(ItemStack stack, Supplier<T> value) {
      return this.get(stack).orElse((I)this.instance.get().setBaseValue(value.get()));
   }

   public I getOrCreate(ItemStack stack, T value) {
      return this.getOrCreate(stack, () -> value);
   }

   public I getOrCreate(ItemStack stack, Random random, VAttribute.Instance.Generator<T> generator) {
      return this.getOrCreate(stack, () -> generator.generate(stack, random));
   }

   public I getOrCreate(ItemStack stack, Supplier<T> value) {
      return this.get(stack).orElseGet(() -> this.create(stack, value));
   }

   public I create(ItemStack stack, T value) {
      return this.create(stack, () -> value);
   }

   public I create(ItemStack stack, Random random, VAttribute.Instance.Generator<T> generator) {
      return this.create(stack, () -> generator.generate(stack, random));
   }

   public I create(ItemStack stack, Supplier<T> value) {
      CompoundTag nbt = stack.getOrCreateTagElement("Vault");
      if (!nbt.contains(this.getTagKey(), 9)) {
         nbt.put(this.getTagKey(), new ListTag());
      }

      ListTag attributesList = nbt.getList(this.getTagKey(), 10);
      CompoundTag attributeNBT = attributesList.stream()
         .map(element -> (CompoundTag)element)
         .filter(tag -> tag.getString("Id").equals(this.getId().toString()))
         .findFirst()
         .orElseGet(() -> {
            CompoundTag tag = new CompoundTag();
            attributesList.add(tag);
            return tag;
         });
      I instance = this.instance.get();
      instance.parent = this;
      instance.delegate = attributeNBT;
      instance.setBaseValue(value.get());
      return instance;
   }

   public abstract static class Instance<T> implements INBTSerializable<CompoundTag>, VAttribute.Modifier<T> {
      protected VAttribute<T, ? extends VAttribute.Instance<T>> parent;
      protected T baseValue;
      private VAttribute.Modifier<T> modifier;
      protected CompoundTag delegate;

      protected Instance() {
      }

      protected Instance(VAttribute.Modifier<T> modifier) {
         this.modifier = modifier;
      }

      public final CompoundTag serializeNBT() {
         CompoundTag nbt = new CompoundTag();
         nbt.putString("Id", this.parent.id.toString());
         this.write(nbt);
         return nbt;
      }

      public final void deserializeNBT(CompoundTag nbt) {
         this.read(nbt);
      }

      public abstract void write(CompoundTag var1);

      public abstract void read(CompoundTag var1);

      public T getBaseValue() {
         return this.baseValue;
      }

      public VAttribute.Instance<T> setBaseValue(T baseValue) {
         this.baseValue = baseValue;
         this.updateNBT();
         return this;
      }

      public T getValue(ItemStack stack) {
         T value = this.getBaseValue();
         if (this.parent == null) {
            return value;
         } else {
            for (VAttribute<T, ? extends VAttribute.Instance<T>> modifier : this.parent.modifiers) {
               Optional<? extends VAttribute.Instance<T>> instance = modifier.get(stack);
               if (instance.isPresent()) {
                  value = instance.get().apply(stack, (VAttribute.Instance<T>)instance.get(), value);
               }
            }

            return value;
         }
      }

      @Override
      public T apply(ItemStack stack, VAttribute.Instance<T> parent, T value) {
         return this.modifier == null ? value : this.modifier.apply(stack, parent, value);
      }

      public void updateNBT() {
         if (this.delegate != null) {
            CompoundTag nbt = this.serializeNBT();

            for (String key : nbt.getAllKeys()) {
               Tag value = nbt.get(key);
               if (value != null) {
                  this.delegate.put(key, value);
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
      T apply(ItemStack var1, VAttribute.Instance<T> var2, T var3);
   }
}
