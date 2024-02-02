package iskallia.vault.block.entity;

import iskallia.vault.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

public class AngelBlockTileEntity extends BlockEntity {
   public int tickCount;
   private float activeRotation;

   public AngelBlockTileEntity(BlockPos pPos, BlockState pState) {
      super(ModBlocks.ANGEL_BLOCK_TILE_ENTITY, pPos, pState);
   }

   public static void tick(Level pLevel, BlockPos pPos, BlockState pState, AngelBlockTileEntity pBlockEntity) {
      pBlockEntity.tickCount++;
      pBlockEntity.activeRotation++;
   }

   public float getActiveRotation(float p_59198_) {
      return (this.activeRotation + p_59198_) * -0.0375F;
   }

   public void onLoad() {
      super.onLoad();
      ModBlocks.ANGEL_BLOCK.addPlayerAngelBlock(this.level.dimension(), this.worldPosition);
   }
}
