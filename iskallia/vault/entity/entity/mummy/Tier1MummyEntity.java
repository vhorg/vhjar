package iskallia.vault.entity.entity.mummy;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Husk;
import net.minecraft.world.level.Level;

public class Tier1MummyEntity extends MummyEntity {
   public Tier1MummyEntity(EntityType<? extends Husk> entityType, Level world) {
      super(entityType, world);
   }
}
