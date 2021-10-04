package iskallia.vault.attribute;

import java.util.Random;
import net.minecraft.nbt.CompoundNBT;

public class LongAttribute extends NumberAttribute<Long> {
   public LongAttribute() {
   }

   public LongAttribute(VAttribute.Modifier<Long> modifier) {
      super(modifier);
   }

   @Override
   public void write(CompoundNBT nbt) {
      nbt.func_74772_a("BaseValue", this.getBaseValue());
   }

   @Override
   public void read(CompoundNBT nbt) {
      this.setBaseValue(Long.valueOf(nbt.func_74763_f("BaseValue")));
   }

   public static LongAttribute.Generator generator() {
      return new LongAttribute.Generator();
   }

   public static LongAttribute.Generator.Operator of(NumberAttribute.Type type) {
      return new LongAttribute.Generator.Operator(type);
   }

   public static class Generator extends NumberAttribute.Generator<Long, LongAttribute.Generator.Operator> {
      public Long getDefaultValue(Random random) {
         return 0L;
      }

      public static LongAttribute.Generator.Operator of(NumberAttribute.Type type) {
         return new LongAttribute.Generator.Operator(type);
      }

      public static class Operator extends NumberAttribute.Generator.Operator<Long> {
         public Operator(NumberAttribute.Type type) {
            super(type);
         }

         public Long apply(Long value, Long modifier) {
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
