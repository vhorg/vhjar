package iskallia.vault.entity.ai.eyesore;

import iskallia.vault.entity.EyesoreEntity;
import iskallia.vault.util.data.WeightedList;
import java.util.Random;

public class EyesoreBrain {
   private final EyesoreEntity boss;
   public CreepyIdleTask creepyIdle;
   public WeightedList<EyesoreTask<EyesoreEntity>> tasks = new WeightedList<>();
   public EyesoreTask<EyesoreEntity> activeTask;

   public EyesoreBrain(EyesoreEntity boss) {
      this.boss = boss;
      this.creepyIdle = new CreepyIdleTask(boss);
      this.tasks.add(new BasicAttackTask(this.boss), 3);
      this.tasks.add(new LaserAttackTask(this.boss), 1);
   }

   public void tick() {
      if (!this.creepyIdle.isFinished()) {
         this.creepyIdle.tick();
      } else {
         if (this.activeTask == null || this.activeTask.isFinished()) {
            this.activeTask = this.tasks.getRandom(new Random());
            this.activeTask.reset();
         }

         this.activeTask.tick();
      }
   }
}
