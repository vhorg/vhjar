package iskallia.vault.container;

import iskallia.vault.block.entity.ToolStationTileEntity;
import iskallia.vault.container.spi.ForgeRecipeContainer;
import iskallia.vault.init.ModContainers;
import java.awt.Point;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.Level;

public class ToolStationContainer extends ForgeRecipeContainer<ToolStationTileEntity> {
   public ToolStationContainer(int windowId, Level world, BlockPos pos, Inventory playerInventory) {
      super(ModContainers.TOOL_STATION_CONTAINER, windowId, world, pos, playerInventory);
   }

   @Override
   protected Class<ToolStationTileEntity> getTileClass() {
      return ToolStationTileEntity.class;
   }

   @Override
   public Point getOffset() {
      return new Point(8, 23);
   }
}
