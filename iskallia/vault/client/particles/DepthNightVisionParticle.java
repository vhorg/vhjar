package iskallia.vault.client.particles;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DepthNightVisionParticle extends SimpleAnimatedParticle {
   private static final ParticleRenderType DEPTH_PARTICLE_SHEET_TRANSLUCENT = new ParticleRenderType() {
      public void begin(BufferBuilder buf, TextureManager mgr) {
         RenderSystem.disableDepthTest();
         PARTICLE_SHEET_TRANSLUCENT.begin(buf, mgr);
      }

      public void end(Tesselator tes) {
         PARTICLE_SHEET_TRANSLUCENT.end(tes);
         RenderSystem.enableDepthTest();
      }
   };

   private DepthNightVisionParticle(ClientLevel world, double x, double y, double z, double motionX, double motionY, double motionZ, SpriteSet spriteWithAge) {
      super(world, x, y, z, spriteWithAge, 0.0F);
      this.xd = motionX;
      this.yd = motionY;
      this.zd = motionZ;
      this.quadSize *= 0.75F;
      this.lifetime = 48 + this.random.nextInt(12);
      this.setSpriteFromAge(spriteWithAge);
   }

   public void render(VertexConsumer buffer, Camera renderInfo, float partialTicks) {
      Player player = Minecraft.getInstance().player;
      if (player != null) {
         double dist = player.distanceToSqr(this.x, this.y, this.z);
         if (dist < 400.0) {
            this.alpha = Mth.clamp((float)Math.sqrt(dist) / 20.0F, 0.0F, 1.0F);
         }
      }

      super.render(buffer, renderInfo, partialTicks);
   }

   public void tick() {
      this.setSpriteFromAge(this.sprites);
      super.tick();
   }

   public ParticleRenderType getRenderType() {
      return DEPTH_PARTICLE_SHEET_TRANSLUCENT;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements ParticleProvider<SimpleParticleType> {
      private final SpriteSet spriteSet;

      public Factory(SpriteSet spriteSet) {
         this.spriteSet = spriteSet;
      }

      public Particle createParticle(SimpleParticleType type, ClientLevel world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         DepthNightVisionParticle particle = new DepthNightVisionParticle(world, x, y, z, 0.0, 0.0, 0.0, this.spriteSet);
         particle.setColor((float)xSpeed, (float)ySpeed, (float)zSpeed);
         return particle;
      }
   }
}
