package iskallia.vault.block;

import iskallia.vault.container.TransmogTableContainer;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;

public class TransmogTableBlock extends Block {
   public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
   public static final VoxelShape SHAPE = Shapes.or(
      Block.box(0.0, 13.0, 0.0, 16.0, 16.0, 16.0),
      new VoxelShape[]{Block.box(4.0, 11.0, 4.0, 12.0, 13.0, 12.0), Block.box(5.0, 2.0, 5.0, 11.0, 11.0, 11.0), Block.box(3.0, 0.0, 3.0, 13.0, 2.0, 13.0)}
   );

   public TransmogTableBlock() {
      super(Properties.of(Material.STONE).requiresCorrectToolForDrops().strength(0.5F).lightLevel(state -> 1).noOcclusion());
   }

   public BlockState getStateForPlacement(BlockPlaceContext context) {
      return (BlockState)this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
   }

   public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      return SHAPE;
   }

   public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
      if (world.isClientSide) {
         return InteractionResult.SUCCESS;
      } else {
         NetworkHooks.openGui((ServerPlayer)player, new MenuProvider() {
            public Component getDisplayName() {
               return new TextComponent("Transmogrification Table");
            }

            @Nullable
            public AbstractContainerMenu createMenu(int windowId, Inventory inventory, Player playerx) {
               return new TransmogTableContainer(windowId, playerx);
            }
         });
         return InteractionResult.SUCCESS;
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
