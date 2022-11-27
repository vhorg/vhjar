package iskallia.vault.core.util;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.world.level.ChunkPos;

public class RegionPos extends Vec3i {
   private final int sizeX;
   private final int sizeZ;

   protected RegionPos(int x, int z, int sizeX, int sizeZ) {
      super(x, 0, z);
      this.sizeX = sizeX;
      this.sizeZ = sizeZ;
   }

   public static RegionPos of(int x, int z, int sizeX, int sizeZ) {
      return new RegionPos(x, z, sizeX, sizeZ);
   }

   public static RegionPos ofChunkPos(ChunkPos chunkPos, int sizeX, int sizeY) {
      int x = chunkPos.x < 0 ? chunkPos.x - sizeX + 1 : chunkPos.x;
      int z = chunkPos.z < 0 ? chunkPos.z - sizeY + 1 : chunkPos.z;
      return new RegionPos(x / sizeX, z / sizeY, sizeX, sizeY);
   }

   public static RegionPos ofBlockPos(BlockPos blockPos, int sizeX, int sizeZ) {
      int x = blockPos.getX() < 0 ? blockPos.getX() - sizeX + 1 : blockPos.getX();
      int z = blockPos.getZ() < 0 ? blockPos.getZ() - sizeZ + 1 : blockPos.getZ();
      return new RegionPos(x / sizeX, z / sizeZ, sizeX, sizeZ);
   }

   public int getSizeX() {
      return this.sizeX;
   }

   public int getSizeZ() {
      return this.sizeZ;
   }

   public RegionPos with(int x, int z) {
      return new RegionPos(x, z, this.sizeX, this.sizeZ);
   }

   public BlockPos getLocalPos(BlockPos blockPos) {
      return new BlockPos(blockPos.getX() % this.sizeX, blockPos.getY(), blockPos.getZ() % this.sizeX);
   }

   public ChunkPos getLocalPos(ChunkPos chunkPos) {
      return new ChunkPos(chunkPos.x % this.sizeX, chunkPos.z % this.sizeX);
   }

   public BlockPos toBlockPos() {
      return new BlockPos(this.getX() * this.getSizeX(), 0, this.getZ() * this.getSizeX());
   }

   public ChunkPos toChunkPos() {
      return new ChunkPos(this.getX() * this.getSizeX(), this.getZ() * this.getSizeX());
   }

   public RegionPos add(int x, int z) {
      return new RegionPos(this.getX() + x, this.getZ() + z, this.sizeX, this.sizeZ);
   }
}
