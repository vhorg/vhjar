package iskallia.vault.skill.ability.effect.sub;

import iskallia.vault.skill.ability.config.sub.CleanseEffectConfig;
import iskallia.vault.skill.ability.effect.CleanseAbility;
import iskallia.vault.skill.talent.type.EffectTalent;
import java.util.List;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class CleanseEffectAbility extends CleanseAbility<CleanseEffectConfig> {
   protected void removeEffects(CleanseEffectConfig config, ServerPlayerEntity player, List<EffectInstance> effects) {
      super.removeEffects(config, player, effects);
      List<String> addEffects = config.getPossibleEffects();
      if (!addEffects.isEmpty()) {
         for (EffectInstance ignored : effects) {
            String effectStr = addEffects.get(rand.nextInt(addEffects.size()));
            Registry.field_212631_t.func_241873_b(new ResourceLocation(effectStr)).ifPresent(effect -> {
               EffectTalent.CombinedEffects grantedEffects = EffectTalent.getEffectData(player, player.func_71121_q(), effect);
               EffectTalent display = grantedEffects.getDisplayEffect();
               EffectTalent.Type type = display.getType();
               if (grantedEffects.getAmplifier() >= 0) {
                  new EffectInstance(effect, 600, grantedEffects.getAmplifier() + config.getEffectAmplifier() + 1, false, type.showParticles, type.showIcon);
               } else {
                  player.func_195064_c(new EffectInstance(effect, 600, config.getEffectAmplifier(), false, type.showParticles, type.showIcon));
               }
            });
         }
      }
   }
}
