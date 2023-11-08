package iskallia.vault.core.random;

import iskallia.vault.core.util.MathUtils;
import net.minecraft.core.BlockPos;

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

   public static ChunkRandom wrap(LCGRandom random) {
      return new ChunkRandom.Wrapper(random);
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

   public long setRegionSeed(long worldSeed, int regionX, int regionZ, long salt) {
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

   public long setModelSeed(int blockX, int blockY, int blockZ) {
      long seed = blockX * 3129871 ^ blockZ * 116129781L ^ blockY;
      seed = seed * seed * 42317861L + seed * 11L >> 16;
      this.setSeed(seed);
      return seed & MathUtils.MASK_48;
   }

   public long setBlockSeed(long worldSeed, BlockPos pos, long salt) {
      return this.setBlockSeed(worldSeed, pos.getX(), pos.getY(), pos.getZ(), salt);
   }

   public long setBlockSeed(long worldSeed, int blockX, int blockY, int blockZ, long salt) {
      this.setSeed(worldSeed + salt);
      long a = this.nextLong() | 1L;
      long b = this.nextLong() | 1L;
      long c = this.nextLong() | 1L;
      long d = this.nextLong() | 1L;
      long seed = blockX * a + blockY * b + blockZ * c + salt * d ^ worldSeed;
      this.setSeed(seed);
      return seed & MathUtils.MASK_48;
   }

   protected static class Wrapper extends ChunkRandom {
      private final LCGRandom delegate;

      protected Wrapper(LCGRandom delegate) {
         super(delegate.getSeed());
         this.delegate = delegate;
      }

      @Override
      public void setSeed(long seed) {
         this.delegate.setSeed(seed);
      }

      @Override
      public long nextLong() {
         return this.delegate.nextLong();
      }
   }
}
