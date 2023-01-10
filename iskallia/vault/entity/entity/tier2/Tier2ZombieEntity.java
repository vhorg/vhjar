package iskallia.vault.entity.entity.tier2;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;

public class Tier2ZombieEntity extends Zombie {
   public Tier2ZombieEntity(EntityType<? extends Zombie> entityType, Level world) {
      super(entityType, world);
   }
}
