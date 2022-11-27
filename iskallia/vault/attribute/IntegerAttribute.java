package iskallia.vault.attribute;

import java.util.Random;
import net.minecraft.nbt.CompoundTag;

public class IntegerAttribute extends NumberAttribute<Integer> {
   public IntegerAttribute() {
   }

   public IntegerAttribute(VAttribute.Modifier<Integer> modifier) {
      super(modifier);
   }

   @Override
   public void write(CompoundTag nbt) {
      nbt.putInt("BaseValue", this.getBaseValue());
   }

   @Override
   public void read(CompoundTag nbt) {
      this.setBaseValue(Integer.valueOf(nbt.getInt("BaseValue")));
   }

   public static IntegerAttribute.Generator generator() {
      return new IntegerAttribute.Generator();
   }

   public static IntegerAttribute.Generator.Operator of(NumberAttribute.Type type) {
      return new IntegerAttribute.Generator.Operator(type);
   }

   public static class Generator extends NumberAttribute.Generator<Integer, IntegerAttribute.Generator.Operator> {
      public Integer getDefaultValue(Random random) {
         return 0;
      }

      public static IntegerAttribute.Generator.Operator of(NumberAttribute.Type type) {
         return new IntegerAttribute.Generator.Operator(type);
      }

      public static class Operator extends NumberAttribute.Generator.Operator<Integer> {
         public Operator(NumberAttribute.Type type) {
            super(type);
         }

         public Integer apply(Integer value, Integer modifier) {
            if (this.getType() == NumberAttribute.Type.SET) {
               return modifier;
            } else if (this.getType() == NumberAttribute.Type.ADD) {
               return value + modifier;
            } else {
               return this.getType() == NumberAttribute.Type.MULTIPLY ? value * modifier : value;
            }
         }
      }
   }
}
