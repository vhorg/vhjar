package iskallia.vault.init;

import iskallia.vault.Vault;
import iskallia.vault.world.gen.decorator.ArchitectEventFeature;
import iskallia.vault.world.gen.decorator.BreadcrumbFeature;
import iskallia.vault.world.gen.decorator.OverworldOreFeature;
import iskallia.vault.world.gen.decorator.RaidChallengeFeature;
import iskallia.vault.world.gen.decorator.VaultFeature;
import iskallia.vault.world.gen.decorator.VaultTroveFeature;
import iskallia.vault.world.gen.structure.ArchitectEventStructure;
import iskallia.vault.world.gen.structure.RaidChallengeStructure;
import iskallia.vault.world.gen.structure.VaultStructure;
import iskallia.vault.world.gen.structure.VaultTroveStructure;
import net.minecraft.util.registry.WorldGenRegistries;
import net.minecraft.world.gen.feature.ConfiguredFeature;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.IFeatureConfig;
import net.minecraft.world.gen.feature.NoFeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.StructureFeature;
import net.minecraft.world.gen.feature.OreFeatureConfig.FillerBlockType;
import net.minecraft.world.gen.feature.structure.Structure;
import net.minecraft.world.gen.placement.Placement;
import net.minecraft.world.gen.placement.TopSolidRangeConfig;
import net.minecraftforge.event.RegistryEvent.Register;

public class ModFeatures {
   public static VaultFeature VAULT_FEATURE;
   public static ArchitectEventFeature ARCHITECT_EVENT_FEATURE;
   public static RaidChallengeFeature RAID_CHALLENGE_FEATURE;
   public static VaultTroveFeature VAULT_TROVE_FEATURE;
   public static ConfiguredFeature<?, ?> BREADCRUMB_CHEST;
   public static ConfiguredFeature<?, ?> VAULT_ROCK_ORE;

   public static void registerStructureFeatures() {
      VAULT_FEATURE = register("vault", new VaultFeature(ModStructures.VAULT_STAR, new VaultStructure.Config(() -> VaultStructure.Pools.FINAL_START, 11)));
      ARCHITECT_EVENT_FEATURE = register(
         "architect_event",
         new ArchitectEventFeature(ModStructures.ARCHITECT_EVENT, new ArchitectEventStructure.Config(() -> ArchitectEventStructure.Pools.START, 1))
      );
      RAID_CHALLENGE_FEATURE = register(
         "raid_challenge",
         new RaidChallengeFeature(ModStructures.RAID_CHALLENGE, new RaidChallengeStructure.Config(() -> RaidChallengeStructure.Pools.START, 1))
      );
      VAULT_TROVE_FEATURE = register(
         "trove", new VaultTroveFeature(ModStructures.VAULT_TROVE, new VaultTroveStructure.Config(() -> VaultTroveStructure.Pools.START, 1))
      );
   }

   public static void registerFeatures(Register<Feature<?>> event) {
      BreadcrumbFeature.register(event);
      OverworldOreFeature.register(event);
      BREADCRUMB_CHEST = register("breadcrumb_chest", BreadcrumbFeature.INSTANCE.func_225566_b_(NoFeatureConfig.field_236559_b_));
      VAULT_ROCK_ORE = register(
         "vault_rock_ore",
         (ConfiguredFeature)OverworldOreFeature.INSTANCE
            .func_225566_b_(new OreFeatureConfig(FillerBlockType.field_241882_a, ModBlocks.VAULT_ROCK_ORE.func_176223_P(), 1))
            .func_227228_a_(Placement.field_242907_l.func_227446_a_(new TopSolidRangeConfig(5, 0, 6)))
            .func_242728_a()
      );
   }

   private static <FC extends IFeatureConfig, F extends Feature<FC>> ConfiguredFeature<FC, F> register(String name, ConfiguredFeature<FC, F> feature) {
      return (ConfiguredFeature<FC, F>)WorldGenRegistries.func_243664_a(WorldGenRegistries.field_243653_e, Vault.id(name), feature);
   }

   private static <SF extends StructureFeature<FC, F>, FC extends IFeatureConfig, F extends Structure<FC>> SF register(String name, SF feature) {
      return (SF)WorldGenRegistries.func_243664_a(WorldGenRegistries.field_243654_f, Vault.id(name), feature);
   }
}
