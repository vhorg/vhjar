package iskallia.vault.block;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.SoundType;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.Direction;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class VaultArtifactBlock extends Block {
   public static final VoxelShape EAST_SHAPE = Block.func_208617_a(15.75, 0.0, 0.0, 16.0, 16.0, 16.0);
   public static final VoxelShape NORTH_SHAPE = Block.func_208617_a(0.0, 0.0, 0.0, 16.0, 16.0, 0.25);
   public static final VoxelShape WEST_SHAPE = Block.func_208617_a(0.0, 0.0, 0.0, 0.25, 16.0, 16.0);
   public static final VoxelShape SOUTH_SHAPE = Block.func_208617_a(0.0, 0.0, 15.75, 16.0, 16.0, 16.0);
   public static final DirectionProperty FACING = BlockStateProperties.field_208157_J;
   protected int order;

   public VaultArtifactBlock(int order) {
      super(Properties.func_200949_a(Material.field_151571_B, MaterialColor.field_151663_o).func_200947_a(SoundType.field_185854_g).func_226896_b_());
      this.func_180632_j((BlockState)((BlockState)this.field_176227_L.func_177621_b()).func_206870_a(FACING, Direction.SOUTH));
      this.order = order;
   }

   public int getOrder() {
      return this.order;
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

   public BlockState func_196258_a(BlockItemUseContext context) {
      return (BlockState)this.func_176223_P().func_206870_a(FACING, context.func_195992_f());
   }

   protected void func_206840_a(Builder<Block, BlockState> builder) {
      builder.func_206894_a(new Property[]{FACING});
   }

   public void onBlockExploded(BlockState state, World world, BlockPos pos, Explosion explosion) {
   }

   public List<ItemStack> func_220076_a(BlockState state, net.minecraft.loot.LootContext.Builder builder) {
      return new ArrayList<>(
         Collections.singletonList(new ItemStack((IItemProvider)Registry.field_212630_s.func_241873_b(this.getRegistryName()).orElse(Items.field_190931_a)))
      );
   }
}
