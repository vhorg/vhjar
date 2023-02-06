package iskallia.vault.core.vault;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.Version;
import iskallia.vault.core.data.DataObject;
import iskallia.vault.core.data.adapter.Adapter;
import iskallia.vault.core.data.compound.UUIDList;
import iskallia.vault.core.data.key.FieldKey;
import iskallia.vault.core.data.key.registry.FieldRegistry;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.core.world.storage.VirtualWorld;
import iskallia.vault.init.ModConfigs;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;

public class NaturalSpawner extends DataObject<NaturalSpawner> {
   public static final FieldRegistry FIELDS = new FieldRegistry();
   public static final FieldKey<Integer> BASE_MAX_MOBS = FieldKey.of("base_max_mobs", Integer.class)
      .with(Version.v1_0, Adapter.ofSegmentedInt(3), DISK.all())
      .register(FIELDS);
   public static final FieldKey<Integer> EXTRA_MAX_MOBS = FieldKey.of("extra_max_mobs", Integer.class)
      .with(Version.v1_0, Adapter.ofSegmentedInt(3), DISK.all())
      .register(FIELDS);
   public static final FieldKey<Integer> MIN_SPAWN_DISTANCE = FieldKey.of("min_spawn_distance", Integer.class)
      .with(Version.v1_0, Adapter.ofSegmentedInt(3), DISK.all())
      .register(FIELDS);
   public static final FieldKey<Integer> MAX_SPAWN_DISTANCE = FieldKey.of("max_spawn_distance", Integer.class)
      .with(Version.v1_0, Adapter.ofSegmentedInt(3), DISK.all())
      .register(FIELDS);
   public static final FieldKey<Integer> DESPAWN_DISTANCE = FieldKey.of("despawn_distance", Integer.class)
      .with(Version.v1_0, Adapter.ofSegmentedInt(3), DISK.all())
      .register(FIELDS);
   public static final FieldKey<UUIDList> SPAWNED_MOBS = FieldKey.of("spawned_mobs", UUIDList.class)
      .with(Version.v1_0, Adapter.ofCompound(), DISK.all(), UUIDList::create)
      .register(FIELDS);

   public NaturalSpawner() {
      this.set(EXTRA_MAX_MOBS, Integer.valueOf(0));
      this.set(SPAWNED_MOBS, UUIDList.create());
   }

   @Override
   public FieldRegistry getFields() {
      return FIELDS;
   }

   public NaturalSpawner setConfig(NaturalSpawner.Config config) {
      this.set(BASE_MAX_MOBS, Integer.valueOf(config.baseMaxMobs));
      this.set(MIN_SPAWN_DISTANCE, Integer.valueOf(config.minSpawnDistance));
      this.set(MAX_SPAWN_DISTANCE, Integer.valueOf(config.maxSpawnDistance));
      this.set(DESPAWN_DISTANCE, Integer.valueOf(config.despawnDistance));
      return this;
   }

   public void tickServer(VirtualWorld world, Vault vault, Listener listener) {
      if (world.getDifficulty() != Difficulty.PEACEFUL) {
         if (world.getGameRules().getBoolean(GameRules.RULE_DOMOBSPAWNING)) {
            listener.getPlayer().ifPresent(player -> {
               this.updateSpawnedMobs(world, player);
               int maxMobs = this.get(BASE_MAX_MOBS) + this.get(EXTRA_MAX_MOBS);
               if (this.get(SPAWNED_MOBS).size() <= maxMobs) {
                  if (this.get(MAX_SPAWN_DISTANCE) > 0) {
                     for (int i = 0; i < 50 && this.get(SPAWNED_MOBS).size() < maxMobs; i++) {
                        this.attemptSpawn(world, vault, player, world.getRandom());
                     }
                  }
               }
            });
         }
      }
   }

   protected void updateSpawnedMobs(VirtualWorld world, ServerPlayer player) {
      this.get(SPAWNED_MOBS).removeIf(uuid -> {
         Entity entity = world.getEntity(uuid);
         if (entity == null) {
            return true;
         } else {
            double distanceSq = entity.distanceToSqr(player);
            double despawnDistance = this.get(DESPAWN_DISTANCE).intValue();
            if (distanceSq > despawnDistance * despawnDistance) {
               entity.remove(RemovalReason.DISCARDED);
               return true;
            } else {
               return false;
            }
         }
      });
   }

   public LivingEntity attemptSpawn(VirtualWorld world, Vault vault, ServerPlayer player, Random random) {
      double min = this.get(MIN_SPAWN_DISTANCE).intValue();
      double max = this.get(MAX_SPAWN_DISTANCE).intValue();
      double angle = (Math.PI * 2) * random.nextDouble();
      double distance = Math.sqrt(random.nextDouble() * (max * max - min * min) + min * min);
      int x = (int)Math.ceil(distance * Math.cos(angle));
      int z = (int)Math.ceil(distance * Math.sin(angle));
      double xzRadius = Math.sqrt(x * x + z * z);
      double yRange = Math.sqrt(max * max - xzRadius * xzRadius);
      int y = random.nextInt((int)Math.ceil(yRange) * 2 + 1) - (int)Math.ceil(yRange);
      BlockPos pos = player.blockPosition();
      LivingEntity spawned = spawnMob(world, vault, pos.getX() + x, pos.getY() + y, pos.getZ() + z, random);
      if (spawned != null) {
         this.get(SPAWNED_MOBS).add(spawned.getUUID());
      }

      return spawned;
   }

   @Nullable
   public static LivingEntity spawnMob(VirtualWorld world, Vault vault, int x, int y, int z, Random random) {
      LivingEntity entity = createMob(world, vault.get(Vault.LEVEL).get(), random);
      BlockState state = world.getBlockState(new BlockPos(x, y - 1, z));
      if (!state.isValidSpawn(world, new BlockPos(x, y - 1, z), entity.getType())) {
         return null;
      } else {
         AABB entityBox = entity.getType().getAABB(x + 0.5, y, z + 0.5);
         if (!world.noCollision(entityBox)) {
            return null;
         } else {
            entity.moveTo(x + 0.5F, y + 0.2F, z + 0.5F, (float)(random.nextDouble() * 2.0 * Math.PI), 0.0F);
            if (entity instanceof Mob) {
               ((Mob)entity).spawnAnim();
               ((Mob)entity).finalizeSpawn(world, new DifficultyInstance(Difficulty.PEACEFUL, 13000L, 0L, 0.0F), MobSpawnType.STRUCTURE, null, null);
            }

            world.addWithUUID(entity);
            return entity;
         }
      }
   }

   private static LivingEntity createMob(VirtualWorld world, int vaultLevel, Random random) {
      return ModConfigs.VAULT_MOBS.getForLevel(vaultLevel).MOB_POOL.getRandom(random).orElseThrow().create(world);
   }

   public static class Config {
      @Expose
      public final int baseMaxMobs;
      @Expose
      public final int minSpawnDistance;
      @Expose
      public final int maxSpawnDistance;
      @Expose
      public final int despawnDistance;

      private Config() {
         this(0, 0, 0, 0);
      }

      public Config(int baseMaxMobs, int minSpawnDistance, int maxSpawnDistance, int despawnDistance) {
         this.baseMaxMobs = baseMaxMobs;
         this.minSpawnDistance = minSpawnDistance;
         this.maxSpawnDistance = maxSpawnDistance;
         this.despawnDistance = despawnDistance;
      }
   }
}
