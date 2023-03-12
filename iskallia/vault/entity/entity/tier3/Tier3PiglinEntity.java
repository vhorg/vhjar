package iskallia.vault.entity.entity.tier3;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.level.Level;

public class Tier3PiglinEntity extends Piglin {
   public Tier3PiglinEntity(EntityType<? extends AbstractPiglin> entityType, Level world) {
      super(entityType, world);
   }
}
