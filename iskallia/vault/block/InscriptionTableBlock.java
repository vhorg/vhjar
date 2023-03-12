package iskallia.vault.block;

import iskallia.vault.block.base.FacedBlock;
import iskallia.vault.block.entity.InscriptionTableTileEntity;
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
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

public class InscriptionTableBlock extends FacedBlock implements EntityBlock {
   public static VoxelShape SHAPE = Shapes.or(Block.box(1.0, 11.0, 1.0, 15.0, 13.0, 15.0), Block.box(3.0, 0.0, 3.0, 13.0, 11.0, 13.0));

   public InscriptionTableBlock() {
      super(Properties.of(Material.STONE).strength(0.5F).lightLevel(state -> 13).noOcclusion());
   }

   @Nonnull
   public VoxelShape getShape(@Nonnull BlockState pState, @Nonnull BlockGetter pLevel, @Nonnull BlockPos pPos, @Nonnull CollisionContext pContext) {
      return SHAPE;
   }

   @Nonnull
   public InteractionResult use(@Nonnull BlockState state, Level level, @Nonnull BlockPos pos, @Nonnull Player player, InteractionHand hand, BlockHitResult hit) {
      if (level.isClientSide()) {
         return InteractionResult.SUCCESS;
      } else if (player instanceof ServerPlayer sPlayer) {
         if (level.getBlockEntity(pos) instanceof InscriptionTableTileEntity toolStationTileEntity) {
            NetworkHooks.openGui(sPlayer, toolStationTileEntity, buffer -> buffer.writeBlockPos(pos));
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
      if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof InscriptionTableTileEntity craftingStation) {
         craftingStation.getInventory()
            .getOverSizedContents()
            .forEach(
               overSizedStack -> overSizedStack.splitByStackSize()
                  .forEach(splitStack -> Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), splitStack))
            );
         Containers.dropContents(level, pos, craftingStation.getResultContainer());
         craftingStation.getInventory().clearContent();
         craftingStation.getResultContainer().clearContent();
         level.updateNeighbourForOutputSignal(pos, this);
      }

      super.onRemove(state, level, pos, newState, isMoving);
   }

   @Nullable
   public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
      return ModBlocks.INSCRIPTION_TABLE_TILE_ENTITY.create(pos, state);
   }
}
