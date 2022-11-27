package iskallia.vault.entity.ai;

import java.util.Random;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.ObjectUtils;

public abstract class GoalTask<T extends LivingEntity> extends Goal {
   private final T entity;

   public GoalTask(T entity) {
      this.entity = entity;
   }

   public T getEntity() {
      return this.entity;
   }

   public Level getWorld() {
      return this.getEntity().level;
   }

   public Random getRandom() {
      return (Random)ObjectUtils.firstNonNull(new Random[]{this.getWorld().getRandom(), this.getEntity().getRandom()});
   }
}
