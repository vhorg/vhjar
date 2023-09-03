package iskallia.vault.client.particles;

import com.mojang.blaze3d.vertex.VertexConsumer;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.client.Camera;
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
public class AlchemyTableParticle extends TextureSheetParticle {
   private final double xStart;
   private final double yStart;
   private final double zStart;
   private final double xEnd;
   private final double yEnd;
   private final double zEnd;
   protected float rColStart = 1.0F;
   protected float gColStart = 1.0F;
   protected float bColStart = 1.0F;
   protected float rColEnd = 1.0F;
   protected float gColEnd = 1.0F;
   protected float bColEnd = 1.0F;
   private final SpriteSet pSprites;

   AlchemyTableParticle(
      ClientLevel p_106464_, double p_106465_, double p_106466_, double p_106467_, double p_106468_, double p_106469_, double p_106470_, SpriteSet pSprites
   ) {
      super(p_106464_, p_106465_, p_106466_, p_106467_);
      this.xEnd = p_106468_;
      this.yEnd = p_106469_;
      this.zEnd = p_106470_;
      this.xStart = p_106465_;
      this.yStart = p_106466_ - 0.2F;
      this.zStart = p_106467_;
      this.xo = p_106465_ + p_106468_;
      this.yo = p_106466_ + p_106469_;
      this.zo = p_106467_ + p_106470_;
      this.x = this.xo;
      this.y = this.yo;
      this.z = this.zo;
      this.quadSize = this.random.nextFloat() * 0.015F + 0.05F;
      float f = this.random.nextFloat() * 0.1F + 0.9F;
      this.rCol = 0.9F * f;
      this.gCol = 0.9F * f;
      this.bCol = 0.9F * f;
      this.rColStart = this.rCol;
      this.gColStart = this.gCol;
      this.bColStart = this.bCol;
      this.rColEnd = this.rCol;
      this.gColEnd = this.gCol;
      this.bColEnd = this.bCol;
      this.hasPhysics = false;
      this.lifetime = 20;
      this.pSprites = pSprites;
   }

   public void setColorEnd(float pParticleRed, float pParticleGreen, float pParticleBlue) {
      this.rColEnd = pParticleRed;
      this.gColEnd = pParticleGreen;
      this.bColEnd = pParticleBlue;
   }

   public void setColorStart(float pParticleRed, float pParticleGreen, float pParticleBlue) {
      this.setColor(pParticleRed, pParticleGreen, pParticleBlue);
      this.rColStart = this.rCol;
      this.gColStart = this.gCol;
      this.bColStart = this.bCol;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   public void move(double pX, double pY, double pZ) {
      this.setBoundingBox(this.getBoundingBox().move(pX, pY, pZ));
      this.setLocationFromBoundingbox();
   }

   public void render(VertexConsumer pBuffer, Camera pRenderInfo, float pPartialTicks) {
      float f = (float)this.age / this.lifetime;
      this.alpha = (float)(this.lifetime - this.age) / this.lifetime * 0.25F + 0.25F;
      this.rCol = this.rColStart + (this.rColEnd - this.rColStart) * Math.min(1.0F, f * 1.5F);
      this.gCol = this.gColStart + (this.gColEnd - this.gColStart) * Math.min(1.0F, f * 1.5F);
      this.bCol = this.bColStart + (this.bColEnd - this.bColStart) * Math.min(1.0F, f * 1.5F);
      super.render(pBuffer, pRenderInfo, pPartialTicks);
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         float f = (float)this.age / this.lifetime;
         float f2 = ((float)this.lifetime - this.age) / this.lifetime;
         float f1 = (float)Math.sin(f2 * Math.PI);
         this.x = this.xStart + (this.xEnd - this.xStart) * (f * 0.8F);
         this.y = this.yStart + (this.yEnd - this.yStart) * f + (f == 1.0F ? f1 * 0.25F : f1 * f1 * 0.25F);
         this.z = this.zStart + (this.zEnd - this.zStart) * (f * 0.8F);
         double x1 = this.x - this.xEnd;
         double z1 = this.z - this.zEnd;
         double toRadians = Math.toRadians(f * f * f * f * 580.0F);
         double cos = Math.cos(toRadians);
         double sin = Math.sin(toRadians);
         double x2 = x1 * cos - z1 * sin;
         double z2 = x1 * sin + z1 * cos;
         this.x = x2 + this.xEnd;
         this.z = z2 + this.zEnd;
         this.setSpriteFromAge(this.pSprites);
      }
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet spriteSet;

      public Provider(SpriteSet sprite) {
         this.spriteSet = sprite;
      }

      @Nullable
      public Particle createParticle(SimpleParticleType typeIn, ClientLevel worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         Random rand = new Random();
         float colorOffset = rand.nextFloat() * 0.6F;
         AlchemyTableParticle luckyHitDrainParticle = new AlchemyTableParticle(worldIn, x, y, z, xSpeed, ySpeed, zSpeed, this.spriteSet);
         luckyHitDrainParticle.pickSprite(this.spriteSet);
         luckyHitDrainParticle.setColor(0.8F - colorOffset, 0.8F - colorOffset, 0.8F - colorOffset);
         luckyHitDrainParticle.setSprite(this.spriteSet.get(rand));
         return luckyHitDrainParticle;
      }
   }
}
