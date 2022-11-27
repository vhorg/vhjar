package iskallia.vault.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class MVPCrownBlock extends Block {
   public static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 6.0, 14.0);

   public MVPCrownBlock() {
      super(Properties.of(Material.STONE, MaterialColor.STONE).strength(1.0F, 3600000.0F).noOcclusion().noCollission());
   }

   public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
      return SHAPE;
   }
}
