package iskallia.vault.entity.entity.elite;

import iskallia.vault.entity.entity.IceBoltEntity;
import iskallia.vault.init.ModEffects;
import iskallia.vault.init.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.goal.RangedBowAttackGoal;
import net.minecraft.world.entity.monster.Stray;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.EntityHitResult;

public class EliteStrayEntity extends Stray {
   public EliteStrayEntity(EntityType<? extends Stray> p_33836_, Level p_33837_) {
      super(p_33836_, p_33837_);
      this.bowGoal = new RangedBowAttackGoal(this, 1.0, 10, 15.0F);
   }

   public void aiStep() {
      super.aiStep();
      if (this.level.isClientSide) {
         this.level.addParticle(ParticleTypes.SNOWFLAKE, this.getRandomX(1.0), this.getY() + 2.9F, this.getRandomZ(1.0), 0.0, 0.01, 0.0);
      }
   }

   public void performRangedAttack(LivingEntity pTarget, float pDistanceFactor) {
      if (this.level.getRandom().nextFloat() > 0.3) {
         super.performRangedAttack(pTarget, pDistanceFactor);
      } else {
         double xDiff = pTarget.getX() - this.getX();
         double yDiff = pTarget.getY(0.33) - this.getY(0.66);
         double zDiff = pTarget.getZ() - this.getZ();

         for (int i = 0; i < 5; i++) {
            IceBoltEntity iceBoltEntity = new IceBoltEntity(this, IceBoltEntity.Model.ARROW, result -> {
               if (result instanceof EntityHitResult hit && hit.getEntity() instanceof LivingEntity living) {
                  living.addEffect(new MobEffectInstance(ModEffects.CHILLED, 60, 9, false, false, true));
               }
            });
            iceBoltEntity.shoot(xDiff, yDiff, zDiff, 1.0F, 8.0F);
            this.level.addFreshEntity(iceBoltEntity);
         }

         this.level.playSound(null, this, ModSounds.ICE_BOLT_ARROW, SoundSource.PLAYERS, 0.3F, this.level.random.nextFloat() * 0.6F + 0.8F);
      }
   }

   public boolean canBeAffected(MobEffectInstance potionEffect) {
      return potionEffect.getEffect() == ModEffects.GLACIAL_SHATTER ? false : super.canBeAffected(potionEffect);
   }
}
