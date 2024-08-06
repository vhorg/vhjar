package iskallia.vault.block;

import iskallia.vault.block.entity.GridGatewayTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.BlockHelper;
import javax.annotation.Nonnull;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class GridGatewayBlock extends Block implements EntityBlock {
   public GridGatewayBlock() {
      super(Properties.of(Material.STONE).sound(SoundType.STONE).strength(-1.0F, 3600000.0F).noDrops().noOcclusion());
      this.registerDefaultState((BlockState)this.stateDefinition.any());
   }

   @Nonnull
   public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter world, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
      return Block.box(3.0, 0.0, 3.0, 13.0, 18.0, 13.0);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type) {
      return BlockHelper.getTicker(
         type, ModBlocks.GRID_GATEWAY_TILE_ENTITY, level.isClientSide() ? GridGatewayTileEntity::clientTick : GridGatewayTileEntity::serverTick
      );
   }

   @Nonnull
   public RenderShape getRenderShape(@Nonnull BlockState state) {
      return RenderShape.ENTITYBLOCK_ANIMATED;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
      return new GridGatewayTileEntity(pPos, pState);
   }

   public int getLightEmission(BlockState state, BlockGetter level, BlockPos pos) {
      return level.getBlockEntity(pos, ModBlocks.GRID_GATEWAY_TILE_ENTITY).map(GridGatewayTileEntity::getLight).orElse(0);
   }
}
