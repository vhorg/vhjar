package iskallia.vault.block.entity;

import iskallia.vault.block.CrakePedestalBlock;
import iskallia.vault.init.ModBlocks;
import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleEngine;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class CrakePedestalTileEntity extends BlockEntity {
   private static final Random rand = new Random();

   public CrakePedestalTileEntity(BlockPos pos, BlockState blockState) {
      super(ModBlocks.CRAKE_PEDESTAL_TILE_ENTITY, pos, blockState);
   }

   public static void tick(Level level, BlockPos pos, BlockState state, CrakePedestalTileEntity tile) {
      if (level.isClientSide()) {
         tile.playEffects();
      }
   }

   @OnlyIn(Dist.CLIENT)
   private void playEffects() {
      if (this.getLevel() != null) {
         BlockPos pos = this.getBlockPos();
         BlockState state = this.getBlockState();
         if (this.getLevel().getGameTime() % 5L == 0L) {
            ParticleEngine mgr = Minecraft.getInstance().particleEngine;
            if ((Boolean)state.getValue(CrakePedestalBlock.CONSUMED)) {
               for (int count = 0; count < 3; count++) {
                  double x = pos.getX() - 0.25 + rand.nextFloat() * 1.5;
                  double y = pos.getY() + rand.nextFloat() * 3.0F;
                  double z = pos.getZ() - 0.25 + rand.nextFloat() * 1.5;
                  Particle fwParticle = mgr.createParticle(ParticleTypes.FIREWORK, x, y, z, 0.0, 0.0, 0.0);
                  if (fwParticle == null) {
                     return;
                  }

                  fwParticle.setColor(0.96862745F, 0.49803922F, 0.7137255F);
               }
            } else {
               for (int count = 0; count < 5; count++) {
                  double x = pos.getX() + rand.nextFloat();
                  double y = pos.getY() + rand.nextFloat() * 10.0F;
                  double z = pos.getZ() + rand.nextFloat();
                  Particle fwParticle = mgr.createParticle(ParticleTypes.FIREWORK, x, y, z, 0.0, 0.0, 0.0);
                  if (fwParticle == null) {
                     return;
                  }

                  fwParticle.setColor(0.96862745F, 0.49803922F, 0.7137255F);
                  fwParticle.setLifetime((int)(fwParticle.getLifetime() * 1.5F));
               }
            }
         }
      }
   }
}
