package iskallia.vault.config.achievement;

import com.google.gson.annotations.Expose;
import iskallia.vault.VaultMod;
import iskallia.vault.config.Config;
import iskallia.vault.core.world.data.entity.EntityPredicate;
import iskallia.vault.core.world.roll.IntRoll;
import iskallia.vault.task.AchievementCompleteTask;
import iskallia.vault.task.AchievementTask;
import iskallia.vault.task.DiscoverTransmogTask;
import iskallia.vault.task.KillEntityTask;
import iskallia.vault.task.Task;
import iskallia.vault.task.counter.TaskCounter;
import iskallia.vault.task.renderer.AchievementRenderer;
import iskallia.vault.task.renderer.KillEntityTaskRenderer;
import iskallia.vault.task.renderer.TransmogRewardRenderer;
import iskallia.vault.task.renderer.Vec2d;
import java.util.List;

public class AchievementConfig extends Config {
   @Expose
   private Task achievements;

   @Override
   public String getName() {
      return "achievements/achievements";
   }

   @Override
   public <T extends Config> T readConfig() {
      return super.readConfig();
   }

   @Override
   protected void reset() {
      AchievementTask achievementTask = new AchievementTask().setId("achivement_tree");
      KillEntityTask killEntityTask = new KillEntityTask(
            new KillEntityTask.Config(EntityPredicate.of("@the_vault:zombie", true).orElse(null)), TaskCounter.ofTargetInt(IntRoll.ofConstant(20))
         )
         .setId("kill_20_zombies");
      DiscoverTransmogTask transmogRewardTask = new DiscoverTransmogTask(
         new DiscoverTransmogTask.Config(
            List.of(
               VaultMod.id("gear/armor/angel/helmet"),
               VaultMod.id("gear/armor/angel/chestplate"),
               VaultMod.id("gear/armor/angel/leggings"),
               VaultMod.id("gear/armor/angel/boots")
            )
         )
      );
      AchievementCompleteTask achievementCompleteTask = new AchievementCompleteTask(
         new AchievementCompleteTask.Config("Kill 20 Zombies", "${player} has achieved ${title}!", VaultMod.id("textures/gui/achievements/altar.png"))
      );
      achievementTask.setRenderer(new AchievementRenderer.Root());
      killEntityTask.setRenderer(
         new KillEntityTaskRenderer.Achievement(
            "Kill 20 Zombies", "Sample description about killing 20 zombies.", VaultMod.id("textures/gui/achievements/altar.png"), Vec2d.ZERO, false
         )
      );
      transmogRewardTask.setRenderer(new TransmogRewardRenderer.Achievement());
      this.achievements = achievementTask.addChildren(killEntityTask.addChildren(new Task[]{transmogRewardTask, achievementCompleteTask}));
   }

   public Task getAchievements() {
      return this.achievements;
   }
}
