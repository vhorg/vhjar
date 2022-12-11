package iskallia.vault.block.entity;

import iskallia.vault.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class CrystalBuddingBlockEntity extends BlockEntity {
   private static final int UPDATE_INTERVAL_TICKS = 20;
   private int tickCount;

   public CrystalBuddingBlockEntity(BlockPos blockPos, BlockState blockState) {
      super(ModBlocks.CRYSTAL_BUDDING_TILE_ENTITY, blockPos, blockState);
   }

   public static void serverTick(Level level, BlockPos blockPos, BlockState blockState, CrystalBuddingBlockEntity blockEntity) {
      if (level instanceof ServerLevel serverLevel && blockState.is(ModBlocks.CRYSTAL_BUDDING)) {
         if (--blockEntity.tickCount <= 0) {
            blockEntity.tickCount = 20;
            blockEntity.setChanged();
            ModBlocks.CRYSTAL_BUDDING.checkAndScheduleTick(blockState, serverLevel, blockPos, serverLevel.random);
         }
      }
   }
}
