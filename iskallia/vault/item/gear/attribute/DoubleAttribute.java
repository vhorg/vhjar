package iskallia.vault.item.gear.attribute;

import java.util.Random;
import net.minecraft.nbt.CompoundNBT;

public class DoubleAttribute extends NumberAttribute<Double> {
   public DoubleAttribute() {
   }

   public DoubleAttribute(ItemAttribute.Modifier<Double> modifier) {
      super(modifier);
   }

   @Override
   public void write(CompoundNBT nbt) {
      nbt.func_74780_a("BaseValue", this.getBaseValue());
   }

   @Override
   public void read(CompoundNBT nbt) {
      this.setBaseValue(Double.valueOf(nbt.func_74769_h("BaseValue")));
   }

   public static DoubleAttribute.Generator generator() {
      return new DoubleAttribute.Generator();
   }

   public static DoubleAttribute.Generator.Operator of(NumberAttribute.Type type) {
      return new DoubleAttribute.Generator.Operator(type);
   }

   public static class Generator extends NumberAttribute.Generator<Double, DoubleAttribute.Generator.Operator> {
      public Double getDefaultValue(Random random) {
         return 0.0;
      }

      public static class Operator extends NumberAttribute.Generator.Operator<Double> {
         public Operator(NumberAttribute.Type type) {
            super(type);
         }

         public Double apply(Double value, Double modifier) {
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
