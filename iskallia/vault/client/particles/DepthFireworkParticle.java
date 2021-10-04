package iskallia.vault.client.particles;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.SimpleAnimatedParticle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class DepthFireworkParticle extends SimpleAnimatedParticle {
   private static final IParticleRenderType DEPTH_PARTICLE_SHEET_TRANSLUCENT = new IParticleRenderType() {
      public void func_217600_a(BufferBuilder buf, TextureManager mgr) {
         RenderSystem.disableDepthTest();
         field_217603_c.func_217600_a(buf, mgr);
      }

      public void func_217599_a(Tessellator tes) {
         field_217603_c.func_217599_a(tes);
         RenderSystem.enableDepthTest();
      }
   };

   private DepthFireworkParticle(ClientWorld world, double x, double y, double z, double motionX, double motionY, double motionZ, IAnimatedSprite spriteWithAge) {
      super(world, x, y, z, spriteWithAge, 0.0F);
      this.field_187129_i = motionX;
      this.field_187130_j = motionY;
      this.field_187131_k = motionZ;
      this.field_70544_f *= 0.75F;
      this.field_70547_e = 48 + this.field_187136_p.nextInt(12);
      this.func_217566_b(spriteWithAge);
   }

   public void func_225606_a_(IVertexBuilder buffer, ActiveRenderInfo renderInfo, float partialTicks) {
      if (this.field_70546_d < this.field_70547_e / 3 || (this.field_70546_d + this.field_70547_e) / 3 % 2 == 0) {
         super.func_225606_a_(buffer, renderInfo, partialTicks);
      }
   }

   public IParticleRenderType func_217558_b() {
      return DEPTH_PARTICLE_SHEET_TRANSLUCENT;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite spriteSet;

      public Factory(IAnimatedSprite spriteSet) {
         this.spriteSet = spriteSet;
      }

      public Particle makeParticle(BasicParticleType type, ClientWorld world, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         DepthFireworkParticle particle = new DepthFireworkParticle(world, x, y, z, 0.0, 0.0, 0.0, this.spriteSet);
         particle.func_70538_b((float)xSpeed, (float)ySpeed, (float)zSpeed);
         return particle;
      }
   }
}
