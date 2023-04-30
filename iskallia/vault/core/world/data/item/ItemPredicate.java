package iskallia.vault.core.world.data.item;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import iskallia.vault.core.world.data.PartialCompoundNbt;
import iskallia.vault.item.crystal.data.adapter.ISimpleAdapter;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

@FunctionalInterface
public interface ItemPredicate {
   ItemPredicate FALSE = (item, nbt) -> false;
   ItemPredicate TRUE = (item, nbt) -> true;

   boolean test(PartialItem var1, PartialCompoundNbt var2);

   default boolean test(PartialStack stack) {
      return this.test(stack.getItem(), stack.getNbt());
   }

   default boolean test(ItemStack stack) {
      return this.test(PartialItem.of(stack.getItem()), PartialCompoundNbt.of(stack.getTag()));
   }

   static Optional<ItemPredicate> of(String string, boolean logErrors) {
      if (string.isEmpty()) {
         return Optional.of(TRUE);
      } else {
         return (switch (string.charAt(0)) {
            case '#' -> PartialItemTag.parse(string, logErrors);
            case '@' -> PartialItemGroup.parse(string, logErrors);
            default -> PartialStack.parse(string, logErrors);
         }).map(o -> (ItemPredicate)o);
      }
   }

   public static class Adapter implements ISimpleAdapter<ItemPredicate, Tag, JsonElement> {
      public Optional<Tag> writeNbt(@Nullable ItemPredicate value) {
         return value == null ? Optional.empty() : Optional.of(StringTag.valueOf(value.toString()));
      }

      @Override
      public Optional<ItemPredicate> readNbt(@Nullable Tag nbt) {
         if (nbt == null) {
            return Optional.empty();
         } else {
            return nbt instanceof StringTag string ? Optional.of(ItemPredicate.of(string.getAsString(), true).orElse(ItemPredicate.FALSE)) : Optional.empty();
         }
      }

      public Optional<JsonElement> writeJson(@Nullable ItemPredicate value) {
         return value == null ? Optional.empty() : Optional.of(new JsonPrimitive(value.toString()));
      }

      @Override
      public Optional<ItemPredicate> readJson(@Nullable JsonElement json) {
         if (json == null) {
            return Optional.empty();
         } else {
            return json instanceof JsonPrimitive primitive && primitive.isString()
               ? Optional.of(ItemPredicate.of(json.getAsString(), true).orElse(ItemPredicate.FALSE))
               : Optional.empty();
         }
      }
   }
}
