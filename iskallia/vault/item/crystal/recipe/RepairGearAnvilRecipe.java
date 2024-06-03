package iskallia.vault.item.crystal.recipe;

import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModItems;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.world.item.ItemStack;

public class RepairGearAnvilRecipe extends VanillaAnvilRecipe {
   @Override
   public boolean onSimpleCraft(AnvilContext context) {
      ItemStack primary = context.getInput()[0];
      ItemStack secondary = context.getInput()[1];
      if (primary.getItem() instanceof VaultGearItem && secondary.getItem() == ModItems.REPAIR_CORE) {
         VaultGearData gear = VaultGearData.read(primary);
         int cost = Math.min(gear.getRepairSlots() - gear.getUsedRepairSlots(), secondary.getCount());
         if (cost == 0) {
            return false;
         } else {
            ItemStack output = primary.copy();
            output.setDamageValue(0);
            gear.setUsedRepairSlots(gear.getUsedRepairSlots() + cost);
            gear.write(output);
            context.setOutput(output);
            context.onTake(context.getTake().append(() -> {
               context.getInput()[0].shrink(1);
               context.getInput()[1].shrink(cost);
            }));
            return true;
         }
      } else {
         return false;
      }
   }

   @Override
   public void onRegisterJEI(IRecipeRegistration registry) {
   }
}
