package iskallia.vault.config.entry.recipe;

import iskallia.vault.VaultMod;
import iskallia.vault.config.entry.ItemEntry;
import iskallia.vault.gear.crafting.recipe.JewelForgeRecipe;
import iskallia.vault.init.ModItems;
import java.util.List;
import net.minecraft.world.item.ItemStack;

public class ConfigJewelRecipe extends ConfigForgeRecipe<JewelForgeRecipe> {
   public ConfigJewelRecipe() {
      super(VaultMod.id("jewel"), new ItemStack(ModItems.JEWEL));
   }

   public JewelForgeRecipe makeRecipe() {
      ItemStack out = this.output.createItemStack();
      List<ItemStack> in = this.inputs.stream().map(ItemEntry::createItemStack).toList();
      return new JewelForgeRecipe(this.id, out, in);
   }
}
