package iskallia.vault.client.particles;

import com.mojang.blaze3d.vertex.VertexConsumer;
import iskallia.vault.util.MathUtilities;
import iskallia.vault.util.Tween;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.Nullable;

public class EffectRangeParticle extends TextureSheetParticle {
   private static final float ROTATION_SPEED = 0.0125F;
   private float rotationSpeed;
   private final double xOrigin;
   private final double zOrigin;
   private final float orbitRadius;
   private final Tween alphaTween;
   private float orbitAngleRadians;

   protected EffectRangeParticle(
      ClientLevel clientLevel,
      double xPosition,
      double yPosition,
      double zPosition,
      double xOrigin,
      double zOrigin,
      float orbitRadius,
      float orbitAngleRadians,
      Tween alphaTween
   ) {
      super(clientLevel, xPosition, yPosition, zPosition);
      this.xOrigin = xOrigin;
      this.zOrigin = zOrigin;
      this.orbitRadius = orbitRadius;
      this.alphaTween = alphaTween;
      this.hasPhysics = false;
      this.orbitAngleRadians = orbitAngleRadians;
      this.setPos(
         this.xOrigin + Math.cos(this.orbitAngleRadians) * this.orbitRadius, this.y, this.zOrigin + Math.sin(this.orbitAngleRadians) * this.orbitRadius
      );
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
   }

   public void setRotationSpeed(float rotationSpeed) {
      this.rotationSpeed = rotationSpeed;
   }

   @Nonnull
   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   public void tick() {
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.xo = this.x;
         this.yo = this.y;
         this.zo = this.z;
         this.orbitAngleRadians = this.orbitAngleRadians + this.rotationSpeed;
         this.setPos(
            this.xOrigin + Math.cos(this.orbitAngleRadians) * this.orbitRadius, this.y, this.zOrigin + Math.sin(this.orbitAngleRadians) * this.orbitRadius
         );
      }
   }

   @ParametersAreNonnullByDefault
   public void render(VertexConsumer vertexConsumer, Camera camera, float partialTicks) {
      this.setAlpha(Mth.clamp(this.alphaTween.tween(this.age + partialTicks, 0.0F, 1.0F, this.lifetime), 0.0F, 1.0F));
      super.render(vertexConsumer, camera, partialTicks);
   }

   protected int getLightColor(float pPartialTick) {
      return 240;
   }

   public static class CircleProvider implements ParticleProvider<SphericalParticleOptions> {
      private static final Random RANDOM = new Random();
      private final SpriteSet sprites;
      private final float orbitSpeed;
      private final int durationTicks;
      private final float scale;
      private final Tween alphaTween;

      public CircleProvider(SpriteSet pSprites, float orbitSpeed, int durationTicks, float scale, Tween alphaTween) {
         this.sprites = pSprites;
         this.orbitSpeed = orbitSpeed;
         this.durationTicks = durationTicks;
         this.scale = scale;
         this.alphaTween = alphaTween;
      }

      @Nullable
      public Particle createParticle(
         SphericalParticleOptions data, @Nonnull ClientLevel level, double xOrigin, double yOrigin, double zOrigin, double xSpeed, double ySpeed, double zSpeed
      ) {
         Vec3 position = MathUtilities.getRandomPointOnCircle(xOrigin, yOrigin, zOrigin, data.range(), RANDOM);
         float orbitRadius = (float)MathUtilities.getDistance(position.x, position.z, xOrigin, zOrigin);
         float orbitAngleRadians = (float)Math.atan2(position.x - xOrigin, position.z - zOrigin);
         EffectRangeParticle particle = new EffectRangeParticle(
            level, position.x, position.y, position.z, xOrigin, zOrigin, orbitRadius, orbitAngleRadians, this.alphaTween
         );
         particle.pickSprite(this.sprites);
         particle.setColor(data.color().x(), data.color().y(), data.color().z());
         particle.scale(this.scale);
         particle.setLifetime(this.durationTicks);
         particle.setRotationSpeed((0.0125F + Mth.randomBetween(RANDOM, -0.01F, 0.01F)) * this.orbitSpeed);
         return particle;
      }
   }

   public static class SphereProvider implements ParticleProvider<SphericalParticleOptions> {
      private static final Random RANDOM = new Random();
      private final SpriteSet sprites;
      private final float orbitSpeed;
      private final int durationTicks;
      private final float scale;
      private final Tween alphaTween;

      public SphereProvider(SpriteSet pSprites, float orbitSpeed, int durationTicks, float scale, Tween alphaTween) {
         this.sprites = pSprites;
         this.orbitSpeed = orbitSpeed;
         this.durationTicks = durationTicks;
         this.scale = scale;
         this.alphaTween = alphaTween;
      }

      @Nullable
      public Particle createParticle(
         SphericalParticleOptions data, @Nonnull ClientLevel level, double xOrigin, double yOrigin, double zOrigin, double xSpeed, double ySpeed, double zSpeed
      ) {
         Vec3 position = MathUtilities.getRandomPointOnSphere(xOrigin, yOrigin, zOrigin, data.range(), RANDOM);
         float orbitRadius = (float)MathUtilities.getDistance(position.x, position.z, xOrigin, zOrigin);
         float orbitAngleRadians = (float)Math.atan2(position.x - xOrigin, position.z - zOrigin);
         EffectRangeParticle particle = new EffectRangeParticle(
            level, position.x, position.y, position.z, xOrigin, zOrigin, orbitRadius, orbitAngleRadians, this.alphaTween
         );
         particle.pickSprite(this.sprites);
         particle.setColor(data.color().x(), data.color().y(), data.color().z());
         particle.scale(this.scale);
         particle.setLifetime(this.durationTicks);
         particle.setRotationSpeed((0.0125F + Mth.randomBetween(RANDOM, -0.01F, 0.01F)) * this.orbitSpeed);
         return particle;
      }
   }
}
