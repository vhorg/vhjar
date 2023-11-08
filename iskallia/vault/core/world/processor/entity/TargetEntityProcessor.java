package iskallia.vault.core.world.processor.entity;

import iskallia.vault.core.world.data.entity.EntityPredicate;

public abstract class TargetEntityProcessor<T extends TargetEntityProcessor<T>> extends EntityProcessor {
   protected EntityPredicate predicate;

   public T target(EntityPredicate predicate) {
      this.predicate = predicate;
      return (T)this;
   }
}
