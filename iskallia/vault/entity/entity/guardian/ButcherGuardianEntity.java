package iskallia.vault.entity.entity.guardian;

import iskallia.vault.entity.entity.guardian.helper.GuardianType;
import iskallia.vault.init.ModEffects;
import javax.annotation.Nonnull;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.piglin.PiglinAi;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class ButcherGuardianEntity extends AbstractGuardianEntity {
   public ButcherGuardianEntity(EntityType<ButcherGuardianEntity> entityType, GuardianType type, Level world) {
      super(entityType, type, world);
   }

   @Override
   public boolean doHurtTarget(@Nonnull Entity entity) {
      boolean hurt = super.doHurtTarget(entity);
      if (hurt && entity instanceof LivingEntity livingEntity && livingEntity.getRandom().nextFloat() <= 0.2) {
         livingEntity.addEffect(new MobEffectInstance(ModEffects.BLEED, 60, 0));
      }

      return hurt;
   }

   @Override
   protected SoundEvent getAmbientSound() {
      return this.level.isClientSide ? null : (SoundEvent)PiglinAi.getSoundForCurrentActivity(this).orElse(null);
   }

   @Override
   protected SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
      return this.getGuardianType() == GuardianType.BRUISER ? SoundEvents.PIGLIN_BRUTE_HURT : SoundEvents.PIGLIN_HURT;
   }

   @Override
   protected SoundEvent getDeathSound() {
      return this.getGuardianType() == GuardianType.BRUISER ? SoundEvents.PIGLIN_BRUTE_DEATH : SoundEvents.PIGLIN_DEATH;
   }
}
