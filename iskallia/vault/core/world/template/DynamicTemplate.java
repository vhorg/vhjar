package iskallia.vault.core.world.template;

import iskallia.vault.core.util.iterator.MappingIterator;
import iskallia.vault.core.world.data.EntityPredicate;
import iskallia.vault.core.world.data.PartialEntity;
import iskallia.vault.core.world.data.tile.PartialTile;
import iskallia.vault.core.world.data.tile.TilePredicate;
import iskallia.vault.core.world.processor.Processor;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import net.minecraft.resources.ResourceLocation;

public class DynamicTemplate extends Template {
   private final List<PartialTile> tiles = new ArrayList<>();
   private final List<PartialEntity> entities = new ArrayList<>();

   public void add(PartialTile tile) {
      this.tiles.add(tile);
   }

   @Override
   public Iterator<ResourceLocation> getTags() {
      return Collections.emptyIterator();
   }

   @Override
   public void addTag(ResourceLocation tag) {
   }

   @Override
   public boolean hasTag(ResourceLocation tag) {
      return false;
   }

   @Override
   public Iterator<PartialTile> getTiles(TilePredicate filter, PlacementSettings settings) {
      return new MappingIterator<>(this.tiles.iterator(), tile -> {
         if (!filter.test(tile)) {
            return null;
         } else {
            tile = tile.copy();

            for (Processor<PartialTile> processor : settings.getTileProcessors()) {
               if (tile == null || !filter.test(tile)) {
                  tile = null;
                  break;
               }

               tile = processor.process(tile, settings.getProcessorContext());
            }

            return tile;
         }
      });
   }

   @Override
   public Iterator<PartialEntity> getEntities(EntityPredicate filter, PlacementSettings settings) {
      return new MappingIterator<>(this.entities.iterator(), entity -> {
         if (!filter.test(entity)) {
            return null;
         } else {
            entity = entity.copy();

            for (Processor<PartialEntity> processor : settings.getEntityProcessors()) {
               if (entity == null || !filter.test(entity)) {
                  entity = null;
                  break;
               }

               entity = processor.process(entity, settings.getProcessorContext());
            }

            return entity;
         }
      });
   }
}
