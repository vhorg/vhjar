package iskallia.vault.block;

import iskallia.vault.block.entity.MonolithTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.BlockHelper;
import java.util.stream.Stream;
import net.minecraft.core.BlockPos;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
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
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class MonolithBlock extends Block implements EntityBlock {
   public static final EnumProperty<MonolithBlock.State> STATE = EnumProperty.create("state", MonolithBlock.State.class);
   private static final VoxelShape SHAPE = Stream.of(Block.box(0.0, 0.0, 0.0, 16.0, 8.0, 16.0), Block.box(2.0, 8.0, 2.0, 14.0, 10.0, 14.0))
      .reduce((v1, v2) -> Shapes.join(v1, v2, BooleanOp.OR))
      .get();

   public MonolithBlock() {
      super(
         Properties.of(Material.STONE)
            .sound(SoundType.METAL)
            .strength(-1.0F, 3600000.0F)
            .noDrops()
            .lightLevel(state -> state.getValue(STATE) == MonolithBlock.State.LIT ? 15 : 0)
      );
      this.registerDefaultState((BlockState)((BlockState)this.stateDefinition.any()).setValue(STATE, MonolithBlock.State.EXTINGUISHED));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{STATE});
   }

   public RenderShape getRenderShape(BlockState state) {
      return RenderShape.MODEL;
   }

   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return ModBlocks.MONOLITH_TILE_ENTITY.create(pos, state);
   }

   public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      return SHAPE;
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level world, BlockState state, BlockEntityType<T> type) {
      return BlockHelper.getTicker(type, ModBlocks.MONOLITH_TILE_ENTITY, MonolithTileEntity::tick);
   }

   public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
      return InteractionResult.PASS;
   }

   public static enum State implements StringRepresentable {
      EXTINGUISHED,
      LIT,
      DESTROYED;

      public String getSerializedName() {
         return this.name().toLowerCase();
      }
   }
}
