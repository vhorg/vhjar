package iskallia.vault.core.world.data;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.entity.BlockEntity;

public class PartialNBT extends CompoundTag {
   protected PartialNBT() {
   }

   public static PartialNBT empty() {
      return new PartialNBT();
   }

   public static PartialNBT of(CompoundTag delegate) {
      PartialNBT nbt = new PartialNBT();
      delegate.getAllKeys().forEach(key -> nbt.put(key, delegate.get(key)));
      return nbt;
   }

   public static PartialNBT of(Entity entity) {
      if (entity == null) {
         return empty();
      } else {
         CompoundTag nbt = new CompoundTag();
         nbt.putString("id", EntityType.getKey(entity.getType()).toString());
         return of(entity.saveWithoutId(nbt));
      }
   }

   public static PartialNBT of(BlockEntity blockEntity) {
      return blockEntity == null ? empty() : of(blockEntity.saveWithFullMetadata());
   }

   public boolean isSubsetOf(CompoundTag nbt) {
      for (String key : nbt.getAllKeys()) {
         Tag nbt1 = nbt.get(key);
         Tag nbt2 = this.get(key);
         if (nbt1 == null) {
            throw new UnsupportedOperationException("Key cannot return a null element");
         }

         if (nbt2 != null) {
            if (nbt1.getType() != nbt2.getType()) {
               return false;
            }

            if (nbt1.getId() == 10) {
               if (!of((CompoundTag)nbt2).isSubsetOf(of((CompoundTag)nbt1))) {
                  return false;
               }
            } else if (!nbt1.equals(nbt2)) {
               return false;
            }
         }
      }

      return true;
   }

   public void fillMissing(CompoundTag nbt) {
      if (nbt != null) {
         for (String key : nbt.getAllKeys()) {
            Tag e = this.get(key);
            if (e != null) {
               if (!this.contains(key)) {
                  this.put(key, e.copy());
               }

               if (e.getId() == 10) {
                  PartialNBT child = of(this.getCompound(key));
                  child.fillMissing((CompoundTag)e);
                  this.put(key, child);
               }
            }
         }
      }
   }

   public void copyInto(CompoundTag target) {
      for (String key : this.getAllKeys()) {
         Tag e = this.get(key);
         if (e != null) {
            e = e.copy();
            if (e.getId() == 10) {
               if (!target.contains(key)) {
                  target.put(key, e);
               } else {
                  of((CompoundTag)e).copyInto(target.getCompound(key));
               }
            } else {
               target.put(key, e);
            }
         }
      }
   }
}
