package iskallia.vault.block;

import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CrakeColumnBlock extends Block {
   private static final VoxelShape SHAPE = Shapes.or(
      Block.box(0.0, 13.0, 0.0, 16.0, 16.0, 16.0), new VoxelShape[]{Block.box(2.0, 2.0, 2.0, 14.0, 13.0, 14.0), Block.box(0.0, 0.0, 0.0, 16.0, 3.0, 16.0)}
   );

   public CrakeColumnBlock() {
      super(Properties.of(Material.STONE).sound(SoundType.STONE).requiresCorrectToolForDrops().strength(2.0F, 6.0F));
   }

   @Nonnull
   public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter world, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
      return SHAPE;
   }

   @Nonnull
   public RenderShape getRenderShape(@Nonnull BlockState state) {
      return RenderShape.MODEL;
   }
}
