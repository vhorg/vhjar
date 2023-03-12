package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.world.data.PartialTile;
import iskallia.vault.core.world.data.TilePredicate;
import java.util.ArrayList;
import java.util.List;

public class ArchitectConfig extends Config {
   @Expose
   private List<String> whitelist;
   @Expose
   private List<String> blacklist;

   @Override
   public String getName() {
      return "architect";
   }

   public boolean isWhitelisted(PartialTile tile) {
      for (String predicate : this.whitelist) {
         if (TilePredicate.of(predicate).test(tile)) {
            return true;
         }
      }

      return false;
   }

   public boolean isBlacklisted(PartialTile tile) {
      for (String predicate : this.blacklist) {
         if (TilePredicate.of(predicate).test(tile)) {
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
