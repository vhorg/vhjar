package iskallia.vault.block;

import iskallia.vault.init.ModBlocks;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.BlockParticleOption;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.BarrierBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.BlockBehaviour.Properties;
import net.minecraft.world.level.material.Material;
import net.minecraft.world.level.pathfinder.PathComputationType;
import org.jetbrains.annotations.Nullable;

public class FloatingTextBlock extends BarrierBlock implements EntityBlock {
   public FloatingTextBlock() {
      super(Properties.of(Material.BARRIER).strength(-1.0F, 3.6E8F).noDrops().noOcclusion().noCollission());
   }

   public boolean isPossibleToRespawnInThis() {
      return true;
   }

   public RenderShape getRenderShape(BlockState state) {
      return RenderShape.INVISIBLE;
   }

   public boolean isPathfindable(BlockState state, BlockGetter worldIn, BlockPos pos, PathComputationType type) {
      return true;
   }

   public void animateTick(BlockState stateIn, Level worldIn, BlockPos pos, Random rand) {
      Minecraft minecraft = Minecraft.getInstance();
      LocalPlayer player = minecraft.player;
      ClientLevel world = minecraft.level;
      if (player != null && world != null && player.getMainHandItem().getItem() == ModBlocks.FLOATING_TEXT.asItem()) {
         int i = pos.getX();
         int j = pos.getY();
         int k = pos.getZ();
         world.addParticle(new BlockParticleOption(ParticleTypes.BLOCK_MARKER, Blocks.BARRIER.defaultBlockState()), i + 0.5, j + 0.5, k + 0.5, 0.0, 0.0, 0.0);
      }
   }

   @Nullable
   public BlockEntity newBlockEntity(BlockPos pPos, BlockState pState) {
      return ModBlocks.FLOATING_TEXT_TILE_ENTITY.create(pPos, pState);
   }
}
