package iskallia.vault.block;

import iskallia.vault.block.entity.WardrobeTileEntity;
import iskallia.vault.container.WardrobeContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
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
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

@EventBusSubscriber
public class WardrobeBlock extends Block implements EntityBlock {
   public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
   protected static final VoxelShape SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 3.0, 15.0);

   public WardrobeBlock() {
      super(Properties.of(Material.STONE).strength(1.5F, 6.0F));
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING});
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return (BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new WardrobeTileEntity(pos, state);
   }

   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return SHAPE;
   }

   public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
      if (level.getBlockEntity(pos) instanceof WardrobeTileEntity wardrobeTile && placer != null) {
         wardrobeTile.setOwner(placer.getUUID());
      }
   }

   public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean isMoving) {
      if (!state.is(newState.getBlock())) {
         if (level.getBlockEntity(pos) instanceof WardrobeTileEntity wardrobeTile) {
            wardrobeTile.getEquipmentSlots().values().forEach(stack -> dropStack(level, pos, stack));
            wardrobeTile.getCuriosItems().values().forEach(stacks -> stacks.values().forEach(stack -> dropStack(level, pos, stack)));

            for (int slot = 0; slot < wardrobeTile.getHotbarItems().getSlots(); slot++) {
               ItemStack stackInSlot = wardrobeTile.getHotbarItems().getStackInSlot(slot);
               if (!stackInSlot.isEmpty()) {
                  dropStack(level, pos, stackInSlot);
               }
            }
         }

         super.onRemove(state, level, pos, newState, isMoving);
      }
   }

   private static void dropStack(Level level, BlockPos pos, ItemStack stack) {
      Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack.copy());
   }

   @SubscribeEvent
   public static void onInteractWithWardrobe(RightClickBlock event) {
      if (event.getEntity() instanceof ServerPlayer player
         && event.getHand() == InteractionHand.OFF_HAND
         && player.isShiftKeyDown()
         && !player.getItemInHand(InteractionHand.OFF_HAND).isEmpty()
         && player.getLevel().getBlockEntity(event.getPos()) instanceof WardrobeTileEntity wardrobeTile) {
         wardrobeTile.swap(player, false);
         event.setCanceled(true);
         event.setCancellationResult(InteractionResult.SUCCESS);
      }
   }

   public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
      if (level.getBlockEntity(pos) instanceof WardrobeTileEntity wardrobeTile) {
         if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
         } else {
            if (player.isShiftKeyDown()) {
               wardrobeTile.swap(player, false);
            } else {
               this.openGui(pos, (ServerPlayer)player);
            }

            return InteractionResult.SUCCESS;
         }
      } else {
         return super.use(state, level, pos, player, hand, hit);
      }
   }

   private void openGui(final BlockPos pos, ServerPlayer player) {
      NetworkHooks.openGui(player, new MenuProvider() {
         public Component getDisplayName() {
            return WardrobeBlock.this.getName();
         }

         public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player playerx) {
            return new WardrobeContainer.Gear(windowId, inventory, pos);
         }
      }, pos);
   }
}
