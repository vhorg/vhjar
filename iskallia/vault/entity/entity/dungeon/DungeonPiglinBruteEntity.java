package iskallia.vault.entity.entity.dungeon;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.piglin.PiglinBrute;
import net.minecraft.world.level.Level;

public class DungeonPiglinBruteEntity extends PiglinBrute {
   public DungeonPiglinBruteEntity(EntityType<? extends PiglinBrute> entityType, Level world) {
      super(entityType, world);
   }
}
