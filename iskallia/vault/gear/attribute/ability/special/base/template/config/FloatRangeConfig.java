package iskallia.vault.gear.attribute.ability.special.base.template.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.gear.attribute.ability.special.base.SpecialAbilityConfig;
import iskallia.vault.gear.attribute.ability.special.base.template.value.FloatValue;
import java.util.Random;

public class FloatRangeConfig extends SpecialAbilityConfig<FloatValue> {
   @Expose
   private float min;
   @Expose
   private float max;
   @Expose
   private float step;

   public FloatRangeConfig(int textColor, int highlightColor, float min, float max, float step) {
      super(textColor, highlightColor);
      this.min = min;
      this.max = max;
      this.step = step;
   }

   public float getMin() {
      return this.min;
   }

   public FloatValue generateValue(Random rand) {
      int steps = Math.round(Math.max(this.max - this.min, 0.0F) / this.step) + 1;
      return new FloatValue(this.min + rand.nextInt(steps) * this.step);
   }

   public Float generateMaximumValue() {
      int steps = Math.round(Math.max(this.max - this.min, 0.0F) / this.step);
      return this.min + steps * this.step;
   }
}
