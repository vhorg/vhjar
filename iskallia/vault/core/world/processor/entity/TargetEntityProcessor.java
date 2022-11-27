package iskallia.vault.core.world.processor.entity;

import iskallia.vault.core.world.data.EntityPredicate;
import net.minecraft.world.entity.EntityType;

public abstract class TargetEntityProcessor<T extends TargetEntityProcessor<T>> extends EntityProcessor {
   protected EntityPredicate predicate;

   public T target(EntityType<?> type) {
      return this.target(EntityPredicate.of(type));
   }

   public T target(EntityPredicate predicate) {
      this.predicate = predicate;
      return (T)this;
   }
}
