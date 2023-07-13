package iskallia.vault.entity.entity.dungeon;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Vindicator;
import net.minecraft.world.level.Level;

public class DungeonVindicatorEntity extends Vindicator {
   public DungeonVindicatorEntity(EntityType<? extends Vindicator> entityType, Level world) {
      super(entityType, world);
   }
}
