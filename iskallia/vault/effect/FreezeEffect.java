package iskallia.vault.effect;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.DyeColor;

public class FreezeEffect extends MobEffect {
   public FreezeEffect(ResourceLocation registryName) {
      super(MobEffectCategory.HARMFUL, DyeColor.LIGHT_BLUE.getFireworkColor());
      this.setRegistryName(registryName);
   }

   public void applyEffectTick(LivingEntity livingEntity, int amplifier) {
      livingEntity.wasInPowderSnow = true;
      livingEntity.isInPowderSnow = true;
   }

   public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
      return true;
   }
}
