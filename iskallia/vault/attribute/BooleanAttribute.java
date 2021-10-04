package iskallia.vault.attribute;

import com.google.gson.annotations.Expose;
import java.util.Optional;
import java.util.Random;
import net.minecraft.nbt.CompoundNBT;

public class BooleanAttribute extends PooledAttribute<Boolean> {
   public BooleanAttribute() {
   }

   public BooleanAttribute(VAttribute.Modifier<Boolean> modifier) {
      super(modifier);
   }

   @Override
   public void write(CompoundNBT nbt) {
      nbt.func_74757_a("BaseValue", this.getBaseValue());
   }

   @Override
   public void read(CompoundNBT nbt) {
      this.setBaseValue(Boolean.valueOf(nbt.func_74767_n("BaseValue")));
   }

   public static BooleanAttribute.Generator generator() {
      return new BooleanAttribute.Generator();
   }

   public static BooleanAttribute.Generator.Operator of(BooleanAttribute.Type type) {
      return new BooleanAttribute.Generator.Operator(type);
   }

   public static class Generator extends PooledAttribute.Generator<Boolean, BooleanAttribute.Generator.Operator> {
      public Boolean getDefaultValue(Random random) {
         return false;
      }

      public static class Operator extends PooledAttribute.Generator.Operator<Boolean> {
         @Expose
         protected String type;

         public Operator(BooleanAttribute.Type type) {
            this.type = type.name();
         }

         public BooleanAttribute.Type getType() {
            return BooleanAttribute.Type.getByName(this.type).orElseThrow(() -> new IllegalStateException("Unknown type \"" + this.type + "\""));
         }

         public Boolean apply(Boolean value, Boolean modifier) {
            if (this.getType() == BooleanAttribute.Type.SET) {
               return modifier;
            } else if (this.getType() == BooleanAttribute.Type.AND) {
               return value & modifier;
            } else if (this.getType() == BooleanAttribute.Type.OR) {
               return value | modifier;
            } else if (this.getType() == BooleanAttribute.Type.XOR) {
               return value ^ modifier;
            } else if (this.getType() == BooleanAttribute.Type.NAND) {
               return !(value & modifier);
            } else if (this.getType() == BooleanAttribute.Type.NOR) {
               return !(value | modifier);
            } else {
               return this.getType() == BooleanAttribute.Type.XNOR ? value == modifier : value;
            }
         }
      }
   }

   public static enum Type {
      SET,
      AND,
      OR,
      XOR,
      NAND,
      NOR,
      XNOR;

      public static Optional<BooleanAttribute.Type> getByName(String name) {
         for (BooleanAttribute.Type value : values()) {
            if (value.name().equalsIgnoreCase(name)) {
               return Optional.of(value);
            }
         }

         return Optional.empty();
      }
   }
}
