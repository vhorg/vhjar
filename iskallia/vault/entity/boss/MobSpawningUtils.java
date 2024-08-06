package iskallia.vault.entity.boss;

import iskallia.vault.VaultMod;
import java.util.Optional;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public class MobSpawningUtils {
   public static final String TAG_BOSS_SUMMONED = "boss_summoned";

   @Nullable
   public static Entity spawnMob(ServerLevel serverLevel, double radius, EntityType<?> entityType, CompoundTag entityNbt, Vec3 spawnCenter) {
      return spawnMob(serverLevel, radius, 0, entityType, entityNbt, spawnCenter, false);
   }

   public static Entity spawnMob(
      ServerLevel serverLevel, double radius, int maxYOffset, EntityType<?> entityType, CompoundTag entityNbt, Vec3 spawnCenter, boolean checkCollision
   ) {
      double x = spawnCenter.x() + (serverLevel.random.nextDouble() - serverLevel.random.nextDouble()) * radius + 0.5;
      double y = spawnCenter.y() + (maxYOffset > 0 ? serverLevel.random.nextInt(2 * maxYOffset) - maxYOffset : 0);
      double z = spawnCenter.z() + (serverLevel.random.nextDouble() - serverLevel.random.nextDouble()) * radius + 0.5;

      int remainingTries;
      for (remainingTries = 20; checkCollision && remainingTries > 0 && !serverLevel.noCollision(entityType.getAABB(x, y, z)); remainingTries--) {
         x = spawnCenter.x() + (serverLevel.random.nextDouble() - serverLevel.random.nextDouble()) * radius + 0.5;
         y = spawnCenter.y();
         z = spawnCenter.z() + (serverLevel.random.nextDouble() - serverLevel.random.nextDouble()) * radius + 0.5;
      }

      if (remainingTries == 0) {
         return null;
      } else {
         BlockPos spawnPos = new BlockPos(x, y, z);
         Entity entity = entityType.spawn(serverLevel, null, null, spawnPos, MobSpawnType.SPAWNER, false, false);
         if (entity == null) {
            VaultMod.LOGGER.error("Unable to spawn entity type {} because its factory returned null", entityType.getRegistryName());
            return null;
         } else {
            if (entityNbt != null) {
               CompoundTag entityTag = entity.saveWithoutId(new CompoundTag());
               entityTag.merge(entityNbt.copy());
               entity.load(entityTag);
            }

            if (entity instanceof Mob) {
               ((Mob)entity).spawnAnim();
            }

            entity.getTags().add("boss_summoned");
            return entity;
         }
      }
   }

   public record EntitySpawnData(EntityType<?> entityType, @javax.annotation.Nullable CompoundTag entityNbt) {
      public static Optional<MobSpawningUtils.EntitySpawnData> from(CompoundTag tag) {
         EntityType<?> entityType = (EntityType<?>)ForgeRegistries.ENTITIES.getValue(new ResourceLocation(tag.getString("EntityType")));
         CompoundTag entityNbt = tag.contains("EntityNbt") ? tag.getCompound("EntityNbt") : null;
         return entityType == null ? Optional.empty() : Optional.of(new MobSpawningUtils.EntitySpawnData(entityType, entityNbt));
      }

      public CompoundTag serializeTo(CompoundTag tag) {
         tag.putString("EntityType", this.entityType.getRegistryName().toString());
         if (this.entityNbt != null) {
            tag.put("EntityNbt", this.entityNbt);
         }

         return tag;
      }
   }
}
