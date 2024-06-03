package iskallia.vault.block;

import iskallia.vault.block.entity.CatalystInfusionTableTileEntity;
import iskallia.vault.init.ModBlocks;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

public class CatalystInfusionBlock extends Block implements EntityBlock {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;

   public CatalystInfusionBlock() {
      super(Properties.of(Material.STONE).strength(1.5F, 6.0F).noOcclusion());
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return (BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
   }

   @Nonnull
   @ParametersAreNonnullByDefault
   public VoxelShape getOcclusionShape(BlockState state, BlockGetter world, BlockPos pos) {
      return LecternBlock.SHAPE_COMMON;
   }

   @Nonnull
   @ParametersAreNonnullByDefault
   public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
      return LecternBlock.SHAPE_COLLISION;
   }

   @Nonnull
   @ParametersAreNonnullByDefault
   public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      return switch ((Direction)state.getValue(FACING)) {
         case NORTH -> LecternBlock.SHAPE_NORTH;
         case SOUTH -> LecternBlock.SHAPE_SOUTH;
         case EAST -> LecternBlock.SHAPE_EAST;
         case WEST -> LecternBlock.SHAPE_WEST;
         default -> LecternBlock.SHAPE_COMMON;
      };
   }

   @Nonnull
   @ParametersAreNonnullByDefault
   public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
      if (world.isClientSide()) {
         return InteractionResult.SUCCESS;
      } else if (world.getBlockEntity(pos) instanceof CatalystInfusionTableTileEntity entity) {
         if (player instanceof ServerPlayer serverPlayer) {
            NetworkHooks.openGui(serverPlayer, entity, buffer -> buffer.writeBlockPos(pos));
            return InteractionResult.SUCCESS;
         } else {
            return InteractionResult.SUCCESS;
         }
      } else {
         return InteractionResult.SUCCESS;
      }
   }

   public void onRemove(BlockState state, @Nonnull Level world, @Nonnull BlockPos pos, BlockState newState, boolean isMoving) {
      if (!state.is(newState.getBlock()) && world.getBlockEntity(pos) instanceof CatalystInfusionTableTileEntity entity) {
         entity.getInventory()
            .getOverSizedContents()
            .forEach(stack -> stack.splitByStackSize().forEach(split -> Containers.dropItemStack(world, pos.getX(), pos.getY(), pos.getZ(), split)));
         Containers.dropContents(world, pos, entity.getResultContainer());
         entity.getInventory().clearContent();
         entity.getResultContainer().clearContent();
         world.updateNeighbourForOutputSignal(pos, this);
      }

      super.onRemove(state, world, pos, newState, isMoving);
   }

   public boolean useShapeForLightOcclusion(@Nonnull BlockState state) {
      return true;
   }

   @ParametersAreNonnullByDefault
   public boolean isPathfindable(BlockState state, BlockGetter worldIn, BlockPos pos, PathComputationType type) {
      return false;
   }

   @Nonnull
   public BlockState rotate(BlockState state, Rotation rot) {
      return (BlockState)state.setValue(FACING, rot.rotate((Direction)state.getValue(FACING)));
   }

   @Nonnull
   public BlockState mirror(BlockState state, Mirror mirrorIn) {
      return state.rotate(mirrorIn.getRotation((Direction)state.getValue(FACING)));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING});
   }

   @ParametersAreNonnullByDefault
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return ModBlocks.CATALYST_INFUSION_TABLE_TILE_ENTITY.create(pos, state);
   }
}
