package iskallia.vault.gear.attribute.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.gear.reader.VaultGearModifierReader;
import iskallia.vault.util.MiscUtils;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;

public class IntegerAttributeGenerator extends NumberRangeGenerator<Integer, IntegerAttributeGenerator.Range> {
   @Nullable
   @Override
   public Class<IntegerAttributeGenerator.Range> getConfigurationObjectClass() {
      return IntegerAttributeGenerator.Range.class;
   }

   @Nullable
   public MutableComponent getConfigRangeDisplay(
      VaultGearModifierReader<Integer> reader, IntegerAttributeGenerator.Range min, IntegerAttributeGenerator.Range max
   ) {
      MutableComponent minDisplay = reader.getValueDisplay(min.min);
      MutableComponent maxDisplay = reader.getValueDisplay(max.max);
      return (MutableComponent)(minDisplay != null && maxDisplay != null ? minDisplay.append("-").append(maxDisplay) : new TextComponent(""));
   }

   @Nullable
   public MutableComponent getConfigDisplay(VaultGearModifierReader<Integer> reader, IntegerAttributeGenerator.Range object) {
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
   public Optional<Integer> getMinimumValue(List<IntegerAttributeGenerator.Range> configurations) {
      return configurations.stream().map(range -> range.min).min(Double::compare);
   }

   @Override
   public Optional<Integer> getMaximumValue(List<IntegerAttributeGenerator.Range> configurations) {
      return configurations.stream().map(IntegerAttributeGenerator.Range::generateMaximumNumber).min(Double::compare);
   }

   public Optional<Float> getRollPercentage(Integer value, List<IntegerAttributeGenerator.Range> configurations) {
      return MiscUtils.getIntValueRange(value, this.getMinimumValue(configurations), this.getMaximumValue(configurations), i -> i);
   }

   public static class Range extends NumberRangeGenerator.NumberRange<Integer> {
      @Expose
      public int min;
      @Expose
      public int max;
      @Expose
      public int step;

      public Range(int min, int max, int step) {
         this.min = min;
         this.max = max;
         this.step = step;
      }

      public Integer generateNumber(Random random) {
         int steps = Math.max(this.max - this.min, 0) / this.step + 1;
         return this.min + random.nextInt(steps) * this.step;
      }

      public Integer generateMaximumNumber() {
         int steps = Math.max(this.max - this.min, 0) / this.step;
         return this.min + steps * this.step;
      }
   }
}
