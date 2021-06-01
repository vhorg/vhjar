package iskallia.vault.item.gear.attribute;

import com.google.gson.annotations.Expose;
import java.util.Optional;

public abstract class NumberAttribute<T> extends PooledAttribute<T> {
   protected NumberAttribute() {
   }

   protected NumberAttribute(ItemAttribute.Modifier<T> modifier) {
      super(modifier);
   }

   public abstract static class Generator<T, O extends NumberAttribute.Generator.Operator<T>> extends PooledAttribute.Generator<T, O> {
      public abstract static class Operator<T> extends PooledAttribute.Generator.Operator<T> {
         @Expose
         protected String type;

         public Operator(NumberAttribute.Type type) {
            this.type = type.name();
         }

         public NumberAttribute.Type getType() {
            return NumberAttribute.Type.getByName(this.type).orElseThrow(() -> new IllegalStateException("Unknown type \"" + this.type + "\""));
         }
      }
   }

   public static enum Type {
      SET,
      ADD,
      MULTIPLY;

      public static Optional<NumberAttribute.Type> getByName(String name) {
         for (NumberAttribute.Type value : values()) {
            if (value.name().equalsIgnoreCase(name)) {
               return Optional.of(value);
            }
         }

         return Optional.empty();
      }
   }
}
