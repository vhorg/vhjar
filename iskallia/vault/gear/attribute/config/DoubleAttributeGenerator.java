package iskallia.vault.gear.attribute.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.gear.reader.VaultGearModifierReader;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;

public class DoubleAttributeGenerator extends NumberRangeGenerator<Double, DoubleAttributeGenerator.Range> {
   @Nullable
   @Override
   public Class<DoubleAttributeGenerator.Range> getConfigurationObjectClass() {
      return DoubleAttributeGenerator.Range.class;
   }

   @Nullable
   public MutableComponent getConfigRangeDisplay(VaultGearModifierReader<Double> reader, DoubleAttributeGenerator.Range min, DoubleAttributeGenerator.Range max) {
      MutableComponent minDisplay = reader.getValueDisplay(min.min);
      MutableComponent maxDisplay = reader.getValueDisplay(max.max);
      return (MutableComponent)(minDisplay != null && maxDisplay != null ? minDisplay.append("-").append(maxDisplay) : new TextComponent(""));
   }

   @Nullable
   public MutableComponent getConfigDisplay(VaultGearModifierReader<Double> reader, DoubleAttributeGenerator.Range object) {
      MutableComponent range = this.getConfigRangeDisplay(reader, object);
      return range == null
         ? null
         : new TextComponent("")
            .withStyle(reader.getColoredTextStyle())
            .append(range.withStyle(reader.getColoredTextStyle()))
            .append(" ")
            .append(new TextComponent(reader.getModifierName()).withStyle(reader.getColoredTextStyle()));
   }

   @Override
   public Optional<Double> getMinimumValue(List<DoubleAttributeGenerator.Range> configurations) {
      return configurations.stream().map(range -> range.min).min(Double::compare);
   }

   @Override
   public Optional<Double> getMaximumValue(List<DoubleAttributeGenerator.Range> configurations) {
      return configurations.stream().map(DoubleAttributeGenerator.Range::generateMaximumNumber).min(Double::compare);
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
         int steps = (int)(Math.round(Math.max(this.max - this.min, 0.0) / this.step) + 1L);
         return this.min + random.nextInt(steps) * this.step;
      }

      public Double generateMaximumNumber() {
         int steps = (int)Math.round(Math.max(this.max - this.min, 0.0) / this.step);
         return this.min + steps * this.step;
      }
   }
}
