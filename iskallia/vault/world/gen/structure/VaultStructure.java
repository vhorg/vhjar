package iskallia.vault.world.gen.structure;

import iskallia.vault.VaultMod;
import iskallia.vault.init.ModStructures;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.PlainVillagePools;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;

public class VaultStructure extends StructureFeature<JigsawConfiguration> {
   public static final ResourceKey<StructureTemplatePool> POOL = ResourceKey.create(Registry.TEMPLATE_POOL_REGISTRY, VaultMod.id("vault/starts"));

   public VaultStructure() {
      super(JigsawConfiguration.CODEC, context -> Optional.empty());
   }

   public Decoration step() {
      return Decoration.UNDERGROUND_STRUCTURES;
   }

   public VaultStructure.Feature configured() {
      return new VaultStructure.Feature(
         this, new JigsawConfiguration(PlainVillagePools.START, 1), BuiltinRegistries.BIOME.getOrCreateTag(ModStructures.EMPTY), false, new HashMap<>()
      );
   }

   public static class Feature extends ConfiguredStructureFeature<JigsawConfiguration, VaultStructure> implements IRegistryIdentifiable {
      private ResourceLocation id;

      public Feature(
         VaultStructure structure,
         JigsawConfiguration config,
         HolderSet<Biome> biomes,
         boolean adaptNoise,
         Map<MobCategory, StructureSpawnOverride> spawnOverrides
      ) {
         super(structure, config, biomes, adaptNoise, spawnOverrides);
      }

      @Override
      public ResourceLocation getId() {
         return this.id;
      }

      @Override
      public void setId(ResourceLocation id) {
         this.id = id;
      }
   }
}
