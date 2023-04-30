package iskallia.vault.core;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import iskallia.vault.VaultMod;
import iskallia.vault.block.PlaceholderBlock;
import iskallia.vault.core.data.key.TemplatePoolKey;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.vault.VaultRegistry;
import iskallia.vault.core.world.data.tile.PartialBlockState;
import iskallia.vault.core.world.processor.tile.TileProcessor;
import iskallia.vault.core.world.template.JigsawTemplate;
import iskallia.vault.core.world.template.PlacementSettings;
import iskallia.vault.core.world.template.configured.ConfiguredTemplate;
import iskallia.vault.init.ModBlocks;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import net.minecraft.CrashReport;
import net.minecraft.ReportedException;
import net.minecraft.SharedConstants;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.SectionPos;
import net.minecraft.resources.RegistryOps;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.biome.BiomeSource.StepFeatureData;
import net.minecraft.world.level.biome.Climate.Sampler;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.NoiseChunk;
import net.minecraft.world.level.levelgen.NoiseGeneratorSettings;
import net.minecraft.world.level.levelgen.NoiseRouter;
import net.minecraft.world.level.levelgen.RandomSupport;
import net.minecraft.world.level.levelgen.SurfaceSystem;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.XoroshiroRandomSource;
import net.minecraft.world.level.levelgen.GenerationStep.Carving;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.Heightmap.Types;
import net.minecraft.world.level.levelgen.blending.Blender;
import net.minecraft.world.level.levelgen.carver.CarvingContext;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureSet;
import net.minecraft.world.level.levelgen.synth.NormalNoise.NoiseParameters;

public class SkyVaultsChunkGenerator extends ChunkGenerator {
   public static final Codec<SkyVaultsChunkGenerator> CODEC = RecordCodecBuilder.create(
      builder -> commonCodec(builder)
         .and(
            builder.group(
               RegistryCodecs.homogeneousList(Registry.STRUCTURE_SET_REGISTRY).optionalFieldOf("structure_overrides").forGetter(gen -> gen.structureOverrides),
               RegistryOps.retrieveRegistry(Registry.NOISE_REGISTRY).forGetter(gen -> gen.noises),
               BiomeSource.CODEC.fieldOf("biome_source").forGetter(gen -> gen.biomeSource),
               Codec.LONG.fieldOf("seed").stable().forGetter(gen -> gen.seed),
               NoiseGeneratorSettings.CODEC.fieldOf("settings").forGetter(gen -> gen.settings)
            )
         )
         .apply(builder, builder.stable(SkyVaultsChunkGenerator::new))
   );
   private final Registry<NoiseParameters> noises;
   private final long seed;
   private final Holder<NoiseGeneratorSettings> settings;
   private final Sampler sampler;
   private final SurfaceSystem surfaceSystem;

   public SkyVaultsChunkGenerator(
      Registry<StructureSet> structures,
      HolderSet<StructureSet> overrides,
      Registry<NoiseParameters> noises,
      BiomeSource biomeSource,
      long seed,
      Holder<NoiseGeneratorSettings> settings
   ) {
      this(structures, Optional.ofNullable(overrides), noises, biomeSource, biomeSource, seed, settings);
   }

   private SkyVaultsChunkGenerator(
      Registry<StructureSet> structures,
      Optional<HolderSet<StructureSet>> overrides,
      Registry<NoiseParameters> noises,
      BiomeSource biomeSource,
      long seed,
      Holder<NoiseGeneratorSettings> settings
   ) {
      this(structures, overrides, noises, biomeSource, biomeSource, seed, settings);
   }

   private SkyVaultsChunkGenerator(
      Registry<StructureSet> structures,
      Optional<HolderSet<StructureSet>> overrides,
      Registry<NoiseParameters> noises,
      BiomeSource biomeSource1,
      BiomeSource biomeSource2,
      long seed,
      Holder<NoiseGeneratorSettings> settings
   ) {
      super(structures, overrides, biomeSource1, biomeSource2, seed);
      this.noises = noises;
      this.seed = seed;
      this.settings = settings;
      NoiseGeneratorSettings noiseSettings = (NoiseGeneratorSettings)this.settings.value();
      BlockState defaultBlock = noiseSettings.defaultBlock();
      NoiseRouter router = noiseSettings.createNoiseRouter(noises, seed);
      this.sampler = new Sampler(
         router.temperature(), router.humidity(), router.continents(), router.erosion(), router.depth(), router.ridges(), router.spawnTarget()
      );
      this.surfaceSystem = new SurfaceSystem(noises, defaultBlock, noiseSettings.seaLevel(), seed, noiseSettings.getRandomSource());
   }

