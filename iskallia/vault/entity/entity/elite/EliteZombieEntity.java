package iskallia.vault.entity.entity.elite;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;

public class EliteZombieEntity extends Zombie {
   public EliteZombieEntity(EntityType<? extends Zombie> entityType, Level world) {
      super(entityType, world);
   }
}
