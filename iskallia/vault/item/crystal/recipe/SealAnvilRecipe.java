package iskallia.vault.item.crystal.recipe;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.ItemVaultCrystalSeal;
import iskallia.vault.item.crystal.CrystalData;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;

public class SealAnvilRecipe extends VanillaAnvilRecipe {
   @Override
   public boolean onSimpleCraft(AnvilContext context) {
      if (context.getBlockState().isPresent() && context.getBlockState().get().getBlock() == Blocks.ANVIL) {
         return false;
      } else {
         ItemStack primary = context.getInput()[0];
         ItemStack secondary = context.getInput()[1];
         if (!(secondary.getItem() instanceof ItemVaultCrystalSeal)) {
            return false;
         } else {
            ItemStack output = primary.getItem() == ModItems.VAULT_CRYSTAL ? primary.copy() : new ItemStack(ModItems.VAULT_CRYSTAL);
            CrystalData crystal = CrystalData.read(output);
            if (!ModConfigs.VAULT_CRYSTAL.applySeal(primary, secondary, output, crystal)) {
               return false;
            } else {
               crystal.write(output);
               context.setOutput(output);
               context.onTake(context.getTake().append(() -> {
                  context.getInput()[0].shrink(1);
                  context.getInput()[1].shrink(1);
               }));
               return true;
            }
         }
      }
   }

   @Override
   public void onRegisterJEI(IRecipeRegistration registry) {
   }
}
