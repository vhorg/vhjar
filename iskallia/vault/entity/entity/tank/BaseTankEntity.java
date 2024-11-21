package iskallia.vault.entity.entity.tank;

import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.MoveTowardsTargetGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.AbstractGolem;
import net.minecraft.world.entity.animal.IronGolem.Crackiness;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public abstract class BaseTankEntity extends AbstractGolem {
   private int attackAnimationTick;

   protected BaseTankEntity(EntityType<? extends AbstractGolem> p_27508_, Level p_27509_) {
      super(p_27508_, p_27509_);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(1, new MeleeAttackGoal(this, 1.0, true));
      this.goalSelector.addGoal(2, new MoveTowardsTargetGoal(this, 0.9, 32.0F));
      this.goalSelector.addGoal(7, new LookAtPlayerGoal(this, Player.class, 6.0F));
      this.goalSelector.addGoal(8, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, true));
   }

   public void aiStep() {
      super.aiStep();
      if (this.attackAnimationTick > 0) {
         this.attackAnimationTick--;
      }

      if (this.getDeltaMovement().horizontalDistanceSqr() > 2.5000003E-7F && this.random.nextInt(5) == 0) {
         int i = Mth.floor(this.getX());
         int j = Mth.floor(this.getY() - 0.2F);
         int k = Mth.floor(this.getZ());
         BlockPos pos = new BlockPos(i, j, k);
         BlockState blockstate = this.level.getBlockState(pos);
         if (!blockstate.isAir()) {
            this.level
               .addParticle(
                  new BlockParticleOption(ParticleTypes.BLOCK, blockstate).setPos(pos),
                  this.getX() + (this.random.nextFloat() - 0.5) * this.getBbWidth(),
                  this.getY() + 0.1,
                  this.getZ() + (this.random.nextFloat() - 0.5) * this.getBbWidth(),
                  4.0 * (this.random.nextFloat() - 0.5),
                  0.5,
                  (this.random.nextFloat() - 0.5) * 4.0
               );
         }
      }
   }

   protected float getAttackDamage() {
      return (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
   }

   public boolean doHurtTarget(Entity pEntity) {
      this.attackAnimationTick = 30;
      this.level.broadcastEntityEvent(this, (byte)4);
      float f = this.getAttackDamage();
      float f1 = (int)f > 0 ? f / 2.0F + this.random.nextInt((int)f) : f;
      boolean flag = pEntity.hurt(DamageSource.mobAttack(this), f1);
      if (flag) {
         pEntity.setDeltaMovement(pEntity.getDeltaMovement().add(0.0, 0.4F, 0.0));
         this.doEnchantDamageEffects(this, pEntity);
      }

      this.playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.0F, 1.0F);
      return flag;
   }

   public void handleEntityEvent(byte pId) {
      if (pId == 4) {
         this.attackAnimationTick = 10;
         this.playSound(SoundEvents.IRON_GOLEM_ATTACK, 1.0F, 1.0F);
      } else if (pId != 11 && pId != 34) {
         super.handleEntityEvent(pId);
      }
   }

   public Crackiness getCrackiness() {
      return Crackiness.byFraction(this.getHealth() / this.getMaxHealth());
   }

   public int getAttackAnimationTick() {
      return this.attackAnimationTick;
   }
}
