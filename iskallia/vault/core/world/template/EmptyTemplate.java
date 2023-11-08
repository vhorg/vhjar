package iskallia.vault.core.world.template;

import iskallia.vault.core.world.data.entity.EntityPredicate;
import iskallia.vault.core.world.data.entity.PartialEntity;
import iskallia.vault.core.world.data.tile.PartialTile;
import iskallia.vault.core.world.data.tile.TilePredicate;
import java.util.Collections;
import java.util.Iterator;
import net.minecraft.resources.ResourceLocation;

public class EmptyTemplate extends Template {
   public static final EmptyTemplate INSTANCE = new EmptyTemplate();

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
      return Collections.emptyIterator();
   }

   @Override
   public Iterator<PartialEntity> getEntities(EntityPredicate filter, PlacementSettings settings) {
      return Collections.emptyIterator();
   }
}
