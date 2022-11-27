package iskallia.vault.gear.reader;

import javax.annotation.Nullable;
import net.minecraft.network.chat.MutableComponent;

public class AttackSpeedDecimalReader extends DecimalModifierReader.Added<Double> {
   public AttackSpeedDecimalReader(String modifierName, int rgbColor) {
      super(modifierName, rgbColor);
   }

   @Nullable
   public MutableComponent getValueDisplay(Double value) {
      return super.getValueDisplay(4.0 + value);
   }
}
