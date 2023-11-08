package iskallia.vault.core.world.processor.entity;

import iskallia.vault.core.util.WeightedList;
import iskallia.vault.core.world.data.entity.EntityPredicate;
import iskallia.vault.core.world.data.entity.PartialEntity;
import iskallia.vault.core.world.processor.ProcessorContext;
import java.util.Optional;

public class WeightedEntityProcessor extends TargetEntityProcessor<WeightedEntityProcessor> {
   protected final WeightedList<PartialEntity> output = new WeightedList<>();

   public WeightedEntityProcessor target(EntityPredicate predicate) {
      this.predicate = predicate;
      return this;
   }

   public PartialEntity process(PartialEntity entity, ProcessorContext context) {
      if (this.predicate.test(entity)) {
         Optional<PartialEntity> output = this.output.getRandom(context.getRandom(null));
         output.ifPresent(out -> out.fillInto(entity));
      }

      return entity;
   }
}
