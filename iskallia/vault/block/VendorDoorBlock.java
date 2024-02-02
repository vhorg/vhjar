package iskallia.vault.block;

import iskallia.vault.block.entity.VendorDoorTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.BlockHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DoorBlock;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.block.state.properties.DoorHingeSide;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.material.MaterialColor;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public class VendorDoorBlock extends DoorBlock implements EntityBlock {
   public VendorDoorBlock() {
      super(Properties.of(Material.METAL, MaterialColor.DIAMOND).strength(-1.0F, 3600000.0F).sound(SoundType.METAL).noOcclusion());
      this.registerDefaultState(
         (BlockState)((BlockState)((BlockState)((BlockState)((BlockState)((BlockState)this.getStateDefinition().any()).setValue(FACING, Direction.NORTH))
                     .setValue(OPEN, Boolean.FALSE))
                  .setValue(HINGE, DoorHingeSide.LEFT))
               .setValue(POWERED, Boolean.FALSE))
            .setValue(HALF, DoubleBlockHalf.LOWER)
      );
   }

   public PushReaction getPistonPushReaction(BlockState state) {
      return PushReaction.BLOCK;
   }

   public void neighborChanged(BlockState state, Level worldIn, BlockPos pos, Block blockIn, BlockPos fromPos, boolean isMoving) {
   }

   public boolean canSurvive(BlockState pState, LevelReader pLevel, BlockPos pPos) {
      return true;
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
      return ModBlocks.VENDOR_DOOR_TILE_ENTITY.create(pPos, pState);
   }

   @Nullable
   public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level pLevel, BlockState pState, BlockEntityType<T> pBlockEntityType) {
      return BlockHelper.getTicker(pBlockEntityType, ModBlocks.VENDOR_DOOR_TILE_ENTITY, VendorDoorTileEntity::tick);
   }

   public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hit) {
      Boolean isOpen = (Boolean)state.getValue(OPEN);
      if (!isOpen) {
         this.setOpen(player, world, state, pos, true);
         return InteractionResult.SUCCESS;
      } else {
         return InteractionResult.SUCCESS;
      }
   }
}
