package iskallia.vault.item.crystal.recipe;

import iskallia.vault.block.VaultArtifactBlock;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModItems;
import java.util.ArrayList;
import java.util.List;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.world.item.ItemStack;

public class RerollArtifactAnvilRecipe extends VanillaAnvilRecipe {
   @Override
   public boolean onSimpleCraft(AnvilContext context) {
      ItemStack primary = context.getInput()[0];
      ItemStack secondary = context.getInput()[1];
      if (primary.getItem() == ModBlocks.VAULT_ARTIFACT.asItem() && secondary.getItem() == ModItems.OMEGA_POG) {
         context.setOutput(new ItemStack(ModItems.UNIDENTIFIED_ARTIFACT));
         context.onTake(context.getTake().append(() -> {
            context.getInput()[0].shrink(1);
            context.getInput()[1].shrink(1);
         }));
         return true;
      } else {
         return false;
      }
   }

   @Override
   public void onRegisterJEI(IRecipeRegistration registry) {
      IVanillaRecipeFactory factory = registry.getVanillaRecipeFactory();
      List<ItemStack> primary = new ArrayList<>();
      List<ItemStack> secondary = new ArrayList<>();
      List<ItemStack> output = new ArrayList<>();

      for (int i = 0; i < 25; i++) {
         primary.add(VaultArtifactBlock.createArtifact(i + 1));
      }

      secondary.add(new ItemStack(ModItems.OMEGA_POG));
      output.add(new ItemStack(ModItems.UNIDENTIFIED_ARTIFACT));
      registry.addRecipes(RecipeTypes.ANVIL, List.of(factory.createAnvilRecipe(primary, secondary, output)));
   }
}
