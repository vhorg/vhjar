package iskallia.vault.core.event.common;

import iskallia.vault.core.event.Event;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.biome.BiomeManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.GenerationStep.Carving;

public class CarversGenerationEvent extends Event<CarversGenerationEvent, CarversGenerationEvent.Data> {
   public CarversGenerationEvent() {
   }

   protected CarversGenerationEvent(CarversGenerationEvent parent) {
      super(parent);
   }

   public CarversGenerationEvent createChild() {
      return new CarversGenerationEvent(this);
   }

   public CarversGenerationEvent.Data invoke(
      ChunkGenerator generator,
      WorldGenRegion genRegion,
      long seed,
      BiomeManager biomeManager,
      StructureFeatureManager structureFeatureManager,
      ProtoChunk chunk,
      Carving step
   ) {
      return this.invoke(new CarversGenerationEvent.Data(generator, genRegion, seed, biomeManager, structureFeatureManager, chunk, step));
   }

   public CarversGenerationEvent in(ServerLevel world) {
      return this.filter(data -> data.getGenRegion().getLevel() == world);
   }

   public static class Data {
      private final ChunkGenerator generator;
      private final WorldGenRegion genRegion;
      private final long seed;
      private final BiomeManager biomeManager;
      private final StructureFeatureManager structureFeatureManager;
      private final ProtoChunk chunk;
      private final Carving step;

      public Data(
         ChunkGenerator generator,
         WorldGenRegion genRegion,
         long seed,
         BiomeManager biomeManager,
         StructureFeatureManager structureFeatureManager,
         ProtoChunk chunk,
         Carving step
      ) {
         this.generator = generator;
         this.genRegion = genRegion;
         this.seed = seed;
         this.biomeManager = biomeManager;
         this.structureFeatureManager = structureFeatureManager;
         this.chunk = chunk;
         this.step = step;
      }

      public ChunkGenerator getGenerator() {
         return this.generator;
      }

      public WorldGenRegion getGenRegion() {
         return this.genRegion;
      }

      public long getSeed() {
         return this.seed;
      }

      public BiomeManager getBiomeManager() {
         return this.biomeManager;
      }

      public StructureFeatureManager getStructureFeatureManager() {
         return this.structureFeatureManager;
      }

      public ProtoChunk getChunk() {
         return this.chunk;
      }

      public Carving getStep() {
         return this.step;
      }
   }
}
