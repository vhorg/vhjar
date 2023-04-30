package iskallia.vault.entity.entity.mummy;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.level.Level;

public class Tier0MummyEntity extends MummyEntity {
   public Tier0MummyEntity(EntityType<? extends Husk> entityType, Level world) {
      super(entityType, world);
   }
}
