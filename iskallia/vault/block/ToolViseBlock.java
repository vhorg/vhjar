package iskallia.vault.block;

import iskallia.vault.block.entity.ToolViseTile;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
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
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

public class ToolViseBlock extends Block implements EntityBlock {
   public static final VoxelShape SHAPE_X = Shapes.or(
      Block.box(0.0, 0.0, 0.0, 16.0, 3.0, 16.0), new VoxelShape[]{Block.box(4.0, 3.0, 4.0, 12.0, 7.0, 12.0), Block.box(2.5, 7.0, 0.0, 13.5, 16.0, 16.0)}
   );
   public static final VoxelShape SHAPE_Z = Shapes.or(
      Block.box(0.0, 0.0, 0.0, 16.0, 3.0, 16.0), new VoxelShape[]{Block.box(4.0, 3.0, 4.0, 12.0, 7.0, 12.0), Block.box(0.0, 7.0, 2.5, 16.0, 16.0, 13.5)}
   );
   public static final EnumProperty<Axis> AXIS = BlockStateProperties.AXIS;

   public ToolViseBlock() {
      super(Properties.of(Material.METAL).strength(0.5F).noOcclusion());
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
      super.createBlockStateDefinition(pBuilder);
      pBuilder.add(new Property[]{AXIS});
   }

   public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      return pState.getValue(AXIS) == Axis.X ? SHAPE_X : SHAPE_Z;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
      return new ToolViseTile(pPos, pState);
   }

   public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
      if (level.isClientSide()) {
         return InteractionResult.SUCCESS;
      } else if (player instanceof ServerPlayer sPlayer) {
         if (level.getBlockEntity(pos) instanceof ToolViseTile toolViseTile) {
            NetworkHooks.openGui(sPlayer, toolViseTile, buffer -> buffer.writeBlockPos(pos));
            return InteractionResult.SUCCESS;
         } else {
            return InteractionResult.SUCCESS;
         }
      } else {
         return InteractionResult.SUCCESS;
      }
   }

   public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
      if (!state.is(newState.getBlock()) && level.getBlockEntity(pos) instanceof ToolViseTile toolViseTile) {
         toolViseTile.getInventory()
            .getOverSizedContents()
            .forEach(
               overSizedStack -> overSizedStack.splitByStackSize()
                  .forEach(splitStack -> Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), splitStack))
            );
         Containers.dropContents(level, pos, toolViseTile.getPickaxeInput());
         toolViseTile.getInventory().clearContent();
         toolViseTile.getPickaxeInput().clearContent();
         level.updateNeighbourForOutputSignal(pos, this);
      }

      super.onRemove(state, level, pos, newState, isMoving);
   }

   public BlockState rotate(BlockState pState, Rotation pRot) {
      return (BlockState)pState.cycle(AXIS);
   }

   @org.jetbrains.annotations.Nullable
   public BlockState getStateForPlacement(BlockPlaceContext pContext) {
      return (BlockState)super.getStateForPlacement(pContext).setValue(AXIS, pContext.getHorizontalDirection().getAxis());
   }
}
