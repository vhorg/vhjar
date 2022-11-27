package iskallia.vault.core.random;

import iskallia.vault.core.util.MathUtils;

public class ChunkRandom extends JavaRandom {
   protected ChunkRandom(long seed) {
      super(seed);
   }

   public static ChunkRandom any() {
      return new ChunkRandom(0L);
   }

   public static ChunkRandom ofInternal(long seed) {
      return new ChunkRandom(seed);
   }

   public static ChunkRandom ofScrambled(long seed) {
      return new ChunkRandom(seed ^ MULTIPLIER);
   }

   public long setTerrainSeed(int chunkX, int chunkZ) {
      long seed = chunkX * 341873128712L + chunkZ * 132897987541L;
      this.setSeed(seed);
      return seed & MathUtils.MASK_48;
   }

   public long setPopulationSeed(long worldSeed, int x, int z) {
      this.setSeed(worldSeed);
      long a = this.nextLong() | 1L;
      long b = this.nextLong() | 1L;
      long seed = x * a + z * b ^ worldSeed;
      this.setSeed(seed);
      return seed & MathUtils.MASK_48;
   }

   public long setDecoratorSeed(long populationSeed, int index, int step) {
      return this.setDecoratorSeed(populationSeed, index + 10000 * step);
   }

   public long setDecoratorSeed(long populationSeed, int salt) {
      long seed = populationSeed + salt;
      this.setSeed(seed);
      return seed & MathUtils.MASK_48;
   }

   public long setDecoratorSeed(long worldSeed, int blockX, int blockZ, int index, int step) {
      long populationSeed = this.setPopulationSeed(worldSeed, blockX, blockZ);
      return this.setDecoratorSeed(populationSeed, index, step);
   }

   public long setDecoratorSeed(long worldSeed, int blockX, int blockZ, int salt) {
      long populationSeed = this.setPopulationSeed(worldSeed, blockX, blockZ);
      return this.setDecoratorSeed(populationSeed, salt);
   }

   public long setCarverSeed(long worldSeed, int chunkX, int chunkZ) {
      this.setSeed(worldSeed);
      long a = this.nextLong();
      long b = this.nextLong();
      long seed = chunkX * a ^ chunkZ * b ^ worldSeed;
      this.setSeed(seed);
      return seed & MathUtils.MASK_48;
   }

   public long setRegionSeed(long worldSeed, int regionX, int regionZ, int salt) {
      long seed = regionX * 341873128712L + regionZ * 132897987541L + worldSeed + salt;
      this.setSeed(seed);
      return seed & MathUtils.MASK_48;
   }

   public long setWeakSeed(long worldSeed, int chunkX, int chunkZ) {
      int sX = chunkX >> 4;
      int sZ = chunkZ >> 4;
      long seed = sX ^ sZ << 4 ^ worldSeed;
      this.setSeed(seed);
      return seed & MathUtils.MASK_48;
   }

   public long setSlimeSeed(long worldSeed, int chunkX, int chunkZ, long scrambler) {
      long seed = worldSeed + chunkX * chunkX * 4987142 + chunkX * 5947611 + chunkZ * chunkZ * 4392871L + chunkZ * 389711 ^ scrambler;
      this.setSeed(seed);
      return seed & MathUtils.MASK_48;
   }

   public long setSlimeSeed(long worldSeed, int chunkX, int chunkZ) {
      return this.setSlimeSeed(worldSeed, chunkX, chunkZ, 987234911L);
   }
}
