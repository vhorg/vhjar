package iskallia.vault.container;

import iskallia.vault.block.entity.JewelCraftingTableTileEntity;
import iskallia.vault.container.spi.ForgeRecipeContainer;
import iskallia.vault.init.ModContainers;
import java.awt.Point;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.level.Level;

public class JewelCraftingTableContainer extends ForgeRecipeContainer<JewelCraftingTableTileEntity> {
   public JewelCraftingTableContainer(int windowId, Level world, BlockPos pos, Inventory playerInventory) {
      super(ModContainers.JEWEL_CRAFTING_TABLE_CONTAINER, windowId, world, pos, playerInventory);
   }

   @Override
   protected Class<JewelCraftingTableTileEntity> getTileClass() {
      return JewelCraftingTableTileEntity.class;
   }

   @Override
   public Point getOffset() {
      return new Point(8, 23);
   }
}
