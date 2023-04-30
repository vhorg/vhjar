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

public class CloudEffectParticle extends TextureSheetParticle {
   private final SpriteSet sprites;

   protected CloudEffectParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet pSprites) {
      super(pLevel, pX, pY, pZ);
      this.gravity = -0.01F;
      this.friction = 0.9F;
      this.sprites = pSprites;
      this.setSprite(pSprites.get(new Random()));
      this.scale(0.5F);
      this.xd = (Math.random() * 2.0 - 1.0) * 0.05F;
      this.yd = 0.0;
      this.zd = (Math.random() * 2.0 - 1.0) * 0.05F;
      this.lifetime = 100;
      this.quadSize = 1.0F;
   }

   public void tick() {
      this.alpha = Mth.clamp((float)(this.lifetime - this.age) / this.lifetime, 0.0F, 1.0F);
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
         Particle particle = new CloudEffectParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, this.sprites);
         particle.setColor((float)pXSpeed, (float)pYSpeed, (float)pZSpeed);
         return particle;
      }
   }
}
