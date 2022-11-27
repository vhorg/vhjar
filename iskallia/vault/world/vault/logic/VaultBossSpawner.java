package iskallia.vault.world.vault.logic;

import iskallia.vault.config.VaultMobsConfig;
import iskallia.vault.entity.LegacyEntityScaler;
import iskallia.vault.entity.VaultBoss;
import iskallia.vault.entity.entity.FighterEntity;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModEntities;
import iskallia.vault.world.vault.VaultRaid;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LightningBolt;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.Vec3;

public class VaultBossSpawner {
   public static LivingEntity spawnBossLegacy(VaultRaid vault, ServerLevel world, BlockPos pos) {
      int level = vault.getProperties().getValue(VaultRaid.LEVEL);
      String playerBossName = vault.getProperties().getBase(VaultRaid.PLAYER_BOSS_NAME).orElse(null);
      LegacyEntityScaler.Type bossScalingType = LegacyEntityScaler.Type.BOSS;
      VaultMobsConfig.Mob bossConfig = ModConfigs.VAULT_MOBS.getForLevel(level).BOSS_POOL.getRandom(world.getRandom()).orElseThrow();
      LivingEntity boss;
      Component bossName;
      if (vault.getProperties().getBaseOrDefault(VaultRaid.COW_VAULT, false)) {
         boss = (LivingEntity)ModEntities.AGGRESSIVE_COW_BOSS.create(world);
         boss.addTag("replaced_entity");
         bossName = new TextComponent("an ordinary Vault Boss");
      } else {
         if (playerBossName == null) {
            bossName = new TextComponent("Boss");
         } else {
            bossConfig = ModConfigs.VAULT_MOBS.getForLevel(level).RAFFLE_BOSS_POOL.getRandom(world.getRandom()).orElseThrow();
            bossName = new TextComponent(playerBossName);
            bossScalingType = LegacyEntityScaler.Type.RAFFLE_BOSS;
         }

         boss = bossConfig.create(world);
      }

      LegacyEntityScaler.setScaled(boss);
      if (boss instanceof FighterEntity) {
         ((FighterEntity)boss).changeSize(2.0F);
      }

      boss.moveTo(pos.getX() + 0.5, pos.getY() + 0.2, pos.getZ() + 0.5, 0.0F, 0.0F);
      boss.getTags().add("vault_boss");
      world.addWithUUID(boss);
      if (boss instanceof FighterEntity) {
         ((FighterEntity)boss).bossInfo.setVisible(true);
      }

      if (boss instanceof VaultBoss) {
         ((VaultBoss)boss).getServerBossInfo().setVisible(true);
      }

      LegacyEntityScaler.setScaledEquipmentLegacy(boss, vault, level, new Random(), bossScalingType);
      boss.setCustomName(bossName);
      if (boss instanceof Mob) {
         ((Mob)boss).setPersistenceRequired();
      }

      for (int i = 0; i < 5; i++) {
         BlockPos pos2 = pos.offset(world.random.nextInt(100) - 50, 0, world.random.nextInt(100) - 50);
         LightningBolt bolt = (LightningBolt)EntityType.LIGHTNING_BOLT.create(world);
         bolt.moveTo(Vec3.atBottomCenterOf(pos2));
         bolt.setVisualOnly(true);
         world.addFreshEntity(bolt);
      }

      return boss;
   }
}
