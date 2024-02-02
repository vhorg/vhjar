package iskallia.vault.effect;

import iskallia.vault.init.ModEffects;
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
      if (!entity.level.isClientSide && !entity.isDeadOrDying()) {
         MobEffectInstance instance = entity.getEffect(ModEffects.BLEED);
         if (instance != null) {
            if (instance.getDuration() % 40 == 0) {
               entity.setHealth(entity.getHealth() - (instance.getAmplifier() + 1));
               if (entity.isDeadOrDying()) {
                  entity.die(DamageSource.MAGIC);
               }
            }
         }
      }
   }
}
