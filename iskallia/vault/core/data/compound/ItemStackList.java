package iskallia.vault.core.data.compound;

import iskallia.vault.core.data.DataList;
import iskallia.vault.core.data.adapter.Adapter;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.item.ItemStack;

public class ItemStackList extends DataList<ItemStackList, ItemStack> {
   protected ItemStackList(List<ItemStack> delegate, Adapter<ItemStack> adapter) {
      super(delegate, adapter);
   }

   public static ItemStackList create() {
      return new ItemStackList(new ArrayList<>(), Adapter.ofItemStack());
   }
}
