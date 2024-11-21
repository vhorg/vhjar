package iskallia.vault.core.world.data.tile;

import com.google.gson.JsonElement;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.world.data.entity.PartialCompoundNbt;
import iskallia.vault.item.crystal.data.adapter.ISimpleAdapter;
import java.util.Optional;
import java.util.function.UnaryOperator;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.CommonLevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;

public class PartialBlockState implements TilePlacement<PartialBlockState> {
   private PartialBlock block;
   private PartialBlockProperties properties;

   protected PartialBlockState(PartialBlock block, PartialBlockProperties properties) {
      this.block = block;
      this.properties = properties;
   }

   public static PartialBlockState of(PartialBlock block, PartialBlockProperties properties) {
      return new PartialBlockState(block, properties);
   }

   public static PartialBlockState of(Block block) {
      return new PartialBlockState(PartialBlock.of(block), PartialBlockProperties.empty());
   }

   public static PartialBlockState of(BlockState state) {
      return new PartialBlockState(PartialBlock.of(state), PartialBlockProperties.of(state));
   }

   public static PartialBlockState at(BlockGetter world, BlockPos pos) {
      BlockState state = world.getBlockState(pos);
      return new PartialBlockState(PartialBlock.of(state), PartialBlockProperties.of(state));
   }

   public PartialBlock getBlock() {
      return this.block;
   }

   public PartialBlockProperties getProperties() {
      return this.properties;
   }

   public <T extends Comparable<T>> T get(Property<T> property) {
      return this.properties.get(property);
   }

   public <T extends Comparable<T>, V extends T> PartialBlockState set(Property<T> property, V value) {
      this.properties.set(property, value);
      return this;
   }

   public boolean isSubsetOf(PartialBlockState other) {
      return !this.block.isSubsetOf(other.block) ? false : this.properties.isSubsetOf(other.properties);
   }

   @Override
   public boolean isSubsetOf(LevelReader world, BlockPos pos) {
      return this.isSubsetOf(of(world.getBlockState(pos)));
   }

   public void fillInto(PartialBlockState other) {
      this.block.fillInto(other.block);
      this.properties.fillInto(other.properties);
   }

   @Override
   public void place(CommonLevelAccessor world, BlockPos pos, int flags) {
      this.asWhole().ifPresent(state -> world.setBlock(pos, state, flags));
   }

   @Override
   public boolean test(PartialBlockState state, PartialCompoundNbt nbt) {
      return this.isSubsetOf(state);
   }

   public Optional<BlockState> asWhole() {
      return this.block.asWhole().map(block -> this.properties.apply(block.defaultBlockState()));
   }

   public void mapAndSet(UnaryOperator<BlockState> mapper) {
      this.asWhole().ifPresent(oldState -> {
         BlockState newState = mapper.apply(oldState);
         if (oldState != newState) {
            PartialBlock.of(newState.getBlock()).fillInto(this.block);
            PartialBlockProperties.of(newState).fillInto(this.properties);
         }
      });
   }

   public void mirror(Mirror mirror) {
      this.mapAndSet(state -> state.mirror(mirror));
   }

   public void rotate(Rotation rotation) {
      this.mapAndSet(state -> state.rotate(rotation));
   }

   public boolean is(Block block) {
      return this.block.asWhole().map(value -> block == value).orElse(false);
   }

   public PartialBlockState copy() {
      return of(this.block.copy(), this.properties.copy());
   }

   @Override
   public String toString() {
      return this.block.toString() + this.properties.toString();
   }

   public static Optional<PartialBlockState> parse(String string, boolean logErrors) {
      try {
         return Optional.of(parse(new StringReader(string)));
      } catch (IllegalArgumentException | CommandSyntaxException var3) {
         if (logErrors) {
            var3.printStackTrace();
         }

         return Optional.empty();
      }
   }

   public static PartialBlockState parse(String string) throws CommandSyntaxException {
      return parse(new StringReader(string));
   }

   public static PartialBlockState parse(StringReader reader) throws CommandSyntaxException {
      return of(PartialBlock.parse(reader), PartialBlockProperties.parse(reader));
   }

   public static class Adapter implements ISimpleAdapter<PartialBlockState, Tag, JsonElement> {
      public void writeBits(PartialBlockState value, BitBuffer buffer) {
         buffer.writeBoolean(value == null);
         if (value != null) {
            Adapters.PARTIAL_BLOCK.writeBits(value.block, buffer);
            Adapters.PARTIAL_BLOCK_PROPERTIES.writeBits(value.properties, buffer);
         }
      }

      @Override
      public Optional<PartialBlockState> readBits(BitBuffer buffer) {
         if (buffer.readBoolean()) {
            return Optional.empty();
         } else {
            PartialBlock block = Adapters.PARTIAL_BLOCK.readBits(buffer).orElseThrow();
            PartialBlockProperties properties = Adapters.PARTIAL_BLOCK_PROPERTIES.readBits(buffer).orElseThrow();
            return Optional.of(new PartialBlockState(block, properties));
         }
      }

      public Optional<Tag> writeNbt(@Nullable PartialBlockState value) {
         if (value == null) {
            return Optional.empty();
         } else {
            CompoundTag nbt = new CompoundTag();
            Adapters.PARTIAL_BLOCK.writeNbt(value.block).ifPresent(tag -> nbt.put("Name", tag));
            Adapters.PARTIAL_BLOCK_PROPERTIES.writeNbt(value.properties).ifPresent(tag -> nbt.put("Properties", tag));
            return Optional.of(nbt);
         }
      }

      @Override
      public Optional<PartialBlockState> readNbt(@Nullable Tag nbt) {
         if (nbt == null) {
            return Optional.empty();
         } else {
            return nbt instanceof CompoundTag compound
               ? Optional.of(
                  PartialBlockState.of(
                     Adapters.PARTIAL_BLOCK.readNbt(compound.get("Name")).orElseGet(PartialBlock::empty),
                     Adapters.PARTIAL_BLOCK_PROPERTIES.readNbt(compound.get("Properties")).orElseGet(PartialBlockProperties::empty)
                  )
               )
               : Optional.empty();
         }
      }
   }
}
