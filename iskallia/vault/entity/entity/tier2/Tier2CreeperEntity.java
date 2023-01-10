package iskallia.vault.entity.entity.tier2;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;

public class Tier2CreeperEntity extends Creeper {
   public Tier2CreeperEntity(EntityType<? extends Creeper> entityType, Level world) {
      super(entityType, world);
   }
}
