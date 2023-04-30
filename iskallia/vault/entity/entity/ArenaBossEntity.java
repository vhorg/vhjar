package iskallia.vault.entity.entity;

import iskallia.vault.entity.ai.AOEGoal;
import iskallia.vault.entity.ai.TeleportGoal;
import iskallia.vault.entity.ai.TeleportRandomly;
import iskallia.vault.entity.ai.ThrowProjectilesGoal;
import iskallia.vault.init.ModSounds;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class ArenaBossEntity extends FighterEntity {
   public TeleportRandomly<ArenaBossEntity> teleportTask = new TeleportRandomly(
      this, (entity, source, amount) -> !(source.getEntity() instanceof LivingEntity) ? 0.2 : 0.0
   );

   public ArenaBossEntity(EntityType<? extends Zombie> type, Level world) {
      super(type, world);
      if (!this.level.isClientSide) {
         this.getAttribute(Attributes.KNOCKBACK_RESISTANCE).setBaseValue(1000000.0);
      }

      this.bossInfo.setVisible(true);
   }

   protected void addBehaviourGoals() {
      super.addBehaviourGoals();
      this.goalSelector
         .addGoal(
            1,
            TeleportGoal.builder(this)
               .start(entity -> entity.getTarget() != null && entity.tickCount % 60 == 0)
               .to(
                  entity -> entity.getTarget()
                     .position()
                     .add((entity.random.nextDouble() - 0.5) * 8.0, entity.random.nextInt(16) - 8, (entity.random.nextDouble() - 0.5) * 8.0)
               )
               .then(entity -> entity.playSound(ModSounds.BOSS_TP_SFX, 1.0F, 1.0F))
               .build()
         );
      this.goalSelector.addGoal(1, new ThrowProjectilesGoal(this, 96, 10, SNOWBALLS));
      this.goalSelector.addGoal(1, new AOEGoal(this, e -> !(e instanceof ArenaBossEntity)));
      this.getAttribute(Attributes.FOLLOW_RANGE).setBaseValue(100.0);
   }

   @Override
   public void tick() {
      super.tick();
   }

   private float knockbackAttack(Entity entity) {
      for (int i = 0; i < 20; i++) {
         double d0 = this.level.random.nextGaussian() * 0.02;
         double d1 = this.level.random.nextGaussian() * 0.02;
         double d2 = this.level.random.nextGaussian() * 0.02;
         ((ServerLevel)this.level)
            .sendParticles(
               ParticleTypes.POOF,
               entity.getX() + this.level.random.nextDouble() - d0,
               entity.getY() + this.level.random.nextDouble() - d1,
               entity.getZ() + this.level.random.nextDouble() - d2,
               10,
               d0,
               d1,
               d2,
               1.0
            );
      }

      this.level.playSound(null, entity.blockPosition(), SoundEvents.IRON_GOLEM_HURT, this.getSoundSource(), 1.0F, 1.0F);
      return 15.0F;
   }

   @Override
   public boolean doHurtTarget(Entity entity) {
      boolean ret = false;
      if (this.random.nextInt(12) == 0) {
         double old = this.getAttribute(Attributes.ATTACK_KNOCKBACK).getBaseValue();
         this.getAttribute(Attributes.ATTACK_KNOCKBACK).setBaseValue(this.knockbackAttack(entity));
         boolean result = super.doHurtTarget(entity);
         this.getAttribute(Attributes.ATTACK_KNOCKBACK).setBaseValue(old);
         ret = result;
      }

      if (this.random.nextInt(6) == 0) {
         this.level.broadcastEntityEvent(this, (byte)4);
         float f = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
         float f1 = (int)f > 0 ? f / 2.0F + this.random.nextInt((int)f) : f;
         boolean flag = entity.hurt(DamageSource.mobAttack(this), f1);
         if (flag) {
            entity.setDeltaMovement(entity.getDeltaMovement().add(0.0, 0.6F, 0.0));
            this.doEnchantDamageEffects(this, entity);
         }

         this.level.playSound(null, entity.blockPosition(), SoundEvents.IRON_GOLEM_HURT, this.getSoundSource(), 1.0F, 1.0F);
         ret |= flag;
      }

      return ret || super.doHurtTarget(entity);
   }

   public boolean hurt(DamageSource source, float amount) {
      if (!(source.getEntity() instanceof Player) && !(source.getEntity() instanceof EternalEntity) && source != DamageSource.OUT_OF_WORLD) {
         return false;
      } else if (this.isInvulnerableTo(source) || source == DamageSource.FALL) {
         return false;
      } else {
         return this.teleportTask.attackEntityFrom(source, amount) ? true : super.hurt(source, amount);
      }
   }

   public static Builder createAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.FOLLOW_RANGE, 35.0)
         .add(Attributes.MOVEMENT_SPEED, 0.23F)
         .add(Attributes.ATTACK_DAMAGE, 3.0)
         .add(Attributes.ARMOR, 2.0)
         .add(Attributes.SPAWN_REINFORCEMENTS_CHANCE);
   }
}
