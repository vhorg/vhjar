package iskallia.vault.block.entity;

import iskallia.vault.block.entity.base.FilteredInputInventoryTileEntity;
import iskallia.vault.block.entity.base.InventoryRetainerTileEntity;
import iskallia.vault.container.VaultArtisanStationContainer;
import iskallia.vault.container.oversized.OverSizedInventory;
import iskallia.vault.container.oversized.OverSizedItemStack;
import iskallia.vault.gear.crafting.VaultGearCraftingHelper;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModItems;
import iskallia.vault.integration.IntegrationRefinedStorage;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.CapabilityItemHandler;

public class VaultArtisanStationTileEntity extends BlockEntity implements MenuProvider, FilteredInputInventoryTileEntity, InventoryRetainerTileEntity {
   private final OverSizedInventory inventory = new OverSizedInventory.FilteredInsert(16, this, this::canInsertInput);
   private final SimpleContainer gearInput = new SimpleContainer(1) {
      public void setChanged() {
         super.setChanged();
         VaultArtisanStationTileEntity.this.updateLegacyGearInput();
         VaultArtisanStationTileEntity.this.setChanged();
      }
   };

   public boolean stillValid(Player player) {
      return this.level != null && this.level.getBlockEntity(this.worldPosition) == this ? this.inventory.stillValid(player) : false;
   }

   public VaultArtisanStationTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.VAULT_ARTISAN_STATION_ENTITY, pos, state);
   }

   public OverSizedInventory getInventory() {
      return this.inventory;
   }

   public SimpleContainer getGearInput() {
      return this.gearInput;
   }

   public void updateLegacyGearInput() {
      ItemStack gearInput = this.gearInput.getItem(0);
      if (!gearInput.isEmpty()) {
         if (VaultGearData.hasData(gearInput)) {
            VaultGearData data = VaultGearData.read(gearInput);
            if (!data.hasAttribute(ModGearAttributes.MAX_CRAFTING_POTENTIAL)) {
               VaultGearCraftingHelper.generateCraftingPotential(gearInput);
               this.gearInput.setItem(0, gearInput);
               this.setChanged();
            }
         }
      }
   }

   public boolean canInsertInput(int slot, @Nonnull ItemStack stack) {
      if (stack.isEmpty()) {
         return false;
      } else if (slot == 0) {
         return stack.is(ModItems.VAULT_PLATING);
      } else if (slot == 1) {
         return stack.is(ModBlocks.VAULT_BRONZE);
      } else if (slot == 2) {
         return stack.is(ModItems.WILD_FOCUS);
      } else if (slot == 3) {
         return stack.is(ModItems.AMPLIFYING_FOCUS);
      } else if (slot == 4) {
         return stack.is(ModItems.NULLIFYING_FOCUS);
      } else if (slot == 5) {
         return stack.is(ModItems.FACETED_FOCUS);
      } else if (slot == 6) {
         return stack.is(ModItems.OPPORTUNISTIC_FOCUS);
      } else if (slot == 7) {
         return stack.is(ModItems.RESILIENT_FOCUS);
      } else if (slot == 8) {
         return stack.is(ModItems.FUNDAMENTAL_FOCUS);
      } else if (slot == 9) {
         return stack.is(ModItems.CHAOTIC_FOCUS);
      } else if (slot == 10) {
         return stack.is(ModItems.WAXING_FOCUS);
      } else if (slot == 11) {
         return stack.is(ModItems.WANING_FOCUS);
      } else if (slot == 12) {
         return stack.is(ModItems.EMPOWERED_CHAOTIC_FOCUS);
      } else if (slot == 13) {
         return stack.is(ModItems.CRYONIC_FOCUS);
      } else if (slot == 14) {
         return stack.is(ModItems.PYRETIC_FOCUS);
      } else {
         return slot == 15 ? stack.is(ModItems.VORPAL_FOCUS) : false;
      }
   }

   @Override
   public boolean isInventorySideAccessible(@Nullable Direction side) {
      return true;
   }

   public void load(CompoundTag tag) {
      super.load(tag);
      this.inventory.load(tag);
      this.gearInput.setItem(0, ItemStack.of(tag.getCompound("gearInput")));
      this.updateLegacyGearInput();
   }

   protected void saveAdditional(CompoundTag tag) {
      super.saveAdditional(tag);
      this.inventory.save(tag);
      tag.put("gearInput", this.gearInput.getItem(0).copy().serializeNBT());
   }

   public Component getDisplayName() {
      return this.getBlockState().getBlock().getName();
   }

   @Nonnull
   public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
      if (IntegrationRefinedStorage.shouldPreventImportingCapability(this.getLevel(), this.getBlockPos(), side)) {
         return super.getCapability(cap, side);
      } else {
         return cap == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY
            ? this.getFilteredInputCapability(side, new Container[]{this.inventory}).cast()
            : super.getCapability(cap, side);
      }
   }

   @Nullable
   public AbstractContainerMenu createMenu(int containerId, Inventory inv, Player player) {
      return this.getLevel() == null ? null : new VaultArtisanStationContainer(containerId, this.getLevel(), this.getBlockPos(), inv);
   }

   public static InventoryRetainerTileEntity.ContentDisplayInfo addInventoryTooltip(ItemStack stack, CompoundTag tag) {
      return InventoryRetainerTileEntity.displayContentsOverSized(5, stacks -> {
         ItemStack result = ItemStack.of(tag.getCompound("gearInput"));
         if (!result.isEmpty()) {
            stacks.add(OverSizedItemStack.of(result));
         }

         stacks.addAll(OverSizedInventory.loadContents("inventory", tag));
      });
   }

   @Override
   public void storeInventoryContents(CompoundTag tag) {
      this.getInventory().save("inventory", tag);
      tag.put("gearInput", this.gearInput.getItem(0).copy().serializeNBT());
   }

   @Override
   public void loadInventoryContents(CompoundTag tag) {
      this.inventory.load("inventory", tag);
      this.gearInput.setItem(0, ItemStack.of(tag.getCompound("gearInput")));
   }

   @Override
   public void clearInventoryContents() {
      this.getInventory().clearContent();
      this.getGearInput().clearContent();
   }
}
