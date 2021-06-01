package iskallia.vault.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Item.Properties;
import net.minecraft.util.ResourceLocation;

public class ItemVaultGem extends Item {
   public ItemVaultGem(ItemGroup group, ResourceLocation id) {
      super(new Properties().func_200916_a(group).func_200917_a(64));
      this.setRegistryName(id);
   }
}
