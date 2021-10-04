package iskallia.vault.skill.ability.config.sub;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.GhostWalkConfig;

public class GhostWalkDamageConfig extends GhostWalkConfig {
   @Expose
   private final float damageMultiplierInGhostWalk;

   public GhostWalkDamageConfig(int cost, int level, int durationTicks, float damageMultiplierInGhostWalk) {
      super(cost, level, durationTicks);
      this.damageMultiplierInGhostWalk = damageMultiplierInGhostWalk;
   }

   public float getDamageMultiplierInGhostWalk() {
      return this.damageMultiplierInGhostWalk;
   }
}
