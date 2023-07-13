package iskallia.vault.block.entity;

import iskallia.vault.block.entity.base.ForgeRecipeTileEntity;
import iskallia.vault.config.recipe.ForgeRecipeType;
import iskallia.vault.container.VaultForgeContainer;
import iskallia.vault.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.state.BlockState;

public class VaultForgeTileEntity extends ForgeRecipeTileEntity implements MenuProvider {
   public VaultForgeTileEntity(BlockPos pWorldPosition, BlockState pBlockState) {
      super(ModBlocks.VAULT_FORGE_TILE_ENTITY, pWorldPosition, pBlockState, 9, ForgeRecipeType.GEAR, ForgeRecipeType.TRINKET);
   }

   @Override
   protected AbstractContainerMenu createMenu(int windowId, Inventory playerInventory) {
      return new VaultForgeContainer(windowId, this.getLevel(), this.getBlockPos(), playerInventory);
   }
}
