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

public class ChainingParticle extends TextureSheetParticle {
   private final SpriteSet sprites;

   protected ChainingParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet pSprites) {
      super(pLevel, pX, pY, pZ);
      this.gravity = -0.01F;
      this.setSprite(pSprites.get(new Random()));
      this.xd = 0.0;
      this.yd = 0.0;
      this.zd = 0.0;
      this.friction = 0.96F;
      this.speedUpWhenYMotionIsBlocked = true;
      this.sprites = pSprites;
      this.quadSize = 0.1F;
      this.hasPhysics = false;
      this.setSpriteFromAge(pSprites);
   }

   public void tick() {
      this.setSpriteFromAge(this.sprites);
      this.alpha = Mth.clamp((float)(this.lifetime - this.age) / this.lifetime, 0.0F, 1.0F);
      this.quadSize = this.alpha * 0.1F + 0.05F;
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
         Particle particle = new ChainingParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, this.sprites);
         Random rand = new Random();
         float offset = rand.nextFloat();
         particle.setColor(0.75F + offset * 0.25F, 0.45F + offset * 0.25F, 0.45F + offset * 0.25F);
         particle.setLifetime(pLevel.random.nextInt(10) + 10);
         return particle;
      }
   }
}
