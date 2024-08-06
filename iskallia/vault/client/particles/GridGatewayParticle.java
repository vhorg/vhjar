package iskallia.vault.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GridGatewayParticle extends TextureSheetParticle {
   private final SpriteSet spriteSet;

   public GridGatewayParticle(ClientLevel level, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed, SpriteSet spriteSet) {
      super(level, x, y, z, xSpeed, ySpeed, zSpeed);
      this.hasPhysics = false;
      this.gravity = 0.0F;
      this.lifetime = level.random.nextInt(10) + 10;
      this.quadSize = 0.1F * (level.random.nextFloat() * 0.5F + 0.2F);
      this.spriteSet = spriteSet;
      this.xd = xSpeed;
      this.yd = ySpeed;
      this.zd = zSpeed;
   }

   public void setColor(int color) {
      this.rCol = (color >> 16 & 0xFF) / 255.0F;
      this.gCol = (color >> 8 & 0xFF) / 255.0F;
      this.bCol = (color & 0xFF) / 255.0F;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public void tick() {
      super.tick();
      this.setSpriteFromAge(this.spriteSet);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprite;

      public Provider(SpriteSet pSprites) {
         this.sprite = pSprites;
      }

      public Particle createParticle(
         SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed
      ) {
         GridGatewayParticle ascensionForgeParticle = new GridGatewayParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, this.sprite);
         ascensionForgeParticle.pickSprite(this.sprite);
         return ascensionForgeParticle;
      }
   }
}
