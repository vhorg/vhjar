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
public class ScavengerAltarParticle extends TextureSheetParticle {
   private final double xStart;
   private final double yStart;
   private final double zStart;
   private final SpriteSet pSprites;

   ScavengerAltarParticle(
      ClientLevel p_106464_, double p_106465_, double p_106466_, double p_106467_, double p_106468_, double p_106469_, double p_106470_, SpriteSet pSprites
   ) {
      super(p_106464_, p_106465_, p_106466_, p_106467_);
      this.xd = p_106468_;
      this.yd = p_106469_;
      this.zd = p_106470_;
      this.xStart = p_106465_;
      this.yStart = p_106466_;
      this.zStart = p_106467_;
      this.xo = p_106465_ + p_106468_;
      this.yo = p_106466_ + p_106469_;
      this.zo = p_106467_ + p_106470_;
      this.x = this.xo;
      this.y = this.yo;
      this.z = this.zo;
      this.quadSize = 0.1F * (this.random.nextFloat() * 0.5F + 0.2F);
      float f = this.random.nextFloat() * 0.6F + 0.4F;
      this.rCol = 0.9F * f;
      this.gCol = 0.9F * f;
      this.bCol = f;
      this.hasPhysics = false;
      this.lifetime = (int)(Math.random() * 10.0) + 30;
      this.pSprites = pSprites;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public void move(double pX, double pY, double pZ) {
      this.setBoundingBox(this.getBoundingBox().move(pX, pY, pZ));
      this.setLocationFromBoundingbox();
   }

   public int getLightColor(float pPartialTick) {
      int i = super.getLightColor(pPartialTick);
      float f = (float)this.age / this.lifetime;
      f *= f;
      f *= f;
      int j = i & 0xFF;
      int k = i >> 16 & 0xFF;
      k += (int)(f * 15.0F * 16.0F);
      if (k > 240) {
         k = 240;
      }

      return j | k << 16;
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         float f = (float)this.age / this.lifetime;
         f = 1.0F - f;
         float f1 = 1.0F - f;
         f1 *= f1;
         f1 *= f1;
         this.x = this.xStart + this.xd * f;
         this.y = this.yStart + this.yd * f - f1 * 1.2F;
         this.z = this.zStart + this.zd * f;
         double x1 = this.x - this.xStart;
         double z1 = this.z - this.zStart;
         double x2 = x1 * Math.cos(Math.toRadians(this.age * 3)) - z1 * Math.sin(Math.toRadians(this.age * 3));
         double z2 = x1 * Math.sin(Math.toRadians(this.age * 3)) + z1 * Math.cos(Math.toRadians(this.age * 3));
         this.x = x2 + this.xStart;
         this.z = z2 + this.zStart;
         this.setSpriteFromAge(this.pSprites);
      }
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
         ScavengerAltarParticle scavengerAltarParticle = new ScavengerAltarParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, this.sprite);
         scavengerAltarParticle.pickSprite(this.sprite);
         return scavengerAltarParticle;
      }
   }
}
