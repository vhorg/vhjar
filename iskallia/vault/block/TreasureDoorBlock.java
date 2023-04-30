package iskallia.vault.block;

import com.google.common.base.Functions;
import iskallia.vault.block.entity.TreasureDoorTileEntity;
import iskallia.vault.block.item.TreasureDoorBlockItem;
import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModItems;
import iskallia.vault.util.BlockHelper;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import org.jetbrains.annotations.Nullable;

public class TreasureDoorBlock extends DoorBlock implements EntityBlock {
   public static final EnumProperty<TreasureDoorBlock.Type> TYPE = EnumProperty.create("type", TreasureDoorBlock.Type.class);

   public TreasureDoorBlock() {
      super(Properties.of(Material.METAL, MaterialColor.DIAMOND).strength(-1.0F, 3600000.0F).sound(SoundType.METAL).noOcclusion());
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.getStateDefinition().any())
                           .setValue(FACING, Direction.NORTH))
                        .setValue(OPEN, Boolean.FALSE))
                     .setValue(HINGE, DoorHingeSide.LEFT))
                  .setValue(POWERED, Boolean.FALSE))
               .setValue(HALF, DoubleBlockHalf.LOWER))
            .setValue(TYPE, TreasureDoorBlock.Type.ISKALLIUM)
      );
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      super.createBlockStateDefinition(builder);
      builder.add(new Property[]{TYPE});
   }

   public PushReaction getPistonPushReaction(BlockState state) {
      return PushReaction.BLOCK;
   }

   public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
   }

   public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
      return true;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
      return ModBlocks.TREASURE_DOOR_TILE_ENTITY.create(pPos, pState);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
      return BlockHelper.getTicker(pBlockEntityType, ModBlocks.TREASURE_DOOR_TILE_ENTITY, TreasureDoorTileEntity::tick);
   }

   public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
      ItemStack heldStack = player.getItemInHand(hand);
      Boolean isOpen = (Boolean)state.getValue(OPEN);
      if (!isOpen && heldStack.getItem() == ((TreasureDoorBlock.Type)state.getValue(TYPE)).getKey()) {
         heldStack.shrink(1);
         this.setOpen(player, world, state, pos, true);
         CommonEvents.TREASURE_ROOM_OPEN.invoke(world, player, pos);
         return InteractionResult.SUCCESS;
      } else {
         return InteractionResult.SUCCESS;
      }
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      BlockState state = super.getStateForPlacement(context);
      if (state == null) {
         return null;
      } else {
         CompoundTag nbt = context.getItemInHand().getTag();
         if (nbt != null) {
            TreasureDoorBlock.Type type = TreasureDoorBlock.Type.fromString(nbt.getString("type"));
            if (type != null) {
               state = (BlockState)state.setValue(TYPE, type);
            }
         }

         return state;
      }
   }

   public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> items) {
      for (TreasureDoorBlock.Type type : TreasureDoorBlock.Type.values()) {
         items.add(TreasureDoorBlockItem.fromType(type));
      }
   }

   public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
      ItemStack itemStack = super.getCloneItemStack(state, target, level, pos, player);
      itemStack.getOrCreateTag().putString("type", ((TreasureDoorBlock.Type)state.getValue(TYPE)).getSerializedName());
      return itemStack;
   }

   public static enum Type implements StringRepresentable {
      ISKALLIUM(ModItems.ISKALLIUM_KEY),
      GORGINITE(ModItems.GORGINITE_KEY),
      SPARKLETINE(ModItems.SPARKLETINE_KEY),
      ASHIUM(ModItems.ASHIUM_KEY),
      BOMIGNITE(ModItems.BOMIGNITE_KEY),
      TUBIUM(ModItems.TUBIUM_KEY),
      UPALINE(ModItems.UPALINE_KEY),
      PUFFIUM(ModItems.PUFFIUM_KEY),
      PETZANITE(ModItems.PETZANITE_KEY),
      XENIUM(ModItems.XENIUM_KEY);

      private static final Map<String, TreasureDoorBlock.Type> NAME_TO_TYPE = Arrays.stream(values())
         .collect(Collectors.toMap(TreasureDoorBlock.Type::getSerializedName, Functions.identity()));
      private final Item key;

      private Type(Item key) {
         this.key = key;
      }

      public static TreasureDoorBlock.Type fromString(String name) {
         return NAME_TO_TYPE.get(name.toLowerCase(Locale.ROOT));
      }

      public Item getKey() {
         return this.key;
      }

      public String getSerializedName() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }
}
