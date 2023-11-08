package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import java.util.HashMap;
import java.util.Map;

public class AncientCopperGolemConfig extends Config {
   @Expose
   public int degradeTime;
   @Expose
   public int finalDegradeTime;
   @Expose
   public Map<String, String> LOOT_TABLES = new HashMap<>();

   @Override
   public String getName() {
      return "ancient_copper_golem";
   }

   @Override
   protected void reset() {
      this.degradeTime = 800;
      this.LOOT_TABLES.put("Copper", "the_vault:ancient_copper_golem");
      this.LOOT_TABLES.put("Exposed", "the_vault:ancient_copper_golem_exposed");
      this.LOOT_TABLES.put("Weathered", "the_vault:ancient_copper_golem_weathered");
      this.LOOT_TABLES.put("Oxidized", "the_vault:ancient_copper_golem_oxidized");
   }
}
