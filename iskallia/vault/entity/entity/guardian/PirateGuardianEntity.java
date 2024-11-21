package iskallia.vault.entity.entity.guardian;

import iskallia.vault.entity.entity.guardian.helper.GuardianType;
import iskallia.vault.init.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class PirateGuardianEntity extends AbstractGuardianEntity {
   public PirateGuardianEntity(EntityType<PirateGuardianEntity> entityType, GuardianType type, Level world) {
      super(entityType, type, world);
   }

   @Override
   protected SoundEvent getAmbientSound() {
      return ModSounds.SKELETON_PIRATE_IDLE;
   }

   @Override
   protected SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
      return SoundEvents.SKELETON_HURT;
   }

   @Override
   protected SoundEvent getDeathSound() {
      return SoundEvents.SKELETON_DEATH;
   }
}
