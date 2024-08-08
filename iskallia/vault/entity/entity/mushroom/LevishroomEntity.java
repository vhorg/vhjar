package iskallia.vault.entity.entity.mushroom;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType.EDefaultLoopTypes;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;
import software.bernie.geckolib3.core.manager.AnimationFactory;
import software.bernie.geckolib3.util.GeckoLibUtil;

public class LevishroomEntity extends MushroomEntity implements IAnimatable {
   protected static final AnimationBuilder IDLE_ANIM = new AnimationBuilder().addAnimation("animation.levishroom.idle", EDefaultLoopTypes.LOOP);
   protected static final AnimationBuilder WALK_ANIM = new AnimationBuilder().addAnimation("animation.levishroom.walk", EDefaultLoopTypes.LOOP);
   protected static final AnimationBuilder BITE_ANIM = new AnimationBuilder().addAnimation("animation.levishroom.bite", EDefaultLoopTypes.LOOP);
   private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

   public LevishroomEntity(EntityType<? extends Monster> type, Level world) {
      super(type, world);
   }

   @Override
   public int getTier() {
      return -1;
   }

   public boolean doHurtTarget(Entity target) {
      if (!super.doHurtTarget(target)) {
         return false;
      } else {
         if (target instanceof LivingEntity livingTarget && this.level.random.nextFloat() <= 0.4) {
            livingTarget.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 20, 1, true, true));
         }

         return true;
      }
   }

   public void registerControllers(AnimationData data) {
      data.addAnimationController(new AnimationController(this, "Walking", 0.0F, this::walkAnimController));
      data.addAnimationController(new AnimationController(this, "Idle", 0.0F, this::idleAnimController));
      data.addAnimationController(new AnimationController(this, "Attack", 0.0F, this::attackAnimController));
   }

   protected void updateSwingTime() {
      int i = 15;
      if (this.swinging) {
         this.swingTime++;
         if (this.swingTime >= i) {
            this.swingTime = 0;
            this.swinging = false;
         }
      } else {
         this.swingTime = 0;
      }

      this.attackAnim = (float)this.swingTime / i;
   }

   private PlayState idleAnimController(AnimationEvent<LevishroomEntity> event) {
      if (!event.isMoving() && this.attackAnim == 0.0F) {
         event.getController().setAnimation(IDLE_ANIM);
         return PlayState.CONTINUE;
      } else {
         return PlayState.STOP;
      }
   }

   private PlayState walkAnimController(AnimationEvent<LevishroomEntity> event) {
      if (event.isMoving() && this.attackAnim == 0.0F) {
         event.getController().setAnimation(WALK_ANIM);
         return PlayState.CONTINUE;
      } else {
         return PlayState.STOP;
      }
   }

   private PlayState attackAnimController(AnimationEvent<LevishroomEntity> event) {
      if (this.attackAnim != 0.0F) {
         event.getController().setAnimation(BITE_ANIM);
         return PlayState.CONTINUE;
      } else {
         return PlayState.STOP;
      }
   }

   public AnimationFactory getFactory() {
      return this.factory;
   }
}
