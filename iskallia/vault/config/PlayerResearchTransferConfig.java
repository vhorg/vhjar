package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

public class PlayerResearchTransferConfig extends Config {
   @Expose
   private HashMap<String, String> oldToNewResearch = new HashMap<>();

   @Override
   public String getName() {
      return "player_research_transfer";
   }

   @Override
   protected void reset() {
      this.oldToNewResearch.put("Old Research Name", "New Research Name");
   }

   public Optional<String> getNewResearch(String old) {
      return Optional.ofNullable(this.oldToNewResearch.get(old));
   }

   public Set<String> getRemovedResearches() {
      return Collections.unmodifiableSet(this.oldToNewResearch.keySet());
   }
}
