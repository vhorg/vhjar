package iskallia.vault.block;

import iskallia.vault.block.entity.ToolViseTile;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.Container;
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

public class ToolViseBlock extends Block implements EntityBlock {
   public static final VoxelShape SHAPE_X = Shapes.or(
      Block.box(0.0, 0.0, 0.0, 16.0, 3.0, 16.0), new VoxelShape[]{Block.box(4.0, 3.0, 4.0, 12.0, 7.0, 12.0), Block.box(2.5, 7.0, 0.0, 13.5, 16.0, 16.0)}
   );
   public static final VoxelShape SHAPE_Z = Shapes.or(
      Block.box(0.0, 0.0, 0.0, 16.0, 3.0, 16.0), new VoxelShape[]{Block.box(4.0, 3.0, 4.0, 12.0, 7.0, 12.0), Block.box(0.0, 7.0, 2.5, 16.0, 16.0, 13.5)}
   );
   public static final EnumProperty<Axis> AXIS = BlockStateProperties.AXIS;

   public ToolViseBlock() {
      super(Properties.of(Material.METAL).requiresCorrectToolForDrops().strength(0.5F).noOcclusion());
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

   public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
      if (pLevel.isClientSide) {
         return InteractionResult.SUCCESS;
      } else {
         if (pLevel.getBlockEntity(pPos) instanceof ToolViseTile tile) {
            pPlayer.openMenu(tile);
         }

         return InteractionResult.CONSUME;
      }
   }

   public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
      if (!pState.is(pNewState.getBlock())) {
         if (pLevel.getBlockEntity(pPos) instanceof Container container) {
            Containers.dropContents(pLevel, pPos, container);
            pLevel.updateNeighbourForOutputSignal(pPos, this);
         }

         super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
      }
   }

   public BlockState rotate(BlockState pState, Rotation pRot) {
      return (BlockState)pState.cycle(AXIS);
   }

   @org.jetbrains.annotations.Nullable
   public BlockState getStateForPlacement(BlockPlaceContext pContext) {
      return (BlockState)super.getStateForPlacement(pContext).setValue(AXIS, pContext.getHorizontalDirection().getAxis());
   }
}
