package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import java.util.HashMap;
import java.util.Map;

public class StreamerMultipliersConfig extends Config {
   @Expose
   private Map<String, StreamerMultipliersConfig.StreamerMultipliers> MULTIPLIERS = new HashMap<>();

   @Override
   public String getName() {
      return "streamer_multipliers";
   }

   public StreamerMultipliersConfig.StreamerMultipliers ofStreamer(String mcNickname) {
      StreamerMultipliersConfig.StreamerMultipliers multipliers = this.MULTIPLIERS.get(mcNickname);
      return multipliers == null ? new StreamerMultipliersConfig.StreamerMultipliers() : multipliers;
   }

   @Override
   protected void reset() {
      this.MULTIPLIERS.put("iskall85", new StreamerMultipliersConfig.StreamerMultipliers());
   }

   public static class StreamerMultipliers {
      @Expose
      public int weightPerGiftedSubT1 = 5;
      @Expose
      public int weightPerGiftedSubT2 = 10;
      @Expose
      public int weightPerGiftedSubT3 = 25;
      @Expose
      public int weightPerDonationUnit = 1;
      @Expose
      public int weightPerHundredBits = 1;
      @Expose
      public int subsNeededForArena = 100;
      @Expose
      public int subsMultiplier = 1;
   }
}
