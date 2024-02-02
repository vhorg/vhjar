package iskallia.vault.container;

import iskallia.vault.block.entity.AscensionForgeTileEntity;
import iskallia.vault.config.AscensionForgeConfig;
import iskallia.vault.container.inventory.AscensionForgeInventory;
import iskallia.vault.container.oversized.OverSizedSlotContainer;
import iskallia.vault.container.oversized.OverSizedTabSlot;
import iskallia.vault.container.slot.RecipeOutputSlot;
import iskallia.vault.container.slot.TabSlot;
import iskallia.vault.container.spi.AbstractElementContainer;
import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModContainers;
import iskallia.vault.init.ModDynamicModels;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModSlotIcons;
import iskallia.vault.world.data.DiscoveredModelsData;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class AscensionForgeContainer extends OverSizedSlotContainer {
   protected AscensionForgeInventory internalInventory;
   protected AbstractElementContainer.SlotIndexRange playerInventoryIndexRange;
   protected AbstractElementContainer.SlotIndexRange hotbarIndexRange;
   protected AbstractElementContainer.SlotIndexRange internalInventoryIndexRange;
   private ItemStack previewItemStack = ItemStack.EMPTY;
   protected ResourceLocation selectedModelId;
   private final AscensionForgeTileEntity tileEntity;
   private final BlockPos tilePos;
   private int cost = Integer.MAX_VALUE;

   public AscensionForgeContainer(int id, Level level, BlockPos pos, Inventory playerInventory) {
      super(ModContainers.ASCENSION_FORGE_CONTAINER, id, playerInventory.player);
      this.tilePos = pos;
      if (level.getBlockEntity(this.tilePos) instanceof AscensionForgeTileEntity ascensionForgeTileEntity) {
         this.tileEntity = ascensionForgeTileEntity;
         this.initInventory();
      } else {
         this.tileEntity = null;
      }
   }

   private void initInventory() {
      this.internalInventory = this.tileEntity.getInternalInventory();
      this.internalInventory.stopOpen(this.player);
      int offsetX = 0;
      int offsetY = 0;
      int containerSlotIndex = 0;

      for (int row = 0; row < 3; row++) {
         for (int column = 0; column < 9; column++) {
            int index = column + (row + 1) * 9;
            int x = 8 + column * 18 + offsetX;
            int y = 84 + row * 18 + offsetY;
            this.addSlot(new TabSlot(this.player.getInventory(), index, x, y));
            containerSlotIndex++;
         }
      }

      this.playerInventoryIndexRange = new AbstractElementContainer.SlotIndexRange(0, containerSlotIndex);

      for (int hotbarSlot = 0; hotbarSlot < 9; hotbarSlot++) {
         int x = 8 + hotbarSlot * 18 + offsetX;
         int y = 142 + offsetY;
         this.addSlot(new TabSlot(this.player.getInventory(), hotbarSlot, x, y));
         containerSlotIndex++;
      }

      this.hotbarIndexRange = new AbstractElementContainer.SlotIndexRange(this.playerInventoryIndexRange.end(), containerSlotIndex);
      this.addSlot((new OverSizedTabSlot(this.internalInventory, 0, 92, 61) {
         public void setChanged() {
            super.setChanged();
            AscensionForgeContainer.this.slotsChanged(this.container);
         }
      }).setFilter(itemStack -> itemStack.getItem() == ModItems.EMBER).setBackground(InventoryMenu.BLOCK_ATLAS, ModSlotIcons.EMBER_NO_ITEM));
      containerSlotIndex++;
      this.addSlot(new RecipeOutputSlot(this.internalInventory, this.internalInventory.outputSlotIndex(), 152, 61) {
         public void setChanged() {
            super.setChanged();
            AscensionForgeContainer.this.slotsChanged(this.container);
         }
      });
      this.internalInventoryIndexRange = new AbstractElementContainer.SlotIndexRange(this.hotbarIndexRange.end(), ++containerSlotIndex);
   }

   public AscensionForgeInventory getInternalInventory() {
      return this.internalInventory;
   }

   public AbstractElementContainer.SlotIndexRange getInternalInventoryIndexRange() {
      return this.internalInventoryIndexRange;
   }

   public ResourceLocation getSelectedModelId() {
      return this.selectedModelId;
   }

   public void selectItem(@Nullable ResourceLocation modelId, ItemStack previewItemStack) {
      this.selectedModelId = modelId;
      this.cost = ModConfigs.ASCENSION_FORGE
         .getListings()
         .stream()
         .filter(
            listing -> listing.modelId() != null
               ? listing.modelId().equals(this.selectedModelId)
               : ItemStack.isSameItemSameTags(listing.stack(), previewItemStack)
         )
         .findFirst()
         .map(AscensionForgeConfig.AscensionForgeListing::cost)
         .orElse(Integer.MAX_VALUE);
      if (modelId == null) {
         this.previewItemStack = previewItemStack;
      } else {
         ModDynamicModels.REGISTRIES.getModelAndAssociatedItem(modelId).ifPresent(pair -> {
            Item item = (Item)pair.getSecond();
            if (!(item instanceof VaultGearItem)) {
               this.previewItemStack = ItemStack.EMPTY;
            } else {
               this.previewItemStack = new ItemStack(item);
               VaultGearData gearData = VaultGearData.read(this.previewItemStack);
               gearData.setState(VaultGearState.IDENTIFIED);
               gearData.updateAttribute(ModGearAttributes.GEAR_MODEL, modelId);
               gearData.write(this.previewItemStack);
            }
         });
      }
   }

   public ItemStack getPreviewItemStack() {
      return this.previewItemStack;
   }

   public boolean priceFulfilled() {
      Slot emberSlot = this.getSlot(this.internalInventoryIndexRange.getContainerIndex(0));
      return emberSlot.hasItem() && emberSlot.getItem().is(ModItems.EMBER) && emberSlot.getItem().getCount() >= this.emberCost();
   }

   public int emberCost() {
      return this.cost;
   }

   public void slotsChanged(Container pInventory) {
      this.tileEntity.setChanged();
      super.slotsChanged(pInventory);
   }

   public boolean stillValid(@Nonnull Player player) {
      return this.tileEntity == null ? false : this.tileEntity.stillValid(player);
   }

   public void removed(@Nonnull Player player) {
      super.removed(player);
      this.internalInventory.stopOpen(player);
   }

   @Nonnull
   public ItemStack quickMoveStack(@Nonnull Player player, int index) {
      ItemStack itemstack = ItemStack.EMPTY;
      Slot slot = (Slot)this.slots.get(index);
      if (slot.hasItem()) {
         ItemStack slotStack = slot.getItem();
         itemstack = slotStack.copy();
         if (index >= 0 && index < 36 && this.moveOverSizedItemStackTo(slotStack, slot, 36, this.slots.size(), false)) {
            return itemstack;
         }

         if (index >= 0 && index < 27) {
            if (!this.moveOverSizedItemStackTo(slotStack, slot, 27, 36, false)) {
               return ItemStack.EMPTY;
            }
         } else if (index >= 27 && index < 36) {
            if (!this.moveOverSizedItemStackTo(slotStack, slot, 0, 27, false)) {
               return ItemStack.EMPTY;
            }
         } else if (!this.moveOverSizedItemStackTo(slotStack, slot, 0, 36, false)) {
            return ItemStack.EMPTY;
         }

         if (slotStack.getCount() == 0) {
            slot.set(ItemStack.EMPTY);
         } else {
            slot.setChanged();
         }

         if (slotStack.getCount() == itemstack.getCount()) {
            return ItemStack.EMPTY;
         }

         slot.onTake(player, slotStack);
      }

      return itemstack;
   }

   public void buy(ServerPlayer player) {
      if (player != null) {
         if (!this.previewItemStack.isEmpty() && this.priceFulfilled()) {
            if (this.selectedModelId != null) {
               DiscoveredModelsData discoveredModelsData = DiscoveredModelsData.get(player.getLevel());
               discoveredModelsData.discoverModelAndBroadcast(this.previewItemStack.getItem(), this.selectedModelId, player);
               Slot inputSlot = this.getSlot(this.getInternalInventoryIndexRange().getContainerIndex(0));
               ItemStack emberStack = inputSlot.getItem();
               emberStack.shrink(this.emberCost());
               inputSlot.set(emberStack);
            } else {
               Slot outputSlot = this.getSlot(this.getInternalInventoryIndexRange().getContainerIndex(this.getInternalInventory().outputSlotIndex()));
               outputSlot.set(this.previewItemStack.copy());
               Slot inputSlot = this.getSlot(this.getInternalInventoryIndexRange().getContainerIndex(0));
               ItemStack emberStack = inputSlot.getItem();
               emberStack.shrink(this.emberCost());
               inputSlot.set(emberStack);
            }
         }
      }
   }
}
