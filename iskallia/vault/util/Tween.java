package iskallia.vault.util;

import net.minecraft.util.Mth;

public abstract class Tween {
   public static final Tween LINEAR = new Tween() {
      @Override
      public float tween(float t, float b, float c, float d) {
         return c * t / d + b;
      }
   };
   public static final Tween PARABOLIC = new Tween() {
      @Override
      public float tween(float t, float b, float c, float d) {
         float p = t / d;
         return (-4.0F * (p - 0.5F) * (p - 0.5F) + 1.0F) * c + b;
      }
   };
   public static final Tween EASE_IN_QUADRATIC = new Tween() {
      @Override
      public float tween(float t, float b, float c, float d) {
         float var5;
         return c * (var5 = t / d) * var5 + b;
      }
   };
   public static final Tween EASE_OUT_QUADRATIC = new Tween() {
      @Override
      public float tween(float t, float b, float c, float d) {
         float var5;
         return -c * (var5 = t / d) * (var5 - 2.0F) + b;
      }
   };
   public static final Tween EASE_INOUT_QUADRATIC = new Tween() {
      @Override
      public float tween(float t, float b, float c, float d) {
         float var5;
         return (var5 = t / (d / 2.0F)) < 1.0F ? c / 2.0F * var5 * var5 + b : -c / 2.0F * (--var5 * (var5 - 2.0F) - 1.0F) + b;
      }
   };
   public static final Tween EASE_IN_CUBIC = new Tween() {
      @Override
      public float tween(float t, float b, float c, float d) {
         float var5;
         return c * (var5 = t / d) * var5 * var5 + b;
      }
   };
   public static final Tween EASE_OUT_CUBIC = new Tween() {
      @Override
      public float tween(float t, float b, float c, float d) {
         float var5;
         return c * ((var5 = t / d - 1.0F) * var5 * var5 + 1.0F) + b;
      }
   };
   public static final Tween EASE_INOUT_CUBIC = new Tween() {
      @Override
      public float tween(float t, float b, float c, float d) {
         float var5;
         float var6;
         return (var5 = t / (d / 2.0F)) < 1.0F ? c / 2.0F * var5 * var5 * var5 + b : c / 2.0F * ((var6 = var5 - 2.0F) * var6 * var6 + 2.0F) + b;
      }
   };
   public static final Tween EASE_IN_QUARTIC = new Tween() {
      @Override
      public float tween(float t, float b, float c, float d) {
         float var5;
         return c * (var5 = t / d) * var5 * var5 * var5 + b;
      }
   };
   public static final Tween EASE_OUT_QUARTIC = new Tween() {
      @Override
      public float tween(float t, float b, float c, float d) {
         float var5;
         return -c * ((var5 = t / d - 1.0F) * var5 * var5 * var5 - 1.0F) + b;
      }
   };
   public static final Tween EASE_INOUT_QUARTIC = new Tween() {
      @Override
      public float tween(float t, float b, float c, float d) {
         float var5;
         float var6;
         return (var5 = t / (d / 2.0F)) < 1.0F ? c / 2.0F * var5 * var5 * var5 * var5 + b : -c / 2.0F * ((var6 = var5 - 2.0F) * var6 * var6 * var6 - 2.0F) + b;
      }
   };
   public static final Tween EASE_IN_QUINTIC = new Tween() {
      @Override
      public float tween(float t, float b, float c, float d) {
         float var5;
         return c * (var5 = t / d) * var5 * var5 * var5 * var5 + b;
      }
   };
   public static final Tween EASE_OUT_QUINTIC = new Tween() {
      @Override
      public float tween(float t, float b, float c, float d) {
         float var5;
         return c * ((var5 = t / d - 1.0F) * var5 * var5 * var5 * var5 + 1.0F) + b;
      }
   };
   public static final Tween EASE_INOUT_QUINTIC = new Tween() {
      @Override
      public float tween(float t, float b, float c, float d) {
         float var5;
         float var6;
         return (var5 = t / (d / 2.0F)) < 1.0F
            ? c / 2.0F * var5 * var5 * var5 * var5 * var5 + b
            : c / 2.0F * ((var6 = var5 - 2.0F) * var6 * var6 * var6 * var6 + 2.0F) + b;
      }
   };
   public static final Tween EASE_IN_SINUSOIDAL = new Tween() {
      @Override
      public float tween(float t, float b, float c, float d) {
         return -c * Mth.cos(t / d * (float) (Math.PI / 2)) + c + b;
      }
   };
   public static final Tween EASE_OUT_SINUSOIDAL = new Tween() {
      @Override
      public float tween(float t, float b, float c, float d) {
         return c * Mth.sin(t / d * (float) (Math.PI / 2)) + b;
      }
   };
   public static final Tween EASE_INOUT_SINUSOIDAL = new Tween() {
      @Override
      public float tween(float t, float b, float c, float d) {
         return -c / 2.0F * (Mth.cos((float) Math.PI * t / d) - 1.0F) + b;
      }
   };
   public static final Tween EASE_IN_EXPONENTIAL = new Tween() {
      @Override
      public float tween(float t, float b, float c, float d) {
         return t == 0.0F ? b : (float)(c * Math.pow(2.0, 10.0F * (t / d - 1.0F)) + b);
      }
   };
   public static final Tween EASE_OUT_EXPONENTIAL = new Tween() {
      @Override
      public float tween(float t, float b, float c, float d) {
         return t == d ? b + c : (float)(c * (-Math.pow(2.0, -10.0F * t / d) + 1.0) + b);
      }
   };
   public static final Tween EASE_INOUT_EXPONENTIAL = new Tween() {
      @Override
      public float tween(float t, float b, float c, float d) {
         if (t == 0.0F) {
            return b;
         } else if (t == d) {
            return b + c;
         } else {
            float var5;
            return (var5 = t / (d / 2.0F)) < 1.0F
               ? (float)(c / 2.0F * Math.pow(2.0, 10.0F * (var5 - 1.0F)) + b)
               : (float)(c / 2.0F * (-Math.pow(2.0, -10.0F * --var5) + 2.0) + b);
         }
      }
   };
   public static final Tween EASE_IN_CIRCLULAR = new Tween() {
      @Override
      public float tween(float t, float b, float c, float d) {
         float var5;
         return -c * (Mth.sqrt(1.0F - (var5 = t / d) * var5) - 1.0F) + b;
      }
   };
   public static final Tween EASE_OUT_CIRCLULAR = new Tween() {
      @Override
      public float tween(float t, float b, float c, float d) {
         float var5;
         return c * Mth.sqrt(1.0F - (var5 = t / d - 1.0F) * var5) + b;
      }
   };
   public static final Tween EASE_INOUT_CIRCLULAR = new Tween() {
      @Override
      public float tween(float t, float b, float c, float d) {
         float var5;
         float var6;
         return (var5 = t / (d / 2.0F)) < 1.0F
            ? -c / 2.0F * (Mth.sqrt(1.0F - var5 * var5) - 1.0F) + b
            : c / 2.0F * (Mth.sqrt(1.0F - (var6 = var5 - 2.0F) * var6) + 1.0F) + b;
      }
   };
   public static final Tween EASE_IN_ELASTIC = new Tween() {
      @Override
      public float tween(float t, float b, float c, float d) {
         if (t == 0.0F) {
            return b;
         } else if ((t = t / d) == 1.0F) {
            return b + c;
         } else {
            float p = d * 0.3F;
            float s = p / 4.0F;
            return (float)(-(c * Math.pow(2.0, 10.0F * --t) * Mth.sin((t * d - s) * (float) (Math.PI * 2) / p)) + b);
         }
      }
   };
   public static final Tween EASE_OUT_ELASTIC = new Tween() {
      @Override
      public float tween(float t, float b, float c, float d) {
         if (t == 0.0F) {
            return b;
         } else if ((t = t / d) == 1.0F) {
            return b + c;
         } else {
            float p = d * 0.3F;
            float s = p / 4.0F;
            return (float)(c * Math.pow(2.0, -10.0F * t) * Mth.sin((t * d - s) * (float) (Math.PI * 2) / p) + c + b);
         }
      }
   };
   public static final Tween EASE_IN_BACK = new Tween() {
      @Override
      public float tween(float t, float b, float c, float d) {
         float var5;
         return c * (var5 = t / d) * var5 * (2.70158F * var5 - 1.70158F) + b;
      }
   };
   public static final Tween EASE_OUT_BACK = new Tween() {
      @Override
      public float tween(float t, float b, float c, float d) {
         float var5;
         return c * ((var5 = t / d - 1.0F) * var5 * (2.70158F * var5 + 1.70158F) + 1.0F) + b;
      }
   };
   public static final Tween EASE_INOUT_BACK = new Tween() {
      @Override
      public float tween(float t, float b, float c, float d) {
         float s = 1.70158F;
         float var6;
         float var7;
         float var8;
         float var9;
         return (var6 = t / (d / 2.0F)) < 1.0F
            ? c / 2.0F * var6 * var6 * (((var8 = s * 1.525F) + 1.0F) * var6 - var8) + b
            : c / 2.0F * ((var7 = var6 - 2.0F) * var7 * (((var9 = s * 1.525F) + 1.0F) * var7 + var9) + 2.0F) + b;
      }
   };
   public static final Tween EASE_IN_BOUNCE = new Tween() {
      @Override
      public float tween(float t, float b, float c, float d) {
         return c - Tween.EASE_OUT_BOUNCE.tween(d - t, 0.0F, c, d) + b;
      }
   };
   public static final Tween EASE_OUT_BOUNCE = new Tween() {
      @Override
      public float tween(float t, float b, float c, float d) {
         if ((t = t / d) < 0.36363636363636365) {
            return c * 7.5625F * t * t + b;
         } else if (t < 0.7272727272727273) {
            float var8;
            return c * (7.5625F * (var8 = t - 0.54545456F) * var8 + 0.75F) + b;
         } else {
            float var6;
            float var7;
            return t < 0.9090909090909091
               ? c * (7.5625F * (var6 = t - 0.8181818F) * var6 + 0.9375F) + b
               : c * (7.5625F * (var7 = t - 0.95454544F) * var7 + 0.984375F) + b;
         }
      }
   };
   public static final Tween EASE_INOUT_BOUNCE = new Tween() {
      @Override
      public float tween(float t, float b, float c, float d) {
         return t < d / 2.0F
            ? Tween.EASE_IN_BOUNCE.tween(t * 2.0F, 0.0F, c, d) * 0.5F + b
            : Tween.EASE_OUT_BOUNCE.tween(t * 2.0F - d, 0.0F, c, d) * 0.5F + c * 0.5F + b;
      }
   };

