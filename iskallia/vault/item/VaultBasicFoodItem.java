package iskallia.vault.item;

import iskallia.vault.init.ModItems;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Item.Properties;

public class VaultBasicFoodItem extends Item {
   public VaultBasicFoodItem(ResourceLocation id, FoodProperties foodProperties) {
      super(new Properties().tab(ModItems.VAULT_MOD_GROUP).food(foodProperties).stacksTo(64));
      this.setRegistryName(id);
   }
}
