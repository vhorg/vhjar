package iskallia.vault.config.entry.recipe;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.entry.ItemEntry;
import iskallia.vault.gear.crafting.ProficiencyType;
import iskallia.vault.gear.crafting.recipe.GearForgeRecipe;
import java.util.List;
import net.minecraft.world.item.ItemStack;

public class ConfigGearRecipe extends ConfigForgeRecipe<GearForgeRecipe> {
   @Expose
   private final ProficiencyType proficiencyType;

   public ConfigGearRecipe(ItemStack output, ProficiencyType proficiencyType) {
      super(output);
      this.proficiencyType = proficiencyType;
   }

   public GearForgeRecipe makeRecipe() {
      ItemStack out = this.output.createItemStack();
      List<ItemStack> in = this.inputs.stream().map(ItemEntry::createItemStack).toList();
      return new GearForgeRecipe(this.id, out, in, this.proficiencyType);
   }
}
