package iskallia.vault.client.particles;

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

public class StonefallFrostWaveParticle extends TextureSheetParticle {
   private final SpriteSet sprites;
   protected double xdStart;
   protected double ydStart;
   protected double zdStart;
   protected double ydExtra;

   protected StonefallFrostWaveParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet pSprites) {
      super(pLevel, pX, pY, pZ, 0.0, 0.0, 0.0);
      this.lifetime = 40 + this.random.nextInt(10);
      float f = this.random.nextFloat() * 0.6F + 0.4F;
      this.rCol = f;
      this.gCol = f;
      this.bCol = f;
      float rand = this.random.nextFloat();
      this.xdStart = pXSpeed * (1.7F + 0.6F * rand);
      this.ydStart = pYSpeed * (1.7F + 0.6F * rand);
      this.zdStart = pZSpeed * (1.7F + 0.6F * rand);
      this.quadSize = 0.25F;
      this.sprites = pSprites;
      this.setSpriteFromAge(pSprites);
   }

   public void tick() {
      this.xd = Math.min(1.0F, (float)(this.lifetime - this.age) / this.lifetime) * this.xdStart;
      this.yd = Math.min(1.0F, (float)this.age / this.lifetime) * this.ydStart + this.ydExtra;
      this.zd = Math.min(1.0F, (float)(this.lifetime - this.age) / this.lifetime) * this.zdStart;
      this.alpha = Math.min(1.0F, (float)(this.lifetime - this.age) / this.lifetime);
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

   public int getLightColor(float pPartialTick) {
      float f = (this.age + pPartialTick) / this.lifetime;
      f = Mth.clamp(f, 0.0F, 1.0F);
      int i = super.getLightColor(pPartialTick);
      int j = i & 0xFF;
      int k = i >> 16 & 0xFF;
      j += (int)(f * 15.0F * 16.0F);
      if (j > 240) {
         j = 240;
      }

      return j | k << 16;
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
         return new StonefallFrostWaveParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, this.sprites);
      }
   }
}
