package iskallia.vault.client.particles;

import java.util.Random;
import javax.annotation.Nonnull;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class DivineAltarConsumeParticle extends TextureSheetParticle {
   protected float rotationDir;
   protected float fallingSpeed;
   protected double xdStart;
   protected double ydStart;
   protected double zdStart;
   protected double ydExtra;

   public DivineAltarConsumeParticle(ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ) {
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

   public static class AltarProvider implements ParticleProvider<AltarParticleOptions> {
      private final SpriteSet sprites;

      public AltarProvider(SpriteSet pSprites) {
         this.sprites = pSprites;
      }

      @Nullable
      public Particle createParticle(
         AltarParticleOptions data, @Nonnull ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed
      ) {
         DivineAltarConsumeParticle particle = new DivineAltarConsumeParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
         particle.pickSprite(this.sprites);
         particle.setColor(data.color().x(), data.color().y(), data.color().z());
         return particle;
      }
   }
}
