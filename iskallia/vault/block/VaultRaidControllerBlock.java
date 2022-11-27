package iskallia.vault.block;

import com.google.common.collect.Lists;
import iskallia.vault.block.entity.VaultRaidControllerTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.BlockHelper;
import iskallia.vault.util.VoxelUtils;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
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
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class VaultRaidControllerBlock extends Block implements EntityBlock {
   public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
   private static final VoxelShape SHAPE_TOP = makeShape().move(0.0, -1.0, 0.0);
   private static final VoxelShape SHAPE_BOTTOM = makeShape();

   public VaultRaidControllerBlock() {
      super(Properties.of(Material.GLASS).sound(SoundType.GLASS).strength(-1.0F, 3600000.0F).noCollission().noOcclusion().noDrops());
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(HALF, DoubleBlockHalf.LOWER));
   }

   @Nullable
   public <A extends BlockEntity> BlockEntityTicker<A> getTicker(Level p_153212_, BlockState state, BlockEntityType<A> tBlockEntityType) {
      return BlockHelper.getTicker(tBlockEntityType, ModBlocks.RAID_CONTROLLER_TILE_ENTITY, VaultRaidControllerTileEntity::tick);
   }

   private static VoxelShape makeShape() {
      VoxelShape m1 = Block.box(0.0, 0.0, 0.0, 16.0, 2.0, 16.0);
      VoxelShape m2 = Block.box(2.0, 2.0, 2.0, 14.0, 29.0, 14.0);
      return VoxelUtils.combineAll(BooleanOp.OR, m1, m2);
   }

   public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
      return state.getValue(HALF) == DoubleBlockHalf.UPPER ? SHAPE_TOP : SHAPE_BOTTOM;
   }

   public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
      if (state.getValue(HALF) == DoubleBlockHalf.UPPER) {
         BlockState downState = world.getBlockState(pos.below());
         return !(downState.getBlock() instanceof VaultRaidControllerBlock)
            ? InteractionResult.SUCCESS
            : this.use(downState, world, pos.below(), player, hand, hit);
      } else {
         return !world.isClientSide() && world instanceof ServerLevel && hand == InteractionHand.MAIN_HAND
            ? InteractionResult.SUCCESS
            : InteractionResult.SUCCESS;
      }
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

   public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootContext.Builder builder) {
      return Lists.newArrayList();
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
      return pState.getValue(HALF) == DoubleBlockHalf.LOWER ? ModBlocks.RAID_CONTROLLER_TILE_ENTITY.create(pPos, pState) : null;
   }

   public RenderShape getRenderShape(BlockState state) {
      return RenderShape.MODEL;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{HALF});
   }
}
