package iskallia.vault.entity.entity.elite;

import com.google.common.base.Preconditions;
import iskallia.vault.init.ModAttributes;
import java.util.Random;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeMap;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.monster.Skeleton;
import net.minecraft.world.level.Level;

public class EliteSkeleton extends Skeleton {
   private long lastAttackTime = 0L;

   public EliteSkeleton(EntityType<? extends Skeleton> entityType, Level level) {
      super(entityType, level);
   }

   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(1, new EliteSkeleton.TurnVanishFuryGoal(this, 100, 300));
   }

   public boolean doHurtTarget(Entity entity) {
      boolean result = super.doHurtTarget(entity);
      if (result) {
         this.lastAttackTime = this.level.getGameTime();
      }

      return result;
   }

   private static class TurnVanishFuryGoal extends Goal {
      private static final AttributeModifier SPEED_MODIFIER = new AttributeModifier("Speed modifier", 1.0, Operation.MULTIPLY_TOTAL);
      private static final AttributeModifier CRIT_CHANCE_MODIFIER = new AttributeModifier("Crit chance modifier", 1.0, Operation.ADDITION);
      private final EliteSkeleton skeleton;
      private final int vanishIntervalMin;
      private final int vanishIntervalMax;
      private int cooldown;
      private boolean removedModifiers = true;
      private long lastVanishTime = 0L;

      public TurnVanishFuryGoal(EliteSkeleton skeleton, int vanishIntervalMin, int vanishIntervalMax) {
         this.skeleton = skeleton;
         this.vanishIntervalMin = vanishIntervalMin;
         this.vanishIntervalMax = vanishIntervalMax;
         this.cooldown = skeleton.getRandom().nextInt(vanishIntervalMax - vanishIntervalMin) + vanishIntervalMin;
      }

      public boolean canUse() {
         return !this.removedModifiers || this.cooldown-- <= 0;
      }

      public void start() {
         this.removedModifiers = false;
         this.skeleton.addEffect(new MobEffectInstance(MobEffects.INVISIBILITY, 100, 0, false, false));
         AttributeMap attributes = this.skeleton.getAttributes();
         AttributeInstance speedAttribute = (AttributeInstance)Preconditions.checkNotNull(
            attributes.getInstance(Attributes.MOVEMENT_SPEED), "Elite Skeleton missing movement_speed attribute"
         );
         if (!speedAttribute.hasModifier(SPEED_MODIFIER)) {
            speedAttribute.addTransientModifier(SPEED_MODIFIER);
         }

         AttributeInstance critChanceAttribute = (AttributeInstance)Preconditions.checkNotNull(
            attributes.getInstance(ModAttributes.CRIT_CHANCE), "Elite Skeleton missing crit_chance attribute"
         );
         if (!critChanceAttribute.hasModifier(CRIT_CHANCE_MODIFIER)) {
            critChanceAttribute.addTransientModifier(CRIT_CHANCE_MODIFIER);
         }

         Random random = this.skeleton.getRandom();
         this.cooldown = random.nextInt(this.vanishIntervalMax - this.vanishIntervalMin) + this.vanishIntervalMin;
         this.lastVanishTime = this.skeleton.level.getGameTime();
         if (this.skeleton.getLevel() instanceof ServerLevel serverLevel) {
            double x = this.skeleton.getX();
            double y = this.skeleton.getY();
            double z = this.skeleton.getZ();
            serverLevel.sendParticles(ParticleTypes.POOF, x - 0.5, y, z - 0.5, 10, 1.0, 2.0, 1.0, 0.0);
            serverLevel.playSound(null, this.skeleton, SoundEvents.ILLUSIONER_PREPARE_BLINDNESS, SoundSource.HOSTILE, 1.0F, 1.0F);
         }
      }

      public void tick() {
         AttributeMap attributes = this.skeleton.getAttributes();
         AttributeInstance critChanceAttribute = (AttributeInstance)Preconditions.checkNotNull(
            attributes.getInstance(ModAttributes.CRIT_CHANCE), "Elite Skeleton missing crit_chance attribute"
         );
         if (this.skeleton.lastAttackTime > this.lastVanishTime && critChanceAttribute.hasModifier(CRIT_CHANCE_MODIFIER)) {
            critChanceAttribute.removeModifier(CRIT_CHANCE_MODIFIER);
         }

         if (!this.skeleton.hasEffect(MobEffects.INVISIBILITY)) {
            critChanceAttribute.removeModifier(CRIT_CHANCE_MODIFIER);
            ((AttributeInstance)Preconditions.checkNotNull(attributes.getInstance(Attributes.MOVEMENT_SPEED), "Elite Skeleton missing movement_speed attribute"))
               .removeModifier(SPEED_MODIFIER);
            this.removedModifiers = true;
         }
      }
   }
}
