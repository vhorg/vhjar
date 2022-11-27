package iskallia.vault.entity.entity;

import iskallia.vault.entity.ai.CowDashAttackGoal;
import iskallia.vault.entity.ai.MobAttackGoal;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.animal.Cow;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class AggressiveCowEntity extends Cow {
   protected int dashCooldown = 0;

   public AggressiveCowEntity(EntityType<? extends Cow> type, Level worldIn) {
      super(type, worldIn);
   }

   public static Builder createAttributes() {
      return Monster.createMonsterAttributes()
         .add(Attributes.FOLLOW_RANGE, 100.0)
         .add(Attributes.MOVEMENT_SPEED, 0.25)
         .add(Attributes.ATTACK_DAMAGE, 3.0)
         .add(Attributes.ATTACK_KNOCKBACK, 3.0)
         .add(Attributes.KNOCKBACK_RESISTANCE, 0.4)
         .add(Attributes.ARMOR, 2.0);
   }

   protected void registerGoals() {
      this.goalSelector.addGoal(8, new WaterAvoidingRandomStrollGoal(this, 1.5));
      this.goalSelector.addGoal(8, new LookAtPlayerGoal(this, Player.class, 16.0F));
      this.goalSelector.addGoal(0, new CowDashAttackGoal(this, 0.3F));
      this.goalSelector.addGoal(1, new MobAttackGoal(this, 1.5, true));
      this.targetSelector.addGoal(0, new NearestAttackableTargetGoal(this, Player.class, 0, true, false, null));
   }

   public void aiStep() {
      super.aiStep();
      this.setAge(0);
      if (this.dashCooldown > 0) {
         this.dashCooldown--;
      }
   }

   public boolean isInvulnerableTo(DamageSource source) {
      return super.isInvulnerableTo(source) || source == DamageSource.FALL || source == DamageSource.DROWN;
   }

   public boolean canDash() {
      return this.dashCooldown <= 0;
   }

   public void onDash() {
      this.dashCooldown = 60;
      this.navigation.stop();
      this.playAmbientSound();
   }
}
