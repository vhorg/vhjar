package iskallia.vault.core.world.template;

import iskallia.vault.core.util.iterator.EmptyIterator;
import iskallia.vault.core.world.data.PartialEntity;
import iskallia.vault.core.world.data.PartialTile;
import java.util.Iterator;
import java.util.function.Predicate;
import net.minecraft.resources.ResourceLocation;

public class EmptyTemplate extends Template {
   public static final EmptyTemplate INSTANCE = new EmptyTemplate();

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
      return new EmptyIterator<>();
   }

   @Override
   public Iterator<PartialEntity> getEntities(Predicate<PartialEntity> filter, PlacementSettings settings) {
      return new EmptyIterator<>();
   }
}
