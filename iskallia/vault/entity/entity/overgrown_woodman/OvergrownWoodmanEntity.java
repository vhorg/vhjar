package iskallia.vault.entity.entity.overgrown_woodman;

import iskallia.vault.init.ModSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.monster.EnderMan;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public class OvergrownWoodmanEntity extends EnderMan {
   public OvergrownWoodmanEntity(EntityType<? extends EnderMan> entityType, Level world) {
      super(entityType, world);
   }

   public float getEyeHeight(@NotNull Pose pPose) {
      return 1.875F;
   }

   public float getEyeHeightAccess(Pose pose, EntityDimensions size) {
      return 1.875F;
   }

   protected SoundEvent getAmbientSound() {
      return ModSounds.OVERGROWN_ZOMBIE_IDLE;
   }

   public int getAmbientSoundInterval() {
      return 240;
   }

   protected SoundEvent getHurtSound(@NotNull DamageSource pDamageSource) {
      return ModSounds.OVERGROWN_ZOMBIE_HURT;
   }

   protected SoundEvent getDeathSound() {
      return ModSounds.OVERGROWN_ZOMBIE_DEATH;
   }
}
