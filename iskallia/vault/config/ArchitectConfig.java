package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.world.data.tile.PartialTile;
import iskallia.vault.core.world.data.tile.TilePredicate;
import java.util.ArrayList;
import java.util.List;

public class ArchitectConfig extends Config {
   @Expose
   private List<TilePredicate> whitelist;
   @Expose
   private List<TilePredicate> blacklist;

   @Override
   public String getName() {
      return "architect";
   }

   public boolean isWhitelisted(PartialTile tile) {
      for (TilePredicate predicate : this.whitelist) {
         if (predicate.test(tile)) {
            return true;
         }
      }

      return false;
   }

   public boolean isBlacklisted(PartialTile tile) {
      for (TilePredicate predicate : this.blacklist) {
         if (predicate.test(tile)) {
            return true;
         }
      }

      return false;
   }

   @Override
   protected void reset() {
      this.whitelist = new ArrayList<>();
      this.blacklist = new ArrayList<>();
   }
}
