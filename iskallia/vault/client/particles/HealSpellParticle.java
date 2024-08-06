package iskallia.vault.client.particles;

import javax.annotation.Nonnull;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.particle.TextureSheetParticle;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

public class HealSpellParticle extends TextureSheetParticle {
   private final SpriteSet sprites;
   private int healedEntityId = 0;

   protected HealSpellParticle(ClientLevel pLevel, double pX, double pY, double pZ, int healedEntityId, SpriteSet pSprites) {
      super(pLevel, pX, pY, pZ);
      this.gravity = 0.0F;
      this.hasPhysics = false;
      this.friction = 1.0F;
      this.sprites = pSprites;
      this.healedEntityId = healedEntityId;
      float f = this.random.nextFloat() * 0.3F + 0.7F;
      this.rCol = f;
      this.gCol = f;
      this.bCol = f;
      this.quadSize = 0.1F * (this.random.nextFloat() * this.random.nextFloat() * 3.0F + 1.0F);
      this.lifetime = (int)(8.0 / (this.random.nextFloat() * 0.8 + 0.2)) + 2;
      this.setSpriteFromAge(pSprites);
      this.updateSpeed();
   }

   private void updateSpeed() {
      Entity entity = this.level.getEntity(this.healedEntityId);
      if (entity != null) {
         double pXSpeed = (entity.getX() - this.x) / this.lifetime * 1.5;
         double pYSpeed = (entity.getY() + entity.getBbHeight() / 2.0 - this.y) / this.lifetime * 1.5;
         double pZSpeed = (entity.getZ() - this.z) / this.lifetime * 1.5;
         this.xd = pXSpeed + (Math.random() * 2.0 - 1.0) * 0.05F;
         this.yd = pYSpeed + (Math.random() * 2.0 - 1.0) * 0.05F;
         this.zd = pZSpeed + (Math.random() * 2.0 - 1.0) * 0.05F;
      }
   }

   @Nonnull
   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_OPAQUE;
   }

   public void tick() {
      super.tick();
      this.updateSpeed();
      this.setSpriteFromAge(this.sprites);
   }

   @OnlyIn(Dist.CLIENT)
   public static class Provider implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet sprites;

      public Provider(SpriteSet pSprites) {
         this.sprites = pSprites;
      }

      @Nullable
      public Particle createParticle(
         SimpleParticleType pType, ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed
      ) {
         int healedEntityId = (int)pXSpeed;
         return new HealSpellParticle(pLevel, pX, pY, pZ, healedEntityId, this.sprites);
      }
   }
}
