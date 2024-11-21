package iskallia.vault.entity.entity.bloodhorde;

import iskallia.vault.init.ModSounds;
import javax.annotation.Nonnull;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class BloodHordeEntity extends Zombie {
   public BloodHordeEntity(EntityType<? extends Zombie> type, Level world) {
      super(type, world);
   }

   protected SoundEvent getAmbientSound() {
      return ModSounds.BLOODHORDE_IDLE;
   }

   @Nonnull
   protected SoundEvent getStepSound() {
      return ModSounds.BLOODHORDE_STEP;
   }

   protected SoundEvent getHurtSound(@NotNull DamageSource pDamageSource) {
      return ModSounds.BLOODHORDE_HURT;
   }

   protected SoundEvent getDeathSound() {
      return ModSounds.BLOODHORDE_DEATH;
   }
}
