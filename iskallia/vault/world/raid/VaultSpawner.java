package iskallia.vault.world.raid;

import iskallia.vault.Vault;
import iskallia.vault.config.VaultMobsConfig;
import iskallia.vault.init.ModConfigs;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.SpawnReason;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Difficulty;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.server.ServerWorld;

public class VaultSpawner {
   private final VaultRaid raid;
   private List<LivingEntity> mobs = new ArrayList<>();
   public int maxMobs;

   public VaultSpawner(VaultRaid raid) {
      this.raid = raid;
   }

   public void init() {
      VaultMobsConfig.Level config = ModConfigs.VAULT_MOBS.getForLevel(this.raid.level);
      this.maxMobs = config.MOB_MISC.MAX_MOBS;
   }

   public int getMaxMobs() {
      return this.maxMobs;
   }

   public void tick(ServerPlayerEntity player) {
      if (player.field_70170_p.func_234923_W_() == Vault.VAULT_KEY) {
         if (this.raid.ticksLeft + 300 <= this.raid.sTickLeft) {
            this.mobs.removeIf(entity -> {
               if (entity.func_70068_e(player) > 576.0) {
                  entity.func_70106_y();
                  return true;
               } else {
                  return false;
               }
            });
            if (this.mobs.size() < this.getMaxMobs()) {
               List<BlockPos> spaces = this.getSpawningSpaces(player);

               while (this.mobs.size() < this.getMaxMobs() && spaces.size() > 0) {
                  BlockPos pos = spaces.remove(player.func_71121_q().func_201674_k().nextInt(spaces.size()));
                  this.spawn(player.func_71121_q(), pos);
               }
            }
         }
      }
   }

   private List<BlockPos> getSpawningSpaces(ServerPlayerEntity player) {
      List<BlockPos> spaces = new ArrayList<>();

      for (int x = -18; x <= 18; x++) {
         for (int z = -18; z <= 18; z++) {
            for (int y = -5; y <= 5; y++) {
               ServerWorld world = player.func_71121_q();
               BlockPos pos = player.func_233580_cy_().func_177971_a(new BlockPos(x, y, z));
               if (!(player.func_70092_e(pos.func_177958_n(), pos.func_177956_o(), pos.func_177952_p()) < 100.0)
                  && world.func_180495_p(pos).func_215688_a(world, pos, EntityType.field_200725_aD)) {
                  boolean isAir = true;

                  for (int o = 1; o <= 2; o++) {
                     if (world.func_180495_p(pos.func_177981_b(o)).func_229980_m_(world, pos)) {
                        isAir = false;
                        break;
                     }
                  }

                  if (isAir) {
                     spaces.add(pos.func_177984_a());
                  }
               }
            }
         }
      }

      return spaces;
   }

   public void spawn(ServerWorld world, BlockPos pos) {
      VaultMobsConfig.Mob mob = ModConfigs.VAULT_MOBS.getForLevel(this.raid.level).MOB_POOL.getRandom(world.field_73012_v);
      if (mob != null) {
         LivingEntity entity = mob.create(world);
         if (entity != null) {
            entity.func_70012_b(pos.func_177958_n() + 0.5F, pos.func_177956_o() + 0.2F, pos.func_177952_p() + 0.5F, 0.0F, 0.0F);
            world.func_217470_d(entity);
            if (entity instanceof MobEntity) {
               ((MobEntity)entity).func_70656_aK();
               ((MobEntity)entity).func_213386_a(world, new DifficultyInstance(Difficulty.PEACEFUL, 13000L, 0L, 0.0F), SpawnReason.STRUCTURE, null, null);
            }

            this.mobs.add(entity);
         }
      }
   }
}
