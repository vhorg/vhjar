package iskallia.vault.core.world.data;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.CommonLevelAccessor;

public interface EntityPlacement<T> extends EntityPredicate {
   boolean isSubsetOf(T var1);

   boolean isSubsetOf(Entity var1);

   void fillInto(T var1);

   void place(CommonLevelAccessor var1);

   T copy();
}
