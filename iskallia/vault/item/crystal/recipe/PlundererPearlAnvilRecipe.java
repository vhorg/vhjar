package iskallia.vault.item.crystal.recipe;

import iskallia.vault.VaultMod;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.item.crystal.VaultCrystalItem;
import iskallia.vault.item.crystal.objective.NullCrystalObjective;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.world.item.ItemStack;

public class PlundererPearlAnvilRecipe extends VanillaAnvilRecipe {
   @Override
   public boolean onSimpleCraft(AnvilContext context) {
      ItemStack primary = context.getInput()[0];
      ItemStack secondary = context.getInput()[1];
      if (primary.getItem() == ModItems.VAULT_CRYSTAL && secondary.getItem() == ModItems.PLUNDERER_PEARL) {
         ItemStack output = primary.copy();
         CrystalData crystal = CrystalData.read(output);
         if (!crystal.getProperties().isUnmodifiable()
            && crystal.getModifiers().hasRandomModifiers()
            && crystal.getObjective() == NullCrystalObjective.INSTANCE) {
            crystal.getModifiers().setRandomModifiers(false);
            crystal.getProperties().setUnmodifiable(true);
            VaultCrystalItem.scheduleTask(new VaultCrystalItem.AddModifiersTask(VaultMod.id("plunderer_pearl"), 1), output);
         }

         crystal.write(output);
         context.setOutput(output);
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
   }
}
