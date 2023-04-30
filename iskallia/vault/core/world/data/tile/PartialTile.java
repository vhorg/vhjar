package iskallia.vault.core.world.data.tile;

import com.google.gson.JsonElement;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.world.data.PartialCompoundNbt;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.item.crystal.data.adapter.ISimpleAdapter;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.CommonLevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;

public class PartialTile implements TilePlacement<PartialTile> {
   public static PartialTile ERROR = of(PartialBlockState.of(ModBlocks.ERROR_BLOCK.defaultBlockState()), PartialCompoundNbt.empty(), null);
   protected PartialBlockState state;
   protected PartialCompoundNbt entity;
   protected BlockPos pos;

   protected PartialTile(PartialBlockState state, PartialCompoundNbt entity, BlockPos pos) {
      this.state = state;
      this.entity = entity;
      this.pos = pos;
   }

   public static PartialTile of(PartialBlockState state, PartialCompoundNbt entity, BlockPos pos) {
      return new PartialTile(state, entity, pos);
   }

   public static PartialTile of(PartialBlockState state, PartialCompoundNbt entity) {
      return new PartialTile(state, entity, null);
   }

   public static PartialTile at(BlockGetter world, BlockPos pos) {
      return new PartialTile(PartialBlockState.at(world, pos), PartialCompoundNbt.at(world, pos), pos);
   }

   public static PartialTile of(BlockState state) {
      return new PartialTile(PartialBlockState.of(state), PartialCompoundNbt.empty(), null);
   }

   public PartialBlockState getState() {
      return this.state;
   }

   public PartialCompoundNbt getEntity() {
      return this.entity;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public PartialTile setState(PartialBlockState state) {
      this.state = state;
      return this;
   }

   public PartialTile setEntity(PartialCompoundNbt entity) {
      this.entity = entity;
      return this;
   }

   public PartialTile setPos(BlockPos pos) {
      this.pos = pos;
      return this;
   }

   public boolean isSubsetOf(PartialTile other) {
      return !this.state.isSubsetOf(other.state) ? false : this.entity.isSubsetOf(other.entity);
   }

   @Override
   public boolean isSubsetOf(LevelReader world, BlockPos pos) {
      return !this.state.isSubsetOf(world, pos) ? false : this.entity.isSubsetOf(world, pos);
   }

   public void fillInto(PartialTile other) {
      this.state.fillInto(other.state);
      this.entity.fillInto(other.entity);
      if (this.pos != null) {
         other.pos = this.pos.immutable();
      }
   }

   @Override
   public void place(CommonLevelAccessor world, BlockPos pos, int flags) {
      if (pos != null) {
         this.state.place(world, pos, flags);
         this.entity.place(world, pos, flags);
      } else if (this.pos != null) {
         this.state.place(world, this.pos, flags);
         this.entity.place(world, this.pos, flags);
      }
   }

   @Override
   public boolean test(PartialBlockState state, PartialCompoundNbt nbt) {
      return this.isSubsetOf(of(state, nbt, null));
   }

   public PartialTile copy() {
      return new PartialTile(this.state.copy(), this.entity.copy(), this.pos == null ? null : this.pos.immutable());
   }

   @Override
   public String toString() {
      return this.state.toString() + this.entity.toString();
   }

   public static Optional<PartialTile> parse(String string, boolean logErrors) {
      try {
         return Optional.of(parse(new StringReader(string)));
      } catch (IllegalArgumentException | CommandSyntaxException var3) {
         if (logErrors) {
            var3.printStackTrace();
         }

         return Optional.empty();
      }
   }

   public static PartialTile parse(String string) throws CommandSyntaxException {
      return parse(new StringReader(string));
   }

   public static PartialTile parse(StringReader reader) throws CommandSyntaxException {
      return of(PartialBlockState.parse(reader), PartialCompoundNbt.parse(reader), null);
   }

   public static class Adapter implements ISimpleAdapter<PartialTile, Tag, JsonElement> {
      public Optional<Tag> writeNbt(@Nullable PartialTile value) {
         if (value == null) {
            return Optional.empty();
         } else {
            CompoundTag nbt = new CompoundTag();
            if (value.pos != null) {
               ListTag posNBT = new ListTag();
               posNBT.add(IntTag.valueOf(value.pos.getX()));
               posNBT.add(IntTag.valueOf(value.pos.getY()));
               posNBT.add(IntTag.valueOf(value.pos.getZ()));
               nbt.put("pos", posNBT);
            }

            Adapters.PARTIAL_BLOCK_STATE.writeNbt(value.state).ifPresent(tag -> nbt.put("state", tag));
            Adapters.PARTIAL_BLOCK_ENTITY.writeNbt(value.entity).ifPresent(tag -> nbt.put("nbt", tag));
            return Optional.of(nbt);
         }
      }

      @Override
      public Optional<PartialTile> readNbt(@Nullable Tag nbt) {
         if (nbt instanceof CompoundTag compound) {
            PartialBlockState state = Adapters.PARTIAL_BLOCK_STATE.readNbt(compound.get("state")).orElseThrow();
            PartialCompoundNbt entity = Adapters.PARTIAL_BLOCK_ENTITY.readNbt(compound.get("nbt")).orElseGet(PartialCompoundNbt::empty);
            BlockPos pos = null;
            if (compound.contains("pos", 9)) {
               ListTag posNBT = compound.getList("pos", 3);
               pos = new BlockPos(posNBT.getInt(0), posNBT.getInt(1), posNBT.getInt(2));
            }

            return Optional.of(PartialTile.of(state, entity, pos));
         } else {
            return Optional.empty();
         }
      }
   }
}
