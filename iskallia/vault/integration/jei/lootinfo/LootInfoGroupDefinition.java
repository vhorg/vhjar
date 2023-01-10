package iskallia.vault.integration.jei.lootinfo;

import java.util.function.Supplier;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

public record LootInfoGroupDefinition(Supplier<ItemStack> catalystItemStackSupplier, RecipeType<LootInfo> recipeType, Supplier<Component> titleComponentSupplier) {
   public ItemStack itemStack() {
      return this.catalystItemStackSupplier.get();
   }

   public Component titleComponent() {
      return this.titleComponentSupplier.get();
   }
}