   public Sampler climateSampler() {
      return this.sampler;
   }

   protected Codec<? extends ChunkGenerator> codec() {
      return CODEC;
   }

   public ChunkGenerator withSeed(long seed) {
      return new SkyVaultsChunkGenerator(
         this.structureSets, (HolderSet<StructureSet>)this.structureOverrides.orElse(null), this.noises, this.biomeSource.withSeed(seed), seed, this.settings
      );
   }

   public int getBaseHeight(int pX, int pZ, Types type, LevelHeightAccessor world) {
      return world.getMinBuildHeight();
   }

   public NoiseColumn getBaseColumn(int pX, int pZ, LevelHeightAccessor world) {
      return new NoiseColumn(world.getMinBuildHeight(), new BlockState[0]);
   }

   public void addDebugScreenInfo(List<String> p_208054_, BlockPos p_208055_) {
   }

   public CompletableFuture<ChunkAccess> fillFromNoise(Executor executor, Blender blender, StructureFeatureManager structureFeatureManager, ChunkAccess chunk) {
      return CompletableFuture.completedFuture(chunk);
   }

   public void buildSurface(WorldGenRegion genRegion, StructureFeatureManager structureFeatureManager, ChunkAccess chunk) {
      ServerLevel world = genRegion.getLevel();
      if (world.dimension() == Level.OVERWORLD) {
         Version version = Version.latest();
         RandomSource random = JavaRandom.ofInternal(world.getSeed());
         TemplatePoolKey pool = VaultRegistry.TEMPLATE_POOL.getKey(VaultMod.id("skyblock/island"));
         pool.get(version).getRandomFlat(version, random).ifPresent(entry -> {
            PlacementSettings settings = new PlacementSettings().setFlags(3);
            settings.getProcessorContext().random = random;
            Mirror mirror = random.nextBoolean() ? Mirror.NONE : Mirror.FRONT_BACK;
            Rotation rotation = new Rotation[]{Rotation.NONE, Rotation.COUNTERCLOCKWISE_90, Rotation.CLOCKWISE_90, Rotation.CLOCKWISE_180}[random.nextInt(4)];
            settings.addProcessor(TileProcessor.rotate(rotation, 0, 0, true));
            settings.addProcessor(TileProcessor.mirror(mirror, 0, true));
            settings.addProcessor(TileProcessor.translate(0, 128, 0));
            settings.addProcessor(TileProcessor.bound(chunk.getPos(), genRegion.getMinBuildHeight(), genRegion.getMaxBuildHeight()));
            settings.addProcessor(TileProcessor.of((tile, context) -> {
               if (tile.getState().is(ModBlocks.PLACEHOLDER)) {
                  Direction facing = tile.getState().get(PlaceholderBlock.FACING);
                  world.setDefaultSpawnPos(tile.getPos(), facing.toYRot());
                  tile.setState(PartialBlockState.of(Blocks.AIR.defaultBlockState()));

                  for (ServerPlayer player : world.getPlayers(playerx -> true)) {
                     player.setRespawnPosition(Level.OVERWORLD, tile.getPos(), facing.toYRot(), true, false);
                  }
               }

               return tile;
            }));
            ConfiguredTemplate template = JigsawTemplate.of(version, entry, 10, random).configure(ConfiguredTemplate::new, settings);
            template.place(genRegion, chunk.getPos());
         });
      }
   }

   public void applyCarvers(
      WorldGenRegion genRegion, long seed, BiomeManager biomeManager, StructureFeatureManager structureFeatureManager, ChunkAccess chunk, Carving step
   ) {
   }

   public void spawnOriginalMobs(WorldGenRegion world) {
      if (!((NoiseGeneratorSettings)this.settings.value()).disableMobGeneration()) {
         ChunkPos chunkpos = world.getCenter();
         Holder<Biome> holder = world.getBiome(chunkpos.getWorldPosition().atY(world.getMaxBuildHeight() - 1));
         WorldgenRandom worldgenrandom = new WorldgenRandom(new LegacyRandomSource(RandomSupport.seedUniquifier()));
         worldgenrandom.setDecorationSeed(world.getSeed(), chunkpos.getMinBlockX(), chunkpos.getMinBlockZ());
         NaturalSpawner.spawnMobsForChunkGeneration(world, holder, chunkpos, worldgenrandom);
      }
   }

