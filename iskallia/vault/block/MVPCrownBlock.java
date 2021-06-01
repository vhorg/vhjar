package iskallia.vault.block;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.material.Material;
import net.minecraft.block.material.MaterialColor;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.world.IBlockReader;

public class MVPCrownBlock extends Block {
   public static final VoxelShape SHAPE = Block.func_208617_a(2.0, 0.0, 2.0, 14.0, 6.0, 14.0);

   public MVPCrownBlock() {
      super(Properties.func_200949_a(Material.field_151576_e, MaterialColor.field_151665_m).func_200948_a(1.0F, 3600000.0F).func_226896_b_().func_200942_a());
   }

   public VoxelShape func_220053_a(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      return SHAPE;
   }
}
