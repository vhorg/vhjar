package iskallia.vault.entity.entity.tier2;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.level.Level;

public class Tier2DrownedEntity extends Drowned {
   public Tier2DrownedEntity(EntityType<? extends Drowned> entityType, Level world) {
      super(entityType, world);
   }
}
