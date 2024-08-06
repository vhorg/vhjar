package iskallia.vault.antique.condition;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;

public class DropEntityConditionContext extends DropConditionContext {
   private final LivingEntity entity;

   public DropEntityConditionContext(int level, ResourceLocation ownerKey, LivingEntity entity) {
      super(level, DropConditionType.ENTITY, ownerKey);
      this.entity = entity;
   }

   public LivingEntity getEntity() {
      return this.entity;
   }
}
