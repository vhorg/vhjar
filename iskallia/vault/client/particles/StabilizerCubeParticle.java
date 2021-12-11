package iskallia.vault.client.particles;

import iskallia.vault.block.StabilizerBlock;
import iskallia.vault.block.entity.StabilizerTileEntity;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class StabilizerCubeParticle extends BaseFloatingCubeParticle {
   private final BlockPos originPos;

   private StabilizerCubeParticle(ClientWorld world, IAnimatedSprite spriteSet, double x, double y, double z) {
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
      BlockState at = this.field_187122_b.func_180495_p(this.originPos);
      if (!(at.func_177230_c() instanceof StabilizerBlock)) {
         return null;
      } else {
         TileEntity tile = this.field_187122_b.func_175625_s(this.originPos);
         return tile instanceof StabilizerTileEntity ? (StabilizerTileEntity)tile : null;
      }
   }

   @Override
   protected int getActiveColor() {
      return 301982;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite spriteSet;

      public Factory(IAnimatedSprite spriteSet) {
         this.spriteSet = spriteSet;
      }

      @Nullable
      public Particle makeParticle(BasicParticleType type, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new StabilizerCubeParticle(worldIn, this.spriteSet, x, y, z);
      }
   }
}
