package iskallia.vault.container;

import iskallia.vault.block.entity.VaultForgeTileEntity;
import iskallia.vault.container.oversized.OverSizedTabSlot;
import iskallia.vault.container.spi.ForgeRecipeContainer;
import iskallia.vault.init.ModContainers;
import iskallia.vault.init.ModSlotIcons;
import java.awt.Point;
import net.minecraft.core.BlockPos;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.Level;

public class VaultForgeContainer extends ForgeRecipeContainer<VaultForgeTileEntity> {
   public VaultForgeContainer(int windowId, Level world, BlockPos pos, Inventory playerInventory) {
      super(ModContainers.VAULT_FORGE_CONTAINER, windowId, world, pos, playerInventory);
      if (this.getTile() != null) {
         Container invContainer = this.getTile().getOtherInputInventory();
         this.addSlot(new OverSizedTabSlot(invContainer, 0, 124, 7).setBackground(InventoryMenu.BLOCK_ATLAS, ModSlotIcons.VAULT_SCRAP_NO_ITEM));
      }
   }

   @Override
   protected Class<VaultForgeTileEntity> getTileClass() {
      return VaultForgeTileEntity.class;
   }

   @Override
   public Point getOffset() {
      return new Point(14, 50);
   }

   @Override
   public Point getPlayerInventoryOffset() {
      return new Point(23, 56);
   }
}
