package iskallia.vault.entity.entity.dungeon;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.level.Level;

public class DungeonSkeletonEntity extends Skeleton {
   public DungeonSkeletonEntity(EntityType<? extends Skeleton> entityType, Level world) {
      super(entityType, world);
   }
}
