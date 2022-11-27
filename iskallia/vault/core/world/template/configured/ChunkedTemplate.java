package iskallia.vault.core.world.template.configured;

import iskallia.vault.core.util.iterator.MappingIterator;
import iskallia.vault.core.world.data.PartialEntity;
import iskallia.vault.core.world.data.PartialTile;
import iskallia.vault.core.world.processor.entity.EntityProcessor;
import iskallia.vault.core.world.processor.tile.TileProcessor;
import iskallia.vault.core.world.template.PlacementSettings;
import iskallia.vault.core.world.template.StaticTemplate;
import iskallia.vault.core.world.template.Template;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.ServerLevelAccessor;

public class ChunkedTemplate extends ConfiguredTemplate {
   private final Map<ChunkPos, StaticTemplate> cache = new HashMap<>();
   private boolean locked;

   public ChunkedTemplate(Template parent, PlacementSettings settings) {
      super(parent, settings);
   }

   private void onTile(PartialTile tile) {
      StaticTemplate child = this.cache.computeIfAbsent(new ChunkPos(tile.getPos()), pos -> new StaticTemplate(new ArrayList<>(8192), new ArrayList<>(8192)));
      ((List)child.getTiles()).add(tile);
   }

   private void onEntity(PartialEntity entity) {
      StaticTemplate child = this.cache
         .computeIfAbsent(new ChunkPos(entity.getBlockPos()), pos -> new StaticTemplate(new ArrayList<>(8192), new ArrayList<>(8192)));
      ((List)child.getEntities()).add(entity);
   }

   @Override
   public void place(ServerLevelAccessor world, ChunkPos pos) {
      if (!this.locked) {
         new ChunkedTemplate.Wrapping(this.parent, pos).place(world, this.settings);
         this.locked = true;
         this.cache.values().forEach(t -> {
            ((ArrayList)t.getTiles()).trimToSize();
            ((ArrayList)t.getEntities()).trimToSize();
         });
      } else {
         StaticTemplate child = this.cache.get(pos);
         if (child != null) {
            child.place(world, this.settings);
         }
      }
   }

   protected class Wrapping extends Template {
      private final Template parent;
      private final TileProcessor tileBound;
      private final EntityProcessor entityBound;

      public Wrapping(Template parent, ChunkPos pos) {
         this.parent = parent;
         this.tileBound = TileProcessor.bound(pos.x * 16, Integer.MIN_VALUE, pos.z * 16, pos.x * 16 + 15, Integer.MAX_VALUE, pos.z * 16 + 15);
         this.entityBound = EntityProcessor.bound(pos.x * 16, Integer.MIN_VALUE, pos.z * 16, pos.x * 16 + 15, Integer.MAX_VALUE, pos.z * 16 + 15);
      }

      @Override
      public Iterator<ResourceLocation> getTags() {
         return this.parent.getTags();
      }

      @Override
      public void addTag(ResourceLocation tag) {
         this.parent.addTag(tag);
      }

      @Override
      public boolean hasTag(ResourceLocation tag) {
         return this.parent.hasTag(tag);
      }

      @Override
      public Iterator<PartialTile> getTiles(Predicate<PartialTile> filter, PlacementSettings settings) {
         return new MappingIterator<>(this.parent.getTiles(filter, settings), tile -> {
            ChunkedTemplate.this.onTile(tile);
            return this.tileBound.process(tile, settings.getProcessorContext());
         });
      }

      @Override
      public Iterator<PartialEntity> getEntities(Predicate<PartialEntity> filter, PlacementSettings settings) {
         return new MappingIterator<>(this.parent.getEntities(filter, settings), tile -> {
            ChunkedTemplate.this.onEntity(tile);
            return this.entityBound.process(tile, settings.getProcessorContext());
         });
      }
   }
}
