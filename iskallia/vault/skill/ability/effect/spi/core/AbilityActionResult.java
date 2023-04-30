package iskallia.vault.skill.ability.effect.spi.core;

public class AbilityActionResult {
   private static final AbilityActionResult FAIL = new AbilityActionResult(AbilityActionResult.State.FAIL, false, 0);
   private static final AbilityActionResult SUCCESS_COOLDOWN_IMMEDIATE = new AbilityActionResult(AbilityActionResult.State.SUCCESS, true, 0);
   private static final AbilityActionResult SUCCESS_COOLDOWN_DEFERRED = new AbilityActionResult(AbilityActionResult.State.SUCCESS, false, 0);
   private final AbilityActionResult.State state;
   private final boolean startCooldown;
   private final int cooldownDelayTicks;

   private AbilityActionResult(AbilityActionResult.State state, boolean startCooldown, int cooldownDelayTicks) {
      this.state = state;
      this.startCooldown = startCooldown;
      this.cooldownDelayTicks = cooldownDelayTicks;
   }

   public boolean isSuccess() {
      return this.state == AbilityActionResult.State.SUCCESS;
   }

   public boolean startCooldown() {
      return this.startCooldown;
   }

   public int getCooldownDelayTicks() {
      return this.cooldownDelayTicks;
   }

   public static AbilityActionResult fail() {
      return FAIL;
   }

   public static AbilityActionResult successCooldownImmediate() {
      return SUCCESS_COOLDOWN_IMMEDIATE;
   }

   public static AbilityActionResult successCooldownDeferred() {
      return SUCCESS_COOLDOWN_DEFERRED;
   }

   public static AbilityActionResult successCooldownDelayed(int cooldownDelayTicks) {
      return new AbilityActionResult(AbilityActionResult.State.SUCCESS, true, cooldownDelayTicks);
   }

   public static enum State {
      FAIL,
      SUCCESS;
   }
}
