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

public class NovaSpeedParticle extends TextureSheetParticle {
   private final SpriteSet sprites;

   protected NovaSpeedParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet pSprites) {
      super(pLevel, pX, pY, pZ);
      this.gravity = -0.01F;
      this.friction = 0.9F;
      this.sprites = pSprites;
      this.xd = pXSpeed + (Math.random() * 2.0 - 1.0) * 0.05F;
      this.yd = 0.0;
      this.zd = pZSpeed + (Math.random() * 2.0 - 1.0) * 0.05F;
      float f = this.random.nextFloat() * 0.3F + 0.7F;
      this.rCol = f;
      this.gCol = f;
      this.bCol = f;
      this.quadSize = 0.1F * (this.random.nextFloat() * this.random.nextFloat() * 6.0F + 1.0F);
      this.lifetime = (int)(16.0 / (this.random.nextFloat() * 0.8 + 0.2)) + 2;
      this.setSpriteFromAge(pSprites);
      this.scale(0.5F);
   }

   protected int getLightColor(float pPartialTick) {
      return this.level == null ? 15728880 : super.getLightColor(pPartialTick);
   }

   @Nonnull
   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   public void tick() {
      if (this.level != null) {
         super.tick();
         this.setSpriteFromAge(this.sprites);
         float alpha = Mth.clamp(1.0F - (float)this.age / this.lifetime, 0.0F, 1.0F);
         this.alpha = alpha * alpha * alpha;
      }
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
         return new NovaSpeedParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, this.sprites);
      }
   }
}
