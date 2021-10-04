package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.util.data.WeightedList;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nullable;

public class VaultGearCraftingScalingConfig extends Config {
   @Expose
   private final List<VaultGearCraftingScalingConfig.Level> tierOutcomes = new ArrayList<>();

   @Override
   public String getName() {
      return "vault_gear_crafting_scaling";
   }

   @Override
   protected void reset() {
      this.tierOutcomes.clear();
      VaultGearCraftingScalingConfig.Level defaultLevel = new VaultGearCraftingScalingConfig.Level(
         0, new WeightedList<VaultGearCraftingScalingConfig.TierOutcome>().add(new VaultGearCraftingScalingConfig.TierOutcome(0), 1)
      );
      this.tierOutcomes.add(defaultLevel);
      VaultGearCraftingScalingConfig.Level t1level = new VaultGearCraftingScalingConfig.Level(
         100,
         new WeightedList<VaultGearCraftingScalingConfig.TierOutcome>()
            .add(new VaultGearCraftingScalingConfig.TierOutcome(0), 10)
            .add(new VaultGearCraftingScalingConfig.TierOutcome(1), 1)
      );
      this.tierOutcomes.add(t1level);
   }

   public int getRandomTier(int playerLevel) {
      VaultGearCraftingScalingConfig.Level level = this.getForLevel(this.tierOutcomes, playerLevel);
      return level == null ? 0 : Optional.ofNullable(level.outcomes.getRandom(rand)).map(VaultGearCraftingScalingConfig.TierOutcome::getTier).orElse(0);
   }

   @Nullable
   public VaultGearCraftingScalingConfig.Level getForLevel(List<VaultGearCraftingScalingConfig.Level> levels, int level) {
      for (int i = 0; i < levels.size(); i++) {
         if (level < levels.get(i).level) {
            if (i != 0) {
               return levels.get(i - 1);
            }
            break;
         }

         if (i == levels.size() - 1) {
            return levels.get(i);
         }
      }

      return null;
   }

   public static class Level {
      @Expose
      private final int level;
      @Expose
      private final WeightedList<VaultGearCraftingScalingConfig.TierOutcome> outcomes;

      public Level(int level, WeightedList<VaultGearCraftingScalingConfig.TierOutcome> outcomes) {
         this.level = level;
         this.outcomes = outcomes;
      }
   }

   public static class TierOutcome {
      @Expose
      private final int tier;

      public TierOutcome(int tier) {
         this.tier = tier;
      }

      public int getTier() {
         return this.tier;
      }
   }
}
