package iskallia.vault.block.entity;

import iskallia.vault.block.DemagnetizerBlock;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;

public class DemagnetizerTileEntity extends BlockEntity {
   public DemagnetizerTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.DEMAGNETIZER_TILE_ENTITY, pos, state);
   }

   public static boolean hasDemagnetizerAround(Entity entity) {
      int radius = ModConfigs.MAGNET_CONFIG.getDemagnetizerRadius();
      return getDemagnetizers(entity, radius)
         .stream()
         .filter(demagnetizer -> demagnetizer.getLevel() == entity.level)
         .filter(demagnetizer -> demagnetizer.getBlockState().getOptionalValue(DemagnetizerBlock.DEACTIVATED).isPresent())
         .anyMatch(demagnetizer -> !(Boolean)demagnetizer.getBlockState().getValue(DemagnetizerBlock.DEACTIVATED));
   }

   private static List<DemagnetizerTileEntity> getDemagnetizers(Entity entity, double radius) {
      List<DemagnetizerTileEntity> demagnetizers = new ArrayList<>();
      BlockPos entityPosition = entity.blockPosition();
      double radiusSq = radius * radius;
      int iRadius = Mth.ceil(radius);
      Vec3i radVec = new Vec3i(iRadius, iRadius, iRadius);
      ChunkPos posMin = new ChunkPos(entityPosition.subtract(radVec));
      ChunkPos posMax = new ChunkPos(entityPosition.offset(radVec));

      for (int xx = posMin.x; xx <= posMax.x; xx++) {
         for (int zz = posMin.z; zz <= posMax.z; zz++) {
            LevelChunk ch = entity.getLevel().getChunkSource().getChunkNow(xx, zz);
            if (ch != null) {
               Map<BlockPos, BlockEntity> blockEntities = ch.getBlockEntities();
               blockEntities.forEach((pos, tile) -> {
                  if (tile instanceof DemagnetizerTileEntity demagnetizer) {
                     if (pos.distSqr(entityPosition) <= radiusSq) {
                        demagnetizers.add(demagnetizer);
                     }
                  }
               });
            }
         }
      }

      return demagnetizers;
   }
}
