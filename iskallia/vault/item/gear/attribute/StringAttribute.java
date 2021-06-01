package iskallia.vault.item.gear.attribute;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.JsonAdapter;
import iskallia.vault.util.gson.IgnoreEmpty;
import java.util.Optional;
import java.util.Random;
import net.minecraft.nbt.CompoundNBT;

public class StringAttribute extends PooledAttribute<String> {
   public StringAttribute() {
   }

   public StringAttribute(ItemAttribute.Modifier<String> modifier) {
      super(modifier);
   }

   @Override
   public void write(CompoundNBT nbt) {
      nbt.func_74778_a("BaseValue", this.getBaseValue());
   }

   @Override
   public void read(CompoundNBT nbt) {
      this.setBaseValue(nbt.func_74779_i("BaseValue"));
   }

   public static class Generator extends PooledAttribute.Generator<String, StringAttribute.Generator.Operator> {
      public String getDefaultValue(Random random) {
         return "";
      }

      public static class Operator extends PooledAttribute.Generator.Operator<String> {
         @Expose
         protected String type;
         @Expose
         @JsonAdapter(IgnoreEmpty.StringAdapter.class)
         protected String delimiter;
         @Expose
         @JsonAdapter(IgnoreEmpty.StringAdapter.class)
         protected String regex;

         public Operator(StringAttribute.Type type) {
            this.type = type.name();
         }

         public StringAttribute.Type getType() {
            return StringAttribute.Type.getByName(this.type).orElseThrow(() -> new IllegalStateException("Unknown type \"" + this.type + "\""));
         }

         public String apply(String value, String modifier) {
            if (this.getType() == StringAttribute.Type.SET) {
               return modifier;
            } else if (this.getType() == StringAttribute.Type.APPEND) {
               return value + modifier;
            } else if (this.getType() == StringAttribute.Type.JOIN) {
               return value + this.delimiter + modifier;
            } else if (this.getType() == StringAttribute.Type.REPLACE_FIRST) {
               return value.replaceFirst(this.regex, modifier);
            } else {
               return this.getType() == StringAttribute.Type.REPLACE_ALL ? value.replaceAll(this.regex, modifier) : value;
            }
         }
      }
   }

   public static enum Type {
      SET,
      APPEND,
      JOIN,
      REPLACE_FIRST,
      REPLACE_ALL;

      public static Optional<StringAttribute.Type> getByName(String name) {
         for (StringAttribute.Type value : values()) {
            if (value.name().equalsIgnoreCase(name)) {
               return Optional.of(value);
            }
         }

         return Optional.empty();
      }
   }
}
