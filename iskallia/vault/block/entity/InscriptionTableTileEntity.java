package iskallia.vault.block.entity;

import iskallia.vault.block.entity.base.ForgeRecipeTileEntity;
import iskallia.vault.config.recipe.ForgeRecipeType;
import iskallia.vault.container.InscriptionTableContainer;
import iskallia.vault.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;

public class InscriptionTableTileEntity extends ForgeRecipeTileEntity implements MenuProvider {
   public InscriptionTableTileEntity(BlockPos pWorldPosition, BlockState pBlockState) {
      super(ModBlocks.INSCRIPTION_TABLE_TILE_ENTITY, pWorldPosition, pBlockState, 6, ForgeRecipeType.INSCRIPTION);
   }

   @Override
   protected AbstractContainerMenu createMenu(int windowId, Inventory playerInventory) {
      return new InscriptionTableContainer(windowId, this.getLevel(), this.getBlockPos(), playerInventory);
   }
}
