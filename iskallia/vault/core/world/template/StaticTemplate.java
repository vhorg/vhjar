package iskallia.vault.core.world.template;

import iskallia.vault.core.world.data.EntityPredicate;
import iskallia.vault.core.world.data.PartialEntity;
import iskallia.vault.core.world.data.tile.PartialTile;
import iskallia.vault.core.world.data.tile.TilePredicate;
import java.util.Collections;
import java.util.Iterator;
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
   public Iterator<PartialEntity> getEntities(PlacementSettings settings) {
      return super.getEntities(settings);
   }

   @Override
   public Iterator<PartialTile> getTiles(TilePredicate filter, PlacementSettings settings) {
      return this.tiles.iterator();
   }

   @Override
   public Iterator<PartialEntity> getEntities(EntityPredicate filter, PlacementSettings settings) {
      return this.entities.iterator();
   }
}
