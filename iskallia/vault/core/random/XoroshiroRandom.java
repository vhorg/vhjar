package iskallia.vault.core.random;

public abstract class XoroshiroRandom implements RandomSource {
   public abstract void setSeed(long var1);

   public long nextBits(int bits) {
      return this.nextLong() >>> 64 - bits;
   }

   @Override
   public boolean nextBoolean() {
      return (this.nextLong() & 1L) != 0L;
   }

   @Override
   public int nextInt() {
      return (int)this.nextLong();
   }

   @Override
   public int nextInt(int bound) {
      if (bound <= 0) {
         throw new IllegalArgumentException("Bound must be positive");
      } else {
         long i = this.nextInt() & 4294967295L;
         long j = i * bound;
         long k = j & 4294967295L;
         if (k < bound) {
            for (int l = Integer.remainderUnsigned(~bound + 1, bound); k < l; k = j & 4294967295L) {
               i = this.nextInt() & 4294967295L;
               j = i * bound;
            }
         }

         return (int)(j >> 32);
      }
   }

   @Override
   public float nextFloat() {
      return (float)this.nextBits(24) * 5.9604645E-8F;
   }

   @Override
   public double nextDouble() {
      return this.nextBits(53) * 1.110223E-16F;
   }
}
