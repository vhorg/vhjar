package iskallia.vault.block;

import iskallia.vault.block.entity.CrakePedestalTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.BlockHelper;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;

public class CrakePedestalBlock extends Block implements EntityBlock {
   private static final VoxelShape SHAPE = Block.box(2.0, 0.0, 2.0, 14.0, 14.0, 14.0);
   public static final BooleanProperty CONSUMED = BooleanProperty.create("consumed");

   public CrakePedestalBlock() {
      super(Properties.of(Material.GLASS).sound(SoundType.GLASS).strength(-1.0F, 3600000.0F).noDrops());
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(CONSUMED, false));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
      pBuilder.add(new Property[]{CONSUMED});
   }

   @Nonnull
   public VoxelShape getShape(@Nonnull BlockState state, @Nonnull BlockGetter world, @Nonnull BlockPos pos, @Nonnull CollisionContext context) {
      return SHAPE;
   }

   @Nonnull
   public RenderShape getRenderShape(@Nonnull BlockState state) {
      return RenderShape.MODEL;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return ModBlocks.CRAKE_PEDESTAL_TILE_ENTITY.create(pos, state);
   }

   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
      return BlockHelper.getTicker(type, ModBlocks.CRAKE_PEDESTAL_TILE_ENTITY, CrakePedestalTileEntity::tick);
   }
}
