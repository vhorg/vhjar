package iskallia.vault.core.world.template;

import iskallia.vault.core.world.processor.Palette;
import iskallia.vault.core.world.processor.Processor;
import iskallia.vault.core.world.processor.ProcessorContext;
import iskallia.vault.core.world.processor.entity.EntityProcessor;
import iskallia.vault.core.world.processor.tile.TileProcessor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.minecraft.world.entity.MobSpawnType;

public class PlacementSettings {
   public static final PlacementSettings EMPTY = new PlacementSettings(new ProcessorContext());
   protected int flags;
   protected boolean keepFluids = false;
   protected boolean ignoreTiles = false;
   protected boolean ignoreEntities = false;
   protected boolean finalizeEntities = true;
   protected MobSpawnType mobSpawnType = MobSpawnType.STRUCTURE;
   protected List<TileProcessor> tileProcessors = new ArrayList<>();
   protected List<EntityProcessor> entityProcessors = new ArrayList<>();
   protected ProcessorContext processorContext;

   public PlacementSettings(ProcessorContext context) {
      this.processorContext = context;
   }

   public PlacementSettings copy() {
      PlacementSettings copy = new PlacementSettings(this.processorContext);
      copy.flags = this.flags;
      copy.keepFluids = this.keepFluids;
      copy.ignoreTiles = this.ignoreTiles;
      copy.ignoreEntities = this.ignoreEntities;
      copy.finalizeEntities = this.finalizeEntities;
      copy.mobSpawnType = this.mobSpawnType;
      copy.tileProcessors.addAll(this.tileProcessors);
      copy.entityProcessors.addAll(this.entityProcessors);
      return copy;
   }

   public int getFlags() {
      return this.flags;
   }

   public boolean doKeepFluids() {
      return this.keepFluids;
   }

   public boolean doIgnoreTiles() {
      return this.ignoreTiles;
   }

   public boolean doIgnoreEntities() {
      return this.ignoreEntities;
   }

   public boolean doFinalizeEntities() {
      return this.finalizeEntities;
   }

   public MobSpawnType getMobSpawnType() {
      return this.mobSpawnType;
   }

   public List<TileProcessor> getTileProcessors() {
      return this.tileProcessors;
   }

   public List<EntityProcessor> getEntityProcessors() {
      return this.entityProcessors;
   }

   public ProcessorContext getProcessorContext() {
      return this.processorContext;
   }

   public PlacementSettings setFlags(int flags) {
      this.flags = flags;
      return this;
   }

   public PlacementSettings setKeepFluids(boolean keepFluids) {
      this.keepFluids = keepFluids;
      return this;
   }

   public PlacementSettings setIgnoreTiles(boolean ignoreTiles) {
      this.ignoreTiles = ignoreTiles;
      return this;
   }

   public PlacementSettings setIgnoreEntities(boolean ignoreEntities) {
      this.ignoreEntities = ignoreEntities;
      return this;
   }

   public PlacementSettings setFinalizeEntities(boolean finalizeEntities) {
      this.finalizeEntities = finalizeEntities;
      return this;
   }

   public PlacementSettings setMobSpawnType(MobSpawnType mobSpawnType) {
      this.mobSpawnType = mobSpawnType;
      return this;
   }

   public <T extends Processor<?>> PlacementSettings addProcessor(T processor) {
      if (processor instanceof TileProcessor) {
         this.tileProcessors.add((TileProcessor)processor);
      } else if (processor instanceof EntityProcessor) {
         this.entityProcessors.add((EntityProcessor)processor);
      }

      return this;
   }

   public <T extends Processor<?>> PlacementSettings addProcessors(T... processors) {
      for (Processor<?> processor : processors) {
         this.addProcessor(processor);
      }

      return this;
   }

   public <T extends Processor<?>> PlacementSettings addProcessors(Iterable<T> processors) {
      processors.forEach(this::addProcessor);
      return this;
   }

   public <T extends Processor<?>> PlacementSettings addProcessors(Iterator<T> processors) {
      processors.forEachRemaining(this::addProcessor);
      return this;
   }

   public PlacementSettings addProcessor(Palette palette) {
      this.addProcessors(palette.getTileProcessors());
      this.addProcessors(palette.getEntityProcessors());
      return this;
   }

   public PlacementSettings setProcessorContext(ProcessorContext processorContext) {
      this.processorContext = processorContext;
      return this;
   }
}
