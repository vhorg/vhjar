package iskallia.vault.core.world.data.tile;

import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.world.data.PartialCompoundNbt;
import iskallia.vault.item.crystal.data.adapter.ISimpleAdapter;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

@FunctionalInterface
public interface TilePredicate {
   TilePredicate FALSE = (state, nbt) -> false;
   TilePredicate TRUE = (state, nbt) -> true;

   boolean test(PartialBlockState var1, PartialCompoundNbt var2);

   default boolean test(PartialTile tile) {
      return this.test(tile.getState(), tile.getEntity());
   }

   default boolean test(BlockGetter world, BlockPos pos) {
      BlockState state = world.getBlockState(pos);
      BlockEntity entity = world.getBlockEntity(pos);
      return this.test(PartialBlockState.of(state), PartialCompoundNbt.of(entity));
   }

   static TilePredicate of(Block block) {
      return (state, nbt) -> state.getBlock().asWhole().map(other -> other == block).orElse(false);
   }

   static Optional<TilePredicate> of(String string, boolean logErrors) {
      if (string.isEmpty()) {
         return Optional.of(TRUE);
      } else {
         return (switch (string.charAt(0)) {
            case '#' -> PartialBlockTag.parse(string, logErrors);
            case '@' -> PartialBlockGroup.parse(string, logErrors);
            default -> PartialTile.parse(string, logErrors);
         }).map(o -> (TilePredicate)o);
      }
   }

   public static class Adapter implements ISimpleAdapter<TilePredicate, Tag, JsonElement> {
      public void writeBits(@Nullable TilePredicate value, BitBuffer buffer) {
         buffer.writeBoolean(value == null);
         if (value != null) {
            Adapters.UTF_8.writeBits(value.toString(), buffer);
         }
      }

      @Override
      public final Optional<TilePredicate> readBits(BitBuffer buffer) {
         return buffer.readBoolean()
            ? Optional.empty()
            : Adapters.UTF_8.readBits(buffer).map(string -> TilePredicate.of(string, true).orElse(TilePredicate.FALSE));
      }

      public Optional<Tag> writeNbt(@Nullable TilePredicate value) {
         return value == null ? Optional.empty() : Optional.of(StringTag.valueOf(value.toString()));
      }

      @Override
      public Optional<TilePredicate> readNbt(@Nullable Tag nbt) {
         if (nbt == null) {
            return Optional.empty();
         } else {
            return nbt instanceof StringTag string ? Optional.of(TilePredicate.of(string.getAsString(), true).orElse(TilePredicate.FALSE)) : Optional.empty();
         }
      }

      public Optional<JsonElement> writeJson(@Nullable TilePredicate value) {
         return value == null ? Optional.empty() : Optional.of(new JsonPrimitive(value.toString()));
      }

      @Override
      public Optional<TilePredicate> readJson(@Nullable JsonElement json) {
         if (json == null) {
            return Optional.empty();
         } else {
            return json instanceof JsonPrimitive primitive && primitive.isString()
               ? Optional.of(TilePredicate.of(json.getAsString(), true).orElse(TilePredicate.FALSE))
               : Optional.empty();
         }
      }
   }
}
