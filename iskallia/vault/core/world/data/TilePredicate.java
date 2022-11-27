package iskallia.vault.core.world.data;

import com.mojang.brigadier.StringReader;
import iskallia.vault.VaultMod;
import java.util.function.Predicate;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

@FunctionalInterface
public interface TilePredicate extends Predicate<PartialTile> {
   boolean test(PartialState var1, PartialNBT var2);

   default boolean test(PartialTile tile) {
      return this.test(tile.getState(), tile.getNbt());
   }

   static TilePredicate all() {
      return (state, nbt) -> true;
   }

   static TilePredicate of(String target) {
      TileParser parser = new TileParser(new StringReader(target), null, true);
      if (parser.hasTag()) {
         return parser.hasNBT() ? of(parser.getTag(), parser.getPartialNBT()) : of(parser.getTag());
      } else if (parser.hasBlock()) {
         return parser.hasNBT() ? of(parser.getPartialState(), parser.getPartialNBT()) : of(parser.getPartialState());
      } else {
         VaultMod.LOGGER.error("Unknown predicate for input <" + target + ">");
         return (state, nbt) -> false;
      }
   }

   static TilePredicate of(TagKey<Block> tag) {
      return (_state, _nbt) -> _state.getBlock().builtInRegistryHolder().is(tag);
   }

   static TilePredicate of(TagKey<Block> tag, PartialNBT nbt) {
      return (_state, _nbt) -> _state.getBlock().builtInRegistryHolder().is(tag) && nbt.isSubsetOf(_nbt);
   }

   static TilePredicate of(Block block) {
      return (_state, _nbt) -> _state.getBlock() == block;
   }

   static TilePredicate of(Block block, PartialNBT nbt) {
      return (_state, _nbt) -> _state.getBlock() == block && nbt.isSubsetOf(_nbt);
   }

   static TilePredicate of(BlockState state) {
      return (_state, _nbt) -> _state.matches(state);
   }

   static TilePredicate of(BlockState state, PartialNBT nbt) {
      return (_state, _nbt) -> _state.matches(state) && nbt.isSubsetOf(_nbt);
   }

   static TilePredicate of(PartialState state) {
      return (_state, _nbt) -> state.isSubsetOf(_state);
   }

   static TilePredicate of(PartialState state, PartialNBT nbt) {
      return (_state, _nbt) -> state.isSubsetOf(_state) && nbt.isSubsetOf(_nbt);
   }
}
