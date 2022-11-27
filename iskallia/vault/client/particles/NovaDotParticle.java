package iskallia.vault.client.particles;

import java.util.Random;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class NovaDotParticle extends TextureSheetParticle {
   private static final Random RANDOM = new Random();
   private final SpriteSet sprites;

   NovaDotParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet pSprites) {
      super(pLevel, pX, pY, pZ, 0.5 - RANDOM.nextDouble(), pYSpeed, 0.5 - RANDOM.nextDouble());
      this.friction = 0.96F;
      this.gravity = -0.1F;
      this.speedUpWhenYMotionIsBlocked = true;
      this.sprites = pSprites;
      this.yd *= 0.2F;
      if (pXSpeed == 0.0 && pZSpeed == 0.0) {
         this.xd *= 0.1F;
         this.zd *= 0.1F;
      }

      this.quadSize *= 0.75F;
      this.lifetime = (int)(8.0 / (Math.random() * 0.8 + 0.2));
      this.hasPhysics = false;
      this.setSpriteFromAge(pSprites);
      if (this.isCloseToScopingPlayer()) {
         this.setAlpha(0.0F);
      }
   }

   @Nonnull
   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   public void tick() {
      super.tick();
      this.setSpriteFromAge(this.sprites);
      if (this.isCloseToScopingPlayer()) {
         this.setAlpha(0.0F);
      } else {
         this.setAlpha(Mth.lerp(0.05F, this.alpha, 1.0F));
      }
   }

   private boolean isCloseToScopingPlayer() {
      Minecraft minecraft = Minecraft.getInstance();
      LocalPlayer localPlayer = minecraft.player;
      return localPlayer != null
         && localPlayer.getEyePosition().distanceToSqr(this.x, this.y, this.z) <= 9.0
         && minecraft.options.getCameraType().isFirstPerson()
         && localPlayer.isScoping();
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
