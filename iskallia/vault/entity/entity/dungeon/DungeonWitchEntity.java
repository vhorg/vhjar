package iskallia.vault.entity.entity.dungeon;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Witch;
import net.minecraft.world.level.Level;

public class DungeonWitchEntity extends Witch {
   public DungeonWitchEntity(EntityType<? extends Witch> entityType, Level world) {
      super(entityType, world);
   }
}
