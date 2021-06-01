package iskallia.vault.init;

import iskallia.vault.Vault;
import iskallia.vault.world.gen.decorator.BreadcrumbFeature;
import iskallia.vault.world.gen.decorator.OverworldOreFeature;
import iskallia.vault.world.gen.decorator.RegionOreFeature;
import iskallia.vault.world.gen.ruletest.VaultRuleTest;
import iskallia.vault.world.gen.structure.ArenaStructure;
import iskallia.vault.world.gen.structure.VaultStructure;
import net.minecraft.block.Blocks;
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
   public static StructureFeature<VaultStructure.Config, ? extends Structure<VaultStructure.Config>> VAULT_FEATURE;
   public static StructureFeature<VaultStructure.Config, ? extends Structure<VaultStructure.Config>> FINAL_VAULT_FEATURE;
   public static StructureFeature<ArenaStructure.Config, ? extends Structure<ArenaStructure.Config>> ARENA_FEATURE;
   public static ConfiguredFeature<?, ?> VAULT_ORE;
   public static ConfiguredFeature<?, ?> BREADCRUMB_CHEST;
   public static ConfiguredFeature<?, ?> VAULT_ROCK_ORE;

   public static void registerStructureFeatures() {
      VAULT_FEATURE = register("vault", ModStructures.VAULT.func_236391_a_(new VaultStructure.Config(() -> VaultStructure.Pools.START, 6)));
      FINAL_VAULT_FEATURE = register("final_vault", ModStructures.VAULT.func_236391_a_(new VaultStructure.Config(() -> VaultStructure.Pools.FINAL_START, 6)));
      ARENA_FEATURE = register("arena", ModStructures.ARENA.func_236391_a_(new ArenaStructure.Config(() -> ArenaStructure.Pools.START, 8)));
   }

   public static void registerFeatures(Register<Feature<?>> event) {
      RegionOreFeature.register(event);
      BreadcrumbFeature.register(event);
      OverworldOreFeature.register(event);
      VAULT_ORE = register(
         "vault_ore",
         (ConfiguredFeature)RegionOreFeature.INSTANCE
            .func_225566_b_(new OreFeatureConfig(VaultRuleTest.INSTANCE, Blocks.field_196654_e.func_176223_P(), 0))
            .func_242731_b(1)
      );
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

   private static <FC extends IFeatureConfig, F extends Structure<FC>> StructureFeature<FC, F> register(String name, StructureFeature<FC, F> feature) {
      return (StructureFeature<FC, F>)WorldGenRegistries.func_243664_a(WorldGenRegistries.field_243654_f, Vault.id(name), feature);
   }
}
