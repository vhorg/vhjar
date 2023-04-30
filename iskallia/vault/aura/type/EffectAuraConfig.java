package iskallia.vault.aura.type;

import com.google.gson.annotations.Expose;
import iskallia.vault.aura.ActiveAura;
import iskallia.vault.aura.EntityAuraProvider;
import iskallia.vault.config.EternalAuraConfig;
import iskallia.vault.skill.talent.type.EffectTalent;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;

public class EffectAuraConfig extends EternalAuraConfig.AuraConfig {
   @Expose
   private final EffectTalent effect;

   public EffectAuraConfig(MobEffect effect, String name, String icon) {
      this(new EffectTalent(0, 0, 0, effect, 1), name, icon);
   }

   public EffectAuraConfig(EffectTalent effect, String name, String icon) {
      super(name, name, "Grants an aura of " + name, icon, 5.0F);
      this.effect = effect;
   }

   public EffectTalent getEffect() {
      return this.effect;
   }

   @Override
   public void onTick(Level world, ActiveAura aura) {
      super.onTick(world, aura);
      if (aura.getAuraProvider() instanceof EntityAuraProvider) {
         MobEffectInstance effect = this.getEffect().toEffect(259);
         LivingEntity auraTarget = ((EntityAuraProvider)aura.getAuraProvider()).getSource();
         if (!auraTarget.hasEffect(effect.getEffect()) || auraTarget.getEffect(effect.getEffect()).getDuration() < 40) {
            auraTarget.addEffect(effect);
         }
      }
   }
}
