package iskallia.vault.client.particles;

import javax.annotation.Nonnull;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class FloatingAltarItemParticle extends TextureSheetParticle {
   private Entity entity = null;
   private final double radius;
   private final double rotation;

   protected FloatingAltarItemParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet pSprites) {
      super(pLevel, pX, pY, pZ);
      this.radius = pXSpeed;
      this.rotation = pYSpeed;
      Vec3 offset = new Vec3(this.radius * Math.cos(this.rotation), -this.alpha, this.radius * Math.sin(this.rotation));
      this.x = pX + offset.x();
      this.y = pY + offset.y();
      this.z = pZ + offset.z();
      this.xo = pX + offset.x();
      this.yo = pY + offset.y();
      this.zo = pZ + offset.z();
      this.gravity = -0.01F;
      this.friction = 0.9F;
      this.setSprite(pSprites.get(this.random));
      this.scale(0.5F);
      this.setColor(1.0F, 0.95F, 0.6F);
      this.xd = 0.0;
      this.yd = 0.0;
      this.zd = 0.0;
      this.lifetime = 30;
      this.quadSize = 0.15F;
      this.hasPhysics = false;
   }

   protected int getLightColor(float pPartialTick) {
      return 15728880;
   }

   public void setEntity(Entity entity) {
      this.entity = entity;
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      double radius = this.radius * this.alpha * this.alpha * this.alpha;
      if (this.entity != null) {
         Vec3 offset = new Vec3(radius * Math.cos(this.rotation - this.age / 10.0F), -this.alpha, radius * Math.sin(this.rotation - this.age / 10.0F));
         this.x = this.entity.getX() + offset.x();
         this.y = this.entity.getY() + 0.25 + offset.y();
         this.z = this.entity.getZ() + offset.z();
      }

      this.alpha = Mth.clamp((float)(this.lifetime - this.age) / this.lifetime, 0.0F, 1.0F);
      if (this.age++ >= this.lifetime) {
         this.remove();
      }
   }

   @Nonnull
   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
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
         return new FloatingAltarItemParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, this.sprites);
      }
   }
}
