package iskallia.vault.entity.entity.tier1;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Stray;
import net.minecraft.world.level.Level;

public class Tier1StrayEntity extends Stray {
   public Tier1StrayEntity(EntityType<? extends Stray> entityType, Level world) {
      super(entityType, world);
   }
}
