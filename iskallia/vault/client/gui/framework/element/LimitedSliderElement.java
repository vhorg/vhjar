package iskallia.vault.client.gui.framework.element;

import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import java.util.function.Consumer;
import net.minecraft.util.Mth;

public class LimitedSliderElement extends ScalableSliderElement {
   private final float min;
   private final float max;

   public LimitedSliderElement(ISpatial spatial, float min, float max, Consumer<Float> onValueChanged) {
      super(spatial, onValueChanged);
      this.min = min;
      this.max = max;
   }

   @Override
   public void setValue(float value) {
      super.setValue(Mth.clamp(value, this.min, this.max));
   }
}
