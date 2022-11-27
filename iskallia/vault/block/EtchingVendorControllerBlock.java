package iskallia.vault.block;

import iskallia.vault.block.entity.EtchingVendorControllerTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.EntityCollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

public class EtchingVendorControllerBlock extends BaseEntityBlock {
   public EtchingVendorControllerBlock() {
      super(
         Properties.copy(Blocks.BARRIER)
            .noCollission()
            .isRedstoneConductor(EtchingVendorControllerBlock::nonSolid)
            .isViewBlocking(EtchingVendorControllerBlock::nonSolid)
      );
   }

   @Nullable
   public <A extends BlockEntity> BlockEntityTicker<A> getTicker(Level p_153212_, BlockState state, BlockEntityType<A> tBlockEntityType) {
      return BlockHelper.getTicker(tBlockEntityType, ModBlocks.ETCHING_CONTROLLER_TILE_ENTITY, EtchingVendorControllerTileEntity::tick);
   }

   private static boolean nonSolid(BlockState state, BlockGetter reader, BlockPos pos) {
      return false;
   }

   public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) {
      return true;
   }

   public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
      return context instanceof EntityCollisionContext entityCollisionContext
            && entityCollisionContext.getEntity() instanceof Player player
            && player.isCreative()
         ? Shapes.block()
         : Shapes.empty();
   }

   public RenderShape getRenderShape(BlockState state) {
      return RenderShape.INVISIBLE;
   }

   @OnlyIn(Dist.CLIENT)
   public float getShadeBrightness(BlockState state, BlockGetter worldIn, BlockPos pos) {
      return 1.0F;
   }

   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return ModBlocks.ETCHING_CONTROLLER_TILE_ENTITY.create(pos, state);
   }
}
