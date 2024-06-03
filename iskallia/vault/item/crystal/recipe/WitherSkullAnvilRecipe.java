package iskallia.vault.item.crystal.recipe;

import iskallia.vault.VaultMod;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.item.crystal.VaultCrystalItem;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class WitherSkullAnvilRecipe extends VanillaAnvilRecipe {
   @Override
   public boolean onSimpleCraft(AnvilContext context) {
      ItemStack primary = context.getInput()[0];
      ItemStack secondary = context.getInput()[1];
      if (primary.getItem() == ModItems.VAULT_CRYSTAL && secondary.getItem() == Items.WITHER_SKELETON_SKULL) {
         ItemStack output = primary.copy();
         CrystalData crystal = CrystalData.read(output);
         VaultCrystalItem.scheduleTask(new VaultCrystalItem.AddModifiersTask(VaultMod.id("wither_skull_curse"), secondary.getCount()), output);
         crystal.write(output);
         context.setOutput(output);
         context.onTake(context.getTake().append(() -> {
            context.getInput()[0].shrink(1);
            context.getInput()[1].shrink(secondary.getCount());
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
