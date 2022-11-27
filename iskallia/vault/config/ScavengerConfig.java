package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.VaultMod;
import iskallia.vault.config.entry.LevelEntryList;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.core.vault.objective.scavenger.ChestScavengeTask;
import iskallia.vault.core.vault.objective.scavenger.CoinStacksScavengeTask;
import iskallia.vault.core.vault.objective.scavenger.MobScavengeTask;
import iskallia.vault.core.vault.objective.scavenger.ScavengeTask;
import iskallia.vault.core.vault.objective.scavenger.ScavengerGoal;
import iskallia.vault.core.world.loot.LootRoll;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Items;

public class ScavengerConfig extends Config {
   @Expose
   private List<ScavengeTask> tasks;
   @Expose
   private LevelEntryList<ScavengerConfig.Entry> levels;

   @Override
   public String getName() {
      return "scavenger";
   }

   public List<ScavengeTask> getTasks() {
      return this.tasks;
   }

   public List<ScavengerGoal> generateGoals(int level, RandomSource random) {
      List<ScavengerGoal> result = new ArrayList<>();
      List<ScavengeTask> tasks = new ArrayList<>(this.tasks);
      ScavengerConfig.Entry entry = this.levels.getForLevel(level).orElse(null);
      if (entry == null) {
         return result;
      } else {
         int rolls = entry.goalCount.get(random);

         for (int i = 0; i < rolls && !tasks.isEmpty(); i++) {
            int n = random.nextInt(tasks.size());
            ScavengeTask task = tasks.get(n);
            task.generateGoal(entry.itemCount.get(random), random).ifPresent(goal -> {
               result.add(goal);
               tasks.remove(n);
            });
         }

         return result;
      }
   }

   @Override
   protected void reset() {
      this.tasks = new ArrayList<>();
      this.levels = new LevelEntryList<>();
      this.tasks
         .add(
            new ChestScavengeTask(
               "the_vault:wooden_chest",
               0.5,
               1.0,
               VaultMod.id("gui/scav/wooden_chest"),
               new WeightedList<ChestScavengeTask.Entry>()
                  .add(new ChestScavengeTask.Entry(Items.LIGHT_BLUE_WOOL, 16777215), 8)
                  .add(new ChestScavengeTask.Entry(Items.YELLOW_WOOL, 16777215), 4)
                  .add(new ChestScavengeTask.Entry(Items.PURPLE_WOOL, 16777215), 2)
                  .add(new ChestScavengeTask.Entry(Items.LIME_WOOL, 16777215), 1)
            )
         );
      this.tasks
         .add(
            new CoinStacksScavengeTask(
               0.5,
               1.0,
               VaultMod.id("gui/scav/wooden_chest"),
               new WeightedList<CoinStacksScavengeTask.Entry>()
                  .add(new CoinStacksScavengeTask.Entry(Items.LIGHT_BLUE_WOOL, 16777215), 8)
                  .add(new CoinStacksScavengeTask.Entry(Items.YELLOW_WOOL, 16777215), 4)
                  .add(new CoinStacksScavengeTask.Entry(Items.PURPLE_WOOL, 16777215), 2)
                  .add(new CoinStacksScavengeTask.Entry(Items.LIME_WOOL, 16777215), 1)
            )
         );
      this.tasks
         .add(
            new MobScavengeTask(
               0.5,
               1.0,
               VaultMod.id("gui/scav/wooden_chest"),
               16777215,
               new MobScavengeTask.Entry(Items.GOLDEN_APPLE, EntityType.ZOMBIE, EntityType.CREEPER, EntityType.SPIDER)
            )
         );
      this.levels.add(new ScavengerConfig.Entry(0, LootRoll.ofUniform(2, 6), LootRoll.ofUniform(4, 10)));
   }

   private static class Entry implements LevelEntryList.ILevelEntry {
      @Expose
      private final int level;
      @Expose
      private LootRoll goalCount;
      @Expose
      private LootRoll itemCount;

      public Entry(int level, LootRoll goalCount, LootRoll itemCount) {
         this.level = level;
         this.goalCount = goalCount;
         this.itemCount = itemCount;
      }

      @Override
      public int getLevel() {
         return this.level;
      }
   }
}
