package iskallia.vault.entity.entity.deepdark;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.piglin.AbstractPiglin;
import net.minecraft.world.entity.monster.piglin.Piglin;
import net.minecraft.world.level.Level;

public class DeepDarkPiglinEntity extends Piglin {
   public DeepDarkPiglinEntity(EntityType<? extends AbstractPiglin> entityType, Level world) {
      super(entityType, world);
   }
}
