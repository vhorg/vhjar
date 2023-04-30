package iskallia.vault.init;

import iskallia.vault.VaultMod;
import iskallia.vault.core.SkyVaultsChunkGenerator;
import iskallia.vault.core.world.generator.DummyChunkGenerator;
import iskallia.vault.world.gen.decorator.BreadcrumbFeature;
import iskallia.vault.world.gen.decorator.OverworldOreFeature;
import iskallia.vault.world.gen.structure.ArchitectEventStructure;
import iskallia.vault.world.gen.structure.ArenaStructure;
import iskallia.vault.world.gen.structure.FinalVaultLobbyStructure;
import iskallia.vault.world.gen.structure.IRegistryIdentifiable;
import iskallia.vault.world.gen.structure.RaidChallengeStructure;
import iskallia.vault.world.gen.structure.VaultStructure;
import iskallia.vault.world.gen.structure.VaultTroveStructure;
import java.util.Arrays;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.features.OreFeatures;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.NoneFeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.configurations.OreConfiguration;
import net.minecraft.world.level.levelgen.placement.HeightRangePlacement;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.RarityFilter;
import net.minecraftforge.event.RegistryEvent.Register;

public class ModFeatures {
   public static Holder<VaultStructure.Feature> VAULT_FEATURE;
   public static Holder<ArchitectEventStructure.Feature> ARCHITECT_EVENT_FEATURE;
   public static Holder<RaidChallengeStructure.Feature> RAID_CHALLENGE_FEATURE;
   public static Holder<VaultTroveStructure.Feature> VAULT_TROVE_FEATURE;
   public static Holder<FinalVaultLobbyStructure.Feature> FINAL_VAULT_LOBBY_FEATURE;
   public static Holder<ArenaStructure.Feature> ARENA_FEATURE;
   public static Holder<ConfiguredFeature<?, ?>> CONFIGURED_BREADCRUMB_CHEST;
   public static Holder<ConfiguredFeature<?, ?>> CONFIGURED_CHROMATIC_IRON_ORE_SMALL;
   public static Holder<ConfiguredFeature<?, ?>> CONFIGURED_CHROMATIC_IRON_ORE_LARGE;
   public static Holder<ConfiguredFeature<?, ?>> CONFIGURED_VAULT_STONE;
   public static Holder<PlacedFeature> PLACED_BREADCRUMB_CHEST;
   public static Holder<PlacedFeature> PLACED_CHROMATIC_IRON_ORE_SMALL;
   public static Holder<PlacedFeature> PLACED_CHROMATIC_IRON_ORE_LARGE;
   public static Holder<PlacedFeature> PLACED_VAULT_STONE;

   public static void registerStructureFeatures() {
      VAULT_FEATURE = register("vault", ModStructures.VAULT_STAR.configured());
      ARENA_FEATURE = register("arena", ModStructures.ARENA.configured(new JigsawConfiguration(ArenaStructure.Pools.START, 5)));
      ARCHITECT_EVENT_FEATURE = register("architect_event", ModStructures.ARCHITECT_EVENT.configured());
      RAID_CHALLENGE_FEATURE = register("raid_challenge", ModStructures.RAID_CHALLENGE.configured());
      VAULT_TROVE_FEATURE = register("trove", ModStructures.VAULT_TROVE.configured());
      FINAL_VAULT_LOBBY_FEATURE = register("final_vault_lobby", ModStructures.FINAL_VAULT_LOBBY.configured());
      Registry.register(Registry.CHUNK_GENERATOR, VaultMod.id("dummy"), DummyChunkGenerator.CODEC);
      Registry.register(Registry.CHUNK_GENERATOR, VaultMod.id("sky_vaults"), SkyVaultsChunkGenerator.CODEC);
   }

   public static void registerFeatures(Register<Feature<?>> event) {
      BreadcrumbFeature.register(event);
      OverworldOreFeature.register(event);
      CONFIGURED_BREADCRUMB_CHEST = register("breadcrumb_chest", BreadcrumbFeature.INSTANCE, NoneFeatureConfiguration.INSTANCE);
      CONFIGURED_CHROMATIC_IRON_ORE_SMALL = register(
         "chromatic_iron_ore_small",
         OverworldOreFeature.INSTANCE,
         new OreConfiguration(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, ModBlocks.CHROMATIC_IRON_ORE.defaultBlockState(), 12, 0.25F)
      );
      CONFIGURED_CHROMATIC_IRON_ORE_LARGE = register(
         "chromatic_iron_ore_large",
         OverworldOreFeature.INSTANCE,
         new OreConfiguration(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, ModBlocks.CHROMATIC_IRON_ORE.defaultBlockState(), 36, 0.25F)
      );
      CONFIGURED_VAULT_STONE = register(
         "vault_stone",
         OverworldOreFeature.INSTANCE,
         new OreConfiguration(OreFeatures.DEEPSLATE_ORE_REPLACEABLES, ModBlocks.VAULT_STONE.defaultBlockState(), 64)
      );
      PLACED_BREADCRUMB_CHEST = register("breadcrumb_chest", CONFIGURED_BREADCRUMB_CHEST);
      PLACED_CHROMATIC_IRON_ORE_SMALL = register(
         "placed_chromatic_iron_ore_small",
         CONFIGURED_CHROMATIC_IRON_ORE_SMALL,
         RarityFilter.onAverageOnceEvery(8),
         HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(-32)),
         InSquarePlacement.spread()
      );
      PLACED_CHROMATIC_IRON_ORE_LARGE = register(
         "placed_chromatic_iron_ore_large",
         CONFIGURED_CHROMATIC_IRON_ORE_LARGE,
         RarityFilter.onAverageOnceEvery(8),
         HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(-32)),
         InSquarePlacement.spread()
      );
      PLACED_VAULT_STONE = register(
         "placed_vault_stone",
         CONFIGURED_VAULT_STONE,
         RarityFilter.onAverageOnceEvery(10),
         HeightRangePlacement.uniform(VerticalAnchor.bottom(), VerticalAnchor.absolute(-5)),
         InSquarePlacement.spread()
      );
   }

   private static <FC extends FeatureConfiguration, F extends Feature<FC>> Holder<ConfiguredFeature<?, ?>> register(String name, F feature, FC config) {
      return BuiltinRegistries.register(BuiltinRegistries.CONFIGURED_FEATURE, VaultMod.id(name), new ConfiguredFeature(feature, config));
   }

   private static <SF extends ConfiguredStructureFeature<FC, F>, FC extends FeatureConfiguration, F extends StructureFeature<FC>> Holder<SF> register(
      String name, SF feature
   ) {
      if (feature instanceof IRegistryIdentifiable) {
         ((IRegistryIdentifiable)feature).setId(VaultMod.id(name));
      }

      return BuiltinRegistries.register(BuiltinRegistries.CONFIGURED_STRUCTURE_FEATURE, VaultMod.id(name), feature);
   }

   public static Holder<PlacedFeature> register(String name, Holder<? extends ConfiguredFeature<?, ?>> configured, PlacementModifier... modifiers) {
      return BuiltinRegistries.register(
         BuiltinRegistries.PLACED_FEATURE, VaultMod.id(name), new PlacedFeature(Holder.hackyErase(configured), Arrays.asList(modifiers))
      );
   }
}
