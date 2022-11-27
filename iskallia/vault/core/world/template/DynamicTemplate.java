package iskallia.vault.core.world.template;

import iskallia.vault.core.util.iterator.EmptyIterator;
import iskallia.vault.core.util.iterator.MappingIterator;
import iskallia.vault.core.world.data.PartialEntity;
import iskallia.vault.core.world.data.PartialTile;
import iskallia.vault.core.world.processor.Processor;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.resources.ResourceLocation;

public class DynamicTemplate extends Template {
   private final List<PartialTile> tiles = new ArrayList<>();
   private final List<PartialEntity> entities = new ArrayList<>();

   public void add(PartialTile tile) {
      this.tiles.add(tile);
   }

   @Override
   public Iterator<ResourceLocation> getTags() {
      return new EmptyIterator<>();
   }

   @Override
   public void addTag(ResourceLocation tag) {
   }

   @Override
   public boolean hasTag(ResourceLocation tag) {
      return false;
   }

   @Override
   public Iterator<PartialTile> getTiles(Predicate<PartialTile> filter, PlacementSettings settings) {
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
   public Iterator<PartialEntity> getEntities(Predicate<PartialEntity> filter, PlacementSettings settings) {
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