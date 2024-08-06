package iskallia.vault.entity.boss;

import iskallia.vault.core.util.WeightedList;
import iskallia.vault.entity.boss.attack.BossAttackMove;
import javax.annotation.Nullable;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType.EDefaultLoopTypes;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;

public class GolemBossEntity extends VaultBossEntity implements IAnimatable {
   private static final AnimationBuilder LEFT_PUNCH_ANIM = new AnimationBuilder().addAnimation("LeftPunch", EDefaultLoopTypes.PLAY_ONCE);
   private static final AnimationBuilder RIGHT_PUNCH_ANIM = new AnimationBuilder().addAnimation("RightPunch", EDefaultLoopTypes.PLAY_ONCE);
   private static final AnimationBuilder DOUBLE_ATTACK_ANIM = new AnimationBuilder().addAnimation("DoubleAttack", EDefaultLoopTypes.PLAY_ONCE);
   private static final WeightedList<AnimationBuilder> PUNCH_ANIMATIONS = new WeightedList<AnimationBuilder>()
      .add(LEFT_PUNCH_ANIM, 2)
      .add(RIGHT_PUNCH_ANIM, 2)
      .add(DOUBLE_ATTACK_ANIM, 1);
   @Nullable
   private AnimationBuilder currentAttackAnimation = null;

   public GolemBossEntity(EntityType<? extends Monster> entityType, Level level) {
      super(entityType, level);
   }

   @Override
   public void registerControllers(AnimationData data) {
      data.addAnimationController(new AnimationController(this, "AttackAnimation", 5.0F, this::attackMoveAnimController));
   }

   @Override
   public double getAttackReach() {
      return 3.0;
   }

   private PlayState attackMoveAnimController(AnimationEvent<GolemBossEntity> event) {
      AnimationController<GolemBossEntity> controller = event.getController();
      return this.getActiveAttackMove().map(attackMove -> {
         if (attackMove == BossAttackMove.PUNCH) {
            if (this.currentAttackAnimation == null) {
               this.currentAttackAnimation = PUNCH_ANIMATIONS.getRandom(this.level.random).orElse(null);
            }

            if (this.currentAttackAnimation != null) {
               controller.setAnimation(this.currentAttackAnimation);
               return PlayState.CONTINUE;
            } else {
               return PlayState.STOP;
            }
         } else {
            return PlayState.STOP;
         }
      }).orElseGet(() -> {
         this.currentAttackAnimation = null;
         controller.markNeedsReload();
         return PlayState.STOP;
      });
   }
}
