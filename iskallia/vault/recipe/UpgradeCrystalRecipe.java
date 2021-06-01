package iskallia.vault.recipe;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModRecipes;
import iskallia.vault.item.ItemVaultCrystal;
import iskallia.vault.util.VaultRarity;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import net.minecraft.inventory.CraftingInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.item.crafting.SpecialRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class UpgradeCrystalRecipe extends SpecialRecipe {
   public UpgradeCrystalRecipe(ResourceLocation id) {
      super(id);
   }

   public boolean matches(CraftingInventory inv, World world) {
      VaultRarity rarity = null;
      boolean hasSpark = false;
      int count = 0;

      for (int i = 0; i < inv.func_70302_i_(); i++) {
         ItemStack stack = inv.func_70301_a(i);
         if (stack.func_77973_b() instanceof ItemVaultCrystal) {
            if (rarity != null && ((ItemVaultCrystal)stack.func_77973_b()).getRarity() != rarity) {
               return false;
            }

            rarity = ((ItemVaultCrystal)stack.func_77973_b()).getRarity();
            count++;
         } else if (!hasSpark && stack.func_77973_b() == ModItems.SPARK) {
            hasSpark = true;
         } else if (!stack.func_190926_b()) {
            return false;
         }
      }

      int targetCount = Integer.MAX_VALUE;
      if (rarity == VaultRarity.COMMON) {
         targetCount = ModConfigs.CRYSTAL_UPGRADE.COMMON_TO_RARE;
      } else if (rarity == VaultRarity.RARE) {
         targetCount = ModConfigs.CRYSTAL_UPGRADE.RARE_TO_EPIC;
      } else if (rarity == VaultRarity.EPIC) {
         targetCount = ModConfigs.CRYSTAL_UPGRADE.EPIC_TO_OMEGA;
      }

      return rarity != null && hasSpark && count == targetCount;
   }

   public ItemStack getCraftingResult(CraftingInventory inv) {
      List<ItemStack> crystals = new ArrayList<>();
      VaultRarity rarity = null;

      for (int i = 0; i < inv.func_70302_i_(); i++) {
         ItemStack stack = inv.func_70301_a(i);
         if (stack.func_77973_b() instanceof ItemVaultCrystal) {
            rarity = ((ItemVaultCrystal)stack.func_77973_b()).getRarity();
            crystals.add(stack);
         }
      }

      List<String> bossNames = crystals.stream()
         .filter(ItemStack::func_77942_o)
         .filter(stackx -> stackx.func_77978_p().func_150297_b("playerBossName", 8))
         .map(stackx -> stackx.func_77978_p().func_74779_i("playerBossName"))
         .sorted(String::compareToIgnoreCase)
         .collect(Collectors.toList());
      return !bossNames.isEmpty()
         ? ItemVaultCrystal.getCrystalWithBoss(
            VaultRarity.values()[rarity.ordinal() + 1], bossNames.get(new Random(bossNames.hashCode()).nextInt(bossNames.size()))
         )
         : ItemVaultCrystal.getCrystal(VaultRarity.values()[rarity.ordinal() + 1]);
   }

   public boolean func_194133_a(int width, int height) {
      return width * height
         >= Math.min(Math.min(ModConfigs.CRYSTAL_UPGRADE.COMMON_TO_RARE, ModConfigs.CRYSTAL_UPGRADE.RARE_TO_EPIC), ModConfigs.CRYSTAL_UPGRADE.EPIC_TO_OMEGA);
   }

   public IRecipeSerializer<?> func_199559_b() {
      return ModRecipes.Serializer.CRAFTING_SPECIAL_UPGRADE_CRYSTAL;
   }
}
