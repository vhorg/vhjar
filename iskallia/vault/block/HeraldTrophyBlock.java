package iskallia.vault.block;

import com.google.common.base.Functions;
import iskallia.vault.block.entity.HeraldTrophyTileEntity;
import iskallia.vault.block.item.HeraldTrophyItem;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class HeraldTrophyBlock extends Block implements EntityBlock {
   public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
   public static final EnumProperty<HeraldTrophyBlock.Variant> VARIANT = EnumProperty.create("variant", HeraldTrophyBlock.Variant.class);
   public static final VoxelShape SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);

   public HeraldTrophyBlock() {
      super(Properties.of(Material.METAL).sound(SoundType.METAL).requiresCorrectToolForDrops().strength(5.0F, 6.0F));
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.SOUTH))
            .setValue(VARIANT, HeraldTrophyBlock.Variant.BRONZE)
      );
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING}).add(new Property[]{VARIANT});
   }

   @Nonnull
   public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter worldIn, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
      return SHAPE;
   }

   public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
      return ModBlocks.HERALD_TROPHY_TILE_ENTITY.create(pPos, pState);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return (BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
   }

   public void setPlacedBy(@Nonnull Level world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nullable LivingEntity placer, @Nonnull ItemStack stack) {
      if (!world.isClientSide) {
         CompoundTag blockEntityTag = stack.getOrCreateTagElement("BlockEntityTag");
         String variantId = blockEntityTag.contains("Variant", 8) ? blockEntityTag.getString("Variant") : HeraldTrophyBlock.Variant.BRONZE.getSerializedName();
         HeraldTrophyBlock.Variant variant = HeraldTrophyBlock.Variant.valueOf(variantId.toUpperCase());
         world.setBlock(pos, (BlockState)state.setValue(VARIANT, variant), 2);
      }
   }

   public void playerWillDestroy(@Nonnull Level world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull Player player) {
      if (!world.isClientSide && !player.isCreative()) {
         ItemStack itemStack = new ItemStack(this);
         CompoundTag nbt = itemStack.getOrCreateTag();
         CompoundTag blockEntityTag = itemStack.getOrCreateTagElement("BlockEntityTag");
         if (world.getBlockEntity(pos) instanceof HeraldTrophyTileEntity trophy) {
            trophy.writeToEntityTag(blockEntityTag);
         }

         HeraldTrophyBlock.Variant variant = (HeraldTrophyBlock.Variant)state.getValue(VARIANT);
         nbt.putInt("CustomModelData", variant.ordinal());
         blockEntityTag.putString("Variant", variant.getSerializedName());
         ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, itemStack);
         itemEntity.setDefaultPickUpDelay();
         world.addFreshEntity(itemEntity);
      }

      super.playerWillDestroy(world, pos, state, player);
   }

   public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> items) {
      if (ModConfigs.isInitialized()) {
         items.add(HeraldTrophyItem.create(null, HeraldTrophyBlock.Variant.BRONZE, ModConfigs.HERALD_TROPHY.getTime(HeraldTrophyBlock.Variant.BRONZE)));
         items.add(HeraldTrophyItem.create(null, HeraldTrophyBlock.Variant.SILVER, ModConfigs.HERALD_TROPHY.getTime(HeraldTrophyBlock.Variant.SILVER)));
         items.add(HeraldTrophyItem.create(null, HeraldTrophyBlock.Variant.GOLD, ModConfigs.HERALD_TROPHY.getTime(HeraldTrophyBlock.Variant.GOLD)));
         items.add(HeraldTrophyItem.create(null, HeraldTrophyBlock.Variant.PLATINUM, ModConfigs.HERALD_TROPHY.getTime(HeraldTrophyBlock.Variant.PLATINUM)));
      }
   }

   public static enum Variant implements StringRepresentable {
      BRONZE,
      SILVER,
      GOLD,
      PLATINUM;

      private static final Map<String, HeraldTrophyBlock.Variant> NAME_TO_TYPE = Arrays.stream(values())
         .collect(Collectors.toMap(HeraldTrophyBlock.Variant::getName, Functions.identity()));

      public String getName() {
         return this.name().toLowerCase();
      }

      public static HeraldTrophyBlock.Variant fromString(String name) {
         return NAME_TO_TYPE.get(name.toLowerCase());
      }

      public String getSerializedName() {
         return this.getName();
      }
   }
}
