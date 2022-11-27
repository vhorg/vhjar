package iskallia.vault.client.particles;

import java.util.Random;
import javax.annotation.Nullable;
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
public class ScavengerAltarConsumeParticle extends TextureSheetParticle {
   protected float rotationDir;
   protected float fallingSpeed;
   protected double xdStart;
   protected double ydStart;
   protected double zdStart;
   protected double ydExtra;

   public ScavengerAltarConsumeParticle(ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ) {
      super(world, x, y, z);
      this.xd = motionX;
      this.yd = motionY;
      this.zd = motionZ;
      this.xdStart = motionX;
      this.ydStart = motionY;
      this.zdStart = motionZ;
      this.ydExtra = new Random().nextFloat() * (motionY / 10.0);
      this.rotationDir = new Random().nextFloat() - 0.5F;
      this.fallingSpeed = new Random().nextFloat();
      this.lifetime = 50 + (int)(new Random().nextFloat() * 50.0F);
      this.quadSize = 0.25F * (this.random.nextFloat() * 0.5F + 0.2F);
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
      this.xd = Math.min(1.0F, (float)(this.lifetime - this.age) / this.lifetime) * this.xdStart;
      this.yd = Math.min(1.0F, (float)this.age / this.lifetime) * this.ydStart + this.ydExtra;
      this.zd = Math.min(1.0F, (float)(this.lifetime - this.age) / this.lifetime) * this.zdStart;
      super.tick();
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet spriteSet;

      public Factory(SpriteSet sprite) {
         this.spriteSet = sprite;
      }

      @Nullable
      public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         Random rand = new Random();
         float colorOffset = rand.nextFloat() * 0.6F;
         ScavengerAltarConsumeParticle scavengerAltarConsumeParticle = new ScavengerAltarConsumeParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed);
         scavengerAltarConsumeParticle.pickSprite(this.spriteSet);
         scavengerAltarConsumeParticle.setColor(0.8F - colorOffset, 0.8F - colorOffset, 0.8F - colorOffset);
         return scavengerAltarConsumeParticle;
      }
   }
}
