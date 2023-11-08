package iskallia.vault.core.random.lcg;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.util.MathUtils;
import iskallia.vault.item.crystal.data.adapter.ISimpleAdapter;
import java.util.Objects;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import org.jetbrains.annotations.Nullable;

public class Lcg {
   public static final Lcg CC65_M23 = new Lcg(65793L, 4282663L, 8388608L);
   public static final Lcg VISUAL_BASIC = new Lcg(1140671485L, 12820163L, 16777216L);
   public static final Lcg RTL_UNIFORM = new Lcg(2147483629L, 2147483587L, 2147483647L);
   public static final Lcg MINSTD_RAND0_C = new Lcg(16807L, 0L, 2147483647L);
   public static final Lcg MINSTD_RAND_C = new Lcg(48271L, 0L, 2147483647L);
   public static final Lcg CC65_M31 = new Lcg(16843009L, 826366247L, 8388608L);
   public static final Lcg RANDU = new Lcg(65539L, 0L, 2147483648L);
   public static final Lcg GLIB_C = new Lcg(1103515245L, 12345L, 2147483648L);
   public static final Lcg BORLAND_C = new Lcg(22695477L, 1L, 4294967296L);
   public static final Lcg PASCAL = new Lcg(134775813L, 1L, 4294967296L);
   public static final Lcg OPEN_VMS = new Lcg(69069L, 1L, 4294967296L);
   public static final Lcg NUMERICAL_RECIPES = new Lcg(1664525L, 1013904223L, 4294967296L);
   public static final Lcg MS_VISUAL_C = new Lcg(214013L, 2531011L, 4294967296L);
   public static final Lcg JAVA = new Lcg(25214903917L, 11L, 281474976710656L);
   public static final Lcg JAVA_UNIQUIFIER_OLD = new Lcg(181783497276652981L, 0L);
   public static final Lcg JAVA_UNIQUIFIER_NEW = new Lcg(1181783497276652981L, 0L);
   public static final Lcg MMIX = new Lcg(6364136223846793005L, 1442695040888963407L);
   public static final Lcg NEWLIB_C = new Lcg(6364136223846793005L, 1L);
   public static final Lcg XKCD = new Lcg(0L, 4L);
   public final long multiplier;
   public final long addend;
   public final long modulus;
   private final boolean isPowerOf2;
   private final int trailingZeros;

   public Lcg(long multiplier, long addend) {
      this(multiplier, addend, 0L);
   }

   public Lcg(long multiplier, long addend, long modulus) {
      this.multiplier = multiplier;
      this.addend = addend;
      this.modulus = modulus;
      this.isPowerOf2 = this.modulus == 0L || MathUtils.isPowerOf2(this.modulus);
      this.trailingZeros = this.isPowerOf2 ? Long.numberOfTrailingZeros(this.modulus) : -1;
   }

   public static Lcg combine(Lcg... lcgs) {
      Lcg lcg = lcgs[0];

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

   public Lcg combine(long steps) {
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
      return new Lcg(multiplier, addend, this.modulus);
   }

   public Lcg combine(Lcg lcg) {
      if (this.modulus != lcg.modulus) {
         throw new UnsupportedOperationException();
      } else {
         return new Lcg(this.multiplier * lcg.multiplier, lcg.multiplier * this.addend + lcg.addend, this.modulus);
      }
   }

   public Lcg invert() {
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
         return !(other instanceof Lcg lcg) ? false : this.multiplier == lcg.multiplier && this.addend == lcg.addend && this.modulus == lcg.modulus;
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

   public static class Adapter implements ISimpleAdapter<Lcg, CompoundTag, JsonObject> {
      private boolean nullable;

      public Adapter(boolean nullable) {
         this.nullable = nullable;
      }

      public Lcg.Adapter asNullable() {
         return new Lcg.Adapter(true);
      }

      public void writeBits(@Nullable Lcg lcg, BitBuffer buffer) {
         if (this.nullable) {
            buffer.writeBoolean(lcg == null);
         }

         if (lcg != null) {
            Adapters.LONG.writeBits(Long.valueOf(lcg.multiplier), buffer);
            Adapters.LONG_SEGMENTED_15.writeBits(Long.valueOf(lcg.addend), buffer);
            Adapters.LONG.writeBits(Long.valueOf(lcg.modulus), buffer);
         }
      }

      @Override
      public Optional<Lcg> readBits(BitBuffer buffer) {
         return this.nullable && buffer.readBoolean()
            ? Optional.empty()
            : Optional.of(
               new Lcg(
                  Adapters.LONG.readBits(buffer).orElseThrow(),
                  Adapters.LONG_SEGMENTED_15.readBits(buffer).orElseThrow(),
                  Adapters.LONG.readBits(buffer).orElseThrow()
               )
            );
      }
   }
}
