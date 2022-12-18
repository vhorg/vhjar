package iskallia.vault.core.world.data;

import java.util.Map.Entry;
import java.util.function.Function;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.registries.ForgeRegistries;

public class PartialTile {
   protected PartialState state;
   protected PartialNBT nbt;
   protected BlockPos pos;

   protected PartialTile(PartialState state, PartialNBT nbt, BlockPos pos) {
      this.state = state;
      this.nbt = nbt;
      this.pos = pos;
   }

   public PartialState getState() {
      return this.state;
   }

   public PartialNBT getNbt() {
      return this.nbt;
   }

   public BlockPos getPos() {
      return this.pos;
   }

   public PartialTile setState(PartialState state) {
      this.state = state;
      return this;
   }

   public PartialTile setNbt(PartialNBT nbt) {
      this.nbt = nbt;
      return this;
   }

   public PartialTile setPos(BlockPos pos) {
      this.pos = pos;
      return this;
   }

   public static PartialTile of(Block block) {
      return of(block, PartialNBT.empty());
   }

   public static PartialTile of(BlockState state) {
      return of(state, PartialNBT.empty());
   }

   public static PartialTile of(PartialState state) {
      return of(state, PartialNBT.empty());
   }

   public static PartialTile of(Block block, CompoundTag nbt) {
      return of(PartialState.of(block), nbt);
   }

   public static PartialTile of(BlockState block, CompoundTag nbt) {
      return of(PartialState.of(block), nbt);
   }

   public static PartialTile of(PartialState state, CompoundTag nbt) {
      return new PartialTile(state, PartialNBT.of(nbt), null);
   }

   public static PartialTile at(Level world, BlockPos pos) {
      return new PartialTile(PartialState.of(world.getBlockState(pos)), PartialNBT.of(world.getBlockEntity(pos)), pos);
   }

   public PartialTile fillMissing(PartialTile tile) {
      this.fillMissing(tile.state, tile.nbt, tile.pos);
      return this;
   }

   public PartialTile fillMissing(PartialState state, CompoundTag nbt, BlockPos pos) {
      if (this.state != null) {
         this.state.fillMissing(state);
      }

      if (this.nbt != null) {
         this.nbt.fillMissing(nbt);
      }

      if (this.pos == null) {
         this.pos = pos;
      }

      return this;
   }

   public PartialTile copyInto(PartialTile target) {
      if (this.state != null) {
         if (target.getState() == null) {
            target.state = this.state.copy();
         } else {
            this.state.copyInto(target.getState());
         }
      }

      if (this.nbt != null) {
         if (target.getNbt() == null) {
            target.nbt = PartialNBT.of(this.nbt.copy());
         } else {
            this.nbt.copyInto(target.getNbt());
         }
      }

      return null;
   }

   public Tag toNBT(CompoundTag nbt) {
      if (this.pos != null) {
         ListTag posNBT = new ListTag();
         posNBT.add(IntTag.valueOf(this.pos.getX()));
         posNBT.add(IntTag.valueOf(this.pos.getY()));
         posNBT.add(IntTag.valueOf(this.pos.getZ()));
         nbt.put("pos", posNBT);
      }

      if (this.state != null) {
         nbt.put("state", this.state.toNBT(new CompoundTag()));
      }

      if (this.nbt != null) {
         nbt.put("nbt", this.nbt.copy());
      }

      return nbt;
   }

   public Tag toPaletteNBT(CompoundTag nbt, int index) {
      if (this.pos != null) {
         ListTag posNBT = new ListTag();
         posNBT.add(IntTag.valueOf(this.pos.getX()));
         posNBT.add(IntTag.valueOf(this.pos.getY()));
         posNBT.add(IntTag.valueOf(this.pos.getZ()));
         nbt.put("pos", posNBT);
      }

      nbt.putInt("state", index);
      if (this.nbt != null) {
         nbt.put("nbt", this.nbt.copy());
      }

      return nbt;
   }

   public static PartialTile fromNBT(CompoundTag tag) {
      PartialState state = null;
      PartialNBT nbt = null;
      BlockPos pos = null;
      if (tag.contains("state", 10)) {
         state = PartialState.fromNBT(tag.getCompound("state"));
      }

      if (tag.contains("pos", 9)) {
         ListTag posNBT = tag.getList("pos", 3);
         pos = new BlockPos(posNBT.getInt(0), posNBT.getInt(1), posNBT.getInt(2));
      }

      if (tag.contains("nbt", 10)) {
         nbt = PartialNBT.of(tag.getCompound("nbt"));
      }

      return new PartialTile(state, nbt, pos);
   }

   public static PartialTile fromPaletteNBT(CompoundTag tag, Function<Integer, PartialState> stateFunction) {
      PartialState state = null;
      PartialNBT nbt = null;
      BlockPos pos = null;
      if (tag.contains("state", 3)) {
         state = stateFunction.apply(tag.getInt("state"));
      }

      if (tag.contains("pos", 9)) {
         ListTag posNBT = tag.getList("pos", 3);
         pos = new BlockPos(posNBT.getInt(0), posNBT.getInt(1), posNBT.getInt(2));
      }

      if (tag.contains("nbt", 10)) {
         nbt = PartialNBT.of(tag.getCompound("nbt"));
      }

      return new PartialTile(state, nbt, pos);
   }

   @Override
   public String toString() {
      StringBuilder sb = new StringBuilder(ForgeRegistries.BLOCKS.getKey(this.state.getBlock()).toString());
      if (!this.state.getProperties().isEmpty()) {
         sb.append('[');
         boolean isNotFirst = false;

         for (Entry<Property<?>, Comparable<?>> entry : this.state.getProperties().entrySet()) {
            if (isNotFirst) {
               sb.append(',');
            }

            sb.append(entry.getKey().getName());
            sb.append('=');
            sb.append(entry.getKey().getName(entry.getValue()));
            isNotFirst = true;
         }

         sb.append(']');
      }

      if (this.nbt != null) {
         sb.append(this.nbt);
      }

      return sb.toString();
   }

   public PartialTile copy() {
      return new PartialTile(this.state == null ? null : this.state.copy(), this.nbt == null ? null : PartialNBT.of(this.nbt.copy()), this.pos);
   }
}
