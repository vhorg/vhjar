package iskallia.vault.entity.entity.tier1;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.level.Level;

public class Tier1DrownedEntity extends Drowned {
   public Tier1DrownedEntity(EntityType<? extends Drowned> entityType, Level world) {
      super(entityType, world);
   }
}
