package iskallia.vault.item;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;

public class ItemVaultKey extends Item {
   public ItemVaultKey(CreativeModeTab group, ResourceLocation id) {
      super(new Properties().tab(group).stacksTo(64));
      this.setRegistryName(id);
   }
}
