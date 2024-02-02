package iskallia.vault.block;

import iskallia.vault.block.entity.SoulPlaqueTileEntity;
import iskallia.vault.block.item.SoulPlaqueBlockItem;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.util.BlockHelper;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class SoulPlaqueBlock extends Block implements EntityBlock {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final IntegerProperty TIER = IntegerProperty.create("tier", 1, 8);
   public static VoxelShape SOUTH_SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 16.0, 2.0);
   public static VoxelShape NORTH_SHAPE = Block.box(0.0, 0.0, 14.0, 16.0, 16.0, 16.0);
   public static VoxelShape EAST_SHAPE = Block.box(0.0, 0.0, 0.0, 2.0, 16.0, 16.0);
   public static VoxelShape WEST_SHAPE = Block.box(14.0, 0.0, 0.0, 16.0, 16.0, 16.0);

   public SoulPlaqueBlock() {
      super(Properties.of(Material.STONE).strength(1.0F, 3600000.0F));
      this.registerDefaultState((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.EAST)).setValue(TIER, 1));
   }

   public RenderShape getRenderShape(BlockState pState) {
      return RenderShape.MODEL;
   }

   public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      return switch ((Direction)pState.getValue(FACING)) {
         case NORTH -> NORTH_SHAPE;
         case SOUTH -> SOUTH_SHAPE;
         case WEST -> WEST_SHAPE;
         default -> EAST_SHAPE;
      };
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING, TIER});
   }

   @Nonnull
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      Direction face = context.getClickedFace();
      if (face.getAxis() == Axis.Y) {
         face = context.getHorizontalDirection().getOpposite();
      }

      return (BlockState)this.defaultBlockState().setValue(FACING, face);
   }

   public BlockState rotate(BlockState state, Rotation rotation) {
      return (BlockState)state.setValue(FACING, rotation.rotate((Direction)state.getValue(FACING)));
   }

   public BlockState mirror(BlockState state, Mirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.getValue(FACING)));
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
      return ModBlocks.SOUL_PLAQUE_TILE_ENTITY.create(pPos, pState);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
      return BlockHelper.getTicker(pBlockEntityType, ModBlocks.SOUL_PLAQUE_TILE_ENTITY, SoulPlaqueTileEntity::tick);
   }

   public void setPlacedBy(
      @Nonnull Level world, @Nonnull BlockPos pos, @Nonnull BlockState state, @javax.annotation.Nullable LivingEntity placer, @Nonnull ItemStack stack
   ) {
      if (!world.isClientSide) {
         int tier = SoulPlaqueBlockItem.getTier(stack).orElseGet(() -> {
            int score = SoulPlaqueBlockItem.getScore(stack);
            return ModConfigs.ASCENSION.getTier(score);
         });
         world.setBlock(pos, (BlockState)state.setValue(TIER, tier), 2);
      }
   }

   public void playerWillDestroy(@Nonnull Level world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull Player player) {
      if (!world.isClientSide && !player.isCreative()) {
         ItemStack stack;
         if (world.getBlockEntity(pos) instanceof SoulPlaqueTileEntity plaque) {
            stack = SoulPlaqueBlockItem.create(plaque.getUuid(), plaque.getSkin().getLatestNickname(), plaque.getScore());
         } else {
            stack = new ItemStack(this);
         }

         ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
         itemEntity.setDefaultPickUpDelay();
         world.addFreshEntity(itemEntity);
      }

      super.playerWillDestroy(world, pos, state, player);
   }

   public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> items) {
      if (ModConfigs.isInitialized()) {
         for (int score : ModConfigs.ASCENSION.getScoreToTier().keySet()) {
            items.add(SoulPlaqueBlockItem.create(null, score));
         }
      }
   }
}
