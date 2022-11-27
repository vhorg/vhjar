package iskallia.vault.skill.ability.effect.spi.core;

public enum AbilityActionResult {
   FAIL(false),
   SUCCESS_COOLDOWN(true),
   SUCCESS_COOLDOWN_DEFERRED(true);

   private final boolean success;

   private AbilityActionResult(boolean success) {
      this.success = success;
   }

   public boolean isSuccess() {
      return this.success;
   }
}
