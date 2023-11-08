package iskallia.vault.core.world.data.tile;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.data.adapter.array.ArrayAdapter;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.world.data.entity.PartialCompoundNbt;
import iskallia.vault.item.crystal.data.adapter.ISimpleAdapter;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.ListTag;
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
      private static ArrayAdapter<TilePredicate> LIST = Adapters.ofArray(TilePredicate[]::new, new TilePredicate.Adapter());

      public void writeBits(@Nullable TilePredicate value, BitBuffer buffer) {
         buffer.writeBoolean(value == null);
         if (value != null) {
            if (value instanceof OrTilePredicate or) {
               buffer.writeBoolean(true);
               LIST.writeBits(or.getChildren(), buffer);
            } else {
               buffer.writeBoolean(false);
               Adapters.UTF_8.writeBits(value.toString(), buffer);
            }
         }
      }

      @Override
      public final Optional<TilePredicate> readBits(BitBuffer buffer) {
         if (buffer.readBoolean()) {
            return Optional.empty();
         } else {
            return buffer.readBoolean()
               ? LIST.readBits(buffer).map(OrTilePredicate::new)
               : Adapters.UTF_8.readBits(buffer).map(string -> TilePredicate.of(string, true).orElse(TilePredicate.FALSE));
         }
      }

      public Optional<Tag> writeNbt(@Nullable TilePredicate value) {
         if (value == null) {
            return Optional.empty();
         } else {
            return value instanceof OrTilePredicate or ? LIST.writeNbt(or.getChildren()) : Optional.of(StringTag.valueOf(value.toString()));
         }
      }

      @Override
      public Optional<TilePredicate> readNbt(@Nullable Tag nbt) {
         if (nbt == null) {
            return Optional.empty();
         } else if (nbt instanceof ListTag list) {
            return LIST.readNbt(list).map(OrTilePredicate::new);
         } else {
            return nbt instanceof StringTag string ? Optional.of(TilePredicate.of(string.getAsString(), true).orElse(TilePredicate.FALSE)) : Optional.empty();
         }
      }

      public Optional<JsonElement> writeJson(@Nullable TilePredicate value) {
         if (value == null) {
            return Optional.empty();
         } else {
            return value instanceof OrTilePredicate or ? LIST.writeJson(or.getChildren()) : Optional.of(new JsonPrimitive(value.toString()));
         }
      }

      @Override
      public Optional<TilePredicate> readJson(@Nullable JsonElement json) {
         if (json == null) {
            return Optional.empty();
         } else if (json instanceof JsonArray array) {
            return LIST.readJson(array).map(OrTilePredicate::new);
         } else {
            return json instanceof JsonPrimitive primitive && primitive.isString()
               ? Optional.of(TilePredicate.of(json.getAsString(), true).orElse(TilePredicate.FALSE))
               : Optional.empty();
         }
      }
   }
}
