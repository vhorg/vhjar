package iskallia.vault.core.world.data;

import iskallia.vault.core.world.data.tile.PartialBlockState;
import iskallia.vault.core.world.data.tile.TilePlacement;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CollectionTag;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.CommonLevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.phys.Vec3;

public class PartialListNbt implements TilePlacement<PartialListNbt>, EntityPlacement<PartialListNbt> {
   private CollectionTag<?> nbt;

   protected PartialListNbt(CollectionTag<?> nbt) {
      this.nbt = nbt;
   }

   public static PartialListNbt empty() {
      return new PartialListNbt(new ListTag());
   }

   public static PartialListNbt of(CollectionTag<?> nbt) {
      return new PartialListNbt(nbt);
   }

   public boolean isSubsetOf(PartialListNbt other) {
      if (this.nbt == null) {
         return true;
      } else if (other.nbt != null && this.nbt.size() <= other.nbt.size()) {
         for (Tag e1 : this.nbt) {
            for (Tag e2 : other.nbt) {
               if (e1.getType() == e2.getType()) {
                  if (e1.getId() == 10) {
                     if (!PartialCompoundNbt.of((CompoundTag)e1).isSubsetOf(PartialCompoundNbt.of((CompoundTag)e2))) {
                        return false;
                     }
                  } else if (e1 instanceof CollectionTag) {
                     if (!of((CollectionTag<?>)e1).isSubsetOf(of((CollectionTag<?>)e2))) {
                        return false;
                     }
                  } else if (!e1.equals(e2)) {
                     return false;
                  }
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
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean isSubsetOf(Entity entity) {
      throw new UnsupportedOperationException();
   }

   public void fillInto(PartialListNbt other) {
      throw new UnsupportedOperationException();
   }

   @Override
   public void place(CommonLevelAccessor world) {
   }

   @Override
   public void place(CommonLevelAccessor world, BlockPos pos, int flags) {
      throw new UnsupportedOperationException();
   }

   @Override
   public boolean test(Vec3 pos, BlockPos blockPos, PartialCompoundNbt nbt) {
      return false;
   }

   @Override
   public boolean test(PartialBlockState state, PartialCompoundNbt nbt) {
      return false;
   }

   public Optional<CollectionTag<?>> asWhole() {
      return Optional.ofNullable(this.nbt);
   }

   public PartialListNbt copy() {
      return new PartialListNbt(this.nbt == null ? null : (CollectionTag)this.nbt.copy());
   }
}
