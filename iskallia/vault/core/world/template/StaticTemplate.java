package iskallia.vault.core.world.template;

import iskallia.vault.core.util.iterator.EmptyIterator;
import iskallia.vault.core.world.data.PartialEntity;
import iskallia.vault.core.world.data.PartialTile;
import java.util.Iterator;
import java.util.function.Predicate;
import net.minecraft.resources.ResourceLocation;

public class StaticTemplate extends Template {
   private final Iterable<PartialTile> tiles;
   private final Iterable<PartialEntity> entities;

   public StaticTemplate(Iterable<PartialTile> tiles, Iterable<PartialEntity> entities) {
      this.tiles = tiles;
      this.entities = entities;
   }

   public Iterable<PartialTile> getTiles() {
      return this.tiles;
   }

   public Iterable<PartialEntity> getEntities() {
      return this.entities;
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
   public Iterator<PartialEntity> getEntities(PlacementSettings settings) {
      return super.getEntities(settings);
   }

   @Override
   public Iterator<PartialTile> getTiles(Predicate<PartialTile> filter, PlacementSettings settings) {
      return this.tiles.iterator();
   }

   @Override
   public Iterator<PartialEntity> getEntities(Predicate<PartialEntity> filter, PlacementSettings settings) {
      return this.entities.iterator();
   }
}
