package iskallia.vault.core.data.compound;

import iskallia.vault.core.data.DataList;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.vault.LegacyItemStackAdapter;
import iskallia.vault.item.crystal.data.adapter.IBitAdapter;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.ItemStack;

public class ItemStackList extends DataList<ItemStackList, ItemStack> {
   protected ItemStackList(List<ItemStack> delegate, IBitAdapter<ItemStack, ?> adapter) {
      super(delegate, adapter);
   }

   public static ItemStackList createLegacy() {
      return new ItemStackList(new ArrayList<>(), LegacyItemStackAdapter.INSTANCE);
   }

   public static ItemStackList create() {
      return new ItemStackList(new ArrayList<>(), Adapters.ITEM_STACK);
   }
}
