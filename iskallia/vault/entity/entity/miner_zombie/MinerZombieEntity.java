package iskallia.vault.entity.entity.miner_zombie;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;

public class MinerZombieEntity extends Zombie {
   public MinerZombieEntity(EntityType<? extends Zombie> entityType, Level world) {
      super(entityType, world);
   }
}
