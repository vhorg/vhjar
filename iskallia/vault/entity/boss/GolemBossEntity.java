package iskallia.vault.entity.boss;

import com.google.common.base.Preconditions;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.entity.boss.attack.BasicMeleeAttack;
import iskallia.vault.entity.boss.attack.IMeleeAttack;
import java.util.Map;
import java.util.function.BiFunction;
import javax.annotation.Nullable;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier.Builder;
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
   private static final EntityDataAccessor<Byte> DATA_FLAGS_ID = SynchedEntityData.defineId(GolemBossEntity.class, EntityDataSerializers.BYTE);
   private static final AnimationSpeedWrapper LEFT_PUNCH_ANIM = new AnimationSpeedWrapper(
      new AnimationBuilder().addAnimation("LeftPunch", EDefaultLoopTypes.PLAY_ONCE)
   );
   private static final AnimationSpeedWrapper RIGHT_PUNCH_ANIM = new AnimationSpeedWrapper(
      new AnimationBuilder().addAnimation("RightPunch", EDefaultLoopTypes.PLAY_ONCE)
   );
   private static final AnimationSpeedWrapper DOUBLE_ATTACK_ANIM = new AnimationSpeedWrapper(
      new AnimationBuilder().addAnimation("DoubleAttack", EDefaultLoopTypes.PLAY_ONCE), e -> 1.7
   );
   private static final AnimationSpeedWrapper WALK_ANIM = new AnimationSpeedWrapper(new AnimationBuilder().addAnimation("Walk", EDefaultLoopTypes.LOOP));
   private static final AnimationSpeedWrapper THROW_RIGHT_ANIM = new AnimationSpeedWrapper(
      new AnimationBuilder().addAnimation("ThrowRight", EDefaultLoopTypes.PLAY_ONCE)
   );
   private static final AnimationSpeedWrapper THROW_LEFT_ANIM = new AnimationSpeedWrapper(
      new AnimationBuilder().addAnimation("ThrowLeft", EDefaultLoopTypes.PLAY_ONCE)
   );
   private static final WeightedList<AnimationSpeedWrapper> PUNCH_ANIMATIONS = new WeightedList<AnimationSpeedWrapper>()
      .add(LEFT_PUNCH_ANIM, 2)
      .add(RIGHT_PUNCH_ANIM, 2);
   public static final String PUNCH_ATTACK_NAME = "punch";
   public static final String DOUBLE_PUNCH_ATTACK_NAME = "double_punch";
   public static final BasicMeleeAttack.BasicMeleeAttackAttributes PUNCH_ATTACK = new BasicMeleeAttack.BasicMeleeAttackAttributes(
      new BasicMeleeAttack.BasicMeleeAttackAttributes.Slice(0.0F, 0.6F), 20, 8, "punch", 1.5F, 0.0F
   );
   public static final BasicMeleeAttack.BasicMeleeAttackAttributes DOUBLE_PUNCH_ATTACK = new BasicMeleeAttack.BasicMeleeAttackAttributes(
      new BasicMeleeAttack.BasicMeleeAttackAttributes.Slice(0.0F, 0.6F), 40, 10, "double_punch", 1.5F, 0.0F
   );
   public static final Map<String, BiFunction<VaultBossBaseEntity, Double, IMeleeAttack>> MELEE_ATTACK_FACTORIES = Map.of(
      "punch",
      (boss, multiplier) -> new BasicMeleeAttack(boss, multiplier, PUNCH_ATTACK),
      "double_punch",
      (boss, multiplier) -> new BasicMeleeAttack(boss, multiplier, DOUBLE_PUNCH_ATTACK)
   );
   private boolean wasLaunchingLastTick = false;
   private int launchAnimationGraceTime = 0;
   @Nullable
   private AnimationSpeedWrapper currentAttackAnimation = null;

   public GolemBossEntity(EntityType<? extends Monster> entityType, Level level) {
      super(entityType, level);
   }

   public static Builder createAttributes() {
      return VaultBossEntity.createAttributes().add(Attributes.ATTACK_KNOCKBACK, 2.5);
   }

   private void setFlag(int bitIndex, boolean value) {
      Preconditions.checkArgument(bitIndex >= 0 && bitIndex <= 7, "Bit index must be between 0 and 7.");
      byte flags = (Byte)this.entityData.get(DATA_FLAGS_ID);
      if (value) {
         flags = (byte)(flags | 1 << bitIndex);
      } else {
         flags = (byte)(flags & ~(1 << bitIndex));
      }

      this.entityData.set(DATA_FLAGS_ID, flags);
   }

   private boolean getFlag(int bitIndex) {
      Preconditions.checkArgument(bitIndex >= 0 && bitIndex <= 7, "Bit index must be between 0 and 7.");
      return ((Byte)this.entityData.get(DATA_FLAGS_ID) & 1 << bitIndex) != 0;
   }

   public void setLaunchingRightHand(boolean launchingHand) {
      this.setFlag(0, launchingHand);
   }

   public void setLaunchingLeftHand(boolean launchingHand) {
      this.setFlag(2, launchingHand);
   }

   public void setShowRightHand(boolean showRightHand) {
      this.setFlag(1, showRightHand);
   }

   public void setShowLeftHand(boolean showLeftHand) {
      this.setFlag(3, showLeftHand);
   }

   public boolean isLaunchingRightHand() {
      return this.getFlag(0);
   }

   private boolean isLaunchingLeftHand() {
      return this.getFlag(2);
   }

   public boolean showsRightHand() {
      return this.getFlag(1);
   }

   public boolean showsLeftHand() {
      return this.getFlag(3);
   }

   @Override
   protected void defineSynchedData() {
      super.defineSynchedData();
      this.entityData.define(DATA_FLAGS_ID, (byte)10);
   }

   @Override
   public void registerControllers(AnimationData data) {
      data.addAnimationController(new AnimationController(this, "AttackAnimation", 5.0F, this::attackMoveAnimController));
      data.addAnimationController(new AnimationController(this, "Walking", 5.0F, this::walkAnimController));
      data.addAnimationController(new AnimationController(this, "Launching", 5.0F, this::launchAnimController));
   }

   private PlayState launchAnimController(AnimationEvent<GolemBossEntity> event) {
      AnimationController<GolemBossEntity> controller = event.getController();
      if (!this.isLaunchingRightHand() && !this.isLaunchingLeftHand() && !this.wasLaunchingLastTick && --this.launchAnimationGraceTime <= 0) {
         controller.markNeedsReload();
         controller.tickOffset = 1000.0;
         return PlayState.STOP;
      } else {
         this.wasLaunchingLastTick = this.isLaunchingRightHand();
         if (!this.isLaunchingRightHand() && !this.isLaunchingLeftHand() && this.launchAnimationGraceTime <= 0) {
            this.launchAnimationGraceTime = 10;
         }

         if (this.isLaunchingRightHand()) {
            THROW_RIGHT_ANIM.applyTo(controller, this);
         } else if (this.isLaunchingLeftHand()) {
            THROW_LEFT_ANIM.applyTo(controller, this);
         }

         return PlayState.CONTINUE;
      }
   }

   @Override
   public Map<String, BiFunction<VaultBossBaseEntity, Double, IMeleeAttack>> getMeleeAttackFactories() {
      return MELEE_ATTACK_FACTORIES;
   }

   @Override
   public double getAttackReach() {
      return 3.0;
   }

   private PlayState attackMoveAnimController(AnimationEvent<GolemBossEntity> event) {
      AnimationController<GolemBossEntity> controller = event.getController();
      return this.getActiveAttackMove().map(attackMove -> {
         if (attackMove.equals("punch")) {
            if (this.currentAttackAnimation == null) {
               this.currentAttackAnimation = PUNCH_ANIMATIONS.getRandom(this.level.random).orElse(null);
               return PlayState.STOP;
            } else {
               this.currentAttackAnimation.applyTo(controller, this);
               return PlayState.CONTINUE;
            }
         } else {
            if (attackMove.equals("double_punch")) {
               if (this.currentAttackAnimation != null) {
                  this.currentAttackAnimation.applyTo(controller, this);
                  return PlayState.CONTINUE;
               }

               this.currentAttackAnimation = DOUBLE_ATTACK_ANIM;
            }

            return PlayState.STOP;
         }
      }).orElseGet(() -> {
         this.currentAttackAnimation = null;
         controller.markNeedsReload();
         return PlayState.STOP;
      });
   }

   private PlayState walkAnimController(AnimationEvent<GolemBossEntity> event) {
      if (event.isMoving()) {
         WALK_ANIM.applyTo(event.getController(), this);
         return PlayState.CONTINUE;
      } else {
         return PlayState.STOP;
      }
   }
}
