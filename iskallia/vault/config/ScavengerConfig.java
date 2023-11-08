package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.VaultMod;
import iskallia.vault.config.entry.LevelEntryList;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.core.vault.objective.scavenger.ChestScavengerTask;
import iskallia.vault.core.vault.objective.scavenger.CoinStacksScavengerTask;
import iskallia.vault.core.vault.objective.scavenger.MobScavengerTask;
import iskallia.vault.core.vault.objective.scavenger.OreScavengerTask;
import iskallia.vault.core.vault.objective.scavenger.ScavengeTask;
import iskallia.vault.core.vault.objective.scavenger.ScavengerGoal;
import iskallia.vault.core.world.data.tile.PartialTile;
import iskallia.vault.core.world.data.tile.TilePredicate;
import iskallia.vault.core.world.roll.IntRoll;
import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
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

   public HashMap<Item, Integer> getNameColors() {
      HashMap<Item, Integer> nameColors = new HashMap<>();

      for (ScavengeTask task : this.tasks) {
         if (task instanceof ChestScavengerTask) {
            ChestScavengerTask scavengeTask = (ChestScavengerTask)task;

            for (Map.Entry<ChestScavengerTask.Entry, Double> entry : scavengeTask.entries.entrySet()) {
               ChestScavengerTask.Entry taskEntry = entry.getKey();
               nameColors.put(taskEntry.item.getItem(), taskEntry.color);
            }
         } else if (task instanceof CoinStacksScavengerTask scavengeTask) {
            for (Map.Entry<CoinStacksScavengerTask.Entry, Double> entry : scavengeTask.entries.entrySet()) {
               CoinStacksScavengerTask.Entry taskEntry = entry.getKey();
               nameColors.put(taskEntry.item.getItem(), taskEntry.color);
            }
         } else if (task instanceof OreScavengerTask scavengeTask) {
            for (Map.Entry<OreScavengerTask.Entry, Double> entry : scavengeTask.entries.entrySet()) {
               OreScavengerTask.Entry taskEntry = entry.getKey();
               nameColors.put(taskEntry.item.getItem(), taskEntry.color);
            }
         } else if (task instanceof MobScavengerTask scavengeTask) {
            for (MobScavengerTask.Entry entry : scavengeTask.entries) {
               nameColors.put(entry.item.getItem(), Color.WHITE.getRGB());
            }
         }
      }

      return nameColors;
   }

   @Override
   protected void reset() {
      this.tasks = new ArrayList<>();
      this.levels = new LevelEntryList<>();
      this.tasks
         .add(
            new ChestScavengerTask(
               TilePredicate.of("the_vault:wooden_chest", true).orElse(PartialTile.ERROR),
               0.5,
               VaultMod.id("gui/scav/wooden_chest"),
               new WeightedList<ChestScavengerTask.Entry>()
                  .add(new ChestScavengerTask.Entry(new ItemStack(Items.LIGHT_BLUE_WOOL), 1.0, 16777215), 8)
                  .add(new ChestScavengerTask.Entry(new ItemStack(Items.YELLOW_WOOL), 1.0, 16777215), 4)
                  .add(new ChestScavengerTask.Entry(new ItemStack(Items.PURPLE_WOOL), 1.0, 16777215), 2)
                  .add(new ChestScavengerTask.Entry(new ItemStack(Items.LIME_WOOL), 1.0, 16777215), 1)
            )
         );
      this.tasks
         .add(
            new CoinStacksScavengerTask(
               0.5,
               VaultMod.id("gui/scav/wooden_chest"),
               new WeightedList<CoinStacksScavengerTask.Entry>()
                  .add(new CoinStacksScavengerTask.Entry(new ItemStack(Items.LIGHT_BLUE_WOOL), 1.0, 16777215), 8)
                  .add(new CoinStacksScavengerTask.Entry(new ItemStack(Items.YELLOW_WOOL), 1.0, 16777215), 4)
                  .add(new CoinStacksScavengerTask.Entry(new ItemStack(Items.PURPLE_WOOL), 1.0, 16777215), 2)
                  .add(new CoinStacksScavengerTask.Entry(new ItemStack(Items.LIME_WOOL), 1.0, 16777215), 1)
            )
         );
      this.tasks
         .add(
            new MobScavengerTask(
               0.5,
               VaultMod.id("gui/scav/wooden_chest"),
               16777215,
               new MobScavengerTask.Entry(new ItemStack(Items.GOLDEN_APPLE), 1.0, EntityType.ZOMBIE, EntityType.CREEPER, EntityType.SPIDER)
            )
         );
      this.levels.add(new ScavengerConfig.Entry(0, IntRoll.ofUniform(2, 6), IntRoll.ofUniform(4, 10)));
   }

   private static class Entry implements LevelEntryList.ILevelEntry {
      @Expose
      private final int level;
      @Expose
      private IntRoll goalCount;
      @Expose
      private IntRoll itemCount;

      public Entry(int level, IntRoll goalCount, IntRoll itemCount) {
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
