package iskallia.vault.block;

import iskallia.vault.block.base.FacedBlock;
import iskallia.vault.block.base.InventoryRetainerBlock;
import iskallia.vault.block.entity.CatalystInfusionTableTileEntity;
import iskallia.vault.block.entity.base.ForgeRecipeTileEntity;
import iskallia.vault.init.ModBlocks;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

public class CatalystInfusionBlock extends FacedBlock implements EntityBlock, InventoryRetainerBlock<CatalystInfusionTableTileEntity> {
   public CatalystInfusionBlock() {
      super(Properties.of(Material.STONE).strength(1.5F, 6.0F).noOcclusion());
   }

   @Override
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

   public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
      this.addInventoryTooltip(stack, tooltip, ForgeRecipeTileEntity::addInventoryTooltip);
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

   public boolean useShapeForLightOcclusion(@Nonnull BlockState state) {
      return true;
   }

   public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
      this.onInventoryBlockDestroy(level, pos);
      super.onRemove(state, level, pos, newState, isMoving);
   }

   public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
      this.onInventoryBlockPlace(level, pos, stack);
   }

   @ParametersAreNonnullByDefault
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return ModBlocks.CATALYST_INFUSION_TABLE_TILE_ENTITY.create(pos, state);
   }
}
