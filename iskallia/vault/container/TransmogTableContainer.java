package iskallia.vault.container;

import iskallia.vault.container.inventory.TransmogTableInventory;
import iskallia.vault.container.slot.RecipeOutputSlot;
import iskallia.vault.container.slot.TabSlot;
import iskallia.vault.container.slot.VaultCoinSlot;
import iskallia.vault.container.slot.VaultGearSlot;
import iskallia.vault.container.spi.AbstractElementContainer;
import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModContainers;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.util.EntityHelper;
import javax.annotation.Nonnull;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class TransmogTableContainer extends AbstractElementContainer {
   protected TransmogTableInventory internalInventory = new TransmogTableInventory();
   protected AbstractElementContainer.SlotIndexRange playerInventoryIndexRange;
   protected AbstractElementContainer.SlotIndexRange hotbarIndexRange;
   protected AbstractElementContainer.SlotIndexRange internalInventoryIndexRange;
   protected ResourceLocation selectedModelId;

   public TransmogTableContainer(int id, Player player) {
      super(ModContainers.TRANSMOG_TABLE_CONTAINER, id, player);
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
      this.addSlot(new VaultGearSlot(this.internalInventory, 0, 63, 61));
      containerSlotIndex++;
      this.addSlot(new VaultCoinSlot(this.internalInventory, 1, 92, 61, ModBlocks.VAULT_BRONZE));
      containerSlotIndex++;
      this.addSlot(new RecipeOutputSlot(this.internalInventory, this.internalInventory.outputSlotIndex(), 152, 61));
      this.internalInventoryIndexRange = new AbstractElementContainer.SlotIndexRange(this.hotbarIndexRange.end(), ++containerSlotIndex);
   }

   public TransmogTableInventory getInternalInventory() {
      return this.internalInventory;
   }

   public AbstractElementContainer.SlotIndexRange getInternalInventoryIndexRange() {
      return this.internalInventoryIndexRange;
   }

   public ResourceLocation getSelectedModelId() {
      return this.selectedModelId;
   }

   public void selectModelId(ResourceLocation modelId) {
      this.selectedModelId = modelId;
   }

   public ItemStack getPreviewItemStack() {
      ResourceLocation selectedModelId = this.getSelectedModelId();
      if (selectedModelId == null) {
         return ItemStack.EMPTY;
      } else {
         int gearSlotIndex = this.internalInventoryIndexRange.getContainerIndex(0);
         ItemStack gearStack = this.getSlot(gearSlotIndex).getItem();
         if (gearStack.isEmpty()) {
            return ItemStack.EMPTY;
         } else {
            ItemStack displayStack = new ItemStack(gearStack.getItem());
            VaultGearData gearData = VaultGearData.read(displayStack);
            gearData.setState(VaultGearState.IDENTIFIED);
            gearData.updateAttribute(ModGearAttributes.GEAR_MODEL, selectedModelId);
            gearData.write(displayStack);
            return displayStack;
         }
      }
   }

   public boolean priceFulfilled() {
      Slot bronzeSlot = this.getSlot(this.internalInventoryIndexRange.getContainerIndex(1));
      return bronzeSlot.hasItem() && bronzeSlot.getItem().is(ModBlocks.VAULT_BRONZE) && bronzeSlot.getItem().getCount() >= this.copperCost();
   }

   public int copperCost() {
      Slot gearSlot = this.getSlot(this.internalInventoryIndexRange.getContainerIndex(0));
      ItemStack gearItem = gearSlot.getItem();
      if (gearItem.isEmpty()) {
         return -1;
      } else {
         VaultGearData gearData = VaultGearData.read(gearItem);
         return Mth.clamp(gearData.getItemLevel(), 1, 64);
      }
   }

   public boolean stillValid(@Nonnull Player player) {
      return this.internalInventory.stillValid(player);
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
