package iskallia.vault.gear.attribute.ability.special.base.template.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.gear.attribute.ability.special.base.SpecialAbilityConfig;
import iskallia.vault.gear.attribute.ability.special.base.template.value.IntValue;
import java.util.Random;

public class IntRangeConfig extends SpecialAbilityConfig<IntValue> {
   @Expose
   private int min;
   @Expose
   private int max;
   @Expose
   private int step;

   public IntRangeConfig(int textColor, int highlightColor, int min, int max, int step) {
      super(textColor, highlightColor);
      this.min = min;
      this.max = max;
      this.step = step;
   }

   public int getMin() {
      return this.min;
   }

   public IntValue generateValue(Random rand) {
      int steps = Math.max(this.max - this.min, 0) / this.step + 1;
      return new IntValue(this.min + rand.nextInt(steps) * this.step);
   }

   public Integer generateMaximumValue() {
      int steps = Math.max(this.max - this.min, 0) / this.step;
      return this.min + steps * this.step;
   }
}
