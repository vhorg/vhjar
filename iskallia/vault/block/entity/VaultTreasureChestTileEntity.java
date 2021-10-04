package iskallia.vault.block.entity;

import iskallia.vault.init.ModBlocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;

public class VaultTreasureChestTileEntity extends VaultChestTileEntity {
   public VaultTreasureChestTileEntity() {
      super(ModBlocks.VAULT_TREASURE_CHEST_TILE_ENTITY);
      this.func_199721_a(NonNullList.func_191197_a(54, ItemStack.field_190927_a));
   }

   public int func_70302_i_() {
      return 54;
   }
}
