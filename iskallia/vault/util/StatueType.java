package iskallia.vault.util;

public enum StatueType {
   GIFT_NORMAL,
   GIFT_MEGA,
   VAULT_BOSS,
   OMEGA,
   TROPHY,
   OMEGA_VARIANT;

   public float getPlayerRenderYOffset() {
      return 0.9F;
   }

   public boolean isOmega() {
      return this == OMEGA || this == OMEGA_VARIANT;
   }

   public boolean doGrayscaleShader() {
      return !this.isOmega() && this != TROPHY;
   }

   public boolean doesStatueCauldronAccept() {
      return !this.isOmega() && this != TROPHY;
   }

   public boolean hasLimitedItems() {
      return !this.isOmega() && this != TROPHY;
   }

   public boolean dropsItems() {
      return this != TROPHY;
   }

   public boolean allowsRenaming() {
      return this != TROPHY;
   }
}
