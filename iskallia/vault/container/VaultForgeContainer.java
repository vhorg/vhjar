package iskallia.vault.container;

import iskallia.vault.block.entity.VaultForgeTileEntity;
import iskallia.vault.container.spi.ForgeRecipeContainer;
import iskallia.vault.init.ModContainers;
import java.awt.Point;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.Level;

public class VaultForgeContainer extends ForgeRecipeContainer<VaultForgeTileEntity> {
   public VaultForgeContainer(int windowId, Level world, BlockPos pos, Inventory playerInventory) {
      super(ModContainers.VAULT_FORGE_CONTAINER, windowId, world, pos, playerInventory);
   }

   @Override
   protected Class<VaultForgeTileEntity> getTileClass() {
      return VaultForgeTileEntity.class;
   }

   @Override
   protected Point getOffset() {
      return new Point(8, 56);
   }
}
