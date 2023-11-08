package iskallia.vault.client.particles;

import com.mojang.blaze3d.vertex.VertexConsumer;
import iskallia.vault.init.ModParticles;
import javax.annotation.Nonnull;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class ArtifactBossImmunityParticle extends TextureSheetParticle {
   private final double xStart;
   private final double yStart;
   private final double zStart;
   private double rotation;
   private final SpriteSet pSprites;
   protected float rCol2 = 1.0F;
   protected float gCol2 = 1.0F;
   protected float bCol2 = 1.0F;

   ArtifactBossImmunityParticle(
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
      this.quadSize = 0.4F * (this.random.nextFloat() * 0.5F + 0.2F);
      float f = this.random.nextFloat() * 0.6F + 0.4F;
      this.rCol = 0.9F * f;
      this.gCol = 0.9F * f;
      this.bCol = f;
      this.hasPhysics = false;
      this.lifetime = 20;
      this.pSprites = pSprites;
      this.x = this.xStart;
      this.y = this.yStart;
      this.z = this.zStart;
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      Vec3 vector1 = new Vec3(0.0, 1.0, 0.0);
      Vec3 vector2 = new Vec3(this.xd, this.yd, this.zd);
      this.rotation = Math.atan2(vector2.z, vector2.x) - Math.atan2(vector1.z, vector1.x);
   }

   public void setColor2(float pParticleRed, float pParticleGreen, float pParticleBlue) {
      this.rCol2 = pParticleRed;
      this.gCol2 = pParticleGreen;
      this.bCol2 = pParticleBlue;
   }

   public void render(VertexConsumer pBuffer, Camera pRenderInfo, float pPartialTicks) {
      if (this.age > 0) {
         super.render(pBuffer, pRenderInfo, pPartialTicks);
      }
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
         this.x = this.xStart + this.xd * Math.sin(f * Math.PI);
         this.y = this.yStart + this.yd * (f1 * 1.2F);
         this.z = this.zStart + this.zd * Math.sin(f * Math.PI);
         double x1 = this.x - this.xStart;
         double z1 = this.z - this.zStart;
         double x2 = x1 * Math.cos(Math.toRadians(this.rotation)) - z1 * Math.sin(Math.toRadians(this.rotation));
         double z2 = x1 * Math.sin(Math.toRadians(this.rotation)) + z1 * Math.cos(Math.toRadians(this.rotation));
         this.x = x2 + this.xStart;
         this.z = z2 + this.zStart;
         this.setSpriteFromAge(this.pSprites);
         float f2 = (float)(this.age + 1) / this.lifetime;
         if (f2 <= 1.0F) {
            f2 = 1.0F - f2;
            float f3 = 1.0F - f2;
            f3 *= f3;
            double x3 = this.xStart + this.xd * Math.sin(f2 * Math.PI);
            double y3 = this.yStart + this.yd * (f3 * 1.2F);
            double z3 = this.zStart + this.zd * Math.sin(f2 * Math.PI);
            double x4 = (x3 - this.xStart) * Math.cos(Math.toRadians(this.rotation)) - (z3 - this.zStart) * Math.sin(Math.toRadians(this.rotation));
            double z4 = (x3 - this.xStart) * Math.sin(Math.toRadians(this.rotation)) + (z3 - this.zStart) * Math.cos(Math.toRadians(this.rotation));
            x3 = x4 + this.xStart;
            z3 = z4 + this.zStart;
            Vec3 vec3 = new Vec3(x3 - this.x, y3 - this.y, z3 - this.z).scale(0.15F);
            Particle particle = Minecraft.getInstance()
               .particleEngine
               .createParticle((ParticleOptions)ModParticles.ENDER_ANCHOR.get(), this.x, this.y, this.z, vec3.x, vec3.y, vec3.z);
            if (particle != null) {
               particle.setColor(Math.min(1.0F, this.rCol2), Math.min(1.0F, this.gCol2), Math.min(1.0F, this.bCol2));
            }
         }
      }
   }

   public static class AltarProvider implements ParticleProvider<ArtifactBossImmunityParticleOptions> {
      private final SpriteSet sprites;

      public AltarProvider(SpriteSet pSprites) {
         this.sprites = pSprites;
      }

      @Nullable
      public Particle createParticle(
         ArtifactBossImmunityParticleOptions data, @Nonnull ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed
      ) {
         ArtifactBossImmunityParticle particle = new ArtifactBossImmunityParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, this.sprites);
         particle.pickSprite(this.sprites);
         particle.setColor(data.color().x(), data.color().y(), data.color().z());
         particle.setColor2(data.color2().x(), data.color2().y(), data.color2().z());
         particle.setLifetime(data.lifetime());
         return particle;
      }
   }
}
