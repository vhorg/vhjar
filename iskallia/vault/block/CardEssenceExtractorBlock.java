package iskallia.vault.block;

import iskallia.vault.block.entity.CardEssenceExtractorTileEntity;
import iskallia.vault.container.inventory.CardEssenceExtractorContainer;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.BlockHelper;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

public class CardEssenceExtractorBlock extends Block implements EntityBlock {
   public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
   public static final VoxelShape SHAPE_SN = Block.box(2.0, 0.0, 0.0, 14.0, 16.0, 16.0);
   public static final VoxelShape SHAPE_EW = Block.box(0.0, 0.0, 2.0, 16.0, 16.0, 14.0);

   public CardEssenceExtractorBlock() {
      super(Properties.of(Material.METAL).strength(1.5F, 6.0F).noOcclusion());
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(FACING, Direction.NORTH));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING});
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return (BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
   }

   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      Direction facing = (Direction)state.getValue(FACING);
      return facing.getAxis() == Axis.X ? SHAPE_EW : SHAPE_SN;
   }

   public InteractionResult use(BlockState state, final Level level, final BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
      if (level.isClientSide()) {
         return InteractionResult.SUCCESS;
      } else if (player instanceof ServerPlayer sPlayer) {
         BlockEntity tile = level.getBlockEntity(pos);
         if (!(tile instanceof CardEssenceExtractorTileEntity)) {
            return InteractionResult.SUCCESS;
         } else {
            NetworkHooks.openGui(sPlayer, new MenuProvider() {
               public Component getDisplayName() {
                  return CardEssenceExtractorBlock.this.getName();
               }

               @Nullable
               public AbstractContainerMenu createMenu(int id, Inventory inventory, Player playerx) {
                  return new CardEssenceExtractorContainer(id, level, pos, inventory);
               }
            }, buf -> buf.writeBlockPos(pos));
            return InteractionResult.SUCCESS;
         }
      } else {
         return InteractionResult.SUCCESS;
      }
   }

   public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
      return true;
   }

   public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean moving) {
      super.neighborChanged(state, level, pos, block, fromPos, moving);
      BlockPos diff = pos.subtract(fromPos);
      Direction dir = Direction.fromNormal(diff);
      if (dir != null) {
         boolean hasPower = level.hasSignal(fromPos, dir.getOpposite());
         if (hasPower) {
            if (level.getBlockEntity(pos) instanceof CardEssenceExtractorTileEntity tile) {
               tile.startExtract();
            }
         }
      }
   }

   public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
      if (!world.isClientSide() && world.getBlockEntity(pos) instanceof CardEssenceExtractorTileEntity entity) {
         ItemStack stack = new ItemStack(this);
         entity.saveToItem(stack);
         ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack);
         itemEntity.setDefaultPickUpDelay();
         world.addFreshEntity(itemEntity);
      }

      super.playerWillDestroy(world, pos, state, player);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
      return BlockHelper.getTicker(type, ModBlocks.CARD_ESSENCE_EXTRACTOR_TILE_ENTITY, CardEssenceExtractorTileEntity::tick);
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return ModBlocks.CARD_ESSENCE_EXTRACTOR_TILE_ENTITY.create(pos, state);
   }
}
