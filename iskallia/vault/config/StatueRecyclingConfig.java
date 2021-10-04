package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import java.util.HashMap;

public class StatueRecyclingConfig extends Config {
   @Expose
   private int defaultRequirement;
   @Expose
   private HashMap<String, Integer> playerRequirement = new HashMap<>();
   @Expose
   private HashMap<String, Integer> itemValues = new HashMap<>();

   @Override
   public String getName() {
      return "statue_recycling";
   }

   @Override
   protected void reset() {
      this.defaultRequirement = 100;
      this.playerRequirement.put("iskall85", 100);
      this.playerRequirement.put("stressmonster", 100);
      this.itemValues.put("the_vault:arena_player_loot_statue", 1);
      this.itemValues.put("the_vault:vault_player_loot_statue", 2);
      this.itemValues.put("the_vault:gift_normal_statue", 3);
      this.itemValues.put("the_vault:gift_mega_statue", 4);
   }

   public int getItemValue(String id) {
      if (this.itemValues.containsKey(id)) {
         return this.itemValues.get(id);
      } else {
         throw new InternalError("There is no item with the ID: " + id);
      }
   }

   public int getPlayerRequirement(String name) {
      return this.playerRequirement.containsKey(name) ? this.playerRequirement.get(name) : this.defaultRequirement;
   }
}
