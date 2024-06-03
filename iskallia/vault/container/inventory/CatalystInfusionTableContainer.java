package iskallia.vault.container.inventory;

import iskallia.vault.block.entity.CatalystInfusionTableTileEntity;
import iskallia.vault.container.spi.ForgeRecipeContainer;
import iskallia.vault.init.ModContainers;
import java.awt.Point;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.Level;

public class CatalystInfusionTableContainer extends ForgeRecipeContainer<CatalystInfusionTableTileEntity> {
   public CatalystInfusionTableContainer(int windowId, Level world, BlockPos pos, Inventory playerInventory) {
      super(ModContainers.CATALYST_INFUSION_TABLE_CONTAINER, windowId, world, pos, playerInventory);
   }

   @Override
   protected Class<CatalystInfusionTableTileEntity> getTileClass() {
      return CatalystInfusionTableTileEntity.class;
   }

   @Override
   public Point getOffset() {
      return new Point(8, 23);
   }
}
