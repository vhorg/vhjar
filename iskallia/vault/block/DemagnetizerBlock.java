package iskallia.vault.block;

import iskallia.vault.block.entity.DemagnetizerTileEntity;
import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.StateDefinition.Builder;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import org.jetbrains.annotations.Nullable;

public class DemagnetizerBlock extends Block implements EntityBlock {
   public static final BooleanProperty DEACTIVATED = BooleanProperty.create("deactivated");

   public DemagnetizerBlock() {
      super(Properties.of(Material.STONE, MaterialColor.STONE).noOcclusion().strength(2.0F, 10.0F));
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
      return new DemagnetizerTileEntity(pPos, pState);
   }

   public boolean canConnectRedstone(BlockState state, BlockGetter level, BlockPos pos, @Nullable Direction direction) {
      return super.canConnectRedstone(state, level, pos, direction);
   }

   @Nullable
   public BlockState getStateForPlacement(BlockPlaceContext pContext) {
      return (BlockState)super.defaultBlockState().setValue(DEACTIVATED, pContext.getLevel().hasNeighborSignal(pContext.getClickedPos()));
   }

   public void neighborChanged(BlockState pState, Level pLevel, BlockPos pPos, Block pBlock, BlockPos pFromPos, boolean pIsMoving) {
      if (!pLevel.isClientSide) {
         if (pState.hasProperty(DEACTIVATED)) {
            boolean flag = (Boolean)pState.getValue(DEACTIVATED);
            if (flag != pLevel.hasNeighborSignal(pPos)) {
               if (flag) {
                  pLevel.scheduleTick(pPos, this, 1);
               } else {
                  pLevel.setBlock(pPos, (BlockState)pState.cycle(DEACTIVATED), 2);
               }
            }
         } else {
            pLevel.setBlock(pPos, (BlockState)pState.setValue(DEACTIVATED, pLevel.hasNeighborSignal(pPos)), 2);
         }
      }
   }

   protected void createBlockStateDefinition(Builder<Block, BlockState> pBuilder) {
      pBuilder.add(new Property[]{DEACTIVATED});
   }

   public void tick(BlockState pState, ServerLevel pLevel, BlockPos pPos, Random pRand) {
      if (pState.hasProperty(DEACTIVATED) && (Boolean)pState.getValue(DEACTIVATED) && !pLevel.hasNeighborSignal(pPos)) {
         pLevel.setBlock(pPos, (BlockState)pState.cycle(DEACTIVATED), 2);
      }
   }

   public boolean isSignalSource(BlockState pState) {
      return super.isSignalSource(pState);
   }
}
