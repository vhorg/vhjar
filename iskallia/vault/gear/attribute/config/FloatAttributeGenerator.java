package iskallia.vault.gear.attribute.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.gear.reader.VaultGearModifierReader;
import iskallia.vault.util.MiscUtils;
import java.text.DecimalFormat;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;

public class FloatAttributeGenerator extends NumberRangeGenerator<Float, FloatAttributeGenerator.Range> {
   private static final DecimalFormat FORMAT = new DecimalFormat("0.##");

   @Nullable
   @Override
   public Class<FloatAttributeGenerator.Range> getConfigurationObjectClass() {
      return FloatAttributeGenerator.Range.class;
   }

   @Nullable
   public MutableComponent getConfigRangeDisplay(VaultGearModifierReader<Float> reader, FloatAttributeGenerator.Range min, FloatAttributeGenerator.Range max) {
      MutableComponent minDisplay = reader.getValueDisplay(min.min);
      MutableComponent maxDisplay = reader.getValueDisplay(max.max);
      return (MutableComponent)(minDisplay != null && maxDisplay != null ? minDisplay.append("-").append(maxDisplay) : new TextComponent(""));
   }

   @Nullable
   public MutableComponent getConfigDisplay(VaultGearModifierReader<Float> reader, FloatAttributeGenerator.Range object) {
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
   public Optional<Float> getMinimumValue(List<FloatAttributeGenerator.Range> configurations) {
      return configurations.stream().map(range -> range.min).min(Double::compare);
   }

   @Override
   public Optional<Float> getMaximumValue(List<FloatAttributeGenerator.Range> configurations) {
      return configurations.stream().map(FloatAttributeGenerator.Range::generateMaximumNumber).min(Double::compare);
   }

   public Optional<Float> getRollPercentage(Float value, List<FloatAttributeGenerator.Range> configurations) {
      return MiscUtils.getFloatValueRange(value, this.getMinimumValue(configurations), this.getMaximumValue(configurations), f -> f);
   }

   public static class Range extends NumberRangeGenerator.NumberRange<Float> {
      @Expose
      private float min;
      @Expose
      private float max;
      @Expose
      private float step;

      public Range(float min, float max) {
         this(min, max, 0.1F);
      }

      public Range(float min, float max, float step) {
         this.min = min;
         this.max = max;
         this.step = step;
      }

      public Float generateNumber(Random random) {
         int steps = Math.round(Math.max(this.max - this.min, 0.0F) / this.step) + 1;
         return this.min + random.nextInt(steps) * this.step;
      }

      public Float generateMaximumNumber() {
         int steps = Math.round(Math.max(this.max - this.min, 0.0F) / this.step);
         return this.min + steps * this.step;
      }
   }
}
