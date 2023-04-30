package iskallia.vault.entity.entity.deepdark;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;

public class DeepDarkZombieEntity extends Zombie {
   public DeepDarkZombieEntity(EntityType<? extends Zombie> entityType, Level world) {
      super(entityType, world);
   }
}
