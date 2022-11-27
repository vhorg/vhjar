package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import java.util.HashMap;
import java.util.Map;

public class StreamerExpConfig extends Config {
   @Expose
   Map<String, Integer> EXP_PER_SUBSCRIBER;

   @Override
   public String getName() {
      return "streamer_exp";
   }

   public int getExpPerSub(String minecraftNick) {
      return this.EXP_PER_SUBSCRIBER.getOrDefault(minecraftNick, 90);
   }

   @Override
   protected void reset() {
      this.EXP_PER_SUBSCRIBER = new HashMap<>();
      this.EXP_PER_SUBSCRIBER.put("iskall85", 1000);
      this.EXP_PER_SUBSCRIBER.put("iGoodie", 200);
   }
}
