package iskallia.vault.entity.boss;

import iskallia.vault.core.util.WeightedList;
import iskallia.vault.entity.boss.attack.BasicMeleeAttack;
import iskallia.vault.entity.boss.attack.IMeleeAttack;
import java.util.Map;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.level.Level;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.core.PlayState;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.builder.ILoopType.EDefaultLoopTypes;
import software.bernie.geckolib3.core.controller.AnimationController;
import software.bernie.geckolib3.core.easing.EasingType;
import software.bernie.geckolib3.core.event.predicate.AnimationEvent;
import software.bernie.geckolib3.core.manager.AnimationData;

public class BoogiemanBossEntity extends VaultBossEntity implements IAnimatable {
   private static final AnimationSpeedWrapper SLASH_ANIM = new AnimationSpeedWrapper(
      new AnimationBuilder().addAnimation("Slash", EDefaultLoopTypes.PLAY_ONCE), b -> 1.5
   );
   private static final AnimationSpeedWrapper JAB_ANIM = new AnimationSpeedWrapper(
      new AnimationBuilder().addAnimation("Jab", EDefaultLoopTypes.PLAY_ONCE), b -> 1.5
   );
   private static final AnimationSpeedWrapper SNOWBALL_ANIM = new AnimationSpeedWrapper(
      new AnimationBuilder().addAnimation("Snowball", EDefaultLoopTypes.PLAY_ONCE)
   );
   private static final AnimationSpeedWrapper JUMP_ATTACK_ANIM = new AnimationSpeedWrapper(
      new AnimationBuilder().addAnimation("JumpAttack", EDefaultLoopTypes.PLAY_ONCE)
   );
   private static final AnimationSpeedWrapper WALK_ANIM = new AnimationSpeedWrapper(
      new AnimationBuilder().addAnimation("Walk", EDefaultLoopTypes.LOOP), b -> b.getAttribute(Attributes.MOVEMENT_SPEED).getValue() / 0.2F
   );
   private static final WeightedList<AnimationSpeedWrapper> PUNCH_ANIMATIONS = new WeightedList<AnimationSpeedWrapper>().add(SLASH_ANIM, 2).add(JAB_ANIM, 2);
   public static final String JAB_ATTACK_NAME = "jab";
   public static final String SLASH_ATTACK_NAME = "slash";
   public static final BasicMeleeAttack.BasicMeleeAttackAttributes JAB_ATTACK = new BasicMeleeAttack.BasicMeleeAttackAttributes(
      new BasicMeleeAttack.BasicMeleeAttackAttributes.Slice(0.0F, 0.6F), 20, 8, "jab", 0.5F, 0.0F
   );
   public static final BasicMeleeAttack.BasicMeleeAttackAttributes SLASH_ATTACK = new BasicMeleeAttack.BasicMeleeAttackAttributes(
      new BasicMeleeAttack.BasicMeleeAttackAttributes.Slice(0.0F, 0.4F), 20, 8, "slash", 0.5F, -0.2F
   );
   public static final Map<String, BiFunction<VaultBossBaseEntity, Double, IMeleeAttack>> MELEE_ATTACK_FACTORIES = Map.of(
      "jab",
      (boss, multiplier) -> new BasicMeleeAttack(boss, multiplier, JAB_ATTACK),
      "slash",
      (boss, multiplier) -> new BasicMeleeAttack(boss, multiplier, SLASH_ATTACK)
   );
   @Nullable
   private AnimationSpeedWrapper currentAttackAnimation = null;

   public BoogiemanBossEntity(EntityType<? extends Monster> entityType, Level level) {
      super(entityType, level);
   }

   @Override
   public void registerControllers(AnimationData data) {
      data.addAnimationController(new AnimationController(this, "AttackAnimation", 5.0F, EasingType.EaseOutQuint, this::attackMoveAnimController));
      data.addAnimationController(new AnimationController(this, "Walking", 5.0F, this::walkAnimController));
   }

   @Override
   public Map<String, BiFunction<VaultBossBaseEntity, Double, IMeleeAttack>> getMeleeAttackFactories() {
      return MELEE_ATTACK_FACTORIES;
   }

   @Override
   public double getAttackReach() {
      return 3.0;
   }

   private PlayState attackMoveAnimController(AnimationEvent<BoogiemanBossEntity> event) {
      AnimationController<BoogiemanBossEntity> controller = event.getController();
      return this.getActiveAttackMove().map(attackMove -> {
         if (!this.getNavigation().isDone()) {
            return PlayState.CONTINUE;
         } else if (attackMove.equals("slash")) {
            this.currentAttackAnimation = SLASH_ANIM;
            this.currentAttackAnimation.applyTo(controller, this);
            return PlayState.CONTINUE;
         } else if (attackMove.equals("jab")) {
            this.currentAttackAnimation = JAB_ANIM;
            this.currentAttackAnimation.applyTo(controller, this);
            return PlayState.CONTINUE;
         } else {
            return PlayState.STOP;
         }
      }).orElseGet(() -> {
         this.currentAttackAnimation = null;
         controller.markNeedsReload();
         return PlayState.STOP;
      });
   }

   private PlayState walkAnimController(AnimationEvent<BoogiemanBossEntity> event) {
      if (event.isMoving()) {
         AnimationController<BoogiemanBossEntity> controller = event.getController();
         WALK_ANIM.applyTo(controller, this);
         return PlayState.CONTINUE;
      } else {
         return PlayState.STOP;
      }
   }

   @Override
   public void playAttackSound() {
      this.level.playSound(null, this.blockPosition(), SoundEvents.PLAYER_ATTACK_SWEEP, SoundSource.HOSTILE, 1.0F, 1.0F);
   }
}
