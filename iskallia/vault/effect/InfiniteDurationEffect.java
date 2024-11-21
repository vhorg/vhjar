package iskallia.vault.effect;

import javax.annotation.Nonnull;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;

public class InfiniteDurationEffect extends MobEffect {
   public InfiniteDurationEffect(MobEffectCategory category, int color, ResourceLocation resourceLocation) {
      super(category, color);
      this.setRegistryName(resourceLocation);
   }

   public void addTo(LivingEntity livingEntity, int amplifier) {
      livingEntity.addEffect(this.instance(amplifier));
   }

   public MobEffectInstance instance(int amplifier) {
      return this.instance(amplifier, false);
   }

   public MobEffectInstance instance(int amplifier, boolean visible) {
      return new MobEffectInstance(this, 32767, amplifier, false, visible, true);
   }

   public MobEffectInstance timedInstance(int amplifier, int duration) {
      return this.timedInstance(amplifier, duration, false);
   }

   public MobEffectInstance timedInstance(int amplifier, int duration, boolean visible) {
      return new MobEffectInstance(this, duration, amplifier, false, visible, true);
   }

   public boolean isDurationEffectTick(int duration, int amplifier) {
      return duration < 32687;
   }

   public void applyEffectTick(@Nonnull LivingEntity livingEntity, int amplifier) {
      if (!livingEntity.level.isClientSide) {
         this.addTo(livingEntity, amplifier);
      }
   }
}
