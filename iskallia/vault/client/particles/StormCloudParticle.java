package iskallia.vault.client.particles;

import iskallia.vault.entity.entity.VaultStormEntity;
import java.util.List;
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
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class StormCloudParticle extends TextureSheetParticle {
   private final SpriteSet sprites;
   float lightOffset;
   float lightOffsetSet;

   protected StormCloudParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet pSprites) {
      super(pLevel, pX, pY, pZ, 0.0, 0.0, 0.0);
      this.lifetime = 20 + this.random.nextInt(10);
      float f = this.random.nextFloat() * 0.1F + 0.7F;
      this.rCol = f;
      this.gCol = f;
      this.bCol = f;
      this.quadSize = 1.0F;
      this.sprites = pSprites;
      this.setSprite(pSprites.get(new Random()));
      this.lightOffset = 0.0F;
      this.lightOffsetSet = 0.0F;
      List<VaultStormEntity.SmiteBolt> list = this.level
         .getEntitiesOfClass(VaultStormEntity.SmiteBolt.class, new AABB(this.x - 2.0, this.y - 20.0, this.z - 2.0, this.x + 2.0, this.y + 2.0, this.z + 2.0));
      if (list.size() > 0) {
         f = getDistance(list.get(0).position(), new Vec3(this.x, this.y, this.z)) / 2.0F;
         f = Mth.clamp(f, 0.0F, 1.0F);
         f = 1.0F - f;
         this.lightOffsetSet = f;
      }
   }

   public void tick() {
      if (this.age++ >= this.lifetime) {
         this.remove();
      } else {
         this.setSpriteFromAge(this.sprites);
         List<VaultStormEntity.SmiteBolt> list = this.level
            .getEntitiesOfClass(VaultStormEntity.SmiteBolt.class, new AABB(this.x - 2.0, this.y - 20.0, this.z - 2.0, this.x + 2.0, this.y + 2.0, this.z + 2.0));
         if (list.size() > 0) {
            float f = getDistance(list.get(0).position(), new Vec3(this.x, this.y, this.z)) / 2.0F;
            f = Mth.clamp(f, 0.0F, 1.0F);
            f = 1.0F - f;
            this.lightOffsetSet = f;
         } else if (this.lightOffsetSet > 0.0F) {
            this.lightOffsetSet /= 2.0F;
            if (this.lightOffsetSet < 0.05F) {
               this.lightOffsetSet = 0.0F;
            }
         } else {
            this.lightOffsetSet = 0.0F;
         }
      }
   }

   @Nonnull
   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   public static float getDistance(Vec3 point1, Vec3 point2) {
      double deltaX = point2.x() - point1.x();
      double deltaZ = point2.z() - point1.z();
      return (float)Math.sqrt(deltaX * deltaX + deltaZ * deltaZ);
   }

   public int getLightColor(float pPartialTick) {
      this.lightOffset = Mth.lerp(pPartialTick, this.lightOffset, this.lightOffsetSet);
      if (this.lightOffset > 0.0F) {
         float f = this.lightOffset;
         int i = super.getLightColor(pPartialTick);
         int j = i & 0xFF;
         int k = i >> 16 & 0xFF;
         j += (int)(f * 15.0F * 16.0F);
         if (j > 240) {
            j = 240;
         }

         return j | k << 16;
      } else {
         return super.getLightColor(pPartialTick);
      }
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
         return new StormCloudParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, this.sprites);
      }
   }
}
