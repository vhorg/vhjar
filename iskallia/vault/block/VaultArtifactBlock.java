package iskallia.vault.block;

import iskallia.vault.block.base.FacedBlock;
import iskallia.vault.block.property.HiddenIntegerProperty;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.MathUtilities;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.state.IntegerProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;
import net.minecraftforge.registries.ForgeRegistries;

public class VaultArtifactBlock extends FacedBlock {
   public static final int ARTIFACT_COUNT = 25;
   public static final IntegerProperty ORDER_PROPERTY = HiddenIntegerProperty.create("order", 1, 25);
   public static final VoxelShape EAST_SHAPE = Block.func_208617_a(15.75, 0.0, 0.0, 16.0, 16.0, 16.0);
   public static final VoxelShape NORTH_SHAPE = Block.func_208617_a(0.0, 0.0, 0.0, 16.0, 16.0, 0.25);
   public static final VoxelShape WEST_SHAPE = Block.func_208617_a(0.0, 0.0, 0.0, 0.25, 16.0, 16.0);
   public static final VoxelShape SOUTH_SHAPE = Block.func_208617_a(0.0, 0.0, 15.75, 16.0, 16.0, 16.0);

   public VaultArtifactBlock() {
      super(Properties.func_200949_a(Material.field_151571_B, MaterialColor.field_151663_o).func_200947_a(SoundType.field_185854_g).func_226896_b_());
      this.func_180632_j((BlockState)((BlockState)this.field_176227_L.func_177621_b()).func_206870_a(FACING, Direction.SOUTH));
   }

   public void func_149666_a(ItemGroup group, NonNullList<ItemStack> items) {
   }

   public int getOrder(ItemStack stack) {
      CompoundNBT nbt = stack.func_196082_o();
      return nbt.func_74764_b("CustomModelData") ? nbt.func_74762_e("CustomModelData") : 1;
   }

   public VoxelShape func_220053_a(BlockState state, IBlockReader world, BlockPos pos, ISelectionContext context) {
      switch ((Direction)state.func_177229_b(FACING)) {
         case EAST:
            return EAST_SHAPE;
         case NORTH:
            return NORTH_SHAPE;
         case WEST:
            return WEST_SHAPE;
         default:
            return SOUTH_SHAPE;
      }
   }

   @Nonnull
   @Override
   public BlockState func_196258_a(BlockItemUseContext context) {
      ItemStack artifactBlockItem = context.func_195996_i();
      return (BlockState)super.func_196258_a(context).func_206870_a(ORDER_PROPERTY, this.getOrder(artifactBlockItem));
   }

   @Override
   protected void func_206840_a(Builder<Block, BlockState> builder) {
      super.func_206840_a(builder);
      builder.func_206894_a(new Property[]{ORDER_PROPERTY});
   }

   public void onBlockExploded(BlockState state, World world, BlockPos pos, Explosion explosion) {
   }

   public List<ItemStack> func_220076_a(BlockState state, net.minecraft.loot.LootContext.Builder builder) {
      Integer order = (Integer)state.func_177229_b(ORDER_PROPERTY);
      ItemStack artifactStack = createArtifact(order);
      return new ArrayList<>(Collections.singletonList(artifactStack));
   }

   public ItemStack getPickBlock(BlockState state, RayTraceResult target, IBlockReader world, BlockPos pos, PlayerEntity player) {
      Integer order = (Integer)state.func_177229_b(ORDER_PROPERTY);
      return createArtifact(order);
   }

   public static ItemStack createRandomArtifact() {
      return createArtifact(MathUtilities.getRandomInt(0, 25) + 1);
   }

   public static ItemStack createArtifact(int order) {
      Item artifactItem = (Item)ForgeRegistries.ITEMS.getValue(ModBlocks.VAULT_ARTIFACT.getRegistryName());
      ItemStack itemStack = new ItemStack(artifactItem, 1);
      CompoundNBT nbt = new CompoundNBT();
      nbt.func_74768_a("CustomModelData", MathHelper.func_76125_a(order, 0, 25));
      itemStack.func_77982_d(nbt);
      return itemStack;
   }
}
