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

public class GrowingSphereParticle extends TextureSheetParticle {
   private static final float ROTATION_SPEED = 0.0125F;
   private float rotationSpeed;
   private final double xOrigin;
   private final double yOrigin;
   private final double yOffset;
   private final double zOrigin;
   private final float orbitRadius;
   private final Tween alphaTween;
   private float orbitAngleRadians;

   protected GrowingSphereParticle(
      ClientLevel clientLevel,
      double xPosition,
      double yPosition,
      double zPosition,
      double xOrigin,
      double yOrigin,
      double zOrigin,
      float orbitRadius,
      float orbitAngleRadians,
      Tween alphaTween
   ) {
      super(clientLevel, xOrigin, yOrigin, zOrigin);
      this.xOrigin = xOrigin;
      this.yOrigin = yOrigin;
      this.zOrigin = zOrigin;
      this.orbitRadius = orbitRadius;
      this.alphaTween = alphaTween;
      this.hasPhysics = false;
      this.orbitAngleRadians = orbitAngleRadians;
      this.yOffset = yPosition - this.yOrigin;
      this.x = this.xOrigin;
      this.y = this.yOrigin;
      this.z = this.zOrigin;
      this.xo = this.xOrigin;
      this.yo = this.yOrigin;
      this.zo = this.zOrigin;
      this.alpha = 1.0F;
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
         this.alpha = (float)(this.lifetime - this.age) / this.lifetime;
         float radius = Math.min(1.0F, (float)(this.age + this.age) / this.lifetime);
         this.setPos(
            this.xOrigin + Math.cos(this.orbitAngleRadians) * this.orbitRadius * radius,
            this.yOrigin + this.yOffset * radius,
            this.zOrigin + Math.sin(this.orbitAngleRadians) * this.orbitRadius * radius
         );
      }
   }

   public boolean shouldCull() {
      return false;
   }

   @ParametersAreNonnullByDefault
   public void render(VertexConsumer vertexConsumer, Camera camera, float partialTicks) {
      super.render(vertexConsumer, camera, partialTicks);
   }

   protected int getLightColor(float pPartialTick) {
      return 240;
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
         GrowingSphereParticle particle = new GrowingSphereParticle(
            level, position.x, position.y, position.z, xOrigin, yOrigin, zOrigin, orbitRadius, orbitAngleRadians, this.alphaTween
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