   public abstract float tween(float var1, float var2, float var3, float var4);

   public static Tween.InOut inOut(Tween in, Tween out) {
      return new Tween.InOut(in, out);
   }

   public static Tween.InOut inOut(Tween in, Tween out, float threshold) {
      return new Tween.InOut(in, out, threshold);
   }

   public static final float easeInElastic(float t, float b, float c, float d, float a, float p) {
      if (t == 0.0F) {
         return b;
      } else if ((t = t / d) == 1.0F) {
         return b + c;
      } else {
         if (p == 0.0F) {
            p = d * 0.3F;
         }

         float s;
         if (a < Mth.abs(c)) {
            a = c;
            s = p / 4.0F;
         } else {
            s = (float)(p / (float) (Math.PI * 2) * Math.asin(c / a));
         }

         return (float)(-(a * Math.pow(2.0, 10.0F * --t) * Mth.sin((t * d - s) * (float) (Math.PI * 2) / p)) + b);
      }
   }

   public static final class InOut extends Tween {
      private final Tween in;
      private final Tween out;
      private final float threshold;

      private InOut(Tween in, Tween out) {
         this(in, out, 0.5F);
      }

      private InOut(Tween in, Tween out, float threshold) {
         this.in = in;
         this.out = out;
         this.threshold = Mth.clamp(threshold, 0.0F, 1.0F);
      }

      @Override
      public float tween(float t, float b, float c, float d) {
         if (t / d <= this.threshold) {
            return this.in.tween(t, b, c, d * this.threshold);
         } else {
            t = (float)MathUtilities.map(t, d * this.threshold, d, 0.0, d);
            return this.out.tween(t, b + c, -c, d);
         }
      }
   }
}
