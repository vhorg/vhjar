package iskallia.vault.entity.entity.tier1;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;

public class Tier1ZombieEntity extends Zombie {
   public Tier1ZombieEntity(EntityType<? extends Zombie> entityType, Level world) {
      super(entityType, world);
   }
}
