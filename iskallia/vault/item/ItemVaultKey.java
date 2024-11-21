package iskallia.vault.item;

import iskallia.vault.init.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;

public class ItemVaultKey extends Item {
   public ItemVaultKey(ResourceLocation id) {
      super(new Properties().tab(ModItems.VAULT_MOD_GROUP).stacksTo(64));
      this.setRegistryName(id);
   }

   public boolean isActive() {
      return this != ModItems.PUFFIUM_KEY;
   }
}
