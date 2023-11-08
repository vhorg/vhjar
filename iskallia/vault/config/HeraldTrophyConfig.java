package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.block.HeraldTrophyBlock;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

public class HeraldTrophyConfig extends Config {
   @Expose
   private Map<HeraldTrophyBlock.Variant, Integer> trophyTimes;

   public int getTime(HeraldTrophyBlock.Variant variant) {
      return this.trophyTimes.get(variant);
   }

   public HeraldTrophyBlock.Variant getTrophy(int time) {
      HeraldTrophyBlock.Variant trophy = null;
      int minTime = Integer.MAX_VALUE;

      for (Entry<HeraldTrophyBlock.Variant, Integer> entry : this.trophyTimes.entrySet()) {
         if (time <= entry.getValue() && entry.getValue() < minTime) {
            trophy = entry.getKey();
            minTime = entry.getValue();
         }
      }

      return trophy;
   }

   @Override
   public String getName() {
      return "herald_trophy";
   }

   @Override
   protected void reset() {
      this.trophyTimes = new LinkedHashMap<>();
      this.trophyTimes.put(HeraldTrophyBlock.Variant.BRONZE, 24000);
      this.trophyTimes.put(HeraldTrophyBlock.Variant.SILVER, 18000);
      this.trophyTimes.put(HeraldTrophyBlock.Variant.GOLD, 12000);
      this.trophyTimes.put(HeraldTrophyBlock.Variant.PLATINUM, 6000);
   }
}
