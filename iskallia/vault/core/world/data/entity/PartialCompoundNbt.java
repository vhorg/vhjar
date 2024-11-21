package iskallia.vault.core.world.data.entity;

import com.google.gson.JsonElement;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.world.data.item.ItemPlacement;
import iskallia.vault.core.world.data.item.PartialItem;
import iskallia.vault.core.world.data.tile.PartialBlockState;
import iskallia.vault.core.world.data.tile.TilePlacement;
import iskallia.vault.item.crystal.data.adapter.ISimpleAdapter;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.TagParser;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.CommonLevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.Vec3;

public class PartialCompoundNbt implements TilePlacement<PartialCompoundNbt>, EntityPlacement<PartialCompoundNbt>, ItemPlacement<PartialCompoundNbt> {
   private CompoundTag nbt;

   protected PartialCompoundNbt(CompoundTag nbt) {
      this.nbt = nbt;
   }

   public static PartialCompoundNbt empty() {
      return new PartialCompoundNbt(null);
   }

   public static PartialCompoundNbt of(CompoundTag nbt) {
      return new PartialCompoundNbt(nbt);
   }

   public static PartialCompoundNbt of(Entity entity) {
      if (entity == null) {
         return new PartialCompoundNbt(null);
      } else {
         CompoundTag nbt = new CompoundTag();
         nbt.putString("id", EntityType.getKey(entity.getType()).toString());
         return new PartialCompoundNbt(entity.saveWithoutId(nbt));
      }
   }

   public static PartialCompoundNbt of(BlockEntity blockEntity) {
      return blockEntity == null ? new PartialCompoundNbt(null) : new PartialCompoundNbt(blockEntity.saveWithFullMetadata());
   }

   public static PartialCompoundNbt at(BlockGetter world, BlockPos pos) {
      return of(world.getBlockEntity(pos));
   }

   public static PartialCompoundNbt of(ItemStack stack) {
      return new PartialCompoundNbt(stack.getTag());
   }

   public boolean isSubsetOf(PartialCompoundNbt other) {
      if (this.nbt == null) {
         return true;
      } else if (other.nbt != null && this.nbt.size() <= other.nbt.size()) {
         for (String key : this.nbt.getAllKeys()) {
            Tag nbt1 = this.nbt.get(key);
            Tag nbt2 = other.nbt.get(key);
            if (nbt1 != null) {
               if (nbt2 == null || nbt1.getType() != nbt2.getType()) {
                  return false;
               }

               if (nbt1.getId() == 10) {
                  if (!of((CompoundTag)nbt1).isSubsetOf(of((CompoundTag)nbt2))) {
                     return false;
                  }
               } else if (nbt1 instanceof CollectionTag) {
                  if (!PartialListNbt.of((CollectionTag<?>)nbt1).isSubsetOf(PartialListNbt.of((CollectionTag<?>)nbt2))) {
                     return false;
                  }
               } else if (!nbt1.equals(nbt2)) {
                  return false;
               }
            }
         }

         return true;
      } else {
         return false;
      }
   }

   @Override
   public boolean isSubsetOf(LevelReader world, BlockPos pos) {
      if (this.nbt == null) {
         return true;
      } else {
         BlockEntity blockEntity = world.getBlockEntity(pos);
         return blockEntity != null && this.isSubsetOf(of(blockEntity.saveWithId()));
      }
   }

   @Override
   public boolean isSubsetOf(Entity entity) {
      return this.isSubsetOf(of(entity));
   }

   @Override
   public boolean isSubsetOf(ItemStack stack) {
      return this.isSubsetOf(of(stack));
   }

   public void fillInto(PartialCompoundNbt other) {
      if (this.nbt != null) {
         if (other.nbt == null) {
            other.nbt = new CompoundTag();
         }

         for (String key : this.nbt.getAllKeys()) {
            Tag e = this.nbt.get(key);
            if (e != null) {
               e = e.copy();
               if (e.getId() == 10) {
                  if (!other.nbt.contains(key)) {
                     other.nbt.put(key, e);
                  } else {
                     of((CompoundTag)e).fillInto(of(other.nbt.getCompound(key)));
                  }
               } else {
                  other.nbt.put(key, e);
               }
            }
         }
      }
   }

   @Override
   public void place(CommonLevelAccessor world, BlockPos pos, int flags) {
      if (this.nbt != null) {
         BlockEntity blockEntity = world.getBlockEntity(pos);
         if (blockEntity != null) {
            blockEntity.load(this.nbt);
         }
      }
   }

   @Override
   public void place(CommonLevelAccessor world) {
   }

   @Override
   public Optional<ItemStack> generate(int count) {
      return Optional.empty();
   }

   @Override
   public boolean test(PartialBlockState state, PartialCompoundNbt nbt) {
      return this.isSubsetOf(nbt);
   }

   @Override
   public boolean test(Vec3 pos, BlockPos blockPos, PartialCompoundNbt nbt) {
      return this.isSubsetOf(nbt);
   }

   @Override
   public boolean test(PartialItem item, PartialCompoundNbt nbt) {
      return this.isSubsetOf(nbt);
   }

   public Optional<CompoundTag> asWhole() {
      return Optional.ofNullable(this.nbt);
   }

   public PartialCompoundNbt copy() {
      return new PartialCompoundNbt(this.nbt == null ? null : this.nbt.copy());
   }

   @Override
   public String toString() {
      return this.nbt == null ? "" : this.nbt.toString();
   }

   public static Optional<PartialCompoundNbt> parse(String string, boolean logErrors) {
      try {
         return Optional.of(parse(new StringReader(string)));
      } catch (IllegalArgumentException | CommandSyntaxException var3) {
         if (logErrors) {
            var3.printStackTrace();
         }

         return Optional.empty();
      }
   }

   public static PartialCompoundNbt parse(String string) throws CommandSyntaxException {
      return parse(new StringReader(string));
   }

   public static PartialCompoundNbt parse(StringReader reader) throws CommandSyntaxException {
      if (reader.canRead() && reader.peek() == '{') {
         String string = reader.getString().substring(reader.getCursor());
         int index = string.lastIndexOf(125);
         if (index < 0) {
            throw new IllegalArgumentException("Unclosed nbt in tile '" + reader.getString() + "'");
         } else {
            return of(new TagParser(new StringReader(string.substring(0, index + 1))).readStruct());
         }
      } else {
         return of((CompoundTag)null);
      }
   }

   public static class Adapter implements ISimpleAdapter<PartialCompoundNbt, Tag, JsonElement> {
      public void writeBits(PartialCompoundNbt value, BitBuffer buffer) {
         buffer.writeBoolean(value == null);
         if (value != null) {
            Adapters.COMPOUND_NBT.asNullable().writeBits(value.nbt, buffer);
         }
      }

      @Override
      public Optional<PartialCompoundNbt> readBits(BitBuffer buffer) {
         return buffer.readBoolean() ? Optional.empty() : Optional.of(PartialCompoundNbt.of(Adapters.COMPOUND_NBT.asNullable().readBits(buffer).orElse(null)));
      }

      public Optional<Tag> writeNbt(@Nullable PartialCompoundNbt value) {
         return value == null ? Optional.empty() : Adapters.COMPOUND_NBT.writeNbt(value.nbt);
      }

      @Override
      public Optional<PartialCompoundNbt> readNbt(@Nullable Tag nbt) {
         return nbt == null ? Optional.empty() : Adapters.COMPOUND_NBT.readNbt(nbt).map(PartialCompoundNbt::of);
      }
   }
}
