package iskallia.vault.item.gear.attribute;

import java.util.Random;
import net.minecraft.nbt.CompoundNBT;

public class IntegerAttribute extends NumberAttribute<Integer> {
   public IntegerAttribute() {
   }

   public IntegerAttribute(ItemAttribute.Modifier<Integer> modifier) {
      super(modifier);
   }

   @Override
   public void write(CompoundNBT nbt) {
      nbt.func_74768_a("BaseValue", this.getBaseValue());
   }

   @Override
   public void read(CompoundNBT nbt) {
      this.setBaseValue(Integer.valueOf(nbt.func_74762_e("BaseValue")));
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
