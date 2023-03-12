package iskallia.vault.block.entity;

import iskallia.vault.block.entity.base.ForgeRecipeTileEntity;
import iskallia.vault.config.recipe.ForgeRecipeType;
import iskallia.vault.container.ToolStationContainer;
import iskallia.vault.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;

public class ToolStationTileEntity extends ForgeRecipeTileEntity implements MenuProvider {
   public ToolStationTileEntity(BlockPos pWorldPosition, BlockState pBlockState) {
      super(ModBlocks.TOOL_STATION_TILE_ENTITY, pWorldPosition, pBlockState, 6, ForgeRecipeType.JEWEL, ForgeRecipeType.TOOL);
   }

   @Override
   protected AbstractContainerMenu createMenu(int windowId, Inventory playerInventory) {
      return new ToolStationContainer(windowId, this.getLevel(), this.getBlockPos(), playerInventory);
   }
}
