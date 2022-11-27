package iskallia.vault.block.entity;

import iskallia.vault.block.ObeliskBlock;
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
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ObeliskTileEntity extends BlockEntity {
   private static final Random rand = new Random();

   public ObeliskTileEntity(BlockPos pos, BlockState state) {
      super(ModBlocks.OBELISK_TILE_ENTITY, pos, state);
   }

   public static void tick(Level level, BlockPos pos, BlockState state, ObeliskTileEntity tile) {
      if (!level.isClientSide()) {
         BlockState up = level.getBlockState(pos.above());
         if (!(up.getBlock() instanceof ObeliskBlock)) {
            level.setBlockAndUpdate(pos.above(), (BlockState)ModBlocks.OBELISK.defaultBlockState().setValue(ObeliskBlock.HALF, DoubleBlockHalf.UPPER));
         }
      } else {
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
            if ((Boolean)state.getValue(ObeliskBlock.FILLED)) {
               for (int count = 0; count < 3; count++) {
                  double x = pos.getX() - 0.25 + rand.nextFloat() * 1.5;
                  double y = pos.getY() + rand.nextFloat() * 3.0F;
                  double z = pos.getZ() - 0.25 + rand.nextFloat() * 1.5;
                  Particle fwParticle = mgr.createParticle(ParticleTypes.FIREWORK, x, y, z, 0.0, 0.0, 0.0);
                  if (fwParticle == null) {
                     return;
                  }

                  fwParticle.setColor(0.4F, 0.0F, 0.0F);
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

                  fwParticle.setLifetime((int)(fwParticle.getLifetime() * 1.5F));
               }
            }
         }
      }
   }
}
