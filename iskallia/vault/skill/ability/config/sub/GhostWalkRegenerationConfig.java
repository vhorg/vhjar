package iskallia.vault.skill.ability.config.sub;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.GhostWalkConfig;
import iskallia.vault.skill.talent.type.EffectTalent;
import net.minecraft.potion.Effects;

public class GhostWalkRegenerationConfig extends GhostWalkConfig {
   @Expose
   private final int regenerationAmplifier;
   @Expose
   private final String regenerationType;

   public GhostWalkRegenerationConfig(int cost, int level, int durationTicks, int regenAmplifier) {
      super(cost, level, durationTicks);
      this.regenerationAmplifier = regenAmplifier;
      this.regenerationType = EffectTalent.Type.ALL.toString();
   }

   public int getRegenAmplifier() {
      return this.regenerationAmplifier;
   }

   public EffectTalent.Type getRegenerationType() {
      return EffectTalent.Type.fromString(this.regenerationType);
   }

   public EffectTalent makeRegenerationTalent() {
      return new EffectTalent(0, Effects.field_76428_l, this.getRegenAmplifier(), this.getRegenerationType(), EffectTalent.Operator.ADD);
   }
}
