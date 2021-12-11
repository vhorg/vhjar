package iskallia.vault.skill.ability.effect.sub;

import iskallia.vault.event.ActiveFlags;
import iskallia.vault.skill.ability.config.sub.DashDamageConfig;
import iskallia.vault.skill.ability.effect.DashAbility;
import iskallia.vault.util.EntityHelper;
import iskallia.vault.util.PlayerDamageHelper;
import iskallia.vault.util.ServerScheduler;
import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.DamageSource;

public class DashDamageAbility extends DashAbility<DashDamageConfig> {
   public boolean onAction(DashDamageConfig config, ServerPlayerEntity player, boolean active) {
      if (!super.onAction(config, player, active)) {
         return false;
      } else {
         List<LivingEntity> other = EntityHelper.getNearby(player.func_130014_f_(), player.func_233580_cy_(), config.getRadiusOfAttack(), LivingEntity.class);
         other.removeIf(e -> e instanceof PlayerEntity);
         float atk = (float)player.func_233637_b_(Attributes.field_233823_f_);

         for (LivingEntity entity : other) {
            ActiveFlags.IS_AOE_ATTACKING
               .runIfNotSet(() -> entity.func_70097_a(DamageSource.func_76365_a(player), atk * config.getAttackDamagePercentPerDash()));
            ServerScheduler.INSTANCE
               .schedule(0, () -> PlayerDamageHelper.applyMultiplier(player, 0.95F, PlayerDamageHelper.Operation.STACKING_MULTIPLY, true, config.getCooldown()));
         }

         return true;
      }
   }
}
