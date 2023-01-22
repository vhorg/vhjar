package iskallia.vault.block;

import iskallia.vault.block.entity.AncientCryoChamberTileEntity;
import iskallia.vault.block.entity.CryoChamberTileEntity;
import iskallia.vault.container.RenamingContainer;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModNetwork;
import iskallia.vault.network.message.ClientboundResetCryoChamberMessage;
import iskallia.vault.util.BlockHelper;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.util.RenameType;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Direction.Axis;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import net.minecraftforge.network.PacketDistributor;
import org.jetbrains.annotations.Nullable;

public class CryoChamberBlock extends Block implements EntityBlock {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final EnumProperty<DoubleBlockHalf> HALF = BlockStateProperties.DOUBLE_BLOCK_HALF;
   public static final EnumProperty<CryoChamberBlock.ChamberState> CHAMBER_STATE = EnumProperty.create("chamber_state", CryoChamberBlock.ChamberState.class);

   public CryoChamberBlock() {
      super(
         Properties.of(Material.METAL, MaterialColor.METAL)
            .strength(5.0F, 3600000.0F)
            .sound(SoundType.METAL)
            .noOcclusion()
            .isRedstoneConductor(CryoChamberBlock::isntSolid)
            .isViewBlocking(CryoChamberBlock::isntSolid)
      );
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH))
               .setValue(HALF, DoubleBlockHalf.LOWER))
            .setValue(CHAMBER_STATE, CryoChamberBlock.ChamberState.NONE)
      );
   }

   @Nullable
   public <A extends BlockEntity> BlockEntityTicker<A> getTicker(Level p_153212_, BlockState state, BlockEntityType<A> tBlockEntityType) {
      return this.isNormal(state)
         ? BlockHelper.getTicker(tBlockEntityType, ModBlocks.CRYO_CHAMBER_TILE_ENTITY, CryoChamberTileEntity::tick)
         : BlockHelper.getTicker(tBlockEntityType, ModBlocks.ANCIENT_CRYO_CHAMBER_TILE_ENTITY, AncientCryoChamberTileEntity::tick);
   }

   private static boolean isntSolid(BlockState state, BlockGetter reader, BlockPos pos) {
      return false;
   }

   public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
      for (CryoChamberBlock.ChamberState state : CryoChamberBlock.ChamberState.values()) {
         ItemStack stack = new ItemStack(this);
         stack.setDamageValue(state.ordinal());
         items.add(stack);
      }
   }

   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
         return this.isNormal(state) ? ModBlocks.CRYO_CHAMBER_TILE_ENTITY.create(pos, state) : ModBlocks.ANCIENT_CRYO_CHAMBER_TILE_ENTITY.create(pos, state);
      } else {
         return null;
      }
   }

   private boolean isNormal(BlockState state) {
      return state.getValue(CHAMBER_STATE) == CryoChamberBlock.ChamberState.NONE;
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      BlockPos pos = context.getClickedPos();
      Level world = context.getLevel();
      return pos.getY() < 255 && world.getBlockState(pos.above()).canBeReplaced(context)
         ? (BlockState)((BlockState)((BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection()))
               .setValue(HALF, DoubleBlockHalf.LOWER))
            .setValue(CHAMBER_STATE, MiscUtils.getEnumEntry(CryoChamberBlock.ChamberState.class, context.getItemInHand().getDamageValue()))
         : null;
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{HALF, FACING, CHAMBER_STATE});
   }

   public void playerWillDestroy(Level worldIn, BlockPos pos, BlockState state, Player player) {
      if (!worldIn.isClientSide && player.isCreative()) {
         DoubleBlockHalf half = (DoubleBlockHalf)state.getValue(HALF);
         if (half == DoubleBlockHalf.UPPER) {
            BlockPos blockpos = pos.below();
            BlockState blockstate = worldIn.getBlockState(blockpos);
            if (blockstate.getBlock() == state.getBlock() && blockstate.getValue(HALF) == DoubleBlockHalf.LOWER) {
               worldIn.setBlock(blockpos, Blocks.AIR.defaultBlockState(), 35);
               worldIn.levelEvent(player, 2001, blockpos, Block.getId(blockstate));
            }
         }
      }

      super.playerWillDestroy(worldIn, pos, state, player);
   }

   public BlockState updateShape(BlockState stateIn, Direction facing, BlockState facingState, LevelAccessor worldIn, BlockPos currentPos, BlockPos facingPos) {
      DoubleBlockHalf half = (DoubleBlockHalf)stateIn.getValue(HALF);
      if (facing.getAxis() == Axis.Y && half == DoubleBlockHalf.LOWER == (facing == Direction.UP)) {
         return facingState.is(this) && facingState.getValue(HALF) != half
            ? (BlockState)stateIn.setValue(FACING, (Direction)facingState.getValue(FACING))
            : Blocks.AIR.defaultBlockState();
      } else {
         return half == DoubleBlockHalf.LOWER && facing == Direction.DOWN && !stateIn.canSurvive(worldIn, currentPos)
            ? Blocks.AIR.defaultBlockState()
            : super.updateShape(stateIn, facing, facingState, worldIn, currentPos, facingPos);
      }
   }

   public void setPlacedBy(Level worldIn, BlockPos pos, BlockState state, LivingEntity placer, ItemStack stack) {
      worldIn.setBlock(pos.above(), (BlockState)state.setValue(HALF, DoubleBlockHalf.UPPER), 3);
      if (placer != null) {
         CryoChamberTileEntity te = getCryoChamberTileEntity(worldIn, pos, state);
         te.setOwner(placer.getUUID());
      }
   }

   public void onRemove(BlockState state, Level worldIn, BlockPos pos, BlockState newState, boolean isMoving) {
      if (!worldIn.isClientSide) {
         if (newState.isAir()) {
            CryoChamberTileEntity chamber = getCryoChamberTileEntity(worldIn, pos, state);
            if (chamber != null) {
               if (state.getValue(HALF) == DoubleBlockHalf.LOWER) {
                  this.dropCryoChamber(worldIn, pos, state, chamber);
               }

               super.onRemove(state, worldIn, pos, newState, isMoving);
            }
         }
      }
   }

   private void dropCryoChamber(Level world, BlockPos pos, BlockState state, CryoChamberTileEntity te) {
      ItemStack chamberStack = new ItemStack(ModBlocks.CRYO_CHAMBER);
      chamberStack.setDamageValue(((CryoChamberBlock.ChamberState)state.getValue(CHAMBER_STATE)).ordinal());
      CompoundTag nbt = chamberStack.getOrCreateTag();
      nbt.put("BlockEntityTag", te.serializeNBT());
      chamberStack.setTag(nbt);
      ItemEntity entity = new ItemEntity(world, pos.getX(), pos.getY(), pos.getZ(), chamberStack);
      world.addFreshEntity(entity);
   }

   public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
      if (!world.isClientSide() && player instanceof ServerPlayer) {
         CryoChamberTileEntity chamber = getCryoChamberTileEntity(world, pos, state);
         if (chamber == null) {
            return InteractionResult.SUCCESS;
         } else if (chamber.getOwner() != null && !chamber.getOwner().equals(player.getUUID())) {
            return InteractionResult.SUCCESS;
         } else {
            ItemStack heldStack = player.getItemInHand(hand);
            if (chamber.getEternal() != null) {
               if (!player.isShiftKeyDown()) {
                  chamber.pickupSpirit(player, chamber);
                  chamber.resetAll();
                  ModNetwork.CHANNEL.send(PacketDistributor.ALL.noArg(), new ClientboundResetCryoChamberMessage(chamber.getBlockPos()));
                  chamber.sendUpdates();
                  return InteractionResult.SUCCESS;
               }

               if (heldStack.isEmpty()) {
                  final CompoundTag nbt = new CompoundTag();
                  nbt.putInt("RenameType", RenameType.CRYO_CHAMBER.ordinal());
                  nbt.put("Data", chamber.getRenameNBT());
                  NetworkHooks.openGui((ServerPlayer)player, new MenuProvider() {
                     public Component getDisplayName() {
                        return new TextComponent("Cryo Chamber");
                     }

                     @javax.annotation.Nullable
                     public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) {
                        return new RenamingContainer(windowId, nbt);
                     }
                  }, buffer -> buffer.writeNbt(nbt));
                  return InteractionResult.SUCCESS;
               }
            } else if (!((CryoChamberBlock.ChamberState)state.getValue(CHAMBER_STATE)).containsAncient() && heldStack.getItem() == ModItems.ETERNAL_SOUL) {
               if (chamber.getOwner() == null) {
                  chamber.setOwner(player.getUUID());
               }

               if (chamber.addEternalSoul()) {
                  if (!player.isCreative()) {
                     heldStack.shrink(1);
                  }

                  chamber.sendUpdates();
               }
            }

            return InteractionResult.SUCCESS;
         }
      } else {
         return InteractionResult.SUCCESS;
      }
   }

   public static BlockPos getCryoChamberPos(BlockState state, BlockPos pos) {
      return state.getValue(HALF) == DoubleBlockHalf.UPPER ? pos.below() : pos;
   }

   public static CryoChamberTileEntity getCryoChamberTileEntity(Level world, BlockPos pos, BlockState state) {
      BlockPos cryoChamberPos = getCryoChamberPos(state, pos);
      BlockEntity tileEntity = world.getBlockEntity(cryoChamberPos);
      return !(tileEntity instanceof CryoChamberTileEntity) ? null : (CryoChamberTileEntity)tileEntity;
   }

   public static enum ChamberState implements StringRepresentable {
      NONE("none"),
      RUSTY("rusty");

      private final String name;

      private ChamberState(String name) {
         this.name = name;
      }

      public boolean containsAncient() {
         return this == RUSTY;
      }

      public String getSerializedName() {
         return this.name;
      }
   }
}
