package iskallia.vault.block.base;

import javax.annotation.Nonnull;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.HorizontalBlock;
import net.minecraft.block.AbstractBlock.Properties;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.pathfinding.PathType;
import net.minecraft.state.DirectionProperty;
import net.minecraft.state.Property;
import net.minecraft.state.StateContainer.Builder;
import net.minecraft.util.Direction;
import net.minecraft.util.Mirror;
import net.minecraft.util.Rotation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class FacedBlock extends Block {
   public static final DirectionProperty FACING = HorizontalBlock.field_185512_D;

   public FacedBlock(Properties properties) {
      super(properties);
   }

   @Nonnull
   public BlockState func_196258_a(BlockItemUseContext context) {
      return (BlockState)this.func_176223_P().func_206870_a(FACING, context.func_195992_f());
   }

   public BlockState func_185499_a(BlockState state, Rotation rot) {
      return (BlockState)state.func_206870_a(FACING, rot.func_185831_a((Direction)state.func_177229_b(FACING)));
   }

   public BlockState func_185471_a(BlockState state, Mirror mirrorIn) {
      return state.func_185907_a(mirrorIn.func_185800_a((Direction)state.func_177229_b(FACING)));
   }

   protected void func_206840_a(Builder<Block, BlockState> builder) {
      builder.func_206894_a(new Property[]{FACING});
   }

   public boolean func_196266_a(BlockState state, IBlockReader worldIn, BlockPos pos, PathType type) {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public int getDustColor(BlockState state, IBlockReader reader, BlockPos pos) {
      return state.func_185909_g(reader, pos).field_76291_p;
   }
}
