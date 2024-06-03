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
      if (primary.getItem() instanceof VaultGearItem && !secondary.isEmpty() && secondary.getItem() == ModItems.REPAIR_CORE) {
         VaultGearData gear = VaultGearData.read(primary);
         ItemStack output = primary.copy();
         output.setDamageValue(0);
         gear.setUsedRepairSlots(gear.getUsedRepairSlots() + 1);
         gear.write(output);
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
