package iskallia.vault.entity.entity.tier2;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.level.Level;

public class Tier2WitherSkeletonEntity extends WitherSkeleton {
   public Tier2WitherSkeletonEntity(EntityType<? extends WitherSkeleton> entityType, Level world) {
      super(entityType, world);
   }
}
