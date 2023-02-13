package iskallia.vault.config.recipe;

import iskallia.vault.gear.crafting.recipe.GearForgeRecipe;
import iskallia.vault.gear.crafting.recipe.JewelForgeRecipe;
import iskallia.vault.gear.crafting.recipe.ToolForgeRecipe;
import iskallia.vault.gear.crafting.recipe.TrinketForgeRecipe;
import iskallia.vault.gear.crafting.recipe.VaultForgeRecipe;
import iskallia.vault.init.ModConfigs;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public enum ForgeRecipeType {
   GEAR(GearForgeRecipe::new, () -> ModConfigs.GEAR_RECIPES_CONFIG::getRecipe),
   JEWEL(JewelForgeRecipe::new, () -> ModConfigs.JEWEL_RECIPES_CONFIG::getRecipe),
   TRINKET(TrinketForgeRecipe::new, () -> ModConfigs.TRINKET_RECIPES_CONFIG::getRecipe),
   TOOL(ToolForgeRecipe::new, () -> ModConfigs.TOOL_RECIPES_CONFIG::getRecipe);

   private final BiFunction<ResourceLocation, ItemStack, ? extends VaultForgeRecipe> recipeClassCtor;
   private final Supplier<Function<ResourceLocation, VaultForgeRecipe>> recipeGetter;

   private ForgeRecipeType(
      BiFunction<ResourceLocation, ItemStack, VaultForgeRecipe> recipeClassCtor, Supplier<Function<ResourceLocation, VaultForgeRecipe>> recipeGetter
   ) {
      this.recipeClassCtor = recipeClassCtor;
      this.recipeGetter = recipeGetter;
   }

   @Nullable
   public VaultForgeRecipe getRecipe(ResourceLocation id) {
      return this.recipeGetter.get().apply(id);
   }

   public VaultForgeRecipe makeRecipe(ResourceLocation id, ItemStack stack) {
      return this.recipeClassCtor.apply(id, stack);
   }
}
