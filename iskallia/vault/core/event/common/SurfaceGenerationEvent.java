package iskallia.vault.core.event.common;

import iskallia.vault.core.event.Event;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.ProtoChunk;

public class SurfaceGenerationEvent extends Event<SurfaceGenerationEvent, SurfaceGenerationEvent.Data> {
   public SurfaceGenerationEvent() {
   }

   protected SurfaceGenerationEvent(SurfaceGenerationEvent parent) {
      super(parent);
   }

   public SurfaceGenerationEvent createChild() {
      return new SurfaceGenerationEvent(this);
   }

   public SurfaceGenerationEvent.Data invoke(
      ChunkGenerator generator, WorldGenRegion genRegion, StructureFeatureManager structureFeatureManager, ProtoChunk chunk
   ) {
      return this.invoke(new SurfaceGenerationEvent.Data(generator, genRegion, structureFeatureManager, chunk));
   }

   public SurfaceGenerationEvent in(ServerLevel world) {
      return this.filter(data -> data.getGenRegion().getLevel() == world);
   }

   public static class Data {
      private final ChunkGenerator generator;
      private final WorldGenRegion genRegion;
      private final StructureFeatureManager structureFeatureManager;
      private final ProtoChunk chunk;

      public Data(ChunkGenerator generator, WorldGenRegion genRegion, StructureFeatureManager structureFeatureManager, ProtoChunk chunk) {
         this.generator = generator;
         this.genRegion = genRegion;
         this.structureFeatureManager = structureFeatureManager;
         this.chunk = chunk;
      }

      public ChunkGenerator getGenerator() {
         return this.generator;
      }

      public WorldGenRegion getGenRegion() {
         return this.genRegion;
      }

      public StructureFeatureManager getStructureFeatureManager() {
         return this.structureFeatureManager;
      }

      public ProtoChunk getChunk() {
         return this.chunk;
      }
   }
}
