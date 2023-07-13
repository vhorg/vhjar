package iskallia.vault.client.particles;

import com.mojang.blaze3d.vertex.VertexConsumer;
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
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public class UberPylonParticle extends TextureSheetParticle {
   private float scale;
   private double xStart;
   private double yStart;
   private double zStart;
   private double ySpeed;
   private double rotationSpeed;

   protected UberPylonParticle(ClientLevel level, double x, double y, double z, double xCenter, double yCenter, double zCenter) {
      super(level, x, y, z, 0.0, 0.0, 0.0);
      this.hasPhysics = false;
      this.xd = xCenter;
      this.yd = yCenter;
      this.zd = zCenter;
      this.xStart = x;
      this.yStart = y;
      this.zStart = z;
      this.xo = x + xCenter;
      this.yo = y + yCenter + this.ySpeed;
      this.zo = z + zCenter;
      this.x = this.xo;
      this.y = this.yo;
      this.z = this.zo;
   }

   public void setYSpeed(float ySpeed) {
      this.ySpeed = ySpeed;
   }

   public void setRotationSpeed(float rotationSpeed) {
      this.rotationSpeed = rotationSpeed;
   }

   public void setRoll(float rollRadians) {
      this.oRoll = rollRadians;
      this.roll = rollRadians;
   }

   public void tick() {
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.xo = this.x;
         this.yo = this.y;
         this.zo = this.z;
         float f = (float)this.age / this.lifetime;
         f = 1.0F - f;
         float f1 = 1.0F - f;
         f1 *= f1;
         f1 *= f1;
         this.x = this.xStart + this.xd * f;
         this.y = this.yStart + this.yd + this.ySpeed * this.age;
         this.z = this.zStart + this.zd * f;
         double x1 = this.x - this.xStart;
         double z1 = this.z - this.zStart;
         double x2 = x1 * Math.cos(Math.toRadians(this.age * 9)) - z1 * Math.sin(Math.toRadians(this.age * 9));
         double z2 = x1 * Math.sin(Math.toRadians(this.age * 9)) + z1 * Math.cos(Math.toRadians(this.age * 9));
         this.x = x2 + this.xStart;
         this.z = z2 + this.zStart;
         this.setPos(this.x, this.y, this.z);
      }
   }

   @ParametersAreNonnullByDefault
   public void render(VertexConsumer vertexConsumer, Camera camera, float partialTicks) {
      float delta = (this.age + partialTicks) / this.lifetime;
      this.quadSize = 0.1F;
      super.scale(Mth.lerp(delta, this.scale, 0.0F));
      float alpha = 1.0F - delta;
      this.setAlpha(Mth.clamp(alpha, 0.0F, 1.0F));
      super.render(vertexConsumer, camera, partialTicks);
   }

   @Nonnull
   public Particle scale(float scale) {
      this.scale = scale;
      return super.scale(scale);
   }

   @Nonnull
   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   protected int getLightColor(float pPartialTick) {
      return 240;
   }

   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private static final Random RANDOM = new Random();
      private final SpriteSet sprites;

      public Provider(SpriteSet pSprites) {
         this.sprites = pSprites;
      }

      @Nullable
      public Particle createParticle(
         @Nonnull SimpleParticleType data,
         @Nonnull ClientLevel level,
         double xOrigin,
         double yOrigin,
         double zOrigin,
         double xSpeed,
         double ySpeed,
         double zSpeed
      ) {
         UberPylonParticle particle = new UberPylonParticle(level, xOrigin, yOrigin, zOrigin, xSpeed, ySpeed, zSpeed);
         particle.pickSprite(this.sprites);
         particle.scale(Mth.randomBetween(RANDOM, 1.25F, 2.0F));
         particle.setLifetime(40);
         particle.setYSpeed(Mth.randomBetween(RANDOM, 0.025F, 0.0625F));
         particle.setRoll(RANDOM.nextBoolean() ? 0.0F : (float) (Math.PI / 4));
         particle.setRotationSpeed(Mth.randomBetween(RANDOM, 0.125F, 0.8625F));
         return particle;
      }
   }
}
