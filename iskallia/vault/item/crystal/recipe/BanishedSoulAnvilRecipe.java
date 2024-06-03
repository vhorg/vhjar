package iskallia.vault.item.crystal.recipe;

import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.crystal.CrystalData;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.world.item.ItemStack;

public class BanishedSoulAnvilRecipe extends VanillaAnvilRecipe {
   @Override
   public boolean onSimpleCraft(AnvilContext context) {
      ItemStack primary = context.getInput()[0];
      ItemStack secondary = context.getInput()[1];
      if (secondary.getItem() != ModItems.BANISHED_SOUL) {
         return false;
      } else {
         if (primary.getItem() == ModItems.VAULT_CRYSTAL) {
            ItemStack output = primary.copy();
            CrystalData crystal = CrystalData.read(output);
            Integer level = crystal.getProperties().getLevel().orElse(null);
            if (crystal.getModifiers().hasCurse() || crystal.getProperties().isUnmodifiable() || level == null || level <= 0) {
               return false;
            }

            int newLevel = Math.max(0, level - secondary.getCount());
            int price = level - newLevel;
            crystal.getProperties().setLevel(newLevel);
            crystal.write(output);
            context.setOutput(output);
            context.onTake(context.getTake().append(() -> {
               context.getInput()[0].shrink(1);
               context.getInput()[1].shrink(price);
            }));
         } else if (primary.getItem() instanceof VaultGearItem) {
            ItemStack output = primary.copy();
            VaultGearData data = VaultGearData.read(output);
            if (data.getState() != VaultGearState.UNIDENTIFIED || data.getItemLevel() <= 0) {
               return false;
            }

            int newLevel = Math.max(0, data.getItemLevel() - secondary.getCount());
            int price = data.getItemLevel() - newLevel;
            data.setItemLevel(newLevel);
            data.write(output);
            context.setOutput(output);
            context.onTake(context.getTake().append(() -> {
               context.getInput()[0].shrink(1);
               context.getInput()[1].shrink(price);
            }));
         }

         return true;
      }
   }

   @Override
   public void onRegisterJEI(IRecipeRegistration registry) {
   }
}
