package iskallia.vault.item.tool;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;

public class GemstoneItem extends Item {
   public GemstoneItem(ResourceLocation id, Properties properties) {
      super(properties);
      this.setRegistryName(id);
   }
}
