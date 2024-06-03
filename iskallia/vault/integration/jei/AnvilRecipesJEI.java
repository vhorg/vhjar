package iskallia.vault.integration.jei;

import com.google.common.collect.Lists;
import iskallia.vault.core.vault.modifier.VaultModifierStack;
import iskallia.vault.core.vault.modifier.registry.VaultModifierRegistry;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.gear.VaultGearModifierHelper;
import iskallia.vault.gear.VaultGearRarity;
import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.crafting.VaultGearCraftingHelper;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.item.crystal.CrystalData;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import mezz.jei.api.recipe.vanilla.IJeiAnvilRecipe;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class AnvilRecipesJEI {
   private static final Random random = new Random(0L);

   public static List<IJeiAnvilRecipe> getAnvilRecipes(IVanillaRecipeFactory vanillaRecipeFactory) {
      List<IJeiAnvilRecipe> recipeList = Lists.newArrayList();
      return recipeList;
   }

   private static void addModifier(CrystalData data, ResourceLocation id) {
      VaultModifier<?> modifier = VaultModifierRegistry.getOrDefault(id, null);
      if (modifier != null) {
         data.getModifiers().add(VaultModifierStack.of(modifier));
      }
   }

   private static List<ItemStack> generateRandomArmorList(Item gear) {
      List<ItemStack> returnList = new ArrayList<>();
      ItemStack itemStack = new ItemStack(gear);
      VaultGearData data = VaultGearData.read(itemStack);
      VaultGearItem item = VaultGearItem.of(itemStack);
      data.setRarity(VaultGearRarity.EPIC);
      data.write(itemStack);
      ResourceLocation modelKey = item.getRandomModel(itemStack, random);
      if (modelKey != null) {
         data.updateAttribute(ModGearAttributes.GEAR_MODEL, modelKey);
      }

      data.write(itemStack);
      data.setState(VaultGearState.IDENTIFIED);
      data.write(itemStack);
      VaultGearModifierHelper.reRollRepairSlots(itemStack, random);
      VaultGearCraftingHelper.reRollCraftingPotential(itemStack);
      VaultGearModifierHelper.generateAffixSlots(itemStack, random);
      VaultGearModifierHelper.generateImplicits(itemStack, random);
      VaultGearModifierHelper.generateModifiers(itemStack, random);
      returnList.add(itemStack);
      return returnList;
   }
}
