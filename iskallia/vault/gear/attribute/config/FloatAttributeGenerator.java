package iskallia.vault.gear.attribute.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.gear.reader.VaultGearModifierReader;
import java.text.DecimalFormat;
import java.util.Random;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public class FloatAttributeGenerator extends NumberRangeGenerator<Float, FloatAttributeGenerator.Range> {
   private static final DecimalFormat FORMAT = new DecimalFormat("0.##");

   @Nullable
   @Override
   public Class<FloatAttributeGenerator.Range> getConfigurationObjectClass() {
      return FloatAttributeGenerator.Range.class;
   }

   public MutableComponent getConfigDisplay(VaultGearModifierReader<Float> reader, FloatAttributeGenerator.Range object) {
      MutableComponent minDisplay = reader.getValueDisplay(object.min);
      MutableComponent maxDisplay = reader.getValueDisplay(object.max);
      return (MutableComponent)(minDisplay != null && maxDisplay != null ? minDisplay.append("-").append(maxDisplay) : new TextComponent(""));
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
         int steps = Mth.floor(Math.max(this.max - this.min, 0.0F) / this.step) + 1;
         return this.min + random.nextInt(steps) * this.step;
      }
   }
}
