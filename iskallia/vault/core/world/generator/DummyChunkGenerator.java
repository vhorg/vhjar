package iskallia.vault.core.world.generator;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.mixin.AccessorStructureFeatureManager;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Registry;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.Biomes;
import net.minecraft.world.level.biome.Climate;
import net.minecraft.world.level.biome.FixedBiomeSource;
import net.minecraft.world.level.biome.Climate.Sampler;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.GenerationStep.Carving;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.structure.StructureSet;

public class DummyChunkGenerator extends ChunkGenerator {
   public static final Codec<DummyChunkGenerator> CODEC = RecordCodecBuilder.create(
      builder -> commonCodec(builder)
         .and(RegistryOps.retrieveRegistry(Registry.BIOME_REGISTRY).forGetter(DummyChunkGenerator::getBiomes))
         .apply(builder, builder.stable(DummyChunkGenerator::new))
   );
   private final Registry<Biome> biomes;

   public DummyChunkGenerator(Registry<StructureSet> structures, Registry<Biome> biomes) {
      super(structures, Optional.empty(), new FixedBiomeSource(biomes.getOrCreateHolder(Biomes.THE_VOID)));
      this.biomes = biomes;
   }

   protected Codec<? extends ChunkGenerator> codec() {
      return CODEC;
   }

   public Registry<Biome> getBiomes() {
      return this.biomes;
   }

   public ChunkGenerator withSeed(long seed) {
      return this;
   }

   public Sampler climateSampler() {
      return Climate.empty();
   }

   public CompletableFuture<ChunkAccess> fillFromNoise(Executor executor, Blender blender, StructureFeatureManager structureFeatureManager, ChunkAccess chunk) {
      WorldGenRegion genRegion = (WorldGenRegion)((AccessorStructureFeatureManager)structureFeatureManager).getLevelAccessor();
      CommonEvents.NOISE_GENERATION.invoke(this, genRegion, executor, blender, structureFeatureManager, (ProtoChunk)chunk);
      return CompletableFuture.completedFuture(chunk);
   }

   public void buildSurface(WorldGenRegion genRegion, StructureFeatureManager structureFeatureManager, ChunkAccess chunk) {
      CommonEvents.SURFACE_GENERATION.invoke(this, genRegion, structureFeatureManager, (ProtoChunk)chunk);
   }

   public void applyCarvers(
      WorldGenRegion genRegion, long seed, BiomeManager biomeManager, StructureFeatureManager structureFeatureManager, ChunkAccess chunk, Carving step
   ) {
      CommonEvents.CARVERS_GENERATION.invoke(this, genRegion, seed, biomeManager, structureFeatureManager, (ProtoChunk)chunk, step);
   }

   public void spawnOriginalMobs(WorldGenRegion genRegion) {
      CommonEvents.SPAWN_GENERATION.invoke(this, genRegion);
   }

   public int getSeaLevel() {
      return 0;
   }

   public int getGenDepth() {
      return Integer.MAX_VALUE;
   }

   public int getMinY() {
      return Integer.MIN_VALUE;
   }

   public int getBaseHeight(int pX, int pZ, Types type, LevelHeightAccessor world) {
      return world.getMinBuildHeight();
   }

   public NoiseColumn getBaseColumn(int pX, int pZ, LevelHeightAccessor world) {
      return new NoiseColumn(world.getMinBuildHeight(), new BlockState[0]);
   }

   public void addDebugScreenInfo(List<String> lines, BlockPos pos) {
   }
}
