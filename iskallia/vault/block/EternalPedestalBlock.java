package iskallia.vault.block;

import iskallia.vault.block.entity.EternalPedestalTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.BlockHelper;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class EternalPedestalBlock extends Block implements EntityBlock {
   private static final String BLOCK_ENTITY_TAG = "BlockEntityTag";
   public static final EnumProperty<EternalPedestalBlock.TripleBlock> HALF = EnumProperty.create("half", EternalPedestalBlock.TripleBlock.class);
   public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
   protected static final VoxelShape EMPTY = Block.box(0.0, 0.0, 0.0, 0.0, 0.0, 0.0);
   protected static final VoxelShape SHAPE_BASE = Block.box(0.0, 0.0, 0.0, 16.0, 7.0, 16.0);
   protected static final VoxelShape SHAPE_BASE_GOLD = Stream.of(Block.box(0.0, 0.0, 0.0, 16.0, 7.0, 16.0), Block.box(6.0, 8.0, 4.0, 10.0, 16.0, 12.0))
      .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
      .get();
   protected static final VoxelShape SHAPE_BASE_GOLD_TURNED = Stream.of(Block.box(0.0, 0.0, 0.0, 16.0, 7.0, 16.0), Block.box(4.0, 8.0, 6.0, 12.0, 16.0, 10.0))
      .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
      .get();
   protected static final VoxelShape SHAPE_BASE_GOLD_COLLISION = Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
   protected static final VoxelShape SHAPE_MIDDLE = Block.box(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);
   protected static final VoxelShape SHAPE_MIDDLE_GOLD = Stream.of(Block.box(6.0, 0.0, 4.0, 10.0, 16.0, 12.0), Block.box(6.0, 4.0, 0.0, 10.0, 16.0, 16.0))
      .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
      .get();
   protected static final VoxelShape SHAPE_MIDDLE_GOLD_SLIM = Stream.of(Block.box(6.0, 0.0, 4.0, 10.0, 16.0, 12.0), Block.box(6.0, 3.5, 1.0, 10.0, 15.5, 15.0))
      .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
      .get();
   protected static final VoxelShape SHAPE_MIDDLE_GOLD_TURNED = Stream.of(
         Block.box(4.0, 0.0, 6.0, 12.0, 16.0, 10.0), Block.box(0.0, 3.75, 6.0, 16.0, 16.0, 10.0)
      )
      .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
      .get();
   protected static final VoxelShape SHAPE_MIDDLE_GOLD_TURNED_SLIM = Stream.of(
         Block.box(4.0, 0.0, 6.0, 12.0, 16.0, 10.0), Block.box(1.0, 3.5, 6.0, 15.0, 15.5, 10.0)
      )
      .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
      .get();
   protected static final VoxelShape SHAPE_PLAYER_GOLD = Block.box(4.0, 0.0, 4.0, 12.0, 8.0, 12.0);

   public EternalPedestalBlock() {
      super(Properties.copy(Blocks.STONE));
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH))
            .setValue(HALF, EternalPedestalBlock.TripleBlock.LOWER)
      );
   }

   public RenderShape getRenderShape(BlockState pState) {
      if (pState.getValue(HALF) == EternalPedestalBlock.TripleBlock.UPPER) {
         return RenderShape.INVISIBLE;
      } else {
         return pState.getValue(HALF) == EternalPedestalBlock.TripleBlock.MIDDLE ? RenderShape.INVISIBLE : super.getRenderShape(pState);
      }
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING, HALF});
   }

   public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
      if (pLevel.getBlockEntity(pPos) instanceof EternalPedestalTileEntity gladiatorLadderStatueBlockEntity) {
         if (pPlacer instanceof Player) {
            gladiatorLadderStatueBlockEntity.setOwner(pPlacer.getUUID());
         } else {
            gladiatorLadderStatueBlockEntity.setOwner(null);
         }
      }

      super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return (BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return state.getValue(HALF) == EternalPedestalBlock.TripleBlock.LOWER ? new EternalPedestalTileEntity(pos, state) : null;
   }

   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      boolean isSlim = false;
      if (state.getValue(HALF) == EternalPedestalBlock.TripleBlock.LOWER && level.getBlockEntity(pos) instanceof EternalPedestalTileEntity eternalPedestalTile) {
         if (eternalPedestalTile.getEternalId() == null) {
            return SHAPE_BASE;
         }
      } else if (state.getValue(HALF) == EternalPedestalBlock.TripleBlock.MIDDLE
         && level.getBlockEntity(pos.below()) instanceof EternalPedestalTileEntity eternalPedestalTilex) {
         if (eternalPedestalTilex.getEternalId() == null) {
            return EMPTY;
         }

         isSlim = eternalPedestalTilex.getSkinProfile().isSlim();
      } else if (state.getValue(HALF) == EternalPedestalBlock.TripleBlock.UPPER
         && level.getBlockEntity(pos.below().below()) instanceof EternalPedestalTileEntity eternalPedestalTilex
         && eternalPedestalTilex.getEternalId() == null) {
         return EMPTY;
      }

      if (state.hasProperty(HALF) && state.getValue(HALF) != EternalPedestalBlock.TripleBlock.LOWER) {
         if (state.hasProperty(HALF) && state.getValue(HALF) != EternalPedestalBlock.TripleBlock.MIDDLE) {
            return SHAPE_PLAYER_GOLD;
         } else if (!state.hasProperty(FACING)) {
            return SHAPE_MIDDLE;
         } else if (state.getValue(FACING) != Direction.WEST && state.getValue(FACING) != Direction.EAST) {
            return isSlim ? SHAPE_MIDDLE_GOLD_TURNED_SLIM : SHAPE_MIDDLE_GOLD_TURNED;
         } else {
            return isSlim ? SHAPE_MIDDLE_GOLD_SLIM : SHAPE_MIDDLE_GOLD;
         }
      } else {
         return !state.hasProperty(FACING) || state.getValue(FACING) != Direction.WEST && state.getValue(FACING) != Direction.EAST
            ? SHAPE_BASE_GOLD_TURNED
            : SHAPE_BASE_GOLD;
      }
   }

   public VoxelShape getCollisionShape(BlockState state, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      return state.getValue(HALF) == EternalPedestalBlock.TripleBlock.LOWER ? SHAPE_BASE_GOLD_COLLISION : EMPTY;
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
      return BlockHelper.getTicker(type, ModBlocks.ETERNAL_PEDESTAL_TILE_ENTITY, EternalPedestalTileEntity::tick);
   }

   public void onRemove(BlockState state, Level world, BlockPos pos, BlockState newState, boolean isMoving) {
      super.onRemove(state, world, pos, newState, isMoving);
      if (!state.is(newState.getBlock())) {
         if (state.getValue(HALF) == EternalPedestalBlock.TripleBlock.UPPER) {
            BlockState otherState = world.getBlockState(pos.below());
            if (otherState.is(state.getBlock())) {
               world.destroyBlock(pos.below(), true);
            }

            otherState = world.getBlockState(pos.below().below());
            if (otherState.is(state.getBlock())) {
               world.destroyBlock(pos.below().below(), true);
            }
         }

         if (state.getValue(HALF) == EternalPedestalBlock.TripleBlock.MIDDLE) {
            BlockState otherStatex = world.getBlockState(pos.below());
            if (otherStatex.is(state.getBlock())) {
               world.destroyBlock(pos.below(), true);
            }

            otherStatex = world.getBlockState(pos.above());
            if (otherStatex.is(state.getBlock())) {
               world.destroyBlock(pos.above(), true);
            }
         } else {
            BlockState otherStatexx = world.getBlockState(pos.above());
            if (otherStatexx.is(state.getBlock())) {
               world.destroyBlock(pos.above(), true);
            }

            otherStatexx = world.getBlockState(pos.above().above());
            if (otherStatexx.is(state.getBlock())) {
               world.destroyBlock(pos.above().above(), true);
            }
         }
      }
   }

   public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
      AtomicBoolean bool = new AtomicBoolean(false);
      if (state.getValue(HALF) == EternalPedestalBlock.TripleBlock.UPPER) {
         pos = pos.below().below();
      }

      if (state.getValue(HALF) == EternalPedestalBlock.TripleBlock.MIDDLE) {
         pos = pos.below();
      }

      level.getBlockEntity(pos, ModBlocks.ETERNAL_PEDESTAL_TILE_ENTITY).ifPresent(be -> {
         bool.set(be.interact(state, player, hand, hit));
         if (!level.isClientSide() && bool.get()) {
            be.setChanged();
         }
      });
      return bool.get() ? InteractionResult.SUCCESS : super.use(state, level, pos, player, hand, hit);
   }

   public PushReaction getPistonPushReaction(BlockState pState) {
      return PushReaction.BLOCK;
   }

   protected void spawnDestroyParticles(Level pLevel, Player pPlayer, BlockPos pos, BlockState state) {
      if (state.getValue(HALF) == EternalPedestalBlock.TripleBlock.UPPER) {
         pos = pos.below().below();
      }

      if (state.getValue(HALF) == EternalPedestalBlock.TripleBlock.MIDDLE) {
         pos = pos.below();
      }

      super.spawnDestroyParticles(pLevel, pPlayer, pos, pLevel.getBlockState(pos));
   }

   public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootContext.Builder builder) {
      BlockEntity blockentity = (BlockEntity)builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
      if (blockentity instanceof EternalPedestalTileEntity) {
         CompoundTag stackNBT = blockentity.saveWithoutMetadata();
         ItemStack itemStack = new ItemStack(this);
         if (!stackNBT.isEmpty()) {
            itemStack.addTagElement("BlockEntityTag", stackNBT);
         }

         return List.of(itemStack);
      } else {
         return super.getDrops(state, builder);
      }
   }

   public static enum TripleBlock implements StringRepresentable {
      UPPER,
      MIDDLE,
      LOWER;

      @Override
      public String toString() {
         return this.getSerializedName();
      }

      public String getSerializedName() {
         return this == UPPER ? "upper" : (this == MIDDLE ? "middle" : "lower");
      }
   }
}
