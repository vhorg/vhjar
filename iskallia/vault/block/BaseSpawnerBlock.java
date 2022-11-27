package iskallia.vault.block;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public abstract class BaseSpawnerBlock extends Block implements EntityBlock {
   private static final VoxelShape SHAPE = Block.box(0.1, 0.1, 0.1, 15.9, 15.9, 15.9);

   protected BaseSpawnerBlock() {
      super(Properties.of(Material.BARRIER).strength(-1.0F, 3600000.8F).noDrops().noOcclusion().isValidSpawn((state, blockGetter, pos, entityType) -> false));
   }

   public RenderShape getRenderShape(BlockState state) {
      return RenderShape.ENTITYBLOCK_ANIMATED;
   }

   public boolean propagatesSkylightDown(BlockState state, BlockGetter blockGetter, BlockPos pos) {
      return true;
   }

   public VoxelShape getShape(BlockState state, BlockGetter blockGetter, BlockPos pos, CollisionContext context) {
      return SHAPE;
   }
}
