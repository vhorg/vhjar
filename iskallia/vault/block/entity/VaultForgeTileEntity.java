package iskallia.vault.block.entity;

import iskallia.vault.block.entity.base.ForgeRecipeTileEntity;
import iskallia.vault.block.entity.base.InventoryRetainerTileEntity;
import iskallia.vault.config.recipe.ForgeRecipeType;
import iskallia.vault.container.VaultForgeContainer;
import iskallia.vault.container.oversized.OverSizedInventory;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModItems;
import iskallia.vault.world.data.PlayerProficiencyData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;

public class VaultForgeTileEntity extends ForgeRecipeTileEntity implements MenuProvider, InventoryRetainerTileEntity {
   private final OverSizedInventory otherInputInventory = new OverSizedInventory.FilteredInsert(1, this, (slot, stack) -> stack.is(ModItems.VAULT_SCRAP));

   public VaultForgeTileEntity(BlockPos pWorldPosition, BlockState pBlockState) {
      super(ModBlocks.VAULT_FORGE_TILE_ENTITY, pWorldPosition, pBlockState, 9, ForgeRecipeType.GEAR, ForgeRecipeType.TRINKET);
   }

   public void increaseProficiency(ServerPlayer player) {
      int scrapInput = this.otherInputInventory.getItem(0).getCount();
      PlayerProficiencyData data = PlayerProficiencyData.get(player.getLevel());
      data.addAbsoluteProficiency(player.getUUID(), scrapInput);
      data.sendProficiencyInformation(player);
      if (!player.isCreative()) {
         this.otherInputInventory.setItem(0, ItemStack.EMPTY);
         this.setChanged();
      }
   }

   public OverSizedInventory getOtherInputInventory() {
      return this.otherInputInventory;
   }

   @Override
   protected <T> LazyOptional<T> getInventoryCapability(Direction side) {
      return this.getFilteredInputCapability(side, new Container[]{this.getInventory(), this.getOtherInputInventory()});
   }

   @Override
   public boolean stillValid(Player player) {
      return super.stillValid(player) && this.otherInputInventory.stillValid(player);
   }

   @Override
   public void load(@NotNull CompoundTag tag) {
      super.load(tag);
      this.otherInputInventory.load("otherInputs", tag);
   }

   @Override
   protected void saveAdditional(@NotNull CompoundTag tag) {
      super.saveAdditional(tag);
      this.otherInputInventory.save("otherInputs", tag);
   }

   @Override
   protected AbstractContainerMenu createMenu(int windowId, Inventory playerInventory) {
      return new VaultForgeContainer(windowId, this.getLevel(), this.getBlockPos(), playerInventory);
   }

   @Override
   public void storeInventoryContents(CompoundTag tag) {
      super.storeInventoryContents(tag);
      this.getOtherInputInventory().save("otherInputs", tag);
   }

   @Override
   public void loadInventoryContents(CompoundTag tag) {
      super.loadInventoryContents(tag);
      this.getOtherInputInventory().load("otherInputs", tag);
   }

   @Override
   public void clearInventoryContents() {
      super.clearInventoryContents();
      this.getOtherInputInventory().clearContent();
   }

   public static InventoryRetainerTileEntity.ContentDisplayInfo addInventoryTooltip(ItemStack stack, CompoundTag tag) {
      InventoryRetainerTileEntity.ContentDisplayInfo forgeContent = InventoryRetainerTileEntity.displayContentsOverSized(
         5, stacks -> stacks.addAll(OverSizedInventory.loadContents("otherInputs", tag))
      );
      return ForgeRecipeTileEntity.addInventoryTooltip(stack, tag).append(forgeContent);
   }
}
