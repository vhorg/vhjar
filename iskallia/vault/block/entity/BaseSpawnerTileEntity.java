package iskallia.vault.block.entity;

import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.registries.ForgeRegistries;

public abstract class BaseSpawnerTileEntity extends BlockEntity {
   public BaseSpawnerTileEntity(BlockEntityType<?> type, BlockPos pos, BlockState state) {
      super(type, pos, state);
   }

   protected static void spawnEntity(
      BlockPos blockPos,
      ServerLevel serverLevel,
      ResourceLocation entityName,
      @Nullable CompoundTag entityNbt,
      boolean isPersistent,
      Runnable logEntityTypeMissing
   ) {
      EntityType<?> entityType = (EntityType<?>)ForgeRegistries.ENTITIES.getValue(entityName);
      if (entityType == null) {
         logEntityTypeMissing.run();
      } else {
         Entity entity = entityType.spawn(serverLevel, null, null, blockPos, MobSpawnType.SPAWNER, false, false);
         if (entityNbt != null) {
            CompoundTag entityTag = entity.saveWithoutId(new CompoundTag());
            entityTag.merge(entityNbt.copy());
            entity.load(entityTag);
         }

         if (entity instanceof Mob mob && isPersistent) {
            mob.setPersistenceRequired();
         }
      }
   }
}
