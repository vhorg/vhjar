package iskallia.vault.skill.ability.effect.sub;

import iskallia.vault.skill.ability.config.sub.CleanseEffectConfig;
import iskallia.vault.skill.ability.effect.CleanseAbility;
import java.util.List;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.potion.EffectInstance;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class CleanseEffectAbility extends CleanseAbility<CleanseEffectConfig> {
   protected void removeEffects(CleanseEffectConfig config, PlayerEntity player, List<EffectInstance> effects) {
      super.removeEffects(config, player, effects);
      List<String> addEffects = config.getPossibleEffects();
      if (!addEffects.isEmpty()) {
         int amplifierToSet = config.getEffectAmplifier();

         for (EffectInstance ignored : effects) {
            String effectStr = addEffects.get(rand.nextInt(addEffects.size()));
            Registry.field_212631_t.func_241873_b(new ResourceLocation(effectStr)).ifPresent(effect -> {
               EffectInstance existing = player.func_70660_b(effect);
               if (existing != null && existing.func_76458_c() >= config.getEffectAmplifier()) {
                  player.func_195064_c(new EffectInstance(effect, 600, existing.func_76458_c() + amplifierToSet + 1));
               } else {
                  player.func_195064_c(new EffectInstance(effect, 600, amplifierToSet));
               }
            });
         }
      }
   }
}
