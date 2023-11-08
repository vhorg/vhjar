package iskallia.vault.core.world.data.entity;

import java.util.Arrays;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public class OrEntityPredicate implements EntityPredicate {
   private EntityPredicate[] children;

   public OrEntityPredicate(EntityPredicate... children) {
      this.children = children;
   }

   public EntityPredicate[] getChildren() {
      return this.children;
   }

   @Override
   public boolean test(Vec3 pos, BlockPos blockPos, PartialCompoundNbt nbt) {
      for (EntityPredicate child : this.children) {
         if (child.test(pos, blockPos, nbt)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public String toString() {
      return Arrays.toString((Object[])this.children);
   }
}
