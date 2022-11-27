package iskallia.vault.block;

import iskallia.vault.block.entity.MonolithTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.BlockHelper;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class MonolithBlock extends Block implements EntityBlock {
   public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
   public static final BooleanProperty FILLED = BooleanProperty.create("filled");
   private static final VoxelShape SHAPE = Stream.of(
         Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0),
         Block.box(0.0, 8.0, 14.0, 2.0, 16.0, 16.0),
         Block.box(14.0, 8.0, 14.0, 16.0, 16.0, 16.0),
         Block.box(14.0, 8.0, 0.0, 16.0, 16.0, 2.0),
         Block.box(0.0, 8.0, 0.0, 2.0, 16.0, 2.0),
         Block.box(2.0, 8.0, 2.0, 14.0, 10.0, 14.0)
      )
      .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
      .get();
   private static final VoxelShape SHAPE_TOP = Stream.of(
         Block.box(1.0, 4.0, 13.0, 15.0, 7.0, 15.0),
         Block.box(1.0, 4.0, 3.0, 3.0, 7.0, 13.0),
         Block.box(13.0, 4.0, 3.0, 15.0, 7.0, 13.0),
         Block.box(1.0, 4.0, 1.0, 15.0, 7.0, 3.0),
         Block.box(12.0, 7.0, 4.0, 14.0, 10.0, 12.0),
         Block.box(2.0, 7.0, 12.0, 14.0, 10.0, 14.0),
         Block.box(2.0, 7.0, 2.0, 14.0, 10.0, 4.0),
         Block.box(2.0, 7.0, 4.0, 4.0, 10.0, 12.0),
         Block.box(11.0, 10.0, 5.0, 13.0, 13.0, 11.0),
         Block.box(3.0, 10.0, 11.0, 13.0, 13.0, 13.0),
         Block.box(3.0, 10.0, 5.0, 5.0, 13.0, 11.0),
         Block.box(4.0, 13.0, 10.0, 12.0, 16.0, 12.0),
         Block.box(4.0, 13.0, 6.0, 6.0, 16.0, 10.0),
         Block.box(4.0, 13.0, 4.0, 12.0, 16.0, 6.0),
         Block.box(10.0, 13.0, 6.0, 12.0, 16.0, 10.0),
         Block.box(0.0, 0.0, 14.0, 16.0, 4.0, 16.0),
         Block.box(14.0, 0.0, 2.0, 16.0, 4.0, 14.0),
         Block.box(0.0, 0.0, 0.0, 16.0, 4.0, 2.0),
         Block.box(0.0, 0.0, 2.0, 2.0, 4.0, 14.0),
         Block.box(3.0, 10.0, 3.0, 13.0, 13.0, 5.0)
      )
      .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
      .get();

   public MonolithBlock() {
      super(
         Properties.of(Material.STONE)
            .sound(SoundType.METAL)
            .strength(-1.0F, 3600000.0F)
            .noDrops()
            .lightLevel(state -> state.hasProperty(FILLED) ? (state.getValue(FILLED) ? 15 : 0) : 0)
      );
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(HALF, DoubleBlockHalf.LOWER)).setValue(FILLED, false)
      );
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{HALF}).add(new Property[]{FILLED});
   }

   public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      return state.getValue(HALF) == DoubleBlockHalf.UPPER ? SHAPE_TOP : SHAPE;
   }

   public RenderShape getRenderShape(BlockState state) {
      return RenderShape.MODEL;
   }

   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return state.getValue(HALF) == DoubleBlockHalf.LOWER ? ModBlocks.MONOLITH_TILE_ENTITY.create(pos, state) : null;
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
      return BlockHelper.getTicker(type, ModBlocks.MONOLITH_TILE_ENTITY, MonolithTileEntity::tick);
   }

   public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
      return InteractionResult.PASS;
   }

   public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
      super.onRemove(state, world, pos, newState, isMoving);
      if (!state.is(newState.getBlock())) {
         if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
            BlockState otherState = world.getBlockState(pos.below());
            if (otherState.is(state.getBlock())) {
               world.removeBlock(pos.below(), isMoving);
            }
         } else {
            BlockState otherState = world.getBlockState(pos.above());
            if (otherState.is(state.getBlock())) {
               world.removeBlock(pos.above(), isMoving);
            }
         }
      }
   }
}
