package iskallia.vault.block;

import iskallia.vault.block.base.FacedBlock;
import iskallia.vault.block.base.InventoryRetainerBlock;
import iskallia.vault.block.entity.JewelCraftingTableTileEntity;
import iskallia.vault.block.entity.base.ForgeRecipeTileEntity;
import iskallia.vault.init.ModBlocks;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;

public class JewelCraftingTableBlock extends FacedBlock implements EntityBlock, InventoryRetainerBlock<JewelCraftingTableTileEntity> {
   public static VoxelShape SHAPE = Shapes.or(Block.box(1.0, 10.0, 1.0, 15.0, 12.0, 15.0), Block.box(2.0, 0.0, 2.0, 14.0, 10.0, 14.0));

   public JewelCraftingTableBlock() {
      super(Properties.of(Material.STONE).strength(0.5F).noOcclusion());
   }

   @Nonnull
   public VoxelShape getShape(@Nonnull BlockState pState, @Nonnull BlockGetter pLevel, @Nonnull BlockPos pPos, @Nonnull CollisionContext pContext) {
      return SHAPE;
   }

   public void appendHoverText(ItemStack stack, @Nullable BlockGetter level, List<Component> tooltip, TooltipFlag flag) {
      this.addInventoryTooltip(stack, tooltip, ForgeRecipeTileEntity::addInventoryTooltip);
   }

   public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
      if (level.isClientSide()) {
         return InteractionResult.SUCCESS;
      } else if (player instanceof ServerPlayer sPlayer) {
         if (level.getBlockEntity(pos) instanceof JewelCraftingTableTileEntity jewelCraftingTile) {
            NetworkHooks.openGui(sPlayer, jewelCraftingTile, buffer -> buffer.writeBlockPos(pos));
            return InteractionResult.SUCCESS;
         } else {
            return InteractionResult.SUCCESS;
         }
      } else {
         return InteractionResult.SUCCESS;
      }
   }

   @Override
   public boolean isPathfindable(BlockState pState, BlockGetter pLevel, BlockPos pPos, PathComputationType pType) {
      return false;
   }

   public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
      this.onInventoryBlockDestroy(level, pos);
      super.onRemove(state, level, pos, newState, isMoving);
   }

   public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
      this.onInventoryBlockPlace(level, pos, stack);
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return ModBlocks.JEWEL_CRAFTING_TABLE_ENTITY.create(pos, state);
   }
}
