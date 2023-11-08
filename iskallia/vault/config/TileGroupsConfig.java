package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.world.data.entity.PartialCompoundNbt;
import iskallia.vault.core.world.data.tile.PartialBlockState;
import iskallia.vault.core.world.data.tile.PartialTile;
import iskallia.vault.core.world.data.tile.TilePredicate;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;

public class TileGroupsConfig extends Config {
   @Expose
   private Map<ResourceLocation, Set<TilePredicate>> groups;

   @Override
   public String getName() {
      return "tile_groups";
   }

   public boolean isInGroup(ResourceLocation groupId, PartialBlockState state, PartialCompoundNbt nbt) {
      for (TilePredicate predicate : this.groups.getOrDefault(groupId, new HashSet<>())) {
         if (predicate.test(state, nbt)) {
            return true;
         }
      }

      return false;
   }

   public boolean isInGroup(ResourceLocation groupId, PartialTile tile) {
      return this.isInGroup(groupId, tile.getState(), tile.getEntity());
   }

   @Override
   protected void reset() {
      this.groups = new LinkedHashMap<>();
   }
}
