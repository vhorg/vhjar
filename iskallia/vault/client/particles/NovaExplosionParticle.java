package iskallia.vault.client.particles;

import java.util.Random;
import javax.annotation.Nonnull;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class NovaExplosionParticle extends TextureSheetParticle {
   private final SpriteSet sprites;
   protected double xdStart;
   protected double ydStart;
   protected double zdStart;
   protected double ydExtra;

   protected NovaExplosionParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet pSprites) {
      super(pLevel, pX, pY, pZ, 0.0, 0.0, 0.0);
      this.lifetime = 10 + this.random.nextInt(6);
      float f = this.random.nextFloat() * 0.6F + 0.4F;
      this.rCol = f;
      this.gCol = f;
      this.bCol = f;
      float rand = this.random.nextFloat();
      this.xdStart = pXSpeed * (0.9F + 1.1F * rand);
      this.ydStart = pYSpeed * (0.9F + 1.1F * rand);
      this.zdStart = pZSpeed * (0.9F + 1.1F * rand);
      this.quadSize = 1.3125F;
      this.sprites = pSprites;
      this.setSpriteFromAge(pSprites);
   }

   public void tick() {
      this.xd = Math.min(1.0F, (float)(this.lifetime - this.age) / this.lifetime) * this.xdStart;
      this.yd = Math.min(1.0F, (float)this.age / this.lifetime) * this.ydStart + this.ydExtra;
      this.zd = Math.min(1.0F, (float)(this.lifetime - this.age) / this.lifetime) * this.zdStart;
      this.alpha = Mth.clamp(0.85F * ((float)(this.lifetime - this.age) / this.lifetime), 0.0F, 1.0F);
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.setSpriteFromAge(this.sprites);
      }

      super.tick();
   }

   @Nonnull
   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public Provider(SpriteSet pSprites) {
         this.sprites = pSprites;
      }

      public Particle createParticle(
         SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed
      ) {
         Particle particle = new NovaExplosionParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, this.sprites);
         Random rand = new Random();
         float offset = rand.nextFloat();
         return particle;
      }
   }
}
