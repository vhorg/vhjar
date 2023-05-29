package iskallia.vault.entity.entity;

import javax.annotation.Nonnull;
import net.minecraft.util.Mth;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.LeapAtTargetGoal;
import net.minecraft.world.entity.ai.goal.LookAtPlayerGoal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.WaterAvoidingRandomStrollGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.CaveSpider;
import net.minecraft.world.entity.monster.Spider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Block;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.HitResult.Type;

public class VaultSpiderEntity extends CaveSpider {
   public VaultSpiderEntity(EntityType<? extends CaveSpider> entityType, Level world) {
      super(entityType, world);
   }

   private Vec3 getPosition(Entity entityLivingBaseIn) {
      double d0 = entityLivingBaseIn.getX();
      double d1 = entityLivingBaseIn.getY() + entityLivingBaseIn.getBbHeight() / 2.0F;
      double d2 = entityLivingBaseIn.getZ();
      return new Vec3(d0, d1, d2);
   }

   public boolean doHurtTarget(@Nonnull Entity entity) {
      Vec3 eyePos1 = this.getEyePosition(1.0F);
      Vec3 eyePos2 = this.getPosition(entity);
      ClipContext context = new ClipContext(eyePos1, eyePos2, Block.COLLIDER, Fluid.NONE, this);
      BlockHitResult result = this.level.clip(context);
      if (result.getType() != Type.MISS) {
         return false;
      } else {
         float f = (float)this.getAttributeValue(Attributes.ATTACK_DAMAGE);
         float f1 = (float)this.getAttributeValue(Attributes.ATTACK_KNOCKBACK);
         if (entity instanceof LivingEntity) {
            f += EnchantmentHelper.getDamageBonus(this.getMainHandItem(), ((LivingEntity)entity).getMobType());
            f1 += EnchantmentHelper.getKnockbackBonus(this);
         }

         int i = EnchantmentHelper.getFireAspect(this);
         if (i > 0) {
            entity.setSecondsOnFire(i * 4);
         }

         boolean flag = entity.hurt(DamageSource.mobAttack(this), f);
         if (flag) {
            if (f1 > 0.0F && entity instanceof LivingEntity) {
               ((LivingEntity)entity)
                  .knockback(f1 * 0.5F, Mth.sin(this.getYRot() * (float) (Math.PI / 180.0)), -Mth.cos(this.getYRot() * (float) (Math.PI / 180.0)));
               this.setDeltaMovement(this.getDeltaMovement().multiply(0.6, 1.0, 0.6));
            }

            if (entity instanceof Player player) {
               this.maybeDisableShield(player, this.getMainHandItem(), player.isUsingItem() ? player.getUseItem() : ItemStack.EMPTY);
            }

            this.doEnchantDamageEffects(this, entity);
            this.setLastHurtMob(entity);
         }

         return flag;
      }
   }

   private void maybeDisableShield(Player p_21425_, ItemStack p_21426_, ItemStack p_21427_) {
      if (!p_21426_.isEmpty() && !p_21427_.isEmpty() && p_21426_.getItem() instanceof AxeItem && p_21427_.is(Items.SHIELD)) {
         float f = 0.25F + EnchantmentHelper.getBlockEfficiency(this) * 0.05F;
         if (this.random.nextFloat() < f) {
            p_21425_.getCooldowns().addCooldown(Items.SHIELD, 100);
            this.level.broadcastEntityEvent(p_21425_, (byte)30);
         }
      }
   }

   protected void registerGoals() {
      this.goalSelector.removeGoal(new FloatGoal(this));
      this.goalSelector.addGoal(1, new FloatGoal(this));
      this.goalSelector.addGoal(3, new LeapAtTargetGoal(this, 0.4F));
      this.goalSelector.addGoal(4, new VaultSpiderEntity.SpiderAttackGoal(this));
      this.goalSelector.addGoal(5, new WaterAvoidingRandomStrollGoal(this, 0.8));
      this.goalSelector.addGoal(6, new LookAtPlayerGoal(this, Player.class, 8.0F));
      this.goalSelector.addGoal(6, new RandomLookAroundGoal(this));
      this.targetSelector.addGoal(1, new HurtByTargetGoal(this, new Class[0]));
      this.targetSelector.addGoal(2, new NearestAttackableTargetGoal(this, Player.class, false));
   }

   static class SpiderAttackGoal extends MeleeAttackGoal {
      public SpiderAttackGoal(Spider p_33822_) {
         super(p_33822_, 1.0, true);
      }

      public boolean canUse() {
         return super.canUse() && !this.mob.isVehicle();
      }

      public boolean canContinueToUse() {
         float f = this.mob.getBrightness();
         if (f >= 0.5F && this.mob.getRandom().nextInt(100) == 0) {
            this.mob.setTarget((LivingEntity)null);
            return false;
         } else {
            return super.canContinueToUse();
         }
      }

      protected double getAttackReachSqr(LivingEntity pAttackTarget) {
         return 2.0F + pAttackTarget.getBbWidth();
      }
   }
}
