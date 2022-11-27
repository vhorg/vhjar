package iskallia.vault.skill.ability.config.sub;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.HealConfig;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.world.effect.MobEffect;

public class HealEffectConfig extends HealConfig {
   @Expose
   private final List<MobEffect> removeEffects = new ArrayList<>();

   public HealEffectConfig(int learningCost, int regretCost, int cooldownTicks, int levelRequirement, float manaCost, List<MobEffect> removeEffects) {
      super(learningCost, regretCost, cooldownTicks, levelRequirement, manaCost, 0.0F);
      this.removeEffects.addAll(removeEffects);
   }

   public List<MobEffect> getRemoveEffects() {
      return this.removeEffects;
   }
}
