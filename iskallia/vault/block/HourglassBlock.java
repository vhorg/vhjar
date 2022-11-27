package iskallia.vault.block;

import iskallia.vault.block.entity.HourglassTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.MiscUtils;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class HourglassBlock extends Block implements EntityBlock {
   private static final Random rand = new Random();
   public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;

   public HourglassBlock() {
      super(Properties.of(Material.GLASS, MaterialColor.COLOR_BROWN).noOcclusion().requiresCorrectToolForDrops().strength(3.0F, 3600000.0F));
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(HALF, DoubleBlockHalf.LOWER));
   }

   public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
      return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
      return pState.getValue(HALF) == DoubleBlockHalf.LOWER ? ModBlocks.HOURGLASS_TILE_ENTITY.create(pPos, pState) : null;
   }

   public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
      if (world.isClientSide()) {
         return InteractionResult.SUCCESS;
      } else if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
         BlockState down = world.getBlockState(pos.below());
         return down.use(world, player, hand, hit.withPosition(pos.below()));
      } else {
         ItemStack interacted = player.getItemInHand(hand);
         return InteractionResult.SUCCESS;
      }
   }

   private void playFullEffects(Level world, BlockPos pos) {
      for (int i = 0; i < 30; i++) {
         Vec3 offset = MiscUtils.getRandomOffset(pos, rand, 2.0F);
         ((ServerLevel)world).sendParticles(ParticleTypes.HAPPY_VILLAGER, offset.x, offset.y, offset.z, 3, 0.0, 0.0, 0.0, 1.0);
      }

      world.playSound(null, pos, SoundEvents.PLAYER_LEVELUP, SoundSource.BLOCKS, 1.0F, 1.0F);
   }

   @javax.annotation.Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      BlockPos pos = context.getClickedPos();
      Level world = context.getLevel();
      return world.isInWorldBounds(pos) && world.getBlockState(pos.above()).canBeReplaced(context)
         ? (BlockState)this.defaultBlockState().setValue(HALF, DoubleBlockHalf.LOWER)
         : null;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{HALF});
   }

   public void playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player) {
      if (!worldIn.isClientSide() && player.isCreative()) {
         DoubleBlockHalf half = (DoubleBlockHalf)state.getValue(HALF);
         if (half == DoubleBlockHalf.UPPER) {
            BlockPos blockpos = pos.below();
            BlockState blockstate = worldIn.getBlockState(blockpos);
            if (blockstate.getBlock() == state.getBlock() && blockstate.getValue(HALF) == DoubleBlockHalf.LOWER) {
               worldIn.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 35);
               worldIn.levelEvent(player, 2001, blockpos, Block.getId(blockstate));
            }
         }
      }

      super.playerWillDestroy(worldIn, pos, state, player);
   }

   public BlockState updateShape(BlockState state, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
      DoubleBlockHalf half = (DoubleBlockHalf)state.getValue(HALF);
      if (facing.getAxis() == Axis.Y && half == DoubleBlockHalf.LOWER == (facing == Direction.UP)) {
         return facingState.is(this) && facingState.getValue(HALF) != half ? state : Blocks.AIR.defaultBlockState();
      } else {
         return half == DoubleBlockHalf.LOWER && facing == Direction.DOWN && !state.canSurvive(worldIn, currentPos)
            ? Blocks.AIR.defaultBlockState()
            : super.updateShape(state, facing, facingState, worldIn, currentPos, facingPos);
      }
   }

   public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, @javax.annotation.Nullable LivingEntity placer, ItemStack stack) {
      worldIn.setBlock(pos.above(), (BlockState)state.setValue(HALF, DoubleBlockHalf.UPPER), 3);
   }

   public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
      if (!state.is(newState.getBlock()) || !newState.hasBlockEntity()) {
         BlockEntity te = getBlockTileEntity(world, pos, state);
         if (te instanceof HourglassTileEntity && state.getValue(HALF) == DoubleBlockHalf.LOWER) {
            ItemStack stack = new ItemStack(ModBlocks.HOURGLASS);
            stack.getOrCreateTag().put("BlockEntityTag", te.serializeNBT());
            Block.popResource(world, pos, stack);
         }
      }

      super.onRemove(state, world, pos, newState, isMoving);
   }

   public static BlockPos getTileEntityPos(BlockState state, BlockPos pos) {
      return state.getValue(HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos;
   }

   public static BlockEntity getBlockTileEntity(Level world, BlockPos pos, BlockState state) {
      BlockPos vendingMachinePos = getTileEntityPos(state, pos);
      return world.getBlockEntity(vendingMachinePos);
   }
}
