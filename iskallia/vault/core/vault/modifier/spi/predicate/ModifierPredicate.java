package iskallia.vault.core.vault.modifier.spi.predicate;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.array.ArrayAdapter;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.item.crystal.data.adapter.ISimpleAdapter;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;

@FunctionalInterface
public interface ModifierPredicate {
   ModifierPredicate FALSE = modifier -> false;
   ModifierPredicate TRUE = modifier -> true;

   boolean test(VaultModifier<?> var1);

   static Optional<ModifierPredicate> of(String string, boolean logErrors) {
      if (string.isEmpty()) {
         return Optional.of(TRUE);
      } else {
         return (switch (string.charAt(0)) {
            case '#' -> TypeModifierPredicate.parse(string, logErrors);
            case '@' -> GroupModifierPredicate.parse(string, logErrors);
            default -> IdModifierPredicate.parse(string, logErrors);
         }).map(predicate -> (ModifierPredicate)predicate);
      }
   }

   public static class Adapter implements ISimpleAdapter<ModifierPredicate, Tag, JsonElement> {
      private static ArrayAdapter<ModifierPredicate> LIST = Adapters.ofArray(ModifierPredicate[]::new, new ModifierPredicate.Adapter());

      public void writeBits(@Nullable ModifierPredicate value, BitBuffer buffer) {
         buffer.writeBoolean(value == null);
         if (value != null) {
            if (value instanceof OrModifierPredicate or) {
               buffer.writeBoolean(true);
               LIST.writeBits(or.getChildren(), buffer);
            } else {
               buffer.writeBoolean(false);
               Adapters.UTF_8.writeBits(value.toString(), buffer);
            }
         }
      }

      @Override
      public final Optional<ModifierPredicate> readBits(BitBuffer buffer) {
         if (buffer.readBoolean()) {
            return Optional.empty();
         } else {
            return buffer.readBoolean()
               ? LIST.readBits(buffer).map(OrModifierPredicate::new)
               : Adapters.UTF_8.readBits(buffer).map(string -> ModifierPredicate.of(string, true).orElse(ModifierPredicate.FALSE));
         }
      }

      public Optional<Tag> writeNbt(@Nullable ModifierPredicate value) {
         if (value == null) {
            return Optional.empty();
         } else {
            return value instanceof OrModifierPredicate or ? LIST.writeNbt(or.getChildren()) : Optional.of(StringTag.valueOf(value.toString()));
         }
      }

      @Override
      public Optional<ModifierPredicate> readNbt(@Nullable Tag nbt) {
         if (nbt == null) {
            return Optional.empty();
         } else if (nbt instanceof ListTag list) {
            return LIST.readNbt(list).map(OrModifierPredicate::new);
         } else {
            return nbt instanceof StringTag string
               ? Optional.of(ModifierPredicate.of(string.getAsString(), true).orElse(ModifierPredicate.FALSE))
               : Optional.empty();
         }
      }

      public Optional<JsonElement> writeJson(@Nullable ModifierPredicate value) {
         if (value == null) {
            return Optional.empty();
         } else {
            return value instanceof OrModifierPredicate or ? LIST.writeJson(or.getChildren()) : Optional.of(new JsonPrimitive(value.toString()));
         }
      }

      @Override
      public Optional<ModifierPredicate> readJson(@Nullable JsonElement json) {
         if (json == null) {
            return Optional.empty();
         } else if (json instanceof JsonArray array) {
            return LIST.readJson(array).map(OrModifierPredicate::new);
         } else {
            return json instanceof JsonPrimitive primitive && primitive.isString()
               ? Optional.of(ModifierPredicate.of(json.getAsString(), true).orElse(ModifierPredicate.FALSE))
               : Optional.empty();
         }
      }
   }
}
