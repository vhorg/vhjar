package iskallia.vault.entity.entity.tier1;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.level.Level;

public class Tier1HuskEntity extends Husk {
   public Tier1HuskEntity(EntityType<? extends Husk> entityType, Level world) {
      super(entityType, world);
   }
}
