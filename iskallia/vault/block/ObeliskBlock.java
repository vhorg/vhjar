package iskallia.vault.block;

import iskallia.vault.block.entity.ObeliskTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class ObeliskBlock extends Block implements EntityBlock {
   public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
   public static final BooleanProperty FILLED = BooleanProperty.create("filled");
   private static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 32.0, 14.0);
   private static final VoxelShape SHAPE_TOP = SHAPE.move(0.0, -1.0, 0.0);

   public ObeliskBlock() {
      super(Properties.of(Material.STONE).sound(SoundType.METAL).strength(-1.0F, 3600000.0F).noDrops());
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
      return state.getValue(HALF) == DoubleBlockHalf.LOWER ? ModBlocks.OBELISK_TILE_ENTITY.create(pos, state) : null;
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
      return BlockHelper.getTicker(type, ModBlocks.OBELISK_TILE_ENTITY, ObeliskTileEntity::tick);
   }

   public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
      return InteractionResult.PASS;
   }

   private void spawnParticles(Level world, BlockPos pos) {
      for (int i = 0; i < 20; i++) {
         double d0 = world.random.nextGaussian() * 0.02;
         double d1 = world.random.nextGaussian() * 0.02;
         double d2 = world.random.nextGaussian() * 0.02;
         ((ServerLevel)world)
            .sendParticles(
               ParticleTypes.POOF,
               pos.getX() + world.random.nextDouble() - d0,
               pos.getY() + world.random.nextDouble() - d1,
               pos.getZ() + world.random.nextDouble() - d2,
               10,
               d0,
               d1,
               d2,
               1.0
            );
      }

      world.playSound(null, pos, SoundEvents.CONDUIT_ACTIVATE, SoundSource.BLOCKS, 1.0F, 1.0F);
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
