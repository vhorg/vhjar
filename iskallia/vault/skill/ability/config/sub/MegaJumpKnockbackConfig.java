package iskallia.vault.skill.ability.config.sub;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.MegaJumpConfig;

public class MegaJumpKnockbackConfig extends MegaJumpConfig {
   @Expose
   private final float radius;
   @Expose
   private final float knockbackStrengthMultiplier;

   public MegaJumpKnockbackConfig(int cost, int extraHeight, float radius, float knockbackStrengthMultiplier) {
      super(cost, extraHeight);
      this.radius = radius;
      this.knockbackStrengthMultiplier = knockbackStrengthMultiplier;
   }

   public float getRadius() {
      return this.radius;
   }

   public float getKnockbackStrengthMultiplier() {
      return this.knockbackStrengthMultiplier;
   }
}
