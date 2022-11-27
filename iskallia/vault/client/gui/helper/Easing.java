package iskallia.vault.client.gui.helper;

import java.util.function.Function;

public enum Easing {
   CONSTANT_ONE(x -> 1.0F),
   LINEAR_IN(x -> x),
   LINEAR_OUT(x -> 1.0F - x),
   EASE_IN_OUT_SINE(x -> -((float)Math.cos(Math.PI * x.floatValue()) - 1.0F) / 2.0F),
   EXPO_OUT(x -> x == 1.0F ? 1.0F : 1.0F - (float)Math.pow(2.0, -10.0F * x)),
   EASE_OUT_BOUNCE(x -> {
      float n1 = 7.5625F;
      float d1 = 2.75F;
      if (x < 1.0F / d1) {
         return n1 * x * x;
      } else if (x < 2.0F / d1) {
         Float var5;
         return n1 * var5 = x - 1.5F / d1 * var5 + 0.75F;
      } else {
         Float var3;
         Float var4;
         return x.floatValue() < 2.5 / d1 ? n1 * var3 = x - 2.25F / d1 * var3 + 0.9375F : n1 * var4 = x - 2.625F / d1 * var4 + 0.984375F;
      }
   });

   final Function<Float, Float> function;

   private Easing(Function<Float, Float> function) {
      this.function = function;
   }

   public Function<Float, Float> getFunction() {
      return this.function;
   }

   public float calc(float time) {
      return this.function.apply(time);
   }
}
