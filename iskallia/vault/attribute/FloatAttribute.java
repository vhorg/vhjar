package iskallia.vault.attribute;

import java.util.Random;
import net.minecraft.nbt.CompoundTag;

public class FloatAttribute extends NumberAttribute<Float> {
   public FloatAttribute() {
   }

   public FloatAttribute(VAttribute.Modifier<Float> modifier) {
      super(modifier);
   }

   @Override
   public void write(CompoundTag nbt) {
      nbt.putFloat("BaseValue", this.getBaseValue());
   }

   @Override
   public void read(CompoundTag nbt) {
      this.setBaseValue(Float.valueOf(nbt.getFloat("BaseValue")));
   }

   public static FloatAttribute.Generator generator() {
      return new FloatAttribute.Generator();
   }

   public static FloatAttribute.Generator.Operator of(NumberAttribute.Type type) {
      return new FloatAttribute.Generator.Operator(type);
   }

   public static class Generator extends NumberAttribute.Generator<Float, FloatAttribute.Generator.Operator> {
      public Float getDefaultValue(Random random) {
         return 0.0F;
      }

      public static class Operator extends NumberAttribute.Generator.Operator<Float> {
         public Operator(NumberAttribute.Type type) {
            super(type);
         }

         public Float apply(Float value, Float modifier) {
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
