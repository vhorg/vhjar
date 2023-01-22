package iskallia.vault.container;

import iskallia.vault.block.entity.RelicPedestalTileEntity;
import iskallia.vault.container.inventory.RelicPedestalInventory;
import iskallia.vault.container.slot.RelicRecipeFragmentSlot;
import iskallia.vault.container.slot.TabSlot;
import iskallia.vault.container.spi.AbstractElementContainer;
import iskallia.vault.dynamodel.DynamicModelItem;
import iskallia.vault.init.ModContainers;
import iskallia.vault.init.ModRelics;
import iskallia.vault.util.EntityHelper;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class RelicPedestalContainer extends AbstractElementContainer {
   protected RelicPedestalInventory internalInventory;
   protected AbstractElementContainer.SlotIndexRange playerInventoryIndexRange;
   protected AbstractElementContainer.SlotIndexRange hotbarIndexRange;
   protected AbstractElementContainer.SlotIndexRange internalInventoryIndexRange;
   protected BlockPos pedestalPos;
   @NotNull
   protected ResourceLocation selectedRelicId = ModRelics.EMPTY.getResultingRelic();
   private final RelicPedestalTileEntity tileEntity;

   public RelicPedestalContainer(int id, Player player, BlockPos pedestalPos) {
      super(ModContainers.RELIC_PEDESTAL_CONTAINER, id, player);
      this.pedestalPos = pedestalPos;
      if (player.getLevel().getBlockEntity(this.pedestalPos) instanceof RelicPedestalTileEntity relicPedestalTileEntity) {
         this.tileEntity = relicPedestalTileEntity;
      } else {
         this.tileEntity = null;
      }

      this.internalInventory = new RelicPedestalInventory(this.tileEntity);
      int offsetX = 0;
      int offsetY = 0;
      int containerSlotIndex = 0;

      for (int row = 0; row < 3; row++) {
         for (int column = 0; column < 9; column++) {
            int index = column + (row + 1) * 9;
            int x = 8 + column * 18 + offsetX;
            int y = 84 + row * 18 + offsetY;
            this.addSlot(new TabSlot(player.getInventory(), index, x, y));
            containerSlotIndex++;
         }
      }

      this.playerInventoryIndexRange = new AbstractElementContainer.SlotIndexRange(0, containerSlotIndex);

      for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
         int x = 8 + hotbarSlot * 18 + offsetX;
         int y = 142 + offsetY;
         this.addSlot(new TabSlot(player.getInventory(), hotbarSlot, x, y));
         containerSlotIndex++;
      }

      this.hotbarIndexRange = new AbstractElementContainer.SlotIndexRange(this.playerInventoryIndexRange.end(), containerSlotIndex);

      for (int i = 0; i < this.internalInventory.getInputSlotCount(); i++) {
         this.addSlot(new RelicRecipeFragmentSlot(() -> ModRelics.RECIPE_REGISTRY.get(this.selectedRelicId), this.internalInventory, i, 8 + 18 * i, 61));
         containerSlotIndex++;
      }

      this.internalInventoryIndexRange = new AbstractElementContainer.SlotIndexRange(this.hotbarIndexRange.end(), containerSlotIndex);
   }

   public RelicPedestalInventory getInternalInventory() {
      return this.internalInventory;
   }

   @Nonnull
   public ResourceLocation getSelectedRelicId() {
      return this.selectedRelicId;
   }

   public BlockPos getPedestalPos() {
      return this.pedestalPos;
   }

   public boolean recipeFulfilled() {
      ModRelics.RelicRecipe relicRecipe = ModRelics.RECIPE_REGISTRY.get(this.selectedRelicId);
      if (relicRecipe == null) {
         return false;
      } else {
         for (int i = 0; i < this.internalInventory.getInputSlotCount(); i++) {
            ItemStack itemStack = this.internalInventory.getItem(i);
            if (itemStack.isEmpty()) {
               return false;
            }

            ResourceLocation fragmentId = DynamicModelItem.getGenericModelId(itemStack).orElse(null);
            if (fragmentId == null) {
               return false;
            }

            if (!relicRecipe.getFragments().contains(fragmentId)) {
               return false;
            }
         }

         return true;
      }
   }

   public void selectRelic(ResourceLocation selectedRelicId) {
      this.selectedRelicId = selectedRelicId;
   }

   public boolean stillValid(@Nonnull Player player) {
      return true;
   }

   public void removed(@Nonnull Player player) {
      super.removed(player);
      this.internalInventory.forEachInput(relativeIndex -> {
         ItemStack itemStack = this.internalInventory.getItem(relativeIndex);
         if (!itemStack.isEmpty()) {
            EntityHelper.giveItem(player, itemStack);
         }
      });
   }

   @Nonnull
   public ItemStack quickMoveStack(@Nonnull Player player, int index) {
      Slot slot = (Slot)this.slots.get(index);
      if (!slot.hasItem()) {
         return ItemStack.EMPTY;
      } else {
         ItemStack slotItem = slot.getItem();
         ItemStack copiedStack = slotItem.copy();
         if (!this.playerInventoryIndexRange.contains(index) && !this.hotbarIndexRange.contains(index)) {
            if (this.internalInventoryIndexRange.contains(index)) {
               return this.moveItemStackTo(slotItem, this.playerInventoryIndexRange.start(), this.hotbarIndexRange.end(), false)
                  ? copiedStack
                  : ItemStack.EMPTY;
            } else {
               return copiedStack;
            }
         } else {
            return this.moveItemStackTo(slotItem, this.internalInventoryIndexRange, false) ? copiedStack : ItemStack.EMPTY;
         }
      }
   }
}
