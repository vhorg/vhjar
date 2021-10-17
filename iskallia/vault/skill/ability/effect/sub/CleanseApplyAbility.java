package iskallia.vault.skill.ability.effect.sub;

import iskallia.vault.skill.ability.config.sub.CleanseApplyConfig;
import iskallia.vault.skill.ability.effect.CleanseAbility;
import iskallia.vault.util.EntityHelper;
import java.util.List;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.EffectInstance;

public class CleanseApplyAbility extends CleanseAbility<CleanseApplyConfig> {
   protected void removeEffects(CleanseApplyConfig config, ServerPlayerEntity player, List<EffectInstance> effects) {
      super.removeEffects(config, player, effects);

      for (EffectInstance effect : effects) {
         List<LivingEntity> other = EntityHelper.getNearby(player.func_130014_f_(), player.func_233580_cy_(), config.getApplyRadius(), LivingEntity.class);
         other.removeIf(ex -> ex instanceof PlayerEntity);
         if (!other.isEmpty()) {
            LivingEntity e = other.get(rand.nextInt(other.size()));
            e.func_195064_c(effect);
         }
      }
   }
}
