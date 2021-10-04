package iskallia.vault.skill.ability.config.sub;

import com.google.gson.annotations.Expose;
import iskallia.vault.skill.ability.config.DashConfig;

public class DashDamageConfig extends DashConfig {
   @Expose
   private final float attackDamagePercentPerDash;
   @Expose
   private final float radiusOfAttack;

   public DashDamageConfig(int cost, int extraRadius, float attackDamagePercentPerDash, float radiusOfAttack) {
      super(cost, extraRadius);
      this.attackDamagePercentPerDash = attackDamagePercentPerDash;
      this.radiusOfAttack = radiusOfAttack;
   }

   public float getAttackDamagePercentPerDash() {
      return this.attackDamagePercentPerDash;
   }

   public float getRadiusOfAttack() {
      return this.radiusOfAttack;
   }
}
