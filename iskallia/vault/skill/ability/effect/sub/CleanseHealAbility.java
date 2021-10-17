package iskallia.vault.skill.ability.effect.sub;

import iskallia.vault.skill.ability.config.sub.CleanseHealConfig;
import iskallia.vault.skill.ability.effect.CleanseAbility;
import java.util.List;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.EffectInstance;

public class CleanseHealAbility extends CleanseAbility<CleanseHealConfig> {
   protected void removeEffects(CleanseHealConfig config, ServerPlayerEntity player, List<EffectInstance> effects) {
      super.removeEffects(config, player, effects);

      for (EffectInstance ignored : effects) {
         player.func_70691_i(config.getHealthPerEffectRemoved());
      }
   }
}
