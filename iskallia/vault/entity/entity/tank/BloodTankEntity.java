package iskallia.vault.entity.entity.tank;

import iskallia.vault.entity.entity.bloodmoon.BloodSilverfishEntity;
import iskallia.vault.init.ModEntities;
import iskallia.vault.init.ModSounds;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.NotNull;

public class BloodTankEntity extends BaseTankEntity {
   public BloodTankEntity(EntityType<BloodTankEntity> type, Level world) {
      super(type, world);
   }

   protected SoundEvent getAmbientSound() {
      return ModSounds.BLOODHORDE_TANK_ROAR;
   }

   protected SoundEvent getHurtSound(@NotNull DamageSource pDamageSource) {
      return ModSounds.BLOODHORDE_TANK_ROAR;
   }

   protected void playStepSound(@NotNull BlockPos pPos, @NotNull BlockState pBlock) {
      this.playSound(SoundEvents.IRON_GOLEM_STEP, 1.0F, 1.0F);
   }

   protected SoundEvent getDeathSound() {
      return ModSounds.BLOODHORDE_DEATH;
   }

   public boolean hurt(@NotNull DamageSource pSource, float pAmount) {
      if (this.level.isClientSide) {
         return super.hurt(pSource, pAmount);
      } else {
         float prevHealthPercent = this.getHealth() / this.getMaxHealth();
         int prevArmorCount = (int)(4.0F - prevHealthPercent / 0.25F);
         boolean hurt = super.hurt(pSource, pAmount);
         float healthPercent = this.getHealth() / this.getMaxHealth();
         int armorCount = (int)(4.0F - healthPercent / 0.25F);
         if (prevArmorCount != armorCount && healthPercent < prevHealthPercent) {
            BloodSilverfishEntity silverfish = (BloodSilverfishEntity)ModEntities.BLOOD_SILVERFISH.create(this.level);
            if (silverfish != null) {
               if (this.isPersistenceRequired()) {
                  silverfish.setPersistenceRequired();
               }

               silverfish.setDeltaMovement(new Vec3(0.0, 0.35, 0.0));
               silverfish.moveTo(this.position());
               this.level.addFreshEntity(silverfish);
               silverfish.playSound(SoundEvents.IRON_GOLEM_DAMAGE, 10.0F, 0.95F + silverfish.getRandom().nextFloat() * 0.1F);
            }
         }

         return hurt;
      }
   }
}
