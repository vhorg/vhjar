package iskallia.vault.client.particles;

import com.mojang.blaze3d.vertex.VertexConsumer;
import iskallia.vault.core.vault.influence.VaultGod;
import java.util.Random;
import javax.annotation.Nonnull;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

@OnlyIn(Dist.CLIENT)
public class ArtifactProjectorParticle extends TextureSheetParticle {
   private final double xStart;
   private final double yStart;
   private final double zStart;
   private double rotationOffset;
   private double rotationSpeed;
   private double range;
   private final SpriteSet pSprites;
   protected double dir;

   ArtifactProjectorParticle(
      ClientLevel p_106464_,
      double p_106465_,
      double p_106466_,
      double p_106467_,
      double range,
      double rotationOffset,
      double rotationSpeed,
      SpriteSet pSprites
   ) {
      super(p_106464_, p_106465_, p_106466_, p_106467_);
      this.xd = 0.0;
      this.yd = 0.0;
      this.zd = 0.0;
      this.xStart = p_106465_;
      this.yStart = p_106466_;
      this.zStart = p_106467_;
      this.quadSize = 0.4F * (this.random.nextFloat() * 0.5F + 0.2F);
      float f = this.random.nextFloat() * 0.6F + 0.4F;
      this.rCol = 0.9F * f;
      this.gCol = 0.9F * f;
      this.bCol = f;
      this.hasPhysics = false;
      this.lifetime = 20;
      this.pSprites = pSprites;
      Vec3 vec = new Vec3(range, range, 0.0).zRot((float)Math.toRadians((180.0 + rotationOffset) * rotationSpeed));
      vec = vec.yRot((float)Math.toRadians(this.dir));
      this.x = this.xStart + vec.x();
      this.y = this.yStart + vec.y();
      this.z = this.zStart + vec.z();
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      this.range = range;
      this.rotationOffset = rotationOffset;
      this.rotationSpeed = rotationSpeed;
   }

   public void render(VertexConsumer pBuffer, Camera pRenderInfo, float pPartialTicks) {
      if (this.age > 1) {
         super.render(pBuffer, pRenderInfo, pPartialTicks);
      }
   }

   public boolean shouldCull() {
      return false;
   }

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public void move(double pX, double pY, double pZ) {
      this.setBoundingBox(this.getBoundingBox().move(pX, pY, pZ));
      this.setLocationFromBoundingbox();
   }

   public int getLightColor(float pPartialTick) {
      int i = super.getLightColor(pPartialTick);
      float f = (float)this.age / this.lifetime;
      f *= f;
      f *= f;
      int j = i & 0xFF;
      int k = i >> 16 & 0xFF;
      k += (int)(f * 15.0F * 16.0F);
      if (k > 240) {
         k = 240;
      }

      return j | k << 16;
   }

   public void setDir(double dir) {
      this.dir = dir;
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         float f = (float)this.age / this.lifetime;
         f = 1.0F - f;
         f *= f;
         f *= f;
         Vec3 vec = new Vec3(f * this.range, f * this.range, 0.0).zRot((float)Math.toRadians((f * 180.0F + this.rotationOffset) * this.rotationSpeed));
         vec = vec.yRot((float)Math.toRadians(this.dir));
         this.x = this.xStart + vec.x();
         this.y = this.yStart + vec.y();
         this.z = this.zStart + vec.z();
         this.setSpriteFromAge(this.pSprites);
      }
   }

   public static class AltarProvider implements ParticleProvider<ArtifactProjectorParticleOptions> {
      private final SpriteSet sprites;

      public AltarProvider(SpriteSet pSprites) {
         this.sprites = pSprites;
      }

      @Nullable
      public Particle createParticle(
         ArtifactProjectorParticleOptions data, @Nonnull ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed
      ) {
         ArtifactProjectorParticle particle = new ArtifactProjectorParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, this.sprites);
         particle.pickSprite(this.sprites);

         int col = switch (new Random().nextInt(4)) {
            case 1 -> VaultGod.TENOS.getColor();
            case 2 -> VaultGod.VELARA.getColor();
            case 3 -> VaultGod.WENDARR.getColor();
            default -> VaultGod.IDONA.getColor();
         };
         int r = col >>> 16 & 0xFF;
         int g = col >>> 8 & 0xFF;
         int b = col & 0xFF;
         float colorOffset = new Random().nextFloat(0.3F) + 0.7F;
         particle.setColor(r / 256.0F * colorOffset, g / 256.0F * colorOffset, b / 256.0F * colorOffset);
         particle.setDir(data.dir());
         particle.setLifetime(data.lifetime());
         return particle;
      }
   }
}
