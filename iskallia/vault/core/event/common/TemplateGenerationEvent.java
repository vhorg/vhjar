package iskallia.vault.core.event.common;

import iskallia.vault.core.event.Event;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.RegionPos;
import iskallia.vault.core.world.template.configured.ConfiguredTemplate;
import net.minecraft.server.level.WorldGenRegion;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelWriter;
import net.minecraft.world.level.ServerLevelAccessor;

public class TemplateGenerationEvent extends Event<TemplateGenerationEvent, TemplateGenerationEvent.Data> {
   public TemplateGenerationEvent() {
   }

   protected TemplateGenerationEvent(TemplateGenerationEvent parent) {
      super(parent);
   }

   public TemplateGenerationEvent createChild() {
      return new TemplateGenerationEvent(this);
   }

   public TemplateGenerationEvent.Data invoke(
      ServerLevelAccessor world, ConfiguredTemplate template, RegionPos region, ChunkPos chunkPos, RandomSource random, TemplateGenerationEvent.Phase phase
   ) {
      return this.invoke(new TemplateGenerationEvent.Data(world, template, region, chunkPos, random, phase));
   }

   public TemplateGenerationEvent at(TemplateGenerationEvent.Phase phase) {
      return this.filter(data -> data.phase == phase);
   }

   public TemplateGenerationEvent in(LevelWriter world) {
      return this.filter(data -> data.getWorld() == world || data.getWorld() instanceof WorldGenRegion genRegion && genRegion.getLevel() == world);
   }

   public static class Data {
      private final ServerLevelAccessor world;
      private ConfiguredTemplate template;
      private final RegionPos region;
      private final ChunkPos chunkPos;
      private final RandomSource random;
      private final TemplateGenerationEvent.Phase phase;

      public Data(
         ServerLevelAccessor world, ConfiguredTemplate template, RegionPos region, ChunkPos chunkPos, RandomSource random, TemplateGenerationEvent.Phase phase
      ) {
         this.world = world;
         this.template = template;
         this.region = region;
         this.chunkPos = chunkPos;
         this.random = random;
         this.phase = phase;
      }

      public ServerLevelAccessor getWorld() {
         return this.world;
      }

      public ConfiguredTemplate getTemplate() {
         return this.template;
      }

      public RegionPos getRegion() {
         return this.region;
      }

      public ChunkPos getChunkPos() {
         return this.chunkPos;
      }

      public RandomSource getRandom() {
         return this.random;
      }

      public TemplateGenerationEvent.Phase getPhase() {
         return this.phase;
      }

      public void setTemplate(ConfiguredTemplate template) {
         this.template = template;
      }
   }

   public static enum Phase {
      PRE,
      POST;
   }
}
