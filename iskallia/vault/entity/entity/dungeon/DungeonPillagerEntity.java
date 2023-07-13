package iskallia.vault.entity.entity.dungeon;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Pillager;
import net.minecraft.world.level.Level;

public class DungeonPillagerEntity extends Pillager {
   public DungeonPillagerEntity(EntityType<? extends Pillager> entityType, Level world) {
      super(entityType, world);
   }
}
