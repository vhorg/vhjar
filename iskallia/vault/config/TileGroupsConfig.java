package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.world.data.PartialNBT;
import iskallia.vault.core.world.data.PartialState;
import iskallia.vault.core.world.data.PartialTile;
import iskallia.vault.core.world.data.TilePredicate;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;

public class TileGroupsConfig extends Config {
   @Expose
   private Map<ResourceLocation, Set<String>> groups;

   @Override
   public String getName() {
      return "tile_groups";
   }

   public boolean isInGroup(ResourceLocation groupId, PartialState state, PartialNBT nbt) {
      for (String predicate : this.groups.get(groupId)) {
         if (TilePredicate.of(predicate).test(state, nbt)) {
            return true;
         }
      }

      return false;
   }

   public boolean isInGroup(ResourceLocation groupId, PartialTile tile) {
      return this.isInGroup(groupId, tile.getState(), tile.getNbt());
   }

   @Override
   protected void reset() {
      this.groups = new LinkedHashMap<>();
   }
}
