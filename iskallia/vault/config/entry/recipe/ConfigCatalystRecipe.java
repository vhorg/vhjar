package iskallia.vault.config.entry.recipe;

import iskallia.vault.config.entry.ItemEntry;
import iskallia.vault.gear.crafting.recipe.CatalystForgeRecipe;
import iskallia.vault.item.InfusedCatalystItem;
import java.util.Arrays;
import java.util.List;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class ConfigCatalystRecipe extends ConfigForgeRecipe<CatalystForgeRecipe> {
   public ConfigCatalystRecipe(ResourceLocation id, ResourceLocation pool, int minSize, int maxSize, ResourceLocation... modifiers) {
      super(id, InfusedCatalystItem.createDisplay(pool, minSize, maxSize, Arrays.asList(modifiers)));
   }

   public CatalystForgeRecipe makeRecipe() {
      ItemStack out = this.output.createItemStack();
      List<ItemStack> in = this.inputs.stream().map(ItemEntry::createItemStack).toList();
      return new CatalystForgeRecipe(this.id, out, in);
   }
}
