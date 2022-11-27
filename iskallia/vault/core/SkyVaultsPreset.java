package iskallia.vault.core;

import com.mojang.serialization.Lifecycle;
import net.minecraft.client.gui.screens.worldselection.WorldPreset;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.MappedRegistry;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.WritableRegistry;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.TheEndBiomeSource;
import net.minecraft.world.level.biome.MultiNoiseBiomeSource.Preset;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.levelgen.NoiseBasedChunkGenerator;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.WorldGenSettings;
import net.minecraft.world.level.levelgen.structure.BuiltinStructureSets;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.synth.NormalNoise.NoiseParameters;

public class SkyVaultsPreset extends WorldPreset {
   public static final SkyVaultsPreset INSTANCE = new SkyVaultsPreset();

   public SkyVaultsPreset() {
      super(new TranslatableComponent("generator.sky_vaults"));
   }

   protected ChunkGenerator generator(RegistryAccess registries, long seed) {
      throw new UnsupportedOperationException("Use generic world settings instead");
   }

   public WorldGenSettings create(RegistryAccess registries, long seed, boolean generateFeatures, boolean generateBonusChest) {
      WritableRegistry<LevelStem> stems = new MappedRegistry(Registry.LEVEL_STEM_REGISTRY, Lifecycle.stable(), null);
      return new WorldGenSettings(seed, generateFeatures, generateBonusChest, build(stems, registries, seed, false));
   }

   public static Registry<LevelStem> build(WritableRegistry<LevelStem> stems, RegistryAccess registries, long seed, boolean features) {
      Registry<DimensionType> dimensions = registries.registryOrThrow(Registry.DIMENSION_TYPE_REGISTRY);
      Registry<Biome> biomes = registries.registryOrThrow(Registry.BIOME_REGISTRY);
      Registry<StructureSet> structures = registries.registryOrThrow(Registry.STRUCTURE_SET_REGISTRY);
      Registry<NoiseGeneratorSettings> settings = registries.registryOrThrow(Registry.NOISE_GENERATOR_SETTINGS_REGISTRY);
      Registry<NoiseParameters> noises = registries.registryOrThrow(Registry.NOISE_REGISTRY);
      stems.register(
         LevelStem.OVERWORLD,
         new LevelStem(
            dimensions.getOrCreateHolder(DimensionType.OVERWORLD_LOCATION),
            new SkyVaultsChunkGenerator(
               structures,
               HolderSet.direct(new Holder[0]),
               noises,
               Preset.OVERWORLD.biomeSource(biomes, false),
               seed,
               settings.getOrCreateHolder(NoiseGeneratorSettings.OVERWORLD)
            )
         ),
         Lifecycle.stable()
      );
      stems.register(
         LevelStem.NETHER,
         new LevelStem(
            dimensions.getOrCreateHolder(DimensionType.NETHER_LOCATION),
            new SkyVaultsChunkGenerator(
               structures,
               HolderSet.direct(new Holder[]{structures.getHolderOrThrow(BuiltinStructureSets.NETHER_COMPLEXES)}),
               noises,
               Preset.NETHER.biomeSource(biomes, features),
               seed,
               settings.getOrCreateHolder(NoiseGeneratorSettings.NETHER)
            )
         ),
         Lifecycle.stable()
      );
      stems.register(
         LevelStem.END,
         new LevelStem(
            dimensions.getOrCreateHolder(DimensionType.END_LOCATION),
            new NoiseBasedChunkGenerator(structures, noises, new TheEndBiomeSource(biomes, seed), seed, settings.getOrCreateHolder(NoiseGeneratorSettings.END))
         ),
         Lifecycle.stable()
      );
      return stems;
   }

   public static void register() {
      PRESETS.add(1, INSTANCE);
   }
}
