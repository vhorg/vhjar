package iskallia.vault.entity.entity.dungeon;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.level.Level;

public class DungeonPiglinEntity extends Piglin {
   public DungeonPiglinEntity(EntityType<? extends Piglin> entityType, Level world) {
      super(entityType, world);
   }
}
