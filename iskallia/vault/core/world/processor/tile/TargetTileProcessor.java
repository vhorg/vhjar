package iskallia.vault.core.world.processor.tile;

import iskallia.vault.core.world.data.tile.TilePredicate;
import java.util.Optional;

public abstract class TargetTileProcessor<T extends TargetTileProcessor<T>> extends TileProcessor {
   protected TilePredicate predicate;
   protected String literal;

   public Optional<String> getLiteral() {
      return Optional.ofNullable(this.literal);
   }

   public T target(String target) {
      this.literal = target;
      return this.target(TilePredicate.of(target, true).orElse(TilePredicate.FALSE));
   }

   public T target(TilePredicate predicate) {
      this.predicate = predicate;
      return (T)this;
   }
}
