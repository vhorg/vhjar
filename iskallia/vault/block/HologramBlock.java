package iskallia.vault.block;

import iskallia.vault.block.entity.HologramTileEntity;
import iskallia.vault.block.entity.hologram.RootHologramElement;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class HologramBlock extends Block implements EntityBlock {
   public static final EnumProperty<Direction> FACING = DirectionalBlock.FACING;

   public HologramBlock() {
      super(Properties.copy(Blocks.GLASS));
      this.defaultBlockState().setValue(FACING, Direction.SOUTH);
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING});
   }

   public RenderShape getRenderShape(BlockState state) {
      return RenderShape.INVISIBLE;
   }

   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return new HologramTileEntity(pos, state);
   }

   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
      return BlockHelper.getTicker(type, ModBlocks.HOLOGRAM_TILE_ENTITY, HologramTileEntity::tick);
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return (BlockState)this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection());
   }

   public void setPlacedBy(Level world, BlockPos pos, BlockState state, @Nullable LivingEntity placer, ItemStack stack) {
      super.setPlacedBy(world, pos, state, placer, stack);
      if (placer != null && world.getBlockEntity(pos) instanceof HologramTileEntity hologram) {
         hologram.ensureNonNullTree();
         hologram.getTree().iterate(RootHologramElement.class, root -> root.setEulerRotation(new Vec3(360.0F - placer.getXRot(), placer.getYRot(), 0.0)));
         hologram.setChanged();
         world.setBlock(pos, (BlockState)state.setValue(FACING, Direction.SOUTH), 3);
      }
   }

   public BlockState rotate(BlockState state, Rotation rotation) {
      return (BlockState)state.setValue(FACING, rotation.rotate((Direction)state.getValue(FACING)));
   }

   public BlockState mirror(BlockState state, Mirror mirror) {
      return state.rotate(mirror.getRotation((Direction)state.getValue(FACING)));
   }
}
