package iskallia.vault.core.random.lcg;

import iskallia.vault.core.util.MathUtils;
import java.util.Objects;

public class LCG {
   public static final LCG CC65_M23 = new LCG(65793L, 4282663L, 8388608L);
   public static final LCG VISUAL_BASIC = new LCG(1140671485L, 12820163L, 16777216L);
   public static final LCG RTL_UNIFORM = new LCG(2147483629L, 2147483587L, 2147483647L);
   public static final LCG MINSTD_RAND0_C = new LCG(16807L, 0L, 2147483647L);
   public static final LCG MINSTD_RAND_C = new LCG(48271L, 0L, 2147483647L);
   public static final LCG CC65_M31 = new LCG(16843009L, 826366247L, 8388608L);
   public static final LCG RANDU = new LCG(65539L, 0L, 2147483648L);
   public static final LCG GLIB_C = new LCG(1103515245L, 12345L, 2147483648L);
   public static final LCG BORLAND_C = new LCG(22695477L, 1L, 4294967296L);
   public static final LCG PASCAL = new LCG(134775813L, 1L, 4294967296L);
   public static final LCG OPEN_VMS = new LCG(69069L, 1L, 4294967296L);
   public static final LCG NUMERICAL_RECIPES = new LCG(1664525L, 1013904223L, 4294967296L);
   public static final LCG MS_VISUAL_C = new LCG(214013L, 2531011L, 4294967296L);
   public static final LCG JAVA = new LCG(25214903917L, 11L, 281474976710656L);
   public static final LCG JAVA_UNIQUIFIER_OLD = new LCG(181783497276652981L, 0L);
   public static final LCG JAVA_UNIQUIFIER_NEW = new LCG(1181783497276652981L, 0L);
   public static final LCG MMIX = new LCG(6364136223846793005L, 1442695040888963407L);
   public static final LCG NEWLIB_C = new LCG(6364136223846793005L, 1L);
   public static final LCG XKCD = new LCG(0L, 4L);
   public final long multiplier;
   public final long addend;
   public final long modulus;
   private final boolean isPowerOf2;
   private final int trailingZeros;

   public LCG(long multiplier, long addend) {
      this(multiplier, addend, 0L);
   }

   public LCG(long multiplier, long addend, long modulus) {
      this.multiplier = multiplier;
      this.addend = addend;
      this.modulus = modulus;
      this.isPowerOf2 = this.modulus == 0L || MathUtils.isPowerOf2(this.modulus);
      this.trailingZeros = this.isPowerOf2 ? Long.numberOfTrailingZeros(this.modulus) : -1;
   }

   public static LCG combine(LCG... lcgs) {
      LCG lcg = lcgs[0];

      for (int i = 1; i < lcgs.length; i++) {
         lcg = lcg.combine(lcgs[i]);
      }

      return lcg;
   }

   public boolean isModPowerOf2() {
      return this.isPowerOf2;
   }

   public int getModTrailingZeroes() {
      return this.trailingZeros;
   }

   public boolean isMultiplicative() {
      return this.addend == 0L;
   }

   public long nextSeed(long seed) {
      return this.mod(seed * this.multiplier + this.addend);
   }

   public long mod(long n) {
      if (this.isModPowerOf2()) {
         return n & this.modulus - 1L;
      } else if (n <= 4294967296L) {
         return Long.remainderUnsigned(n, this.modulus);
      } else {
         throw new UnsupportedOperationException();
      }
   }

   public LCG combine(long steps) {
      long multiplier = 1L;
      long addend = 0L;
      long intermediateMultiplier = this.multiplier;
      long intermediateAddend = this.addend;

      for (long k = steps; k != 0L; k >>>= 1) {
         if ((k & 1L) != 0L) {
            multiplier *= intermediateMultiplier;
            addend = intermediateMultiplier * addend + intermediateAddend;
         }

         intermediateAddend = (intermediateMultiplier + 1L) * intermediateAddend;
         intermediateMultiplier *= intermediateMultiplier;
      }

      multiplier = this.mod(multiplier);
      addend = this.mod(addend);
      return new LCG(multiplier, addend, this.modulus);
   }

   public LCG combine(LCG lcg) {
      if (this.modulus != lcg.modulus) {
         throw new UnsupportedOperationException();
      } else {
         return new LCG(this.multiplier * lcg.multiplier, lcg.multiplier * this.addend + lcg.addend, this.modulus);
      }
   }

   public LCG invert() {
      return this.combine(-1L);
   }

   public long distance(long seed1, long seed2) {
      if (DiscreteLog.supports(this)) {
         long aFromZero = DiscreteLog.distanceFromZero(this, seed1);
         long bFromZero = DiscreteLog.distanceFromZero(this, seed2);
         return MathUtils.maskSigned(bFromZero - aFromZero, this.getModTrailingZeroes());
      } else {
         throw new UnsupportedOperationException("DiscreteLog is not supported by this LCG");
      }
   }

   @Override
   public boolean equals(Object other) {
      if (other == this) {
         return true;
      } else {
         return !(other instanceof LCG lcg) ? false : this.multiplier == lcg.multiplier && this.addend == lcg.addend && this.modulus == lcg.modulus;
      }
   }

   @Override
   public int hashCode() {
      return Objects.hash(this.multiplier, this.addend, this.modulus);
   }

   @Override
   public String toString() {
      return "LCG{multiplier=" + this.multiplier + ", addend=" + this.addend + ", modulus=" + this.modulus + "}";
   }
}
