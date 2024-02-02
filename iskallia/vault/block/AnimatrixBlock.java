package iskallia.vault.block;

import iskallia.vault.block.base.FacedBlock;
import iskallia.vault.block.entity.AnimatrixTileEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SpawnEggItem;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.items.ItemStackHandler;
import net.p3pp3rf1y.sophisticatedcore.util.InventoryHelper;
import org.jetbrains.annotations.Nullable;

public class AnimatrixBlock extends FacedBlock implements EntityBlock {
   public AnimatrixBlock() {
      super(Properties.of(Material.METAL).strength(1.5F, 6.0F).lightLevel(state -> 15));
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH));
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
      return new AnimatrixTileEntity(pPos, pState);
   }

   @Nullable
   @Override
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return (BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
   }

   public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
      ItemStack itemStack = pPlayer.getMainHandItem();
      AnimatrixTileEntity tileEntity = (AnimatrixTileEntity)pLevel.getBlockEntity(pPos);
      if (tileEntity == null) {
         return InteractionResult.FAIL;
      } else {
         ItemStackHandler itemHandler = tileEntity.getItemHandler();
         if (itemStack.getItem() instanceof SpawnEggItem) {
            if (itemHandler.isItemValid(0, itemStack)) {
               itemHandler.insertItem(0, pPlayer.getItemInHand(pHand).split(1), false);
            }

            return InteractionResult.SUCCESS;
         } else {
            if (itemStack.isEmpty()) {
               if (itemHandler.getStackInSlot(0).isEmpty()) {
                  return InteractionResult.FAIL;
               }

               popResource(pLevel, pPos, itemHandler.getStackInSlot(0));
               itemHandler.setStackInSlot(0, ItemStack.EMPTY);
            }

            return InteractionResult.FAIL;
         }
      }
   }

   public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
      AnimatrixTileEntity tileEntity = (AnimatrixTileEntity)level.getBlockEntity(pos);
      if (tileEntity != null) {
         InventoryHelper.dropItems(tileEntity.getItemHandler(), level, pos);
      }

      return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
   }
}
