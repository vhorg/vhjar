package iskallia.vault.core.event.common;

import iskallia.vault.core.event.Event;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.chunk.ChunkGenerator;

public class SpawnGenerationEvent extends Event<SpawnGenerationEvent, SpawnGenerationEvent.Data> {
   public SpawnGenerationEvent() {
   }

   protected SpawnGenerationEvent(SpawnGenerationEvent parent) {
      super(parent);
   }

   public SpawnGenerationEvent createChild() {
      return new SpawnGenerationEvent(this);
   }

   public SpawnGenerationEvent.Data invoke(ChunkGenerator generator, WorldGenRegion genRegion) {
      return this.invoke(new SpawnGenerationEvent.Data(generator, genRegion));
   }

   public SpawnGenerationEvent in(ServerLevel world) {
      return this.filter(data -> data.getGenRegion().getLevel() == world);
   }

   public static class Data {
      private final ChunkGenerator generator;
      private final WorldGenRegion genRegion;

      public Data(ChunkGenerator generator, WorldGenRegion genRegion) {
         this.generator = generator;
         this.genRegion = genRegion;
      }

      public ChunkGenerator getGenerator() {
         return this.generator;
      }

      public WorldGenRegion getGenRegion() {
         return this.genRegion;
      }
   }
}
