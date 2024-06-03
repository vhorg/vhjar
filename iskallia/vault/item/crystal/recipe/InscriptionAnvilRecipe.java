package iskallia.vault.item.crystal.recipe;

import iskallia.vault.VaultMod;
import iskallia.vault.config.entry.recipe.ConfigInscriptionRecipe;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.item.crystal.VaultCrystalItem;
import iskallia.vault.item.crystal.layout.ArchitectCrystalLayout;
import iskallia.vault.item.data.InscriptionData;
import java.util.ArrayList;
import java.util.List;
import mezz.jei.api.constants.RecipeTypes;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.world.item.ItemStack;

public class InscriptionAnvilRecipe extends VanillaAnvilRecipe {
   @Override
   public boolean onSimpleCraft(AnvilContext context) {
      ItemStack primary = context.getInput()[0];
      ItemStack secondary = context.getInput()[1];
      if (primary.getItem() == ModItems.VAULT_CRYSTAL && secondary.getItem() == ModItems.INSCRIPTION) {
         ItemStack output = primary.copy();
         CrystalData crystal = CrystalData.read(output);
         InscriptionData data = InscriptionData.from(secondary);
         if (!data.apply(context.getPlayer().orElse(null), output, crystal)) {
            return false;
         } else {
            context.setOutput(output);
            context.onTake(context.getTake().append(() -> {
               context.getInput()[0].shrink(1);
               context.getInput()[1].shrink(1);
            }));
            return true;
         }
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
      primary.add(VaultCrystalItem.create(data -> data.setLayout(new ArchitectCrystalLayout())));

      for (ConfigInscriptionRecipe recipe : ModConfigs.INSCRIPTION_RECIPES.getConfigRecipes()) {
         ItemStack inscription = recipe.makeRecipe().createOutput(new ArrayList<>(), null, 0);
         secondary.add(inscription);
         output.add(VaultCrystalItem.create(primary.get(0), crystal -> {
            InscriptionData data = InscriptionData.from(inscription);
            if (!data.apply(null, null, crystal)) {
               VaultMod.LOGGER.error("Failed to apply inscription to crystal for recipe " + recipe.getId());
            }
         }));
      }

      registry.addRecipes(RecipeTypes.ANVIL, List.of(factory.createAnvilRecipe(primary, secondary, output)));
   }
}
