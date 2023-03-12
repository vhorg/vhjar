package iskallia.vault.block;

import com.google.common.base.Functions;
import iskallia.vault.block.item.PlaceholderBlockItem;
import java.util.Arrays;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.HitResult;

public class PlaceholderBlock extends Block {
   public static final DirectionProperty FACING = BlockStateProperties.FACING;
   public static final EnumProperty<PlaceholderBlock.Type> TYPE = EnumProperty.create("type", PlaceholderBlock.Type.class);

   public PlaceholderBlock() {
      super(Properties.copy(Blocks.WHITE_CONCRETE));
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.NORTH))
            .setValue(TYPE, PlaceholderBlock.Type.WOODEN_CHEST)
      );
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING, TYPE});
   }

   public BlockState rotate(BlockState state, Rotation rotation) {
      return (BlockState)state.setValue(FACING, rotation.rotate((Direction)state.getValue(FACING)));
   }

   public BlockState mirror(BlockState state, Mirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.getValue(FACING)));
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      BlockState blockState = (BlockState)this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection().getOpposite());
      CompoundTag nbt = context.getItemInHand().getTag();
      if (nbt != null) {
         PlaceholderBlock.Type type = PlaceholderBlock.Type.fromString(nbt.getString("type"));
         if (type != null) {
            blockState = (BlockState)blockState.setValue(TYPE, type);
         }
      }

      return blockState;
   }

   public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> items) {
      for (PlaceholderBlock.Type type : PlaceholderBlock.Type.values()) {
         items.add(PlaceholderBlockItem.fromType(type));
      }
   }

   public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player) {
      ItemStack itemStack = super.getCloneItemStack(state, target, level, pos, player);
      itemStack.getOrCreateTag().putString("type", ((PlaceholderBlock.Type)state.getValue(TYPE)).getSerializedName());
      return itemStack;
   }

   public static enum Type implements StringRepresentable {
      WOODEN_CHEST,
      GILDED_CHEST,
      LIVING_CHEST,
      ORNATE_CHEST,
      OBJECTIVE,
      ORE,
      COIN_STACKS,
      VENDOR_PEDESTAL,
      TREASURE_DOOR,
      PYLON,
      DUNGEON_DISCOVERABLE;

      private static final Map<String, PlaceholderBlock.Type> NAME_TO_TYPE = Arrays.stream(values())
         .collect(Collectors.toMap(PlaceholderBlock.Type::getSerializedName, Functions.identity()));

      public static PlaceholderBlock.Type fromString(String name) {
         return NAME_TO_TYPE.get(name.toLowerCase(Locale.ROOT));
      }

      @Nonnull
      public String getSerializedName() {
         return this.name().toLowerCase(Locale.ROOT);
      }
   }
}
