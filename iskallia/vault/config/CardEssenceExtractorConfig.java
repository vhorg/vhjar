package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class CardEssenceExtractorConfig extends Config {
   @Expose
   private final Map<Integer, CardEssenceExtractorConfig.TierConfig> tierConfigs = new LinkedHashMap<>();

   public Optional<CardEssenceExtractorConfig.TierConfig> getConfig(int cardTier) {
      return Optional.ofNullable(this.tierConfigs.get(cardTier));
   }

   @Override
   public String getName() {
      return "card_essence_extractor";
   }

   @Override
   protected void reset() {
      this.tierConfigs.clear();
      this.tierConfigs.put(1, new CardEssenceExtractorConfig.TierConfig(100, new CardEssenceExtractorConfig.Range(100, 150), 10000));
   }

   public static class Range {
      @Expose
      private int min;
      @Expose
      private int max;

      public Range(int min, int max) {
         this.min = min;
         this.max = max;
      }

      public int getRandom() {
         return Config.rand.nextInt(Math.max(this.max - this.min, 1)) + this.min;
      }
   }

   public static class TierConfig {
      @Expose
      private int extractTickTime;
      @Expose
      private CardEssenceExtractorConfig.Range essencePerCard;
      @Expose
      private int essencePerUpgrade;

      public TierConfig(int extractTickTime, CardEssenceExtractorConfig.Range essencePerCard, int essencePerUpgrade) {
         this.extractTickTime = extractTickTime;
         this.essencePerCard = essencePerCard;
         this.essencePerUpgrade = essencePerUpgrade;
      }

      public int getExtractTickTime() {
         return this.extractTickTime;
      }

      public CardEssenceExtractorConfig.Range getEssencePerCard() {
         return this.essencePerCard;
      }

      public int getEssencePerUpgrade() {
         return this.essencePerUpgrade;
      }
   }
}
