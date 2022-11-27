package iskallia.vault.gear.attribute.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.gear.reader.VaultGearModifierReader;
import java.util.Random;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public class IntegerAttributeGenerator extends NumberRangeGenerator<Integer, IntegerAttributeGenerator.Range> {
   @Nullable
   @Override
   public Class<IntegerAttributeGenerator.Range> getConfigurationObjectClass() {
      return IntegerAttributeGenerator.Range.class;
   }

   public MutableComponent getConfigDisplay(VaultGearModifierReader<Integer> reader, IntegerAttributeGenerator.Range object) {
      MutableComponent minDisplay = reader.getValueDisplay(object.min);
      MutableComponent maxDisplay = reader.getValueDisplay(object.max);
      return (MutableComponent)(minDisplay != null && maxDisplay != null ? minDisplay.append("-").append(maxDisplay) : new TextComponent(""));
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
         int steps = Mth.floor(Math.max(this.max - this.min, 0) / this.step) + 1;
         return this.min + random.nextInt(steps) * this.step;
      }
   }
}
