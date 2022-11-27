package iskallia.vault.skill.talent;

import iskallia.vault.gear.attribute.custom.EffectGearAttribute;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;

public interface EffectGrantingTalent extends Talent {
   MobEffect getEffect();

   int getAmplifier();

   default MobEffectInstance makeEffect(int duration) {
      return new MobEffectInstance(this.getEffect(), duration, this.getAmplifier(), false, false, true);
   }

   default EffectGearAttribute asGearAttribute() {
      return new EffectGearAttribute(this.getEffect(), this.getAmplifier());
   }
}
