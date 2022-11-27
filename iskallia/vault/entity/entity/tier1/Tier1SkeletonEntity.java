package iskallia.vault.entity.entity.tier1;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.level.Level;

public class Tier1SkeletonEntity extends Skeleton {
   public Tier1SkeletonEntity(EntityType<? extends Skeleton> entityType, Level world) {
      super(entityType, world);
   }
}
