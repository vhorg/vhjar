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
import net.minecraft.util.Mth;
import org.jetbrains.annotations.Nullable;

public class TotemFountainParticle extends TextureSheetParticle {
   private float scale;

   protected TotemFountainParticle(ClientLevel level, double x, double y, double z) {
      super(level, x, y, z, 0.0, 0.0, 0.0);
      this.hasPhysics = false;
   }

   public void setYSpeed(float ySpeed) {
      this.yd = ySpeed;
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
         this.setPos(this.x, this.y + this.yd, this.z);
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

   public static class Provider implements ParticleProvider<ColoredParticleOptions> {
      private static final Random RANDOM = new Random();
      private final SpriteSet sprites;

      public Provider(SpriteSet pSprites) {
         this.sprites = pSprites;
      }

      @Nullable
      public Particle createParticle(
         @Nonnull ColoredParticleOptions data,
         @Nonnull ClientLevel level,
         double xOrigin,
         double yOrigin,
         double zOrigin,
         double xSpeed,
         double ySpeed,
         double zSpeed
      ) {
         TotemFountainParticle particle = new TotemFountainParticle(level, xOrigin, yOrigin, zOrigin);
         particle.pickSprite(this.sprites);
         particle.setColor(data.color().x(), data.color().y(), data.color().z());
         particle.scale(Mth.randomBetween(RANDOM, 0.5F, 1.0F));
         particle.setLifetime(40);
         particle.setYSpeed(Mth.randomBetween(RANDOM, 0.025F, 0.0625F));
         particle.setRoll(RANDOM.nextBoolean() ? 0.0F : (float) (Math.PI / 4));
         return particle;
      }
   }
}
