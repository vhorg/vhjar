package iskallia.vault.block;

import iskallia.vault.block.entity.ShopPedestalBlockTile;
import iskallia.vault.event.event.ShopPedestalPriceEvent;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.BlockHelper;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.GameMasterBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

public class ShopPedestalBlock extends Block implements EntityBlock, GameMasterBlock {
   public static final BooleanProperty ACTIVE = BooleanProperty.create("active");
   public static final VoxelShape SHAPE = Shapes.or(Block.box(0.0, 12.0, 0.0, 16.0, 16.0, 16.0), Block.box(2.0, 0.0, 2.0, 14.0, 12.0, 14.0));

   public ShopPedestalBlock() {
      super(Properties.of(Material.STONE, MaterialColor.STONE).noOcclusion().strength(3600000.0F, 3600000.0F));
      this.registerDefaultState((BlockState)this.defaultBlockState().setValue(ACTIVE, true));
   }

   @Nullable
   public <A extends BlockEntity> BlockEntityTicker<A> getTicker(Level p_153212_, BlockState state, BlockEntityType<A> tBlockEntityType) {
      return BlockHelper.getTicker(tBlockEntityType, ModBlocks.SHOP_PEDESTAL_TILE_ENTITY, ShopPedestalBlockTile::tick);
   }

   public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      return SHAPE;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
      super.createBlockStateDefinition(pBuilder);
      pBuilder.add(new Property[]{ACTIVE});
   }

   public boolean onDestroyedByPlayer(BlockState state, Level world, BlockPos pos, Player player, boolean willHarvest, FluidState fluid) {
      return player.getAbilities().instabuild && super.onDestroyedByPlayer(state, world, pos, player, willHarvest, fluid);
   }

   public PushReaction getPistonPushReaction(BlockState pState) {
      return PushReaction.BLOCK;
   }

   public InteractionResult use(BlockState state, Level worldIn, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
      if (worldIn.getBlockEntity(pos) instanceof ShopPedestalBlockTile tile && tile.isInitialized() && handIn == InteractionHand.MAIN_HAND) {
         ItemStack offerStack = tile.getOfferStack();
         if (!offerStack.isEmpty()) {
            ShopPedestalPriceEvent event = new ShopPedestalPriceEvent(player, offerStack, tile.getCurrencyStack());
            MinecraftForge.EVENT_BUS.post(event);
            ItemStack currency = event.getCost();
            int required = currency.getCount();
            return player.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null)
               .map(
                  itemHandler -> {
                     int amount = 0;
                     if (!player.isCreative()) {
                        for (int i = 0; i < itemHandler.getSlots(); i++) {
                           ItemStack stack = itemHandler.getStackInSlot(i);
                           if (stack.is(currency.getItem())) {
                              amount += stack.getCount();
                              if (amount >= required) {
                                 break;
                              }
                           }
                        }

                        if (amount < required) {
                           if (worldIn.isClientSide) {
                              player.displayClientMessage(
                                 new TranslatableComponent("message.the_vault.shop_pedestal.fail", new Object[]{currency.getHoverName()}), true
                              );
                           }

                           return InteractionResult.sidedSuccess(worldIn.isClientSide);
                        }
                     }

                     if (!worldIn.isClientSide) {
                        if (!player.isCreative()) {
                           for (int ix = 0; ix < itemHandler.getSlots(); ix++) {
                              ItemStack stack = itemHandler.getStackInSlot(ix);
                              if (stack.is(currency.getItem())) {
                                 int min = Math.min(required, stack.getCount());
                                 itemHandler.extractItem(ix, min, false);
                                 amount -= min;
                                 if (amount <= 0) {
                                    break;
                                 }
                              }
                           }

                           BlockState inactiveState = (BlockState)state.setValue(ACTIVE, false);
                           tile.setRemoved();
                           worldIn.setBlockAndUpdate(pos, inactiveState);
                        }

                        ItemHandlerHelper.giveItemToPlayer(player, offerStack.copy());
                        worldIn.playSound(null, pos, SoundEvents.AMETHYST_BLOCK_STEP, SoundSource.BLOCKS, 1.0F, 1.0F);
                     } else {
                        if (!player.getAbilities().instabuild) {
                           tile.setRemoved();
                        }

                        player.displayClientMessage(
                           new TranslatableComponent(
                              "message.the_vault.shop_pedestal.purchase",
                              new Object[]{offerStack.getCount(), offerStack.getHoverName(), currency.getCount(), currency.getHoverName()}
                           ),
                           true
                        );
                     }

                     return InteractionResult.sidedSuccess(worldIn.isClientSide);
                  }
               )
               .orElse(InteractionResult.PASS);
         } else {
            return InteractionResult.PASS;
         }
      } else {
         if (player.getAbilities().instabuild) {
            ItemStack o = player.getItemInHand(InteractionHand.MAIN_HAND);
            ItemStack c = player.getItemInHand(InteractionHand.OFF_HAND);
            if (!c.isEmpty() && !o.isEmpty()) {
               worldIn.setBlockAndUpdate(pos, (BlockState)state.setValue(ACTIVE, true));
               if (worldIn.getBlockEntity(pos) instanceof ShopPedestalBlockTile tilex) {
                  tilex.setOffer(o.copy(), c.copy());
                  tilex.setChanged();
                  return InteractionResult.sidedSuccess(worldIn.isClientSide);
               }

               worldIn.setBlockAndUpdate(pos, (BlockState)state.setValue(ACTIVE, false));
            }
         }

         return InteractionResult.PASS;
      }
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
      return pState.getValue(ACTIVE) ? new ShopPedestalBlockTile(pPos, pState) : null;
   }
}
