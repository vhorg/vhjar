package iskallia.vault.gear.attribute.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.gear.reader.VaultGearModifierReader;
import java.util.Random;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public class DoubleAttributeGenerator extends NumberRangeGenerator<Double, DoubleAttributeGenerator.Range> {
   @Nullable
   @Override
   public Class<DoubleAttributeGenerator.Range> getConfigurationObjectClass() {
      return DoubleAttributeGenerator.Range.class;
   }

   public MutableComponent getConfigDisplay(VaultGearModifierReader<Double> reader, DoubleAttributeGenerator.Range object) {
      MutableComponent minDisplay = reader.getValueDisplay(object.min);
      MutableComponent maxDisplay = reader.getValueDisplay(object.max);
      return (MutableComponent)(minDisplay != null && maxDisplay != null ? minDisplay.append("-").append(maxDisplay) : new TextComponent(""));
   }

   public static class Range extends NumberRangeGenerator.NumberRange<Double> {
      @Expose
      private double min;
      @Expose
      private double max;
      @Expose
      private double step;

      public Range(double min, double max) {
         this(min, max, 0.1);
      }

      public Range(double min, double max, double step) {
         this.min = min;
         this.max = max;
         this.step = step;
      }

      public Double generateNumber(Random random) {
         int steps = Mth.floor(Math.max(this.max - this.min, 0.0) / this.step) + 1;
         return this.min + random.nextInt(steps) * this.step;
      }
   }
}
