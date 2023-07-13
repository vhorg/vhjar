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

@OnlyIn(Dist.CLIENT)
public class FireballParticle extends TextureSheetParticle {
   private boolean hasHitGround;
   private final SpriteSet sprites;
   protected float startRCol = 1.0F;
   protected float startGCol = 1.0F;
   protected float startBCol = 1.0F;
   protected float endRCol = 1.0F;
   protected float endGCol = 1.0F;
   protected float endBCol = 1.0F;

   FireballParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet pSprites) {
      super(pLevel, pX, pY, pZ);
      this.friction = 0.96F;
      this.xd = pXSpeed;
      this.yd = pYSpeed;
      this.zd = pZSpeed;
      float col = Mth.nextFloat(this.random, 0.1F, 0.25F);
      this.rCol = col;
      this.gCol = col;
      this.bCol = col;
      this.startRCol = col;
      this.startGCol = col;
      this.startBCol = col;
      this.endRCol = col;
      this.endGCol = col;
      this.endBCol = col;
      this.quadSize = Mth.nextFloat(this.random, 0.75F, 1.25F);
      this.lifetime = (int)(10.0 + this.random.nextFloat() * 5.0);
      this.hasHitGround = false;
      this.hasPhysics = false;
      this.sprites = pSprites;
      this.roll = Mth.nextFloat(this.random, 0.0F, (float) (Math.PI * 2));
      this.setSpriteFromAge(pSprites);
   }

   public void setStartColor(float pParticleRed, float pParticleGreen, float pParticleBlue) {
      this.rCol = pParticleRed;
      this.gCol = pParticleGreen;
      this.bCol = pParticleBlue;
      this.startRCol = pParticleRed;
      this.startGCol = pParticleGreen;
      this.startBCol = pParticleBlue;
   }

   public void setEndColor(float pParticleRed, float pParticleGreen, float pParticleBlue) {
      this.endRCol = pParticleRed;
      this.endGCol = pParticleGreen;
      this.endBCol = pParticleBlue;
   }

   public void tick() {
      this.rCol = this.startRCol + (this.endRCol - this.startRCol) * this.age / this.lifetime;
      this.gCol = this.startGCol + (this.endGCol - this.startGCol) * this.age / this.lifetime;
      this.bCol = this.startBCol + (this.endBCol - this.startBCol) * this.age / this.lifetime;
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.setSpriteFromAge(this.sprites);
         if (this.onGround) {
            this.yd = 0.0;
            this.hasHitGround = true;
         }

         if (this.hasHitGround) {
            this.yd += 0.002;
         }

         this.move(this.xd, this.yd, this.zd);
         if (this.y == this.yo) {
            this.xd *= 1.1;
            this.zd *= 1.1;
         }

         this.xd = this.xd * this.friction;
         this.zd = this.zd * this.friction;
         if (this.hasHitGround) {
            this.yd = this.yd * this.friction;
         }
      }
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   public float getQuadSize(float pScaleFactor) {
      return this.quadSize * Mth.clamp((this.age + pScaleFactor) / this.lifetime * 32.0F, 0.0F, 1.0F);
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
         return new FireballParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, this.sprites);
      }
   }
}
