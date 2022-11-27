package iskallia.vault.core.random;

import iskallia.vault.core.random.lcg.LCG;

public abstract class LCGRandom implements RandomSource {
   protected final LCG lcg;
   protected long seed;

   protected LCGRandom(LCG lcg, long seed) {
      this.lcg = lcg;
      this.seed = seed;
   }

   public LCG getLCG() {
      return this.lcg;
   }

   public long getSeed() {
      return this.seed;
   }

   public void setSeed(long seed) {
      this.seed = seed;
   }

   public abstract long nextSeed();
}
