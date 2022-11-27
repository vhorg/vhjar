package iskallia.vault.core.event.common;

import iskallia.vault.core.event.Event;
import java.util.concurrent.Executor;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ProtoChunk;
import net.minecraft.world.level.levelgen.blending.Blender;

public class NoiseGenerationEvent extends Event<NoiseGenerationEvent, NoiseGenerationEvent.Data> {
   public NoiseGenerationEvent() {
   }

   protected NoiseGenerationEvent(NoiseGenerationEvent parent) {
      super(parent);
   }

   public NoiseGenerationEvent createChild() {
      return new NoiseGenerationEvent(this);
   }

   public NoiseGenerationEvent.Data invoke(
      ChunkGenerator generator, WorldGenRegion genRegion, Executor executor, Blender blender, StructureFeatureManager structureFeatureManager, ProtoChunk chunk
   ) {
      return this.invoke(new NoiseGenerationEvent.Data(generator, genRegion, executor, blender, structureFeatureManager, chunk));
   }

   public NoiseGenerationEvent in(ServerLevel world) {
      return this.filter(data -> data.getGenRegion().getLevel() == world);
   }

   public static class Data {
      private final ChunkGenerator generator;
      private final WorldGenRegion genRegion;
      private final Executor executor;
      private final Blender blender;
      private final StructureFeatureManager structureFeatureManager;
      private final ProtoChunk chunk;

      public Data(
         ChunkGenerator generator,
         WorldGenRegion genRegion,
         Executor executor,
         Blender blender,
         StructureFeatureManager structureFeatureManager,
         ProtoChunk chunk
      ) {
         this.generator = generator;
         this.genRegion = genRegion;
         this.executor = executor;
         this.blender = blender;
         this.structureFeatureManager = structureFeatureManager;
         this.chunk = chunk;
      }

      public ChunkGenerator getGenerator() {
         return this.generator;
      }

      public WorldGenRegion getGenRegion() {
         return this.genRegion;
      }

      public Executor getExecutor() {
         return this.executor;
      }

      public Blender getBlender() {
         return this.blender;
      }

      public StructureFeatureManager getStructureFeatureManager() {
         return this.structureFeatureManager;
      }

      public ProtoChunk getChunk() {
         return this.chunk;
      }
   }
}
