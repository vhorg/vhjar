package iskallia.vault.block;

import iskallia.vault.block.entity.VaultChampionTrophyTileEntity;
import iskallia.vault.init.ModBlocks;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.material.Material;
import net.minecraft.entity.item.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.EnumProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.IStringSerializable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class VaultChampionTrophy extends Block {
   public static final DirectionProperty FACING = BlockStateProperties.field_208157_J;
   public static final EnumProperty<VaultChampionTrophy.Variant> VARIANT = EnumProperty.func_177709_a("variant", VaultChampionTrophy.Variant.class);
   public static final VoxelShape SHAPE = Block.func_208617_a(4.0, 0.0, 4.0, 12.0, 16.0, 12.0);

   public VaultChampionTrophy() {
      super(Properties.func_200945_a(Material.field_151573_f).func_200947_a(SoundType.field_185852_e).func_235861_h_().func_200948_a(5.0F, 6.0F));
      this.func_180632_j(
         (BlockState)((BlockState)((BlockState)this.field_176227_L.func_177621_b()).func_206870_a(FACING, Direction.SOUTH))
            .func_206870_a(VARIANT, VaultChampionTrophy.Variant.SILVER)
      );
   }

   protected void func_206840_a(Builder<Block, BlockState> builder) {
      builder.func_206894_a(new Property[]{FACING, VARIANT});
   }

   @Nonnull
   public VoxelShape func_220053_a(@Nonnull BlockState state, @Nonnull IBlockReader worldIn, @Nonnull BlockPos pos, @Nonnull ISelectionContext context) {
      return SHAPE;
   }

   public boolean hasTileEntity(BlockState state) {
      return true;
   }

   @Nullable
   public TileEntity createTileEntity(BlockState state, IBlockReader world) {
      return ModBlocks.VAULT_CHAMPION_TROPHY_TILE_ENTITY.func_200968_a();
   }

   @Nullable
   public BlockState func_196258_a(BlockItemUseContext context) {
      ItemStack stack = context.func_195996_i();
      CompoundNBT blockEntityTag = stack.func_190925_c("BlockEntityTag");
      String variantId = blockEntityTag.func_150297_b("Variant", 8)
         ? blockEntityTag.func_74779_i("Variant")
         : VaultChampionTrophy.Variant.SILVER.func_176610_l();
      VaultChampionTrophy.Variant variant = VaultChampionTrophy.Variant.valueOf(variantId.toUpperCase());
      return (BlockState)((BlockState)this.func_176223_P().func_206870_a(FACING, context.func_195992_f())).func_206870_a(VARIANT, variant);
   }

   public void func_176208_a(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull BlockState state, @Nonnull PlayerEntity player) {
      if (!world.field_72995_K && !player.func_184812_l_()) {
         ItemStack itemStack = new ItemStack(this.getBlock());
         CompoundNBT nbt = itemStack.func_196082_o();
         CompoundNBT blockEntityTag = itemStack.func_190925_c("BlockEntityTag");
         TileEntity tileEntity = world.func_175625_s(pos);
         if (tileEntity instanceof VaultChampionTrophyTileEntity) {
            VaultChampionTrophyTileEntity trophy = (VaultChampionTrophyTileEntity)tileEntity;
            trophy.writeToEntityTag(blockEntityTag);
         }

         VaultChampionTrophy.Variant variant = (VaultChampionTrophy.Variant)state.func_177229_b(VARIANT);
         nbt.func_74768_a("CustomModelData", variant.ordinal());
         blockEntityTag.func_74778_a("Variant", variant.func_176610_l());
         ItemEntity itemEntity = new ItemEntity(world, pos.func_177958_n() + 0.5, pos.func_177956_o() + 0.5, pos.func_177952_p() + 0.5, itemStack);
         itemEntity.func_174869_p();
         world.func_217376_c(itemEntity);
      }

      super.func_176208_a(world, pos, state, player);
   }

   public static enum Variant implements IStringSerializable {
      SILVER,
      BLUE_SILVER,
      GOLDEN,
      PLATINUM;

      @Nonnull
      public String func_176610_l() {
         return this.name().toLowerCase();
      }
   }
}
