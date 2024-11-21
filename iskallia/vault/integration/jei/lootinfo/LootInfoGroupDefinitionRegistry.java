package iskallia.vault.integration.jei.lootinfo;

import iskallia.vault.VaultMod;
import iskallia.vault.init.ModBlocks;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Supplier;
import mezz.jei.api.recipe.RecipeType;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public final class LootInfoGroupDefinitionRegistry {
   private static final Map<ResourceLocation, LootInfoGroupDefinition> MAP = new LinkedHashMap<>();
   private static final Map<ResourceLocation, LootInfoGroupDefinition> UNMODIFIABLE_MAP = Collections.unmodifiableMap(MAP);

   private static void register(String path, Supplier<ItemStack> catalystItemStackSupplier) {
      MAP.put(
         VaultMod.id(path),
         new LootInfoGroupDefinition(catalystItemStackSupplier, recipeType(path), () -> new TranslatableComponent("jei.the_vault." + path + "_loot"))
      );
   }

   private static RecipeType<LootInfo> recipeType(String path) {
      return RecipeType.create("the_vault", path, LootInfo.class);
   }

   public static Map<ResourceLocation, LootInfoGroupDefinition> get() {
      return UNMODIFIABLE_MAP;
   }

   private LootInfoGroupDefinitionRegistry() {
   }

   static {
      register("wooden_chest_raw", () -> new ItemStack(ModBlocks.WOODEN_CHEST));
      register("living_chest_raw", () -> new ItemStack(ModBlocks.LIVING_CHEST));
      register("gilded_chest_raw", () -> new ItemStack(ModBlocks.GILDED_CHEST));
      register("ornate_chest_raw", () -> new ItemStack(ModBlocks.ORNATE_CHEST));
      register("wooden_chest", () -> new ItemStack(ModBlocks.WOODEN_CHEST));
      register("living_chest", () -> new ItemStack(ModBlocks.LIVING_CHEST));
      register("gilded_chest", () -> new ItemStack(ModBlocks.GILDED_CHEST));
      register("ornate_chest", () -> new ItemStack(ModBlocks.ORNATE_CHEST));
      register("hardened_chest", () -> new ItemStack(ModBlocks.HARDENED_CHEST));
      register("flesh_chest", () -> new ItemStack(ModBlocks.FLESH_CHEST));
      register("enigma_chest", () -> new ItemStack(ModBlocks.ENIGMA_CHEST));
      register("coin_pile", () -> new ItemStack(ModBlocks.COIN_PILE));
      register("cube_block", () -> new ItemStack(ModBlocks.CUBE_BLOCK));
      register("treasure_sand", () -> new ItemStack(ModBlocks.TREASURE_SAND));
      register("treasure_chest", () -> new ItemStack(ModBlocks.TREASURE_CHEST));
      register("completion_crate_mono", () -> new ItemStack(ModBlocks.VAULT_CRATE_MONOLITH));
      register("completion_crate_kill", () -> new ItemStack(ModBlocks.VAULT_CRATE));
      register("completion_crate_elixir", () -> new ItemStack(ModBlocks.VAULT_CRATE_ELIXIR));
      register("completion_crate_scavenger", () -> new ItemStack(ModBlocks.VAULT_CRATE_SCAVENGER));
      register("completion_crate_paradox", () -> new ItemStack(ModBlocks.VAULT_CRATE_PARADOX));
      register("altar_chest", () -> new ItemStack(ModBlocks.ALTAR_CHEST));
      register("living_strongbox", () -> new ItemStack(ModBlocks.LIVING_STRONGBOX));
      register("gilded_strongbox", () -> new ItemStack(ModBlocks.GILDED_STRONGBOX));
      register("ornate_strongbox", () -> new ItemStack(ModBlocks.ORNATE_STRONGBOX));
      register("wooden_barrel", () -> new ItemStack(ModBlocks.WOODEN_BARREL));
      register("living_barrel", () -> new ItemStack(ModBlocks.LIVING_BARREL));
      register("gilded_barrel", () -> new ItemStack(ModBlocks.GILDED_BARREL));
      register("ornate_barrel", () -> new ItemStack(ModBlocks.ORNATE_BARREL));
   }
}
