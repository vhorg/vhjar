package iskallia.vault.core.world.processor.entity;

import iskallia.vault.core.util.WeightedList;
import iskallia.vault.core.world.data.EntityPredicate;
import iskallia.vault.core.world.data.PartialEntity;
import iskallia.vault.core.world.processor.ProcessorContext;
import java.util.Optional;
import net.minecraft.world.entity.EntityType;

public class WeightedEntityProcessor extends TargetEntityProcessor<WeightedEntityProcessor> {
   protected final WeightedList<PartialEntity> output = new WeightedList<>();

   public WeightedEntityProcessor target(EntityType<?> type) {
      return this.target(EntityPredicate.of(type));
   }

   public WeightedEntityProcessor target(EntityPredicate predicate) {
      this.predicate = predicate;
      return this;
   }

   public PartialEntity process(PartialEntity entity, ProcessorContext context) {
      if (this.predicate.test(entity)) {
         Optional<PartialEntity> output = this.output.getRandom(context.random);
         if (output.isPresent()) {
            return output.get().copy().fillMissing(entity);
         }
      }

      return entity;
   }
}
