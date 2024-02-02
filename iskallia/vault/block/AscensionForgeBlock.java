package iskallia.vault.block;

import iskallia.vault.block.entity.AscensionForgeTileEntity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModParticles;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.network.NetworkHooks;

public class AscensionForgeBlock extends Block implements EntityBlock {
   public static final VoxelShape SHAPE = Block.box(1.0, 0.0, 1.0, 15.0, 15.0, 15.0);

   public AscensionForgeBlock() {
      super(Properties.of(Material.STONE).strength(0.5F).lightLevel(state -> 7).noOcclusion());
   }

   public VoxelShape getShape(BlockState pState, BlockGetter pLevel, BlockPos pPos, CollisionContext pContext) {
      return SHAPE;
   }

   public InteractionResult use(BlockState state, Level world, BlockPos pos, Player player, InteractionHand handIn, BlockHitResult hit) {
      if (world.isClientSide()) {
         return InteractionResult.SUCCESS;
      } else if (player instanceof ServerPlayer sPlayer) {
         if (world.getBlockEntity(pos) instanceof AscensionForgeTileEntity ascensionForgeTileEntity) {
            NetworkHooks.openGui(sPlayer, ascensionForgeTileEntity, buffer -> buffer.writeBlockPos(pos));
            return InteractionResult.SUCCESS;
         } else {
            return InteractionResult.SUCCESS;
         }
      } else {
         return InteractionResult.SUCCESS;
      }
   }

   public boolean isPathfindable(BlockState state, BlockGetter worldIn, BlockPos pos, PathComputationType type) {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public int getDustColor(BlockState state, BlockGetter reader, BlockPos pos) {
      return state.getMapColor(reader, pos).col;
   }

   @Nullable
   public BlockEntity newBlockEntity(@Nonnull BlockPos pos, @Nonnull BlockState state) {
      return ModBlocks.ASCENSION_FORGE_TILE_ENTITY.create(pos, state);
   }

   @OnlyIn(Dist.CLIENT)
   public void animateTick(BlockState state, Level level, BlockPos pos, Random random) {
      if (random.nextInt(3) == 0) {
         Vec3 center = Vec3.atCenterOf(pos).add(0.0, 0.4375, 0.0);

         for (int i = 0; i < 1 + level.random.nextInt(5); i++) {
            float distRandom = random.nextFloat();
            float dist = 0.0625F + 0.25F * distRandom;
            float angle = random.nextFloat() * (float) Math.PI * 2.0F;
            Vec3 offset = new Vec3(Math.cos(angle) * dist, 0.0, Math.sin(angle) * dist);
            Vec3 pos1 = center.add(offset);
            float speed = 0.03F;
            double particleAngle = 1.413716694115407 - distRandom * Math.PI / 4.0;
            Vec3 speedVector = new Vec3(
               Math.cos(particleAngle) * Math.cos(angle) * speed, Math.sin(particleAngle) * speed, Math.cos(particleAngle) * Math.sin(angle) * speed
            );
            level.addParticle((ParticleOptions)ModParticles.ASCENSION_FORGE.get(), pos1.x, pos1.y, pos1.z, speedVector.x, speedVector.y, speedVector.z);
         }
      }
   }

   public void onRemove(BlockState pState, Level pLevel, BlockPos pPos, BlockState pNewState, boolean pIsMoving) {
      if (!pState.is(pNewState.getBlock())) {
         if (pLevel.getBlockEntity(pPos) instanceof AscensionForgeTileEntity ascensionForgeTileEntity) {
            Containers.dropContents(pLevel, pPos, ascensionForgeTileEntity.getInternalInventory());
            pLevel.updateNeighbourForOutputSignal(pPos, this);
         }

         super.onRemove(pState, pLevel, pPos, pNewState, pIsMoving);
      }
   }
}
