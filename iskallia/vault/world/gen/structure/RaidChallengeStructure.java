package iskallia.vault.world.gen.structure;

import iskallia.vault.VaultMod;
import iskallia.vault.init.ModStructures;
import iskallia.vault.world.gen.VaultJigsawGenerator;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.PlainVillagePools;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.GenerationStep.Decoration;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier.Context;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class RaidChallengeStructure extends StructureFeature<JigsawConfiguration> {
   public static final ResourceKey<StructureTemplatePool> POOL = ResourceKey.create(Registry.TEMPLATE_POOL_REGISTRY, VaultMod.id("raid/starts"));

   public RaidChallengeStructure() {
      super(JigsawConfiguration.CODEC, context -> Optional.empty());
   }

   public Decoration step() {
      return Decoration.UNDERGROUND_STRUCTURES;
   }

   public RaidChallengeStructure.Feature configured() {
      return new RaidChallengeStructure.Feature(
         this, new JigsawConfiguration(PlainVillagePools.START, 1), BuiltinRegistries.BIOME.getOrCreateTag(ModStructures.EMPTY), false, new HashMap<>()
      );
   }

   public static class Feature extends ConfiguredStructureFeature<JigsawConfiguration, RaidChallengeStructure> implements IRegistryIdentifiable {
      private ResourceLocation id;

      public Feature(
         RaidChallengeStructure structure,
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

      public StructureStart generate(
         VaultJigsawGenerator jigsaw,
         RegistryAccess registry,
         ChunkGenerator gen,
         StructureManager manager,
         int references,
         long worldSeed,
         LevelHeightAccessor height
      ) {
         JigsawConfiguration config = new JigsawConfiguration(
            registry.registryOrThrow(Registry.TEMPLATE_POOL_REGISTRY).getHolderOrThrow(RaidChallengeStructure.POOL), 1
         );
         ChunkPos chunkPos = new ChunkPos(jigsaw.getStartPos().getX() >> 4, jigsaw.getStartPos().getZ() >> 4);
         Context<JigsawConfiguration> context = new Context(gen, gen.getBiomeSource(), worldSeed, chunkPos, config, height, biome -> true, manager, registry);
         List<StructurePiece> pieceList = new ArrayList<>();
         jigsaw.generate(registry, context, PoolElementStructurePiece::new, gen, manager, pieceList, new Random(), false, false);
         StructureStart start = new StructureStart(this, chunkPos, references, new PiecesContainer(pieceList));
         return start.isValid() ? start : StructureStart.INVALID_START;
      }
   }
}
