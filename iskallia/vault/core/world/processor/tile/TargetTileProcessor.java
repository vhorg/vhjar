package iskallia.vault.core.world.processor.tile;

import iskallia.vault.core.world.data.PartialNBT;
import iskallia.vault.core.world.data.PartialState;
import iskallia.vault.core.world.data.TilePredicate;
import java.util.Optional;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

public abstract class TargetTileProcessor<T extends TargetTileProcessor<T>> extends TileProcessor {
   protected TilePredicate predicate;
   protected String literal;

   public Optional<String> getLiteral() {
      return Optional.ofNullable(this.literal);
   }

   public T target(String target) {
      this.literal = target;
      return this.target(TilePredicate.of(target));
   }

   public T target(TagKey<Block> tag) {
      return this.target(TilePredicate.of(tag));
   }

   public T target(TagKey<Block> tag, PartialNBT nbt) {
      return this.target(TilePredicate.of(tag, nbt));
   }

   public T target(Block block) {
      return this.target(TilePredicate.of(block));
   }

   public T target(Block block, PartialNBT nbt) {
      return this.target(TilePredicate.of(block, nbt));
   }

   public T target(BlockState state) {
      return this.target(TilePredicate.of(state));
   }

   public T target(BlockState state, PartialNBT nbt) {
      return this.target(TilePredicate.of(state, nbt));
   }

   public T target(PartialState state) {
      return this.target(TilePredicate.of(state));
   }

   public T target(PartialState state, PartialNBT nbt) {
      return this.target(TilePredicate.of(state, nbt));
   }

   public T target(TilePredicate predicate) {
      this.predicate = predicate;
      return (T)this;
   }
}
