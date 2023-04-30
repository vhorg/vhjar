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
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ShockedParticle extends TextureSheetParticle {
   private final SpriteSet sprites;
   private LivingEntity livingEntity = null;
   private double xOffset;
   private double yOffset;
   private double zOffset;

   protected ShockedParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet pSprites) {
      super(pLevel, pX, pY, pZ);
      this.x = pX + pXSpeed;
      this.y = pY + pXSpeed;
      this.z = pZ + pXSpeed;
      this.xOffset = pXSpeed;
      this.yOffset = pYSpeed;
      this.zOffset = pZSpeed;
      this.gravity = -0.01F;
      this.friction = 0.9F;
      this.sprites = pSprites;
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

   public void setLivingEntity(LivingEntity livingEntity) {
      this.livingEntity = livingEntity;
   }

   public void tick() {
      this.xOffset = this.xOffset + (this.random.nextFloat() * 0.15F - 0.075F);
      this.yOffset = this.yOffset + (this.random.nextFloat() * 0.15F - 0.075F);
      this.zOffset = this.zOffset + (this.random.nextFloat() * 0.15F - 0.075F);
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      if (this.livingEntity != null) {
         this.move(
            this.livingEntity.position().x + this.xOffset - this.x,
            this.livingEntity.position().y + this.livingEntity.getBbHeight() / 2.0F + this.yOffset - this.y,
            this.livingEntity.position().z + this.zOffset - this.z
         );
      } else {
         this.move(this.random.nextFloat() * 0.15F - 0.075F, this.random.nextFloat() * 0.15F - 0.075F, this.random.nextFloat() * 0.15F - 0.075F);
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
         return new ShockedParticle(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed, this.sprites);
      }
   }
}
