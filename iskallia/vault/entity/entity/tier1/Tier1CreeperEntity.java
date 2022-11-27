package iskallia.vault.entity.entity.tier1;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;

public class Tier1CreeperEntity extends Creeper {
   public Tier1CreeperEntity(EntityType<? extends Creeper> entityType, Level world) {
      super(entityType, world);
   }
}
