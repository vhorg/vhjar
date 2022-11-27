package iskallia.vault.block;

import iskallia.vault.block.entity.VaultChampionTrophyTileEntity;
import iskallia.vault.init.ModBlocks;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
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
import org.jetbrains.annotations.Nullable;

public class VaultChampionTrophy extends Block implements EntityBlock {
   public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
   public static final EnumProperty<VaultChampionTrophy.Variant> VARIANT = EnumProperty.create("variant", VaultChampionTrophy.Variant.class);
   public static final VoxelShape SHAPE = Block.box(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);

   public VaultChampionTrophy() {
      super(Properties.of(Material.METAL).sound(SoundType.METAL).requiresCorrectToolForDrops().strength(5.0F, 6.0F));
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)this.stateDefinition.any()).setValue(FACING, Direction.SOUTH))
            .setValue(VARIANT, VaultChampionTrophy.Variant.GOLDEN)
      );
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING}).add(new Property[]{VARIANT});
   }

   @Nonnull
   public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter worldIn, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
      return SHAPE;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
      return ModBlocks.VAULT_CHAMPION_TROPHY_TILE_ENTITY.create(pPos, pState);
   }

   @javax.annotation.Nullable
   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return (BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection());
   }

   public void setPlacedBy(
      @Nonnull Level world, @Nonnull BlockPos pos, @Nonnull BlockState state, @javax.annotation.Nullable LivingEntity placer, @Nonnull ItemStack stack
   ) {
      if (!world.isClientSide) {
         CompoundTag blockEntityTag = stack.getOrCreateTagElement("BlockEntityTag");
         String variantId = blockEntityTag.contains("Variant", 8)
            ? blockEntityTag.getString("Variant")
            : VaultChampionTrophy.Variant.GOLDEN.getSerializedName();
         VaultChampionTrophy.Variant variant = VaultChampionTrophy.Variant.valueOf(variantId.toUpperCase());
         world.setBlock(pos, (BlockState)state.setValue(VARIANT, variant), 2);
      }
   }

   public void playerWillDestroy(@Nonnull Level world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull Player player) {
      if (!world.isClientSide && !player.isCreative()) {
         ItemStack itemStack = new ItemStack(this);
         CompoundTag nbt = itemStack.getOrCreateTag();
         CompoundTag blockEntityTag = itemStack.getOrCreateTagElement("BlockEntityTag");
         if (world.getBlockEntity(pos) instanceof VaultChampionTrophyTileEntity trophy) {
            trophy.writeToEntityTag(blockEntityTag);
         }

         VaultChampionTrophy.Variant variant = (VaultChampionTrophy.Variant)state.getValue(VARIANT);
         nbt.putInt("CustomModelData", variant.ordinal());
         blockEntityTag.putString("Variant", variant.getSerializedName());
         ItemEntity itemEntity = new ItemEntity(world, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, itemStack);
         itemEntity.setDefaultPickUpDelay();
         world.addFreshEntity(itemEntity);
      }

      super.playerWillDestroy(world, pos, state, player);
   }

   public static enum Variant implements StringRepresentable {
      GOLDEN,
      BLUE_SILVER,
      PLATINUM,
      SILVER;

      @Nonnull
      public String getSerializedName() {
         return this.name().toLowerCase();
      }
   }
}
