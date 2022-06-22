package iskallia.vault.block;

import java.util.Random;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.NetherPortalBlock;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.particles.ParticleTypes;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Direction;
import net.minecraft.util.NonNullList;
import net.minecraft.util.Direction.Axis;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.BlockPos.Mutable;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.world.IWorld;
import net.minecraft.world.World;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.gen.Heightmap.Type;
import net.minecraft.world.server.ServerWorld;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FinalVaultPortalBlock extends NetherPortalBlock {
   public FinalVaultPortalBlock() {
      super(Properties.func_200950_a(Blocks.field_150427_aO));
      this.func_180632_j((BlockState)((BlockState)this.field_176227_L.func_177621_b()).func_206870_a(field_176550_a, Axis.X));
   }

   public void func_149666_a(ItemGroup group, NonNullList<ItemStack> items) {
   }

   protected static BlockPos getSpawnPoint(ServerWorld p_241092_0_, int p_241092_1_, int p_241092_2_, boolean p_241092_3_) {
      Mutable blockpos$mutable = new Mutable(p_241092_1_, 0, p_241092_2_);
      Biome biome = p_241092_0_.func_226691_t_(blockpos$mutable);
      boolean flag = p_241092_0_.func_230315_m_().func_236037_d_();
      BlockState blockstate = biome.func_242440_e().func_242502_e().func_204108_a();
      if (p_241092_3_ && !blockstate.func_177230_c().func_203417_a(BlockTags.field_205599_H)) {
         return null;
      } else {
         Chunk chunk = p_241092_0_.func_212866_a_(p_241092_1_ >> 4, p_241092_2_ >> 4);
         int i = flag
            ? p_241092_0_.func_72863_F().func_201711_g().func_205470_d()
            : chunk.func_201576_a(Type.MOTION_BLOCKING, p_241092_1_ & 15, p_241092_2_ & 15);
         if (i < 0) {
            return null;
         } else {
            int j = chunk.func_201576_a(Type.WORLD_SURFACE, p_241092_1_ & 15, p_241092_2_ & 15);
            if (j <= i && j > chunk.func_201576_a(Type.OCEAN_FLOOR, p_241092_1_ & 15, p_241092_2_ & 15)) {
               return null;
            } else {
               for (int k = i + 1; k >= 0; k--) {
                  blockpos$mutable.func_181079_c(p_241092_1_, k, p_241092_2_);
                  BlockState blockstate1 = p_241092_0_.func_180495_p(blockpos$mutable);
                  if (!blockstate1.func_204520_s().func_206888_e()) {
                     break;
                  }

                  if (blockstate1.equals(blockstate)) {
                     return blockpos$mutable.func_177984_a().func_185334_h();
                  }
               }

               return null;
            }
         }
      }
   }

   public boolean hasTileEntity(BlockState state) {
      return false;
   }

   public void func_225542_b_(BlockState state, ServerWorld world, BlockPos pos, Random random) {
   }

   public BlockState func_196271_a(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
      World world = null;
      if (worldIn instanceof World) {
         world = (World)worldIn;
      }

      if (world != null && world.func_234923_W_() == World.field_234918_g_) {
         Axis direction$axis = facing.func_176740_k();
         Axis direction$axis1 = (Axis)stateIn.func_177229_b(field_176550_a);
         if (direction$axis1 != direction$axis && direction$axis.func_176722_c()) {
            boolean var11 = true;
         } else {
            boolean var10000 = false;
         }
      }

      return stateIn;
   }

   public void func_196262_a(BlockState state, World world, BlockPos pos, Entity entity) {
      if (!world.field_72995_K && entity instanceof PlayerEntity) {
         if (!entity.func_184218_aH() && !entity.func_184207_aI() && entity.func_184222_aU()) {
            ServerPlayerEntity player = (ServerPlayerEntity)entity;
            VoxelShape playerVoxel = VoxelShapes.func_197881_a(
               player.func_174813_aQ().func_72317_d(-pos.func_177958_n(), -pos.func_177956_o(), -pos.func_177952_p())
            );
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void func_180655_c(BlockState state, World world, BlockPos pos, Random rand) {
      for (int i = 0; i < 4; i++) {
         double d0 = pos.func_177958_n() + rand.nextDouble();
         double d1 = pos.func_177956_o() + rand.nextDouble();
         double d2 = pos.func_177952_p() + rand.nextDouble();
         double d3 = (rand.nextFloat() - 0.5) * 0.5;
         double d4 = (rand.nextFloat() - 0.5) * 0.5;
         double d5 = (rand.nextFloat() - 0.5) * 0.5;
         int j = rand.nextInt(2) * 2 - 1;
         if (!world.func_180495_p(pos.func_177976_e()).func_203425_a(this) && !world.func_180495_p(pos.func_177974_f()).func_203425_a(this)) {
            d0 = pos.func_177958_n() + 0.5 + 0.25 * j;
            d3 = rand.nextFloat() * 2.0F * j;
         } else {
            d2 = pos.func_177952_p() + 0.5 + 0.25 * j;
            d5 = rand.nextFloat() * 2.0F * j;
         }

         world.func_195594_a(ParticleTypes.field_239813_am_, d0, d1, d2, d3, d4, d5);
      }
   }

   protected void func_206840_a(Builder<Block, BlockState> builder) {
      super.func_206840_a(builder);
   }
}
