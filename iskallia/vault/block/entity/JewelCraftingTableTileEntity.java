package iskallia.vault.block.entity;

import iskallia.vault.block.entity.base.ForgeRecipeTileEntity;
import iskallia.vault.config.recipe.ForgeRecipeType;
import iskallia.vault.container.JewelCraftingTableContainer;
import iskallia.vault.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;

public class JewelCraftingTableTileEntity extends ForgeRecipeTileEntity implements MenuProvider {
   public JewelCraftingTableTileEntity(BlockPos pWorldPosition, BlockState pBlockState) {
      super(ModBlocks.JEWEL_CRAFTING_TABLE_ENTITY, pWorldPosition, pBlockState, 6, ForgeRecipeType.JEWEL_CRAFTING);
   }

   @Override
   protected AbstractContainerMenu createMenu(int windowId, Inventory playerInventory) {
      return new JewelCraftingTableContainer(windowId, this.getLevel(), this.getBlockPos(), playerInventory);
   }
}
