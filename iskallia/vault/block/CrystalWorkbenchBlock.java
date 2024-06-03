package iskallia.vault.block;

import iskallia.vault.block.base.FacedBlock;
import iskallia.vault.block.entity.CrystalWorkbenchTileEntity;
import iskallia.vault.init.ModBlocks;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

public class CrystalWorkbenchBlock extends FacedBlock implements EntityBlock {
   public static VoxelShape SHAPE = Block.box(2.0, 1.0, 2.0, 14.0, 14.0, 14.0);

   public CrystalWorkbenchBlock() {
      super(Properties.of(Material.STONE).strength(0.5F).noOcclusion());
   }

   @Nonnull
   public VoxelShape getShape(@Nonnull BlockState pState, @Nonnull BlockGetter pLevel, @Nonnull BlockPos pPos, @Nonnull CollisionContext pContext) {
      return SHAPE;
   }

   public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
      if (level.isClientSide()) {
         return InteractionResult.SUCCESS;
      } else if (player instanceof ServerPlayer serverPlayer) {
         if (level.getBlockEntity(pos) instanceof CrystalWorkbenchTileEntity entity) {
            NetworkHooks.openGui(serverPlayer, entity, buffer -> buffer.writeBlockPos(pos));
            return InteractionResult.SUCCESS;
         } else {
            return InteractionResult.SUCCESS;
         }
      } else {
         return InteractionResult.SUCCESS;
      }
   }

   @Override
   public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
      return false;
   }

   public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
      if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof CrystalWorkbenchTileEntity entity) {
         entity.getIngredients()
            .getOverSizedContents()
            .forEach(
               overSizedStack -> overSizedStack.splitByStackSize()
                  .forEach(splitStack -> Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), splitStack))
            );
         entity.getIngredients().clearContent();
         level.updateNeighbourForOutputSignal(pos, this);
      }

      super.onRemove(state, level, pos, newState, isMoving);
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return ModBlocks.CRYSTAL_WORKBENCH_ENTITY.create(pos, state);
   }
}
