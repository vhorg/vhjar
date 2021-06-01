package iskallia.vault.entity.ai;

import java.util.Random;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.goal.Goal;
import net.minecraft.world.World;
import org.apache.commons.lang3.ObjectUtils;

public abstract class GoalTask<T extends LivingEntity> extends Goal {
   private final T entity;

   public GoalTask(T entity) {
      this.entity = entity;
   }

   public T getEntity() {
      return this.entity;
   }

   public World getWorld() {
      return this.getEntity().field_70170_p;
   }

   public Random getRandom() {
      return (Random)ObjectUtils.firstNonNull(new Random[]{this.getWorld().func_201674_k(), this.getEntity().func_70681_au()});
   }
}
