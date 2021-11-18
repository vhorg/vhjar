package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.entry.RangeEntry;
import iskallia.vault.util.data.WeightedList;
import java.util.Random;

public class FlawedRubyConfig extends Config {
   @Expose
   private FlawedRubyConfig.RubyEntry ARTISAN;
   @Expose
   private FlawedRubyConfig.RubyEntry TREASURE_HUNTER;

   @Override
   public String getName() {
      return "flawed_ruby";
   }

   @Override
   protected void reset() {
      WeightedList<FlawedRubyConfig.Outcome> artisanOutcomes = new WeightedList<>();
      artisanOutcomes.add(new WeightedList.Entry<>(FlawedRubyConfig.Outcome.FAIL, 5));
      artisanOutcomes.add(new WeightedList.Entry<>(FlawedRubyConfig.Outcome.IMBUE, 25));
      artisanOutcomes.add(new WeightedList.Entry<>(FlawedRubyConfig.Outcome.BREAK, 70));
      this.ARTISAN = new FlawedRubyConfig.RubyEntry(artisanOutcomes, new RangeEntry(1, 1));
      WeightedList<FlawedRubyConfig.Outcome> treasureHunterOutcomes = new WeightedList<>();
      treasureHunterOutcomes.add(new WeightedList.Entry<>(FlawedRubyConfig.Outcome.FAIL, 5));
      treasureHunterOutcomes.add(new WeightedList.Entry<>(FlawedRubyConfig.Outcome.IMBUE, 5));
      treasureHunterOutcomes.add(new WeightedList.Entry<>(FlawedRubyConfig.Outcome.BREAK, 90));
      this.TREASURE_HUNTER = new FlawedRubyConfig.RubyEntry(treasureHunterOutcomes, new RangeEntry(1, 1));
   }

   public FlawedRubyConfig.Outcome getForArtisan() {
      return this.ARTISAN.outcomes.getRandom(new Random());
   }

   public FlawedRubyConfig.Outcome getForTreasureHunter() {
      return this.TREASURE_HUNTER.outcomes.getRandom(new Random());
   }

   public int getArtisanAdditionalModifierCount() {
      return this.ARTISAN.additionModifierCount.getRandom();
   }

   public int getTreasureHunterAdditionalModifierCount() {
      return this.TREASURE_HUNTER.additionModifierCount.getRandom();
   }

   public static enum Outcome {
      FAIL,
      IMBUE,
      BREAK;
   }

   public static class RubyEntry {
      @Expose
      private WeightedList<FlawedRubyConfig.Outcome> outcomes;
      @Expose
      private RangeEntry additionModifierCount;

      public RubyEntry(WeightedList<FlawedRubyConfig.Outcome> outcomes, RangeEntry additionModifierCount) {
         this.outcomes = outcomes;
         this.additionModifierCount = additionModifierCount;
      }
   }
}
