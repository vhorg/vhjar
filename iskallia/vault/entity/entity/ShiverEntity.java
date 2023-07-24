package iskallia.vault.entity.entity;

import iskallia.vault.init.ModSounds;
import iskallia.vault.util.EntityHelper;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

public class ShiverEntity extends Zombie {
   public ShiverEntity(EntityType<? extends Zombie> entityType, Level world) {
      super(entityType, world);
   }

   protected SoundEvent getAmbientSound() {
      return ModSounds.SHIVER_IDLE;
   }

   protected SoundEvent getHurtSound(DamageSource pDamageSource) {
      return ModSounds.SHIVER_HURT;
   }

   protected SoundEvent getDeathSound() {
      return ModSounds.SHIVER_DEATH;
   }

   public void aiStep() {
      super.aiStep();
      if (this.level.isClientSide && this.level.random.nextInt(10) < 7) {
         BlockState blockState = Blocks.FROSTED_ICE.defaultBlockState();
         this.level.addParticle(new BlockParticleOption(ParticleTypes.BLOCK, blockState), this.getX(), this.getY() + 1.5, this.getZ(), 0.0, 0.0, 0.0);
      }
   }

   public void tick() {
      super.tick();
      float range = 2.0F;
      EntityHelper.getNearby(this.level, this.blockPosition(), range, Player.class).forEach(player -> {
         player.wasInPowderSnow = true;
         player.isInPowderSnow = true;
         player.setTicksFrozen(Math.min(this.getTicksRequiredToFreeze(), this.getTicksFrozen() + 1));
         if (!this.level.isClientSide()) {
            player.addEffect(new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 10, 3, true, true));
         }

         if (this.level.isClientSide() || this.tickCount % 10 >= 5) {
            int particleCount = 15;
            float stepAngle = (float) (Math.PI * 2) / particleCount;

            for (int i = 0; i < particleCount; i++) {
               Vec3 offset = new Vec3(range, 0.0, 0.0).yRot(i * stepAngle);
               Vec3 pos = this.position().add(offset);
               this.level.addParticle(ParticleTypes.SNOWFLAKE, pos.x, pos.y + 0.1F, pos.z, 0.0, 0.1, 0.0);
            }
         }
      });
   }
}
