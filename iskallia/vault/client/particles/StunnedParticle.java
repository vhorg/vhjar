package iskallia.vault.client.particles;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class StunnedParticle extends TextureSheetParticle {
   private final double xStart;
   private final double yStart;
   private final double zStart;
   private final double radius;
   private final double rotation;
   private final SpriteSet pSprites;

   StunnedParticle(
      ClientLevel p_106464_, double p_106465_, double p_106466_, double p_106467_, double p_106468_, double p_106469_, double p_106470_, SpriteSet pSprites
   ) {
      super(p_106464_, p_106465_, p_106466_, p_106467_);
      this.xd = 0.0;
      this.yd = 0.0;
      this.zd = 0.0;
      this.radius = p_106468_;
      this.rotation = this.random.nextFloat() * 360.0F;
      Vec3 offset = new Vec3(this.radius * Math.cos(this.rotation), 0.0, this.radius * Math.sin(this.rotation));
      this.xStart = p_106465_;
      this.yStart = p_106466_;
      this.zStart = p_106467_;
      this.xo = p_106465_ + offset.x();
      this.yo = p_106466_ + offset.y();
      this.zo = p_106467_ + offset.z();
      this.x = this.xo;
      this.y = this.yo;
      this.z = this.zo;
      this.quadSize = 0.2F * (this.random.nextFloat() * 0.5F + 0.2F);
      float f = this.random.nextFloat() * 0.6F + 0.4F;
      this.rCol = 0.9F * f;
      this.gCol = 0.9F * f;
      this.bCol = f;
      this.hasPhysics = false;
      this.lifetime = (int)(Math.random() * 10.0) + 10;
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
      return 15728880;
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         Vec3 offset = new Vec3(
            this.radius * Math.cos(this.rotation + this.age / 20.0F),
            Math.sin((this.rotation + this.age / 20.0F) * 5.0) / 10.0,
            this.radius * Math.sin(this.rotation + this.age / 20.0F)
         );
         this.x = this.xStart + offset.x();
         this.y = this.yStart + offset.y();
         this.z = this.zStart + offset.z();
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
         StunnedParticle scavengerAltarParticle = new StunnedParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, this.sprite);
         scavengerAltarParticle.pickSprite(this.sprite);
         return scavengerAltarParticle;
      }
   }
}
