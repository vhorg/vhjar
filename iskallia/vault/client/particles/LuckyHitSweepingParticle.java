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

public class LuckyHitSweepingParticle extends TextureSheetParticle {
   private final SpriteSet sprites;
   protected double xdStart;
   protected double ydStart;
   protected double zdStart;

   protected LuckyHitSweepingParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet pSprites) {
      super(pLevel, pX, pY, pZ, 0.0, 0.0, 0.0);
      this.lifetime = 40 + this.random.nextInt(10);
      float f = this.random.nextFloat() * 0.3F + 0.3F;
      this.rCol = this.random.nextFloat() * 0.3F + 0.7F;
      this.gCol = f;
      this.bCol = f;
      float rand = this.random.nextFloat();
      this.xdStart = pXSpeed * (0.8F + 0.8F * rand);
      this.ydStart = pYSpeed * (0.8F + 0.8F * rand);
      this.zdStart = pZSpeed * (0.8F + 0.8F * rand);
      this.quadSize *= 0.75F;
      this.sprites = pSprites;
      this.setSprite(pSprites.get(new Random()));
   }

   public void tick() {
      this.gCol *= 0.96F;
      this.bCol *= 0.93F;
      this.rCol *= 0.93F;
      this.alpha = Mth.clamp((float)(this.lifetime - this.age) / this.lifetime, 0.0F, 1.0F);
      float percent = Math.min(1.0F, (float)(this.lifetime - this.age) / this.lifetime);
      this.xd = percent * percent * percent * percent * this.xdStart;
      this.yd = Math.min(1.0F, (float)this.age / this.lifetime) * -this.ydStart;
      this.zd = percent * percent * percent * percent * this.zdStart;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.setSpriteFromAge(this.sprites);
      }

      super.tick();
   }

   public float getQuadSize(float pScaleFactor) {
      return this.quadSize * Mth.clamp((this.age + pScaleFactor) / this.lifetime * 32.0F, 0.0F, 1.0F);
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
         return new LuckyHitSweepingParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, this.sprites);
      }
   }
}
