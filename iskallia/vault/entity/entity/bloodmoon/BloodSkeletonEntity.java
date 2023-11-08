package iskallia.vault.entity.entity.bloodmoon;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.level.Level;

public class BloodSkeletonEntity extends Skeleton {
   public BloodSkeletonEntity(EntityType<? extends Skeleton> entityType, Level world) {
      super(entityType, world);
   }
}
