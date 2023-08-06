package iskallia.vault.item;

import iskallia.vault.init.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.StandingAndWallBlockItem;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.block.Block;

public class SconceItem extends StandingAndWallBlockItem {
   public SconceItem(ResourceLocation id, Block block, Block wallBlock) {
      super(block, wallBlock, new Properties().tab(ModItems.VAULT_MOD_GROUP));
      this.setRegistryName(id);
   }
}
