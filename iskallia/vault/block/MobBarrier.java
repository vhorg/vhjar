package iskallia.vault.block;

import iskallia.vault.entity.entity.EternalEntity;
import iskallia.vault.init.ModBlocks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class MobBarrier extends Block implements EntityBlock {
   public MobBarrier() {
      super(
         Properties.copy(Blocks.GLASS)
            .strength(-1.0F, 3600000.0F)
            .noOcclusion()
            .isSuffocating((state, blockGetter, pos) -> false)
            .isViewBlocking((state, blockGetter, pos) -> false)
      );
   }

   public float getShadeBrightness(BlockState pState, BlockGetter pLevel, BlockPos pPos) {
      return 1.0F;
   }

   public boolean propagatesSkylightDown(BlockState pState, BlockGetter pReader, BlockPos pPos) {
      return true;
   }

   public VoxelShape getCollisionShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      if (pContext instanceof EntityCollisionContext entityCollisionContext) {
         Entity entity = entityCollisionContext.getEntity();
         if (entity instanceof Player || entity instanceof EternalEntity) {
            return Shapes.empty();
         }
      }

      return super.getCollisionShape(pState, pLevel, pPos, pContext);
   }

   public boolean skipRendering(BlockState pState, BlockState pAdjacentBlockState, Direction pSide) {
      return pAdjacentBlockState.is(this) ? true : super.skipRendering(pState, pAdjacentBlockState, pSide);
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return ModBlocks.MOB_BARRIER_ENTITY.create(pos, state);
   }
}
