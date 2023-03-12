package iskallia.vault.config.entry.recipe;

import iskallia.vault.config.entry.ItemEntry;
import iskallia.vault.gear.crafting.recipe.GearForgeRecipe;
import java.util.List;
import net.minecraft.world.item.ItemStack;

public class ConfigGearRecipe extends ConfigForgeRecipe<GearForgeRecipe> {
   public ConfigGearRecipe(ItemStack output) {
      super(output);
   }

   public GearForgeRecipe makeRecipe() {
      ItemStack out = this.output.createItemStack();
      List<ItemStack> in = this.inputs.stream().map(ItemEntry::createItemStack).toList();
      return new GearForgeRecipe(this.id, out, in);
   }
}
