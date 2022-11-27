package iskallia.vault.attribute;

import java.util.Random;
import net.minecraft.nbt.CompoundTag;

public class DoubleAttribute extends NumberAttribute<Double> {
   public DoubleAttribute() {
   }

   public DoubleAttribute(VAttribute.Modifier<Double> modifier) {
      super(modifier);
   }

   @Override
   public void write(CompoundTag nbt) {
      nbt.putDouble("BaseValue", this.getBaseValue());
   }

   @Override
   public void read(CompoundTag nbt) {
      this.setBaseValue(Double.valueOf(nbt.getDouble("BaseValue")));
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
