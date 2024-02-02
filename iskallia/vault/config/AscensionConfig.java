package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.entry.LevelEntryMap;
import java.util.Map;

public class AscensionConfig extends Config {
   @Expose
   private LevelEntryMap<Integer> scoreToEmbers;
   @Expose
   private LevelEntryMap<Integer> scoreToTier;

   @Override
   public String getName() {
      return "ascension";
   }

   public Map<Integer, Integer> getScoreToEmbers() {
      return this.scoreToEmbers;
   }

   public LevelEntryMap<Integer> getScoreToTier() {
      return this.scoreToTier;
   }

   public int getEmberCount(int level) {
      int total = 0;

      for (int i = 0; i < level; i++) {
         total += this.scoreToEmbers.getForLevel(i).orElse(0);
      }

      return total;
   }

   public int getTier(int score) {
      return this.scoreToTier.getForLevel(score).orElse(1);
   }

   @Override
   protected void reset() {
      this.scoreToEmbers = new LevelEntryMap<>();
      this.scoreToEmbers.put(Integer.valueOf(0), Integer.valueOf(1));
      this.scoreToTier = new LevelEntryMap<>();

      for (int i = 0; i < 8; i++) {
         this.scoreToTier.put(Integer.valueOf(i), Integer.valueOf(i + 1));
      }
   }
}
