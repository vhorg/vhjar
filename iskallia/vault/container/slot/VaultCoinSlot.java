package iskallia.vault.container.slot;

import com.mojang.datafixers.util.Pair;
import iskallia.vault.init.ModSlotIcons;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class VaultCoinSlot extends Slot {
   protected Item coinItem;

   public VaultCoinSlot(Container container, int index, int x, int y, Item coinItem) {
      super(container, index, x, y);
      this.coinItem = coinItem;
   }

   @Nullable
   public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
      return Pair.of(InventoryMenu.BLOCK_ATLAS, ModSlotIcons.COINS_NO_ITEM);
   }

   public boolean mayPlace(ItemStack itemStack) {
      Item item = itemStack.getItem();
      return item == this.coinItem;
   }
}
