package iskallia.vault.block;

import iskallia.vault.block.entity.SpiritExtractorTileEntity;
import iskallia.vault.container.SpiritExtractorContainer;
import iskallia.vault.entity.entity.SpiritEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModEntities;
import iskallia.vault.util.BlockHelper;
import java.util.List;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.Entity.RemovalReason;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
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
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.Nullable;

public class SpiritExtractorBlock extends Block implements EntityBlock {
   public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
   protected static final VoxelShape SHAPE = Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0);
   private static final String BLOCK_ENTITY_TAG = "BlockEntityTag";

   public SpiritExtractorBlock() {
      super(Properties.copy(Blocks.STONE));
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
      return new SpiritExtractorTileEntity(pos, state);
   }

   public VoxelShape getShape(BlockState state, BlockGetter level, BlockPos pos, CollisionContext context) {
      return SHAPE;
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState state, BlockEntityType<T> blockEntityType) {
      return BlockHelper.getTicker(blockEntityType, ModBlocks.SPIRIT_EXTRACTOR_TILE_ENTITY, SpiritExtractorTileEntity::tick);
   }

   public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
      if (!(level.getBlockEntity(pos) instanceof SpiritExtractorTileEntity spiritExtractorTile)) {
         return super.use(state, level, pos, player, hand, hit);
      } else if (!player.getPassengers().isEmpty() && player.getPassengers().get(0) instanceof SpiritEntity spirit) {
         if (level.isClientSide()) {
            return InteractionResult.SUCCESS;
         } else if (this.hasSpiritAlready(spiritExtractorTile)) {
            return InteractionResult.FAIL;
         } else {
            spirit.getGameProfile().ifPresent(spiritExtractorTile::setGameProfile);
            spiritExtractorTile.setItems(spirit.getItems());
            spiritExtractorTile.setVaultLevel(spirit.getVaultLevel());
            spiritExtractorTile.setPlayerLevel(spirit.getPlayerLevel());
            spiritExtractorTile.setRecyclable(spirit.isRecyclable());
            spiritExtractorTile.setRescuedBonus(spirit.getRescuedBonus());
            level.sendBlockUpdated(pos, state, state, 3);
            spirit.remove(RemovalReason.DISCARDED);
            return InteractionResult.SUCCESS;
         }
      } else if (level.isClientSide()) {
         return InteractionResult.SUCCESS;
      } else {
         if (player.isShiftKeyDown() && player.getOffhandItem().isEmpty() && player.getPassengers().isEmpty()) {
            this.pickupSpirit(player, spiritExtractorTile);
         } else {
            this.openGui(pos, (ServerPlayer)player);
         }

         return InteractionResult.SUCCESS;
      }
   }

   private void pickupSpirit(Player player, SpiritExtractorTileEntity spiritExtractorTile) {
      if (ModEntities.SPIRIT.spawn((ServerLevel)player.level, null, null, player.blockPosition(), MobSpawnType.EVENT, false, false) instanceof SpiritEntity spirit
         )
       {
         spiritExtractorTile.getGameProfile().ifPresent(spirit::setGameProfile);
         spirit.setVaultLevel(spiritExtractorTile.getVaultLevel());
         spirit.setPlayerLevel(spiritExtractorTile.getPlayerLevel());
         spirit.setItems(spiritExtractorTile.getItems());
         spirit.setRescuedBonus(spiritExtractorTile.getRescuedBonus());
         spirit.putInPlayersHand(player);
         spiritExtractorTile.removeSpirit();
      }
   }

   private void openGui(final BlockPos pos, ServerPlayer player) {
      NetworkHooks.openGui(player, new MenuProvider() {
         public Component getDisplayName() {
            return new TextComponent("Spirit Extractor");
         }

         public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player playerx) {
            return new SpiritExtractorContainer(windowId, inventory, pos);
         }
      }, pos);
   }

   private boolean hasSpiritAlready(SpiritExtractorTileEntity spiritExtractorTile) {
      return spiritExtractorTile.getGameProfile().isPresent();
   }

   public void setPlacedBy(Level level, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
      CompoundTag tag = stack.getTag();
      if (tag != null && tag.contains("BlockEntityTag") && level.getBlockEntity(pos) instanceof SpiritExtractorTileEntity spiritExtractorTile) {
         spiritExtractorTile.load(tag.getCompound("BlockEntityTag"));
      }
   }

   public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootContext.Builder builder) {
      BlockEntity blockentity = (BlockEntity)builder.getOptionalParameter(LootContextParams.BLOCK_ENTITY);
      if (blockentity instanceof SpiritExtractorTileEntity) {
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
}
