package iskallia.vault.core.world.data.item;

import java.util.Optional;
import net.minecraft.world.item.ItemStack;

public interface ItemPlacement<T> extends ItemPredicate {
   boolean isSubsetOf(T var1);

   boolean isSubsetOf(ItemStack var1);

   void fillInto(T var1);

   Optional<ItemStack> generate(int var1);

   T copy();
}
