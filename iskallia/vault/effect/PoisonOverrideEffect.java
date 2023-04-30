package iskallia.vault.effect;

import iskallia.vault.util.damage.DamageUtil;
import iskallia.vault.world.data.ServerVaults;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;

public class PoisonOverrideEffect extends MobEffect {
   public PoisonOverrideEffect() {
      super(MobEffectCategory.HARMFUL, 5149489);
      this.setRegistryName(new ResourceLocation("minecraft", "poison"));
   }

   public boolean isDurationEffectTick(int duration, int amplifier) {
      return true;
   }

   public void applyEffectTick(LivingEntity entity, int amplifier) {
      MobEffectInstance instance = entity.getEffect(MobEffects.POISON);
      if (instance != null) {
         int duration = instance.getDuration();
         if (ServerVaults.get(entity.level).isEmpty()) {
            int tickPart = 25 >> amplifier;
            boolean doEffect = true;
            if (tickPart > 0) {
               doEffect = duration % tickPart == 0;
            }

            if (doEffect && entity.getHealth() > 1.0F) {
               entity.hurt(DamageSource.MAGIC, 1.0F);
            }
         } else {
            if (duration % 20 == 0) {
               DamageUtil.shotgunAttack(entity, e -> e.hurt(DamageSource.MAGIC, amplifier + 1));
            }
         }
      }
   }
}
