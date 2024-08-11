package iskallia.vault.config.recipe;

import iskallia.vault.gear.crafting.recipe.CatalystForgeRecipe;
import iskallia.vault.gear.crafting.recipe.GearForgeRecipe;
import iskallia.vault.gear.crafting.recipe.InscriptionForgeRecipe;
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
   GEAR(GearForgeRecipe::new, () -> ModConfigs.GEAR_RECIPES),
   JEWEL(JewelForgeRecipe::new, () -> ModConfigs.JEWEL_RECIPES),
   TRINKET(TrinketForgeRecipe::new, () -> ModConfigs.TRINKET_RECIPES),
   TOOL(ToolForgeRecipe::new, () -> ModConfigs.TOOL_RECIPES),
   INSCRIPTION(InscriptionForgeRecipe::new, () -> ModConfigs.INSCRIPTION_RECIPES),
   CATALYST(CatalystForgeRecipe::new, () -> ModConfigs.CATALYST_RECIPES);

   private final BiFunction<ResourceLocation, ItemStack, ? extends VaultForgeRecipe> recipeClassCtor;
   private final Supplier<ForgeRecipesConfig<?, ?>> configSupplier;
   private final Supplier<Function<ResourceLocation, VaultForgeRecipe>> recipeGetter;

   private ForgeRecipeType(BiFunction<ResourceLocation, ItemStack, VaultForgeRecipe> recipeClassCtor, Supplier<ForgeRecipesConfig<?, ?>> configSupplier) {
      this(recipeClassCtor, configSupplier, () -> configSupplier.get()::getRecipe);
   }

   private ForgeRecipeType(
      BiFunction<ResourceLocation, ItemStack, ? extends VaultForgeRecipe> recipeClassCtor,
      Supplier<ForgeRecipesConfig<?, ?>> configSupplier,
      Supplier<Function<ResourceLocation, VaultForgeRecipe>> recipeGetter
   ) {
      this.recipeClassCtor = recipeClassCtor;
      this.configSupplier = configSupplier;
      this.recipeGetter = recipeGetter;
   }

   @Nullable
   public VaultForgeRecipe getRecipe(ResourceLocation id) {
      return this.recipeGetter.get().apply(id);
   }

   public ForgeRecipesConfig<?, ?> getRecipeConfig() {
      return this.configSupplier.get();
   }

   public VaultForgeRecipe makeRecipe(ResourceLocation id, ItemStack stack) {
      return this.recipeClassCtor.apply(id, stack);
   }
}
