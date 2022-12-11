package iskallia.vault.core.random;

import iskallia.vault.core.random.lcg.LCG;
import java.util.Random;

public class JavaRandom extends LCGRandom {
   public static final long MULTIPLIER = LCG.JAVA.multiplier;
   public static final long ADDEND = LCG.JAVA.addend;
   public static final long MASK = LCG.JAVA.modulus - 1L;
   protected double nextNextGaussian;
   protected boolean haveNextNextGaussian;

   protected JavaRandom(long seed) {
      super(LCG.JAVA, seed);
   }

   public static JavaRandom ofInternal(long seed) {
      return new JavaRandom(seed);
   }

   public static JavaRandom ofScrambled(long seed) {
      return new JavaRandom(seed ^ MULTIPLIER);
   }

   public static JavaRandom ofNanoTime() {
      return new JavaRandom(new Random().nextLong());
   }

   @Override
   public void setSeed(long seed) {
      super.setSeed(seed ^ MULTIPLIER);
   }

   public int next(int bits) {
      return (int)(this.nextSeed() >>> 48 - bits);
   }

   @Override
   public long nextSeed() {
      return this.seed = this.seed * MULTIPLIER + ADDEND & MASK;
   }

   @Override
   public boolean nextBoolean() {
      return this.next(1) != 0;
   }

   @Override
   public int nextInt() {
      return this.next(32);
   }

   @Override
   public int nextInt(int bound) {
      if (bound <= 0) {
         throw new IllegalArgumentException("bound must be positive");
      } else if ((bound & -bound) == bound) {
         return (int)((long)bound * this.next(31) >> 31);
      } else {
         int bits;
         int value;
         do {
            bits = this.next(31);
            value = bits % bound;
         } while (bits - value + (bound - 1) < 0);

         return value;
      }
   }

   @Override
   public float nextFloat() {
      return this.next(24) / 1.6777216E7F;
   }

   @Override
   public long nextLong() {
      return ((long)this.next(32) << 32) + this.next(32);
   }

   @Override
   public double nextDouble() {
      return (((long)this.next(26) << 27) + this.next(27)) * 1.110223E-16F;
   }

   @Override
   public double nextGaussian() {
      if (this.haveNextNextGaussian) {
         this.haveNextNextGaussian = false;
         return this.nextNextGaussian;
      } else {
         double v1;
         double v2;
         double s;
         do {
            v1 = 2.0 * this.nextDouble() - 1.0;
            v2 = 2.0 * this.nextDouble() - 1.0;
            s = v1 * v1 + v2 * v2;
         } while (s >= 1.0 || s == 0.0);

         double multiplier = StrictMath.sqrt(-2.0 * StrictMath.log(s) / s);
         this.nextNextGaussian = v2 * multiplier;
         this.haveNextNextGaussian = true;
         return v1 * multiplier;
      }
   }

   public Random toRandom() {
      return new Random(this.seed ^ MULTIPLIER);
   }

   public Random asRandomView() {
      return new JavaRandom.View(this);
   }

   public JavaRandom copy() {
      JavaRandom copy = new JavaRandom(this.seed);
      copy.haveNextNextGaussian = this.haveNextNextGaussian;
      copy.nextNextGaussian = this.nextNextGaussian;
      return copy;
   }

   protected static class View extends Random {
      private final JavaRandom delegate;

      protected View(JavaRandom delegate) {
         this.delegate = delegate;
      }

      @Override
      public void setSeed(long seed) {
         if (this.delegate != null) {
            this.delegate.setSeed(seed);
         }
      }

      @Override
      protected int next(int bits) {
         return this.delegate.next(bits);
      }

      @Override
      public double nextGaussian() {
         return this.delegate.nextGaussian();
      }
   }
}
