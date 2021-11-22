package iskallia.vault.block;

import iskallia.vault.init.ModBlocks;
import javax.annotation.Nullable;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.ContainerBlock;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.shapes.EntitySelectionContext;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EtchingVendorControllerBlock extends ContainerBlock {
   public EtchingVendorControllerBlock() {
      super(
         Properties.func_200950_a(Blocks.field_180401_cv)
            .func_200942_a()
            .func_235828_a_(EtchingVendorControllerBlock::nonSolid)
            .func_235847_c_(EtchingVendorControllerBlock::nonSolid)
      );
   }

   private static boolean nonSolid(BlockState state, IBlockReader reader, BlockPos pos) {
      return false;
   }

   public boolean func_200123_i(BlockState state, IBlockReader reader, BlockPos pos) {
      return true;
   }

   public VoxelShape func_220053_a(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
      if (context instanceof EntitySelectionContext) {
         Entity e = context.getEntity();
         if (e instanceof PlayerEntity && ((PlayerEntity)e).func_184812_l_()) {
            return VoxelShapes.func_197868_b();
         }
      }

      return VoxelShapes.func_197880_a();
   }

   public BlockRenderType func_149645_b(BlockState state) {
      return BlockRenderType.INVISIBLE;
   }

   @OnlyIn(Dist.CLIENT)
   public float func_220080_a(BlockState state, IBlockReader worldIn, BlockPos pos) {
      return 1.0F;
   }

   @Nullable
   public TileEntity func_196283_a_(IBlockReader world) {
      return ModBlocks.ETCHING_CONTROLLER_TILE_ENTITY.func_200968_a();
   }
}
