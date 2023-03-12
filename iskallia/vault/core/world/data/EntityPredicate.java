package iskallia.vault.core.world.data;

import iskallia.vault.init.ModConfigs;
import java.util.function.Predicate;
import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;

@FunctionalInterface
public interface EntityPredicate extends Predicate<PartialEntity> {
   boolean test(Vec3 var1, BlockPos var2, PartialNBT var3);

   default boolean test(PartialEntity entity) {
      return this.test(entity.getPos(), entity.getBlockPos(), entity.getNBT());
   }

   default boolean test(Entity entity) {
      return this.test(entity.position(), entity.blockPosition(), PartialNBT.of(entity.serializeNBT()));
   }

   static EntityPredicate of(String target) {
      target = target.trim();
      if (target.startsWith("@")) {
         ResourceLocation groupId = new ResourceLocation(target.substring(1));
         return (pos, blockPos, nbt) -> ModConfigs.ENTITY_GROUPS.isInGroup(groupId, pos, blockPos, nbt);
      } else {
         ResourceLocation entityId = new ResourceLocation(target);
         return (pos, blockPos, nbt) -> {
            ResourceLocation checkId = new ResourceLocation(nbt.getString("id"));
            return checkId.equals(entityId);
         };
      }
   }

   static EntityPredicate of(EntityType<?> type) {
      return (pos, blockPos, nbt) -> nbt.getString("id").equals(EntityType.getKey(type).toString());
   }
}
