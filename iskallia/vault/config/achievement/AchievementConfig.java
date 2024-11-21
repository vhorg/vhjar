package iskallia.vault.config.achievement;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.Config;
import iskallia.vault.task.Task;

public class AchievementConfig extends Config {
   @Expose
   private Task achievements;

   @Override
   public String getName() {
      return "achievements/achievements";
   }

   @Override
   protected void reset() {
   }

   public Task getAchievements() {
      return this.achievements;
   }
}
