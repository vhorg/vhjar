package iskallia.vault.effect;

import iskallia.vault.init.ModEffects;
import iskallia.vault.util.damage.DamageUtil;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class BleedEffect extends MobEffect {
   public BleedEffect(ResourceLocation id) {
      super(MobEffectCategory.HARMFUL, 16711680);
      this.setRegistryName(id);
   }

   public boolean isDurationEffectTick(int duration, int amplifier) {
      return true;
   }

   public void applyEffectTick(LivingEntity entity, int amplifier) {
      MobEffectInstance instance = entity.getEffect(ModEffects.BLEED);
      if (instance != null) {
         if (instance.getDuration() % 40 == 0) {
            DamageUtil.shotgunAttack(entity, e -> {
               e.hurt(DamageSource.MAGIC, 0.001F);
               e.setHealth(e.getHealth() - (instance.getAmplifier() + 1));
            });
         }
      }
   }
}
