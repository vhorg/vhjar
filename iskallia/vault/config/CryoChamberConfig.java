package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import java.util.HashMap;

public class CryoChamberConfig extends Config {
   @Expose
   private int INFUSION_TIME;
   @Expose
   private int GROW_ETERNAL_TIME;
   @Expose
   private HashMap<String, Integer> STREAMER_CORE_REQ = new HashMap<>();

   @Override
   public String getName() {
      return "cryo_chamber";
   }

   public int getPlayerCoreCount(String name) {
      return this.STREAMER_CORE_REQ.containsKey(name) ? this.STREAMER_CORE_REQ.get(name) : 100;
   }

   public int getGrowEternalTime() {
      return this.GROW_ETERNAL_TIME * 20;
   }

   public int getInfusionTime() {
      return this.INFUSION_TIME * 20;
   }

   @Override
   protected void reset() {
      this.INFUSION_TIME = 4;
      this.GROW_ETERNAL_TIME = 10;
      this.STREAMER_CORE_REQ.put("iskall85", 100);
      this.STREAMER_CORE_REQ.put("Stressmonster101", 100);
      this.STREAMER_CORE_REQ.put("AntonioAsh", 100);
   }
}
