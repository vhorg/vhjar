package iskallia.vault.config.entry.recipe;

import iskallia.vault.VaultMod;
import iskallia.vault.config.entry.ItemEntry;
import iskallia.vault.gear.crafting.recipe.InscriptionForgeRecipe;
import iskallia.vault.init.ModItems;
import java.util.List;
import net.minecraft.world.item.ItemStack;

public class ConfigInscriptionRecipe extends ConfigForgeRecipe<InscriptionForgeRecipe> {
   public ConfigInscriptionRecipe() {
      super(VaultMod.id("inscription"), new ItemStack(ModItems.INSCRIPTION));
   }

   public InscriptionForgeRecipe makeRecipe() {
      ItemStack out = this.output.createItemStack();
      List<ItemStack> in = this.inputs.stream().map(ItemEntry::createItemStack).toList();
      return new InscriptionForgeRecipe(this.id, out, in);
   }
}
