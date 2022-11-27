package iskallia.vault.entity.entity.elite;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Drowned;
import net.minecraft.world.level.Level;

public class EliteDrownedEntity extends Drowned {
   public EliteDrownedEntity(EntityType<? extends Drowned> entityType, Level world) {
      super(entityType, world);
   }
}
