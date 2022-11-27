package iskallia.vault.config.bounty;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.Config;
import iskallia.vault.config.bounty.task.TaskConfig;
import iskallia.vault.config.entry.LevelEntryMap;
import iskallia.vault.util.data.WeightedList;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;

public class BountyConfig extends Config {
   @Expose
   private int totalBountyCount;
   @Expose
   private int maxActive;
   @Expose
   private int abandonedPenaltyInHours;
   @Expose
   private int waitingPeriodInHours;
   @Expose
   private WeightedList<ResourceLocation> weightedTaskList = new WeightedList<>();
   @Expose
   protected LevelEntryMap<Integer> rerollBronzeCost = new LevelEntryMap<>();

   @Override
   public String getName() {
      return "bounty/bounties";
   }

   @Override
   protected void reset() {
      this.totalBountyCount = 3;
      this.maxActive = 1;
      this.abandonedPenaltyInHours = 8;
      this.waitingPeriodInHours = 2;
      TaskConfig.getTaskConfigs().keySet().forEach(id -> this.weightedTaskList.add(id, 3));

      for (int i = 0; i < 30; i += 5) {
         this.rerollBronzeCost.put(Integer.valueOf(i), Integer.valueOf(2));
      }
   }

   public int getTotalBountyCount() {
      return this.totalBountyCount;
   }

   public int getMaxActive() {
      return this.maxActive;
   }

   public int getAbandonedPenaltyInHours() {
      return this.abandonedPenaltyInHours;
   }

   public int getWaitingPeriodInHours() {
      return this.waitingPeriodInHours;
   }

   public ResourceLocation getRandomTask() {
      return this.weightedTaskList.getRandom(rand);
   }

   public int getBaseCost(int vaultLevel) {
      Optional<Integer> entry = this.rerollBronzeCost.getForLevel(vaultLevel);
      return entry.isEmpty() ? vaultLevel : entry.get();
   }

   public int getCost(int vaultLevel, long timeRemaining) {
      int baseCost = this.getBaseCost(vaultLevel);
      int extra = (int)Math.max(Math.round(timeRemaining / 3600000.0), 1L);
      return baseCost + extra;
   }
}
