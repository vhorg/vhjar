package iskallia.vault.gear.attribute.ability.special.base;

import com.google.gson.annotations.Expose;
import java.util.Random;

public abstract class SpecialAbilityConfig<V extends SpecialAbilityConfigValue> {
   @Expose
   private int textColor;
   @Expose
   private int highlightColor;

   protected SpecialAbilityConfig(int textColor, int highlightColor) {
      this.textColor = textColor;
      this.highlightColor = highlightColor;
   }

   public abstract V generateValue(Random var1);

   public int getTextColor() {
      return this.textColor;
   }

   public int getHighlightColor() {
      return this.highlightColor;
   }
}
