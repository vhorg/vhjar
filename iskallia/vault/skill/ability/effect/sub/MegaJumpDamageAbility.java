package iskallia.vault.skill.ability.effect.sub;

import iskallia.vault.skill.ability.config.sub.MegaJumpDamageConfig;
import iskallia.vault.skill.ability.effect.MegaJumpAbility;
import iskallia.vault.util.EntityHelper;
import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.ai.attributes.Attributes;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.DamageSource;

public class MegaJumpDamageAbility extends MegaJumpAbility<MegaJumpDamageConfig> {
   public boolean onAction(MegaJumpDamageConfig config, PlayerEntity player, boolean active) {
      if (super.onAction(config, player, active)) {
         List<LivingEntity> entities = EntityHelper.getNearby(player.func_130014_f_(), player.func_233580_cy_(), config.getRadius(), LivingEntity.class);
         entities.removeIf(e -> e instanceof PlayerEntity);
         float atk = (float)player.func_233637_b_(Attributes.field_233823_f_) * config.getPercentAttackDamageDealt();
         DamageSource src = DamageSource.func_76365_a(player);

         for (LivingEntity entity : entities) {
            entity.func_70097_a(src, atk);
            double xDiff = player.func_226277_ct_() - entity.func_226277_ct_();
            double zDiff = player.func_226281_cx_() - entity.func_226281_cx_();
            if (xDiff * xDiff + zDiff * zDiff < 1.0E-4) {
               xDiff = (Math.random() - Math.random()) * 0.01;
               zDiff = (Math.random() - Math.random()) * 0.01;
            }

            entity.func_233627_a_(0.4F * config.getKnockbackStrengthMultiplier(), xDiff, zDiff);
         }

         return true;
      } else {
         return false;
      }
   }
}