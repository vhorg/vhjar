package iskallia.vault.core.random;

import com.google.gson.JsonObject;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.core.random.lcg.Lcg;
import java.util.Optional;
import java.util.Random;
import net.minecraft.nbt.CompoundTag;

public class JavaRandom extends LCGRandom {
   public static final long MULTIPLIER = Lcg.JAVA.multiplier;
   public static final long ADDEND = Lcg.JAVA.addend;
   public static final long MASK = Lcg.JAVA.modulus - 1L;
   protected double nextNextGaussian;
   protected boolean haveNextNextGaussian;

   protected JavaRandom(long seed) {
      super(Lcg.JAVA, seed);
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

   @Override
   public void writeBits(BitBuffer buffer) {
      Adapters.ofBoundedLong(this.lcg.modulus).writeBits(Long.valueOf(this.seed), buffer);
      Adapters.BOOLEAN.writeBits(this.haveNextNextGaussian, buffer);
      if (this.haveNextNextGaussian) {
         Adapters.DOUBLE.writeBits(Double.valueOf(this.nextNextGaussian), buffer);
      }
   }

   @Override
   public void readBits(BitBuffer buffer) {
      this.seed = Adapters.ofBoundedLong(this.lcg.modulus).readBits(buffer).orElseThrow();
      this.haveNextNextGaussian = Adapters.BOOLEAN.readBits(buffer).orElseThrow();
      if (this.haveNextNextGaussian) {
         this.nextNextGaussian = Adapters.DOUBLE.readBits(buffer).orElseThrow();
      }
   }

   @Override
   public Optional<CompoundTag> writeNbt() {
      CompoundTag nbt = new CompoundTag();
      Adapters.LONG.writeNbt(Long.valueOf(this.seed)).ifPresent(value -> nbt.put("seed", value));
      Adapters.BOOLEAN.writeNbt(this.haveNextNextGaussian).ifPresent(value -> nbt.put("haveNextNextGaussian", value));
      if (this.haveNextNextGaussian) {
         Adapters.DOUBLE.writeNbt(Double.valueOf(this.nextNextGaussian)).ifPresent(value -> nbt.put("nextNextGaussian", value));
      }

      return Optional.of(nbt);
   }

   @Override
   public void readNbt(CompoundTag nbt) {
      this.seed = Adapters.LONG.readNbt(nbt.get("seed")).orElseThrow();
      this.haveNextNextGaussian = Adapters.BOOLEAN.readNbt(nbt.get("haveNextNextGaussian")).orElseThrow();
      if (this.haveNextNextGaussian) {
         this.nextNextGaussian = Adapters.DOUBLE.readNbt(nbt.get("nextNextGaussian")).orElseThrow();
      }
   }

   @Override
   public Optional<JsonObject> writeJson() {
      JsonObject json = new JsonObject();
      Adapters.LONG.writeJson(Long.valueOf(this.seed)).ifPresent(value -> json.add("seed", value));
      Adapters.BOOLEAN.writeJson(this.haveNextNextGaussian).ifPresent(value -> json.add("haveNextNextGaussian", value));
      if (this.haveNextNextGaussian) {
         Adapters.DOUBLE.writeJson(Double.valueOf(this.nextNextGaussian)).ifPresent(value -> json.add("nextNextGaussian", value));
      }

      return Optional.of(json);
   }

   @Override
   public void readJson(JsonObject json) {
      this.seed = Adapters.LONG.readJson(json.get("seed")).orElseThrow();
      this.haveNextNextGaussian = Adapters.BOOLEAN.readJson(json.get("haveNextNextGaussian")).orElseThrow();
      if (this.haveNextNextGaussian) {
         this.nextNextGaussian = Adapters.DOUBLE.readJson(json.get("nextNextGaussian")).orElseThrow();
      }
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
