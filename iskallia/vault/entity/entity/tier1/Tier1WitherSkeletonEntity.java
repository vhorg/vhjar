package iskallia.vault.entity.entity.tier1;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.WitherSkeleton;
import net.minecraft.world.level.Level;

public class Tier1WitherSkeletonEntity extends WitherSkeleton {
   public Tier1WitherSkeletonEntity(EntityType<? extends WitherSkeleton> entityType, Level world) {
      super(entityType, world);
   }
}
