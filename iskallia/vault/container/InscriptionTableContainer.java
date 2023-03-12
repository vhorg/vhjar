package iskallia.vault.container;

import iskallia.vault.block.entity.InscriptionTableTileEntity;
import iskallia.vault.container.spi.ForgeRecipeContainer;
import iskallia.vault.init.ModContainers;
import java.awt.Point;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.Level;

public class InscriptionTableContainer extends ForgeRecipeContainer<InscriptionTableTileEntity> {
   public InscriptionTableContainer(int windowId, Level world, BlockPos pos, Inventory playerInventory) {
      super(ModContainers.INSCRIPTION_TABLE_CONTAINER, windowId, world, pos, playerInventory);
   }

   @Override
   protected Class<InscriptionTableTileEntity> getTileClass() {
      return InscriptionTableTileEntity.class;
   }

   @Override
   public Point getOffset() {
      return new Point(8, 23);
   }
}
