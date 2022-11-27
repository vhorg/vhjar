package iskallia.vault.skill.ability.effect.sub;

import iskallia.vault.skill.ability.config.sub.DashDamageConfig;
import iskallia.vault.skill.ability.effect.DashAbility;
import iskallia.vault.skill.ability.effect.spi.core.AbilityActionResult;
import iskallia.vault.skill.ability.effect.spi.core.AbilityTickResult;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.entity.player.Player;

public class DashDamageAbility extends DashAbility<DashDamageConfig> {
   private static final double DAMAGE_RANGE = 2.0;
   private static final int DAMAGE_DURATION_TICKS = 20;
   private static final TargetingConditions TARGETING_CONDITIONS = TargetingConditions.forCombat().range(2.0).selector(entity -> !(entity instanceof Player));
   private static final Map<UUID, DashDamageAbility.PlayerDashDamageData> PLAYER_DATA_MAP = new HashMap<>();

   protected AbilityActionResult doAction(DashDamageConfig config, ServerPlayer player, boolean active) {
      super.doAction(config, player, active);
      PLAYER_DATA_MAP.put(player.getUUID(), new DashDamageAbility.PlayerDashDamageData(20));
      return AbilityActionResult.SUCCESS_COOLDOWN;
   }

   public AbilityTickResult onTick(DashDamageConfig config, ServerPlayer player, boolean active) {
      UUID playerUUID = player.getUUID();
      DashDamageAbility.PlayerDashDamageData data = PLAYER_DATA_MAP.get(playerUUID);
      if (data == null) {
         return AbilityTickResult.PASS;
      } else {
         for (LivingEntity nearbyEntity : player.level
            .getNearbyEntities(LivingEntity.class, TARGETING_CONDITIONS, player, player.getBoundingBox().inflate(4.0))) {
            UUID nearbyEntityUUID = nearbyEntity.getUUID();
            if (!data.hitEntityIdSet.contains(nearbyEntityUUID)) {
               float playerAttackDamage = (float)player.getAttributeValue(Attributes.ATTACK_DAMAGE);
               float playerDashDamage = playerAttackDamage * config.getAttackDamagePercentPerDash();
               nearbyEntity.hurt(DamageSource.playerAttack(player), playerDashDamage);
               data.hitEntityIdSet.add(nearbyEntityUUID);
            }
         }

         data.durationTicks--;
         if (data.durationTicks <= 0) {
            PLAYER_DATA_MAP.remove(playerUUID);
         }

         return AbilityTickResult.PASS;
      }
   }

   private static class PlayerDashDamageData {
      private final Set<UUID> hitEntityIdSet = new HashSet<>();
      private int durationTicks;

      private PlayerDashDamageData(int durationTicks) {
         this.durationTicks = durationTicks;
      }
   }
}
