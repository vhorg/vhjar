package iskallia.vault.entity.entity.tier2;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.level.Level;

public class Tier2PiglinEntity extends Piglin {
   public Tier2PiglinEntity(EntityType<? extends AbstractPiglin> entityType, Level world) {
      super(entityType, world);
   }
}
