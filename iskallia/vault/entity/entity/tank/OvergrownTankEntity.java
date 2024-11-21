package iskallia.vault.entity.entity.tank;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
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

public class OvergrownTankEntity extends BaseTankEntity implements IAnimatable {
   protected static final AnimationBuilder LOOK_ANIM = new AnimationBuilder().addAnimation("animation.overgrown_tank.look", EDefaultLoopTypes.LOOP);
   protected static final AnimationBuilder STAND_ANIM = new AnimationBuilder().addAnimation("animation.overgrown_tank.stand", EDefaultLoopTypes.LOOP);
   protected static final AnimationBuilder WALKING_ANIM = new AnimationBuilder().addAnimation("animation.overgrown_tank.walk", EDefaultLoopTypes.LOOP);
   protected static final AnimationBuilder PUNCH_ANIM = new AnimationBuilder().addAnimation("animation.overgrown_tank.punch", EDefaultLoopTypes.LOOP);
   protected static final AnimationBuilder THROW_ANIM = new AnimationBuilder().addAnimation("animation.overgrown_tank.throw", EDefaultLoopTypes.LOOP);
   private final AnimationFactory factory = GeckoLibUtil.createFactory(this);

   public OvergrownTankEntity(EntityType<OvergrownTankEntity> type, Level world) {
      super(type, world);
   }

   @Override
   protected void registerGoals() {
      super.registerGoals();
      this.goalSelector.addGoal(6, new OvergrownTankEntity.ThrowRockGoal(this));
   }

   public void registerControllers(AnimationData data) {
      data.addAnimationController(new AnimationController(this, "Idle", 0.0F, this::idleAnimController));
      data.addAnimationController(new AnimationController(this, "Walking", 0.0F, this::walkingAnimController));
      data.addAnimationController(new AnimationController(this, "Punch", 0.0F, this::punchAnimController));
   }

   private PlayState idleAnimController(AnimationEvent<OvergrownTankEntity> event) {
      if (!event.isMoving() && this.getAttackAnimationTick() == 0.0F) {
         event.getController().setAnimation(STAND_ANIM);
         return PlayState.CONTINUE;
      } else {
         return PlayState.STOP;
      }
   }

   private PlayState walkingAnimController(AnimationEvent<OvergrownTankEntity> event) {
      if (event.isMoving() && this.getAttackAnimationTick() == 0.0F) {
         event.getController().setAnimation(WALKING_ANIM);
         return PlayState.CONTINUE;
      } else {
         return PlayState.STOP;
      }
   }

   private PlayState punchAnimController(AnimationEvent<OvergrownTankEntity> event) {
      if (this.getAttackAnimationTick() != 0) {
         event.getController().setAnimation(PUNCH_ANIM);
         return PlayState.CONTINUE;
      } else {
         return PlayState.STOP;
      }
   }

   public AnimationFactory getFactory() {
      return this.factory;
   }

   public static class ThrowRockGoal extends Goal {
      protected final PathfinderMob mob;
      protected int throwTick;

      public ThrowRockGoal(PathfinderMob mob) {
         this.mob = mob;
      }

      public void start() {
         this.throwTick = 12;
      }

      public void tick() {
         this.throwTick--;
      }

      public boolean canUse() {
         return false;
      }
   }
}
