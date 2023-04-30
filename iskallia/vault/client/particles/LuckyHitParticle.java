package iskallia.vault.client.particles;

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

public class LuckyHitParticle extends TextureSheetParticle {
   LuckyHitParticle(ClientLevel p_105919_, double p_105920_, double p_105921_, double p_105922_, double p_105923_, double p_105924_, double p_105925_) {
      super(p_105919_, p_105920_, p_105921_, p_105922_, 0.0, 0.0, 0.0);
      this.friction = 0.7F;
      this.gravity = 0.5F;
      this.xd *= 0.1F;
      this.yd *= 0.1F;
      this.zd *= 0.1F;
      this.xd += p_105923_ * 0.4;
      this.yd += p_105924_ * 0.4;
      this.zd += p_105925_ * 0.4;
      this.rCol = 0.42745098F;
      this.gCol = 0.9607843F;
      this.bCol = 0.6392157F;
      this.quadSize *= 0.75F;
      this.lifetime = Math.max((int)(6.0 / (Math.random() * 0.8 + 0.6)), 1) * 3;
      this.hasPhysics = false;
      this.tick();
   }

   public float getQuadSize(float pScaleFactor) {
      return this.quadSize * Mth.clamp((this.age + pScaleFactor) / this.lifetime * 32.0F, 0.0F, 1.0F);
   }

   public void tick() {
      super.tick();
      this.oRoll = this.roll;
      this.roll = (float)this.age / this.lifetime;
      this.alpha *= 0.9F;
      this.gCol *= 0.99F;
      this.bCol *= 0.96F;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
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
         LuckyHitParticle critparticle = new LuckyHitParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);
         critparticle.pickSprite(this.sprite);
         return critparticle;
      }
   }
}
