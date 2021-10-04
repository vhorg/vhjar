package iskallia.vault.attribute;

import com.google.gson.annotations.Expose;
import java.util.Optional;
import java.util.Random;
import net.minecraft.nbt.CompoundNBT;

public class EnumAttribute<E extends Enum<E>> extends PooledAttribute<E> {
   private final Class<E> enumClass;

   public EnumAttribute(Class<E> enumClass) {
      this.enumClass = enumClass;
   }

   public EnumAttribute(Class<E> enumClass, VAttribute.Modifier<E> modifier) {
      super(modifier);
      this.enumClass = enumClass;
   }

   public Class<E> getEnumClass() {
      return this.enumClass;
   }

   @Override
   public void write(CompoundNBT nbt) {
      nbt.func_74778_a("BaseValue", this.getBaseValue().name());
   }

   @Override
   public void read(CompoundNBT nbt) {
      this.setBaseValue(this.getEnumConstant(nbt.func_74779_i("BaseValue")));
   }

   public E getEnumConstant(String value) {
      try {
         return Enum.valueOf(this.getEnumClass(), value);
      } catch (Exception var4) {
         E[] enumConstants = this.getEnumClass().getEnumConstants();
         return enumConstants.length == 0 ? null : enumConstants[0];
      }
   }

   public static <E extends Enum<E>> EnumAttribute.Generator<E> generator(Class<E> enumClass) {
      return new EnumAttribute.Generator<>();
   }

   public static <E extends Enum<E>> EnumAttribute.Generator.Operator<E> of(EnumAttribute.Type type) {
      return new EnumAttribute.Generator.Operator<>(type);
   }

   public static class Generator<E extends Enum<E>> extends PooledAttribute.Generator<E, EnumAttribute.Generator.Operator<E>> {
      public E getDefaultValue(Random random) {
         return null;
      }

      public static class Operator<E extends Enum<E>> extends PooledAttribute.Generator.Operator<E> {
         @Expose
         protected String type;

         public Operator(EnumAttribute.Type type) {
            this.type = type.name();
         }

         public EnumAttribute.Type getType() {
            return EnumAttribute.Type.getByName(this.type).orElseThrow(() -> new IllegalStateException("Unknown type \"" + this.type + "\""));
         }

         public E apply(E value, E modifier) {
            return this.getType() == EnumAttribute.Type.SET ? modifier : value;
         }
      }
   }

   public static enum Type {
      SET;

      public static Optional<EnumAttribute.Type> getByName(String name) {
         for (EnumAttribute.Type value : values()) {
            if (value.name().equalsIgnoreCase(name)) {
               return Optional.of(value);
            }
         }

         return Optional.empty();
      }
   }
}
