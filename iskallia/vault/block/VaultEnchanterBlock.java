package iskallia.vault.block;

import iskallia.vault.block.base.FacedBlock;
import iskallia.vault.block.entity.VaultEnchanterTileEntity;
import iskallia.vault.block.entity.base.BookAnimatingTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.BlockHelper;
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
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

public class VaultEnchanterBlock extends FacedBlock implements EntityBlock {
   public static final VoxelShape SHAPE = Shapes.or(Block.box(1.0, 0.0, 1.0, 15.0, 9.0, 15.0), Block.box(0.0, 9.0, 0.0, 16.0, 16.0, 16.0));

   public VaultEnchanterBlock() {
      super(Properties.of(Material.STONE).noOcclusion().strength(1.5F).lightLevel(value -> 9));
   }

   public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      return SHAPE;
   }

   @Nonnull
   public InteractionResult use(@Nonnull BlockState state, Level level, @Nonnull BlockPos pos, @Nonnull Player player, InteractionHand hand, BlockHitResult hit) {
      if (level.isClientSide()) {
         return InteractionResult.SUCCESS;
      } else if (player instanceof ServerPlayer sPlayer) {
         if (level.getBlockEntity(pos) instanceof VaultEnchanterTileEntity enchanter) {
            NetworkHooks.openGui(sPlayer, enchanter, buffer -> buffer.writeBlockPos(pos));
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
      if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof VaultEnchanterTileEntity enchanter) {
         Containers.dropContents(level, pos, enchanter.getInventory());
         enchanter.getInventory().clearContent();
         level.updateNeighbourForOutputSignal(pos, this);
      }

      super.onRemove(state, level, pos, newState, isMoving);
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return ModBlocks.VAULT_ENCHANTER_TILE_ENTITY.create(pos, state);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState blockState, BlockEntityType<T> tileEntityType) {
      return level.isClientSide
         ? BlockHelper.getTicker(tileEntityType, ModBlocks.VAULT_ENCHANTER_TILE_ENTITY, BookAnimatingTileEntity::bookAnimationTick)
         : super.getTicker(level, blockState, tileEntityType);
   }
}
