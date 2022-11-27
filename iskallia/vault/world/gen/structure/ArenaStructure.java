package iskallia.vault.world.gen.structure;

import com.google.common.collect.ImmutableList;
import com.mojang.datafixers.util.Pair;
import iskallia.vault.VaultMod;
import iskallia.vault.init.ModStructures;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.data.worldgen.ProcessorLists;
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
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool.Projection;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;

public class ArenaStructure extends StructureFeature<JigsawConfiguration> {
   public static final int START_Y = 32;

   public ArenaStructure() {
      super(JigsawConfiguration.CODEC, context -> Optional.empty());
   }

   public Decoration step() {
      return Decoration.UNDERGROUND_STRUCTURES;
   }

   public ArenaStructure.Feature configured(JigsawConfiguration config) {
      return new ArenaStructure.Feature(this, config, BuiltinRegistries.BIOME.getOrCreateTag(ModStructures.EMPTY), false, new HashMap<>());
   }

   public static class Feature extends ConfiguredStructureFeature<JigsawConfiguration, ArenaStructure> implements IRegistryIdentifiable {
      private ResourceLocation id;

      public Feature(
         ArenaStructure structure,
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
         ChunkPos chunkPos, RegistryAccess registry, ChunkGenerator gen, StructureManager manager, int references, long worldSeed, LevelHeightAccessor height
      ) {
         BlockPos blockPos = new BlockPos(chunkPos.x * 16, 32, chunkPos.z * 16);
         List<StructurePiece> pieceList = new ArrayList<>();
         JigsawGeneratorLegacy.addPieces(
            registry, (JigsawConfiguration)this.config, PoolElementStructurePiece::new, gen, manager, blockPos, pieceList, new Random(), false, false, height
         );
         StructureStart start = new StructureStart(this, chunkPos, references, new PiecesContainer(pieceList));
         return start.isValid() ? start : StructureStart.INVALID_START;
      }
   }

   public static class Pools {
      public static Holder<StructureTemplatePool> START = net.minecraft.data.worldgen.Pools.register(
         new StructureTemplatePool(
            VaultMod.id("arena/starts"),
            new ResourceLocation("empty"),
            ImmutableList.of(Pair.of(StructurePoolElement.single(VaultMod.sId("arena/arena1/p_p"), ProcessorLists.EMPTY), 1)),
            Projection.RIGID
         )
      );

      public static void init() {
      }
   }
}
