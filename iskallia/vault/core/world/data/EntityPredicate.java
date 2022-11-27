package iskallia.vault.core.world.data;

import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;

@FunctionalInterface
public interface EntityPredicate extends Predicate<PartialEntity> {
   boolean test(Vec3 var1, BlockPos var2, PartialNBT var3);

   default boolean test(PartialEntity entity) {
      return this.test(entity.getPos(), entity.getBlockPos(), entity.getNBT());
   }

   static EntityPredicate of(EntityType<?> type) {
      return (pos, blockPos, nbt) -> nbt.getString("id").equals(EntityType.getKey(type).toString());
   }
}