   public int getGenDepth() {
      return ((NoiseGeneratorSettings)this.settings.value()).noiseSettings().height();
   }

   public int getSeaLevel() {
      return ((NoiseGeneratorSettings)this.settings.value()).seaLevel();
   }

   public int getMinY() {
      return ((NoiseGeneratorSettings)this.settings.value()).noiseSettings().minY();
   }

   @Deprecated
   public Optional<BlockState> topMaterial(
      CarvingContext p_188669_, Function<BlockPos, Holder<Biome>> p_188670_, ChunkAccess p_188671_, NoiseChunk p_188672_, BlockPos p_188673_, boolean p_188674_
   ) {
      return this.surfaceSystem
         .topMaterial(((NoiseGeneratorSettings)this.settings.value()).surfaceRule(), p_188669_, p_188670_, p_188671_, p_188672_, p_188673_, p_188674_);
   }

   public void applyBiomeDecoration(WorldGenLevel world, ChunkAccess pChunk, StructureFeatureManager structureFeatureManager) {
      ChunkPos chunkpos = pChunk.getPos();
      if (structureFeatureManager.shouldGenerateFeatures()) {
         if (!SharedConstants.debugVoidTerrain(chunkpos)) {
            SectionPos sectionpos = SectionPos.of(chunkpos, world.getMinSection());
            BlockPos blockpos = sectionpos.origin();
            Registry<ConfiguredStructureFeature<?, ?>> registry = world.registryAccess().registryOrThrow(Registry.CONFIGURED_STRUCTURE_FEATURE_REGISTRY);
            Map<Integer, List<ConfiguredStructureFeature<?, ?>>> map = registry.stream()
               .collect(Collectors.groupingBy(p_211653_ -> p_211653_.feature.step().ordinal()));
            List<StepFeatureData> list = this.biomeSource.featuresPerStep();
            WorldgenRandom random = new WorldgenRandom(new XoroshiroRandomSource(RandomSupport.seedUniquifier()));
            long populationSeed = random.setDecorationSeed(world.getSeed(), blockpos.getX(), blockpos.getZ());
            int j = list.size();

            try {
               int steps = Math.max(Decoration.values().length, j);

               for (int step = 0; step < steps; step++) {
                  int index = 0;

                  for (ConfiguredStructureFeature<?, ?> feature : map.getOrDefault(step, Collections.emptyList())) {
                     random.setFeatureSeed(populationSeed, index, step);
                     Supplier<String> supplier = () -> registry.getResourceKey(feature).map(Object::toString).orElseGet(feature::toString);

                     try {
                        world.setCurrentlyGenerating(supplier);
                        structureFeatureManager.startsForFeature(sectionpos, feature)
                           .forEach(p_211647_ -> p_211647_.placeInChunk(world, structureFeatureManager, this, random, getWritableArea(pChunk), chunkpos));
                     } catch (Exception var22) {
                        CrashReport crashreport1 = CrashReport.forThrowable(var22, "Feature placement");
                        crashreport1.addCategory("Feature").setDetail("Description", supplier::get);
                        throw new ReportedException(crashreport1);
                     }

                     index++;
                  }
               }

               world.setCurrentlyGenerating(null);
            } catch (Exception var23) {
               CrashReport crashreport = CrashReport.forThrowable(var23, "Biome decoration");
               crashreport.addCategory("Generation").setDetail("CenterX", chunkpos.x).setDetail("CenterZ", chunkpos.z).setDetail("Seed", populationSeed);
               throw new ReportedException(crashreport);
            }
         }
      }
   }

   private static BoundingBox getWritableArea(ChunkAccess chunkAccess) {
      ChunkPos chunkpos = chunkAccess.getPos();
      int i = chunkpos.getMinBlockX();
      int j = chunkpos.getMinBlockZ();
      LevelHeightAccessor levelheightaccessor = chunkAccess.getHeightAccessorForGeneration();
      int k = levelheightaccessor.getMinBuildHeight() + 1;
      int l = levelheightaccessor.getMaxBuildHeight() - 1;
      return new BoundingBox(i, k, j, i + 15, l, j + 15);
   }

   public static boolean matches(Level world) {
      MinecraftServer server = world.getServer();
      return server == null ? false : server.overworld().getChunkSource().getGenerator() instanceof SkyVaultsChunkGenerator;
   }
}
