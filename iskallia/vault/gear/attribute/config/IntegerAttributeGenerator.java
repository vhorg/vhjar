package iskallia.vault.gear.attribute.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.gear.reader.VaultGearModifierReader;
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

   public static class Range extends NumberRangeGenerator.NumberRange<Integer> {
      @Expose
      private int min;
      @Expose
      private int max;
      @Expose
      private int step;

      public Range(int min, int max, int step) {
         this.min = min;
         this.max = max;
         this.step = step;
      }

      public Integer generateNumber(Random random) {
         int steps = Math.max(this.max - this.min, 0) / this.step + 1;
         return this.min + random.nextInt(steps) * this.step;
      }
   }
}
