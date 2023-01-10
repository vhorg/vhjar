package iskallia.vault.entity.entity.tier2;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.level.Level;

public class Tier2SkeletonEntity extends Skeleton {
   public Tier2SkeletonEntity(EntityType<? extends Skeleton> entityType, Level world) {
      super(entityType, world);
   }
}
