package iskallia.vault.client.particles;

import iskallia.vault.block.StabilizerBlock;
import iskallia.vault.block.entity.StabilizerTileEntity;
import javax.annotation.Nullable;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class StabilizerCubeParticle extends BaseFloatingCubeParticle {
   private final BlockPos originPos;

   private StabilizerCubeParticle(ClientLevel world, SpriteSet spriteSet, double x, double y, double z) {
      super(world, spriteSet, x, y, z);
      this.originPos = new BlockPos(x, y, z);
   }

   @Override
   protected boolean isValid() {
      return this.getTileRef() != null;
   }

   @Override
   protected boolean isActive() {
      StabilizerTileEntity tile = this.getTileRef();
      return tile != null && tile.isActive();
   }

   @Nullable
   private StabilizerTileEntity getTileRef() {
      BlockState at = this.level.getBlockState(this.originPos);
      if (!(at.getBlock() instanceof StabilizerBlock)) {
         return null;
      } else {
         BlockEntity tile = this.level.getBlockEntity(this.originPos);
         return tile instanceof StabilizerTileEntity ? (StabilizerTileEntity)tile : null;
      }
   }

   @Override
   protected int getActiveColor() {
      return 301982;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet spriteSet;

      public Factory(SpriteSet spriteSet) {
         this.spriteSet = spriteSet;
      }

      @Nullable
      public Particle createParticle(SimpleParticleType type, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new StabilizerCubeParticle(worldIn, this.spriteSet, x, y, z);
      }
   }
}
