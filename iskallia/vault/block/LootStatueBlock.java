package iskallia.vault.block;

import iskallia.vault.block.entity.LootStatueTileEntity;
import iskallia.vault.container.LootStatueContainer;
import iskallia.vault.container.RenamingContainer;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.util.BlockHelper;
import iskallia.vault.util.RenameType;
import java.util.List;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.NbtUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SlabBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Half;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import org.jetbrains.annotations.NotNull;

public class LootStatueBlock extends Block implements EntityBlock {
   public static final VoxelShape SHAPE = Shapes.or(LootStatueUpperBlock.SHAPE, Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0));
   public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;

   protected LootStatueBlock(Properties properties) {
      super(properties);
      this.registerDefaultState((BlockState)((BlockState)this.getStateDefinition().any()).setValue(FACING, Direction.SOUTH));
   }

   public LootStatueBlock() {
      this(Properties.of(Material.STONE, MaterialColor.STONE).strength(1.0F, 3600000.0F));
   }

   public <A extends BlockEntity> BlockEntityTicker<A> getTicker(Level pLevel, BlockState state, BlockEntityType<A> tBlockEntityType) {
      return BlockHelper.getTicker(tBlockEntityType, ModBlocks.LOOT_STATUE_TILE_ENTITY, LootStatueTileEntity::tick);
   }

   public PushReaction getPistonPushReaction(BlockState pState) {
      return PushReaction.BLOCK;
   }

   public BlockState updateShape(
      BlockState pState, Direction pDirection, BlockState pNeighborState, LevelAccessor pLevel, BlockPos pCurrentPos, BlockPos pNeighborPos
   ) {
      return pDirection == Direction.UP && pNeighborState != ModBlocks.LOOT_STATUE_UPPER.defaultBlockState()
         ? Blocks.AIR.defaultBlockState()
         : super.updateShape(pState, pDirection, pNeighborState, pLevel, pCurrentPos, pNeighborPos);
   }

   public void setPlacedBy(Level pLevel, BlockPos pos, BlockState pState, LivingEntity pPlacer, ItemStack stack) {
      BlockPos blockpos = pos.above();
      pLevel.setBlock(blockpos, (BlockState)ModBlocks.LOOT_STATUE_UPPER.defaultBlockState().setValue(LootStatueUpperBlock.HALF, Half.BOTTOM), 3);
      pLevel.setBlock(blockpos.above(), (BlockState)ModBlocks.LOOT_STATUE_UPPER.defaultBlockState().setValue(LootStatueUpperBlock.HALF, Half.TOP), 3);
      if (pPlacer instanceof ServerPlayer player) {
         if (pLevel.getBlockEntity(pos) instanceof LootStatueTileEntity tile && (!stack.hasTag() || !stack.getTag().contains("BlockEntityTag"))) {
            final CompoundTag data = new CompoundTag();
            ListTag itemList = new ListTag();

            for (ItemStack option : ModConfigs.STATUE_LOOT.getOptions()) {
               itemList.add(option.serializeNBT());
            }

            data.put("Items", itemList);
            data.put("Position", NbtUtils.writeBlockPos(pos));
            NetworkHooks.openGui(player, new MenuProvider() {
               public Component getDisplayName() {
                  return new TextComponent("Loot Statue Options");
               }

               public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) {
                  return new LootStatueContainer(windowId, data);
               }
            }, buffer -> buffer.writeNbt(data));
         }
      }
   }

   public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
      BlockEntity te = world.getBlockEntity(pos);
      if (world.getBlockEntity(pos) instanceof LootStatueTileEntity statue) {
         ItemStack heldItem = player.getMainHandItem();
         if (player.isSecondaryUseActive()) {
            ItemStack chip = statue.removeChip();
            if (chip != ItemStack.EMPTY) {
               if (!player.addItem(chip)) {
                  player.drop(chip, false);
               }

               return InteractionResult.sidedSuccess(world.isClientSide);
            }

            if (heldItem.isEmpty()) {
               if (player instanceof ServerPlayer serverPlayer) {
                  final CompoundTag nbt = new CompoundTag();
                  nbt.putInt("RenameType", RenameType.PLAYER_STATUE.ordinal());
                  CompoundTag data = statue.saveWithoutMetadata();
                  data.put("Pos", NbtUtils.writeBlockPos(pos));
                  nbt.put("Data", data);
                  NetworkHooks.openGui((ServerPlayer)player, new MenuProvider() {
                     public Component getDisplayName() {
                        return new TextComponent("Player Statue");
                     }

                     @NotNull
                     public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) {
                        return new RenamingContainer(windowId, nbt);
                     }
                  }, buffer -> buffer.writeNbt(nbt));
               }

               return InteractionResult.sidedSuccess(world.isClientSide);
            }
         } else {
            if (heldItem.getItem() != ModItems.ACCELERATION_CHIP) {
               if (heldItem.getItem() instanceof BlockItem bi && bi.getBlock() instanceof SlabBlock) {
                  ItemStack s = heldItem.copy();
                  s.setCount(1);
                  statue.setStand(heldItem, bi);
                  world.playSound(null, pos, bi.getBlock().defaultBlockState().getSoundType().getPlaceSound(), SoundSource.PLAYERS, 0.6F, 1.2F);
                  return InteractionResult.sidedSuccess(world.isClientSide);
               }

               if (world.isClientSide) {
                  statue.wobble();
               }

               return InteractionResult.sidedSuccess(world.isClientSide);
            }

            if (statue.addChip()) {
               if (!player.getAbilities().instabuild) {
                  heldItem.shrink(1);
               }

               return InteractionResult.sidedSuccess(world.isClientSide);
            }
         }
      }

      return super.use(state, world, pos, player, handIn, hit);
   }

   public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
      return ModBlocks.LOOT_STATUE_TILE_ENTITY.create(pPos, pState);
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING});
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      BlockPos pos = context.getClickedPos();
      Level world = context.getLevel();
      return pos.getY() < world.getMaxBuildHeight() - 3
            && world.getBlockState(pos.above(1)).canBeReplaced(context)
            && world.getBlockState(pos.above(2)).canBeReplaced(context)
         ? (BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection())
         : null;
   }

   public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
      return SHAPE;
   }

   public void playerWillDestroy(Level world, BlockPos pos, BlockState state, Player player) {
      if (!world.isClientSide) {
         BlockEntity tileEntity = world.getBlockEntity(pos);
         ItemStack itemStack = new ItemStack(this);
         if (tileEntity instanceof LootStatueTileEntity tile) {
            CompoundTag stackNBT = new CompoundTag();
            stackNBT.put("BlockEntityTag", tile.saveWithoutMetadata());
            itemStack.setTag(stackNBT);
         }

         ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, itemStack);
         itemEntity.setDefaultPickUpDelay();
         world.addFreshEntity(itemEntity);
      }

      super.playerWillDestroy(world, pos, state, player);
   }

   public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter world, BlockPos pos, Player player) {
      ItemStack itemstack = super.getCloneItemStack(state, target, world, pos, player);
      if (world.getBlockEntity(pos) instanceof LootStatueTileEntity tile) {
         CompoundTag stackNBT = new CompoundTag();
         stackNBT.put("BlockEntityTag", tile.saveWithoutMetadata());
         if (!stackNBT.isEmpty()) {
            itemstack.setTag(stackNBT);
         }
      }

      return itemstack;
   }

   public RenderShape getRenderShape(BlockState pState) {
      return RenderShape.ENTITYBLOCK_ANIMATED;
   }

   public List<ItemStack> getDrops(BlockState state, net.minecraft.world.level.storage.loot.LootContext.Builder builder) {
      return List.of();
   }
}
