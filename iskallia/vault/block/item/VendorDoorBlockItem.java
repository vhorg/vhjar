package iskallia.vault.block.item;

import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModItems;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item.Properties;

public class VendorDoorBlockItem extends BlockItem {
   public VendorDoorBlockItem() {
      super(ModBlocks.VENDOR_DOOR, new Properties().tab(ModItems.VAULT_MOD_GROUP));
   }
}
