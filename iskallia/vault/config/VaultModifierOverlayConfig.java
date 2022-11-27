package iskallia.vault.config;

import com.google.gson.annotations.Expose;

public class VaultModifierOverlayConfig extends Config {
   @Expose
   public int columns;
   @Expose
   public int spacingX;
   @Expose
   public int spacingY;
   @Expose
   public int size;
   @Expose
   public int rightMargin;
   @Expose
   public int bottomMargin;
   @Expose
   public int textOffsetX;
   @Expose
   public int textOffsetY;

   @Override
   public String getName() {
      return "vault_modifier_overlay";
   }

   @Override
   protected void reset() {
      this.columns = 4;
      this.spacingX = 8;
      this.spacingY = 4;
      this.size = 16;
      this.rightMargin = 8;
      this.bottomMargin = 4;
      this.textOffsetX = 4;
      this.textOffsetY = 2;
   }
}
