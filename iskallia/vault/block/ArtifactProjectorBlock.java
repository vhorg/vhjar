package iskallia.vault.block;

import iskallia.vault.block.base.FacedBlock;
import iskallia.vault.block.entity.ArtifactProjectorTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.util.BlockHelper;
import iskallia.vault.world.data.PlayerGreedData;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.LecternBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jetbrains.annotations.Nullable;

public class ArtifactProjectorBlock extends FacedBlock implements EntityBlock {
   public ArtifactProjectorBlock() {
      super(Properties.copy(Blocks.IRON_BLOCK));
   }

   public InteractionResult use(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer, InteractionHand pHand, BlockHitResult pHit) {
      if (pLevel.getBlockEntity(pPos) instanceof ArtifactProjectorTileEntity artifactProjectorTileEntity && !artifactProjectorTileEntity.consuming) {
         if (pLevel instanceof ServerLevel sWorld) {
            artifactProjectorTileEntity.consume(pPlayer);
         }

         return InteractionResult.SUCCESS;
      } else {
         return super.use(pState, pLevel, pPos, pPlayer, pHand, pHit);
      }
   }

   @Nonnull
   @ParametersAreNonnullByDefault
   public VoxelShape getOcclusionShape(BlockState state, BlockGetter world, BlockPos pos) {
      return LecternBlock.SHAPE_COMMON;
   }

   @Nonnull
   @ParametersAreNonnullByDefault
   public VoxelShape getCollisionShape(BlockState state, BlockGetter worldIn, BlockPos pos, CollisionContext context) {
      return LecternBlock.SHAPE_COLLISION;
   }

   @Nonnull
   @ParametersAreNonnullByDefault
   public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context) {
      return switch ((Direction)state.getValue(FACING)) {
         case SOUTH -> LecternBlock.SHAPE_NORTH;
         case NORTH -> LecternBlock.SHAPE_SOUTH;
         case WEST -> LecternBlock.SHAPE_EAST;
         case EAST -> LecternBlock.SHAPE_WEST;
         default -> LecternBlock.SHAPE_COMMON;
      };
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
      return ModBlocks.ARTIFACT_PROJECTOR_ENTITY.create(pos, state);
   }

   @Nullable
   public <A extends BlockEntity> BlockEntityTicker<A> getTicker(Level level, BlockState state, BlockEntityType<A> blockEntityType) {
      return level.isClientSide()
         ? BlockHelper.getTicker(blockEntityType, ModBlocks.ARTIFACT_PROJECTOR_ENTITY, ArtifactProjectorTileEntity::tickClient)
         : BlockHelper.getTicker(blockEntityType, ModBlocks.ARTIFACT_PROJECTOR_ENTITY, ArtifactProjectorTileEntity::tickServer);
   }

   public void setPlacedBy(Level pLevel, BlockPos pPos, BlockState pState, @Nullable LivingEntity pPlacer, ItemStack pStack) {
      if (!pLevel.isClientSide) {
         ArtifactProjectorTileEntity projectorTile = (ArtifactProjectorTileEntity)pLevel.getBlockEntity(pPos);
         if (projectorTile != null && pPlacer instanceof Player) {
            projectorTile.setOwner(pPlacer.getUUID());
            if (PlayerGreedData.get().get(pPlacer.getUUID()).hasCompletedArtifacts()) {
               projectorTile.completed = true;
            }

            super.setPlacedBy(pLevel, pPos, pState, pPlacer, pStack);
         }
      }
   }
}
