package iskallia.vault.world.vault.logic;

import iskallia.vault.config.VaultMobsConfig;
import iskallia.vault.entity.EntityScaler;
import iskallia.vault.entity.FighterEntity;
import iskallia.vault.entity.VaultBoss;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModEntities;
import iskallia.vault.world.data.GlobalDifficultyData;
import iskallia.vault.world.vault.VaultRaid;
import java.util.Random;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.MobEntity;
import net.minecraft.entity.effect.LightningBoltEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.server.ServerWorld;

public class VaultBossSpawner {
   public static LivingEntity spawnBoss(VaultRaid vault, ServerWorld world, BlockPos pos) {
      int level = vault.getProperties().getValue(VaultRaid.LEVEL);
      String playerBossName = vault.getProperties().getBase(VaultRaid.PLAYER_BOSS_NAME).orElse(null);
      EntityScaler.Type bossScalingType = EntityScaler.Type.BOSS;
      VaultMobsConfig.Mob bossConfig = ModConfigs.VAULT_MOBS.getForLevel(level).BOSS_POOL.getRandom(world.func_201674_k());
      LivingEntity boss;
      ITextComponent bossName;
      if (vault.getProperties().getBaseOrDefault(VaultRaid.COW_VAULT, false)) {
         boss = (LivingEntity)ModEntities.AGGRESSIVE_COW_BOSS.func_200721_a(world);
         boss.func_184211_a("replaced_entity");
         bossName = new StringTextComponent("an ordinary Vault Boss");
      } else {
         if (playerBossName == null) {
            bossName = new StringTextComponent("Boss");
         } else {
            bossConfig = ModConfigs.VAULT_MOBS.getForLevel(level).RAFFLE_BOSS_POOL.getRandom(world.func_201674_k());
            bossName = new StringTextComponent(playerBossName);
            bossScalingType = EntityScaler.Type.RAFFLE_BOSS;
         }

         boss = bossConfig.create(world);
      }

      GlobalDifficultyData.Difficulty difficulty = GlobalDifficultyData.get(world).getVaultDifficulty();
      VaultMobsConfig.Mob.scale(boss, vault, difficulty);
      EntityScaler.setScaled(boss);
      if (boss instanceof FighterEntity) {
         ((FighterEntity)boss).changeSize(2.0F);
      }

      boss.func_70012_b(pos.func_177958_n() + 0.5, pos.func_177956_o() + 0.2, pos.func_177952_p() + 0.5, 0.0F, 0.0F);
      boss.func_184216_O().add("vault_boss");
      world.func_217470_d(boss);
      if (boss instanceof FighterEntity) {
         ((FighterEntity)boss).bossInfo.func_186758_d(true);
      }

      if (boss instanceof VaultBoss) {
         ((VaultBoss)boss).getServerBossInfo().func_186758_d(true);
      }

      EntityScaler.setScaledEquipment(boss, vault, difficulty, level, new Random(), bossScalingType);
      boss.func_200203_b(bossName);
      if (boss instanceof MobEntity) {
         ((MobEntity)boss).func_110163_bv();
      }

      for (int i = 0; i < 5; i++) {
         BlockPos pos2 = pos.func_177982_a(world.field_73012_v.nextInt(100) - 50, 0, world.field_73012_v.nextInt(100) - 50);
         pos2 = world.func_205770_a(Type.MOTION_BLOCKING, pos2);
         LightningBoltEntity bolt = (LightningBoltEntity)EntityType.field_200728_aG.func_200721_a(world);
         bolt.func_233576_c_(Vector3d.func_237492_c_(pos2));
         bolt.func_233623_a_(true);
         world.func_217376_c(bolt);
      }

      return boss;
   }
}
