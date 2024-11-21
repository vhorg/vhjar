package iskallia.vault.entity.boss;

import java.util.function.ToDoubleFunction;
import software.bernie.geckolib3.core.builder.AnimationBuilder;
import software.bernie.geckolib3.core.controller.AnimationController;

public record AnimationSpeedWrapper(AnimationBuilder animationBuilder, ToDoubleFunction<VaultBossEntity> calculateSpeed) {
   public AnimationSpeedWrapper(AnimationBuilder animationBuilder) {
      this(animationBuilder, b -> 1.0);
   }

   public void applyTo(AnimationController<?> controller, VaultBossEntity boss) {
      controller.setAnimation(this.animationBuilder);
      controller.setAnimationSpeed(this.calculateSpeed.applyAsDouble(boss));
   }
}
