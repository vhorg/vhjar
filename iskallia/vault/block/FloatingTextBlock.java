package iskallia.vault.block;

import iskallia.vault.init.ModBlocks;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BarrierBlock;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.block.material.Material;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.pathfinding.PathType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraft.world.World;

public class FloatingTextBlock extends BarrierBlock {
   public FloatingTextBlock() {
      super(Properties.func_200945_a(Material.field_175972_I).func_200948_a(-1.0F, 3.6E8F).func_222380_e().func_226896_b_().func_200942_a());
   }

   public boolean func_181623_g() {
      return true;
   }

   public BlockRenderType func_149645_b(BlockState state) {
      return BlockRenderType.INVISIBLE;
   }

   public boolean func_196266_a(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
      return true;
   }

   public void func_180655_c(BlockState stateIn, World worldIn, BlockPos pos, Random rand) {
      Minecraft minecraft = Minecraft.func_71410_x();
      ClientPlayerEntity player = minecraft.field_71439_g;
      ClientWorld world = minecraft.field_71441_e;
      if (player != null && world != null && player.func_184614_ca().func_77973_b() == ModBlocks.FLOATING_TEXT.func_199767_j()) {
         int i = pos.func_177958_n();
         int j = pos.func_177956_o();
         int k = pos.func_177952_p();
         world.func_195594_a(ParticleTypes.field_197610_c, i + 0.5, j + 0.5, k + 0.5, 0.0, 0.0, 0.0);
      }
   }

   public boolean hasTileEntity(BlockState state) {
      return true;
   }

   @Nullable
   public TileEntity createTileEntity(BlockState state, IBlockReader world) {
      return ModBlocks.FLOATING_TEXT_TILE_ENTITY.func_200968_a();
   }
}
