package iskallia.vault.entity.entity.wraith;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class VaultWraithEntity extends Zombie {
   public VaultWraithEntity(EntityType<? extends Zombie> type, Level level) {
      super(type, level);
   }

   protected SoundEvent getAmbientSound() {
      return null;
   }

   @NotNull
   protected SoundEvent getStepSound() {
      return super.getStepSound();
   }

   protected SoundEvent getHurtSound(@NotNull DamageSource damageSource) {
      return null;
   }

   protected SoundEvent getDeathSound() {
      return null;
   }
}
