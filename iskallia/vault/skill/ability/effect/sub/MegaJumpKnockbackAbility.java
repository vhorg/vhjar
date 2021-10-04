package iskallia.vault.skill.ability.effect.sub;

import iskallia.vault.skill.ability.config.sub.MegaJumpKnockbackConfig;
import iskallia.vault.skill.ability.effect.MegaJumpAbility;
import iskallia.vault.util.EntityHelper;
import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;

public class MegaJumpKnockbackAbility extends MegaJumpAbility<MegaJumpKnockbackConfig> {
   public boolean onAction(MegaJumpKnockbackConfig config, PlayerEntity player, boolean active) {
      if (super.onAction(config, player, active)) {
         List<LivingEntity> entities = EntityHelper.getNearby(player.func_130014_f_(), player.func_233580_cy_(), config.getRadius(), LivingEntity.class);
         entities.removeIf(e -> e instanceof PlayerEntity);

         for (LivingEntity entity : entities) {
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
