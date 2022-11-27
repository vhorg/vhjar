package iskallia.vault.block;

import iskallia.vault.container.KeyPressContainer;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Axis;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.FallingBlockEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.FallingBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;

public class KeyPressBlock extends FallingBlock {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   private static final VoxelShape PART_BASE = Block.box(2.0, 0.0, 2.0, 14.0, 4.0, 14.0);
   private static final VoxelShape PART_LOWER_X = Block.box(3.0, 4.0, 4.0, 13.0, 5.0, 12.0);
   private static final VoxelShape PART_MID_X = Block.box(4.0, 5.0, 6.0, 12.0, 10.0, 10.0);
   private static final VoxelShape PART_UPPER_X = Block.box(0.0, 10.0, 3.0, 16.0, 16.0, 13.0);
   private static final VoxelShape PART_LOWER_Z = Block.box(4.0, 4.0, 3.0, 12.0, 5.0, 13.0);
   private static final VoxelShape PART_MID_Z = Block.box(6.0, 5.0, 4.0, 10.0, 10.0, 12.0);
   private static final VoxelShape PART_UPPER_Z = Block.box(3.0, 10.0, 0.0, 13.0, 16.0, 16.0);
   private static final VoxelShape X_AXIS_AABB = Shapes.or(PART_BASE, new VoxelShape[]{PART_LOWER_X, PART_MID_X, PART_UPPER_X});
   private static final VoxelShape Z_AXIS_AABB = Shapes.or(PART_BASE, new VoxelShape[]{PART_LOWER_Z, PART_MID_Z, PART_UPPER_Z});

   public KeyPressBlock() {
      super(Properties.of(Material.HEAVY_METAL, MaterialColor.METAL).sound(SoundType.ANVIL).strength(2.0F, 3600000.0F));
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return (BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getClockWise());
   }

   public VoxelShape getShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
      Direction direction = (Direction)state.getValue(FACING);
      return direction.getAxis() == Axis.X ? X_AXIS_AABB : Z_AXIS_AABB;
   }

   public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
      if (world.isClientSide) {
         return InteractionResult.SUCCESS;
      } else {
         NetworkHooks.openGui((ServerPlayer)player, new MenuProvider() {
            public Component getDisplayName() {
               return new TextComponent("Key Press");
            }

            @Nullable
            public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player playerx) {
               return new KeyPressContainer(windowId, playerx);
            }
         });
         return InteractionResult.SUCCESS;
      }
   }

   protected void falling(FallingBlockEntity fallingEntity) {
      fallingEntity.setHurtsEntities(2.0F, 40);
   }

   public void onLand(Level worldIn, BlockPos pos, BlockState fallingState, BlockState hitState, FallingBlockEntity fallingBlock) {
      if (!fallingBlock.isSilent()) {
         worldIn.levelEvent(1031, pos, 0);
      }
   }

   public void onBroken(Level worldIn, BlockPos pos, FallingBlockEntity fallingBlock) {
      if (!fallingBlock.isSilent()) {
         worldIn.levelEvent(1029, pos, 0);
      }
   }

   public BlockState rotate(BlockState state, Rotation rot) {
      return (BlockState)state.setValue(FACING, rot.rotate((Direction)state.getValue(FACING)));
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> builder) {
      builder.add(new Property[]{FACING});
   }

   public boolean isPathfindable(BlockState state, BlockGetter worldIn, BlockPos pos, PathComputationType type) {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public int getDustColor(BlockState state, BlockGetter reader, BlockPos pos) {
      return state.getMapColor(reader, pos).col;
   }
}
