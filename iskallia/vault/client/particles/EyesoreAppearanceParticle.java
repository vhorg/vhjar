package iskallia.vault.client.particles;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import iskallia.vault.entity.model.EyesoreModel;
import iskallia.vault.entity.renderer.EyesoreRenderer;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.IRenderTypeBuffer.Impl;
import net.minecraft.client.renderer.model.Model;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class EyesoreAppearanceParticle extends Particle {
   private final Model model = new EyesoreModel();
   private final RenderType renderType = RenderType.func_228644_e_(EyesoreRenderer.SORE_EYE_TEXTURE);

   protected EyesoreAppearanceParticle(ClientWorld world, double x, double y, double z) {
      super(world, x, y, z);
      this.field_70545_g = 0.0F;
      this.field_70547_e = 30;
   }

   @Nonnull
   public IParticleRenderType func_217558_b() {
      return IParticleRenderType.field_217605_e;
   }

   public void func_225606_a_(@Nonnull IVertexBuilder buffer, ActiveRenderInfo renderInfo, float partialTicks) {
      float f = (this.field_70546_d + partialTicks) / this.field_70547_e;
      float f1 = 0.05F + 0.5F * MathHelper.func_76126_a(f * (float) Math.PI);
      MatrixStack matrixstack = new MatrixStack();
      matrixstack.func_227863_a_(renderInfo.func_227995_f_());
      matrixstack.func_227863_a_(Vector3f.field_229179_b_.func_229187_a_(150.0F * f - 60.0F));
      matrixstack.func_227862_a_(-1.0F, -1.0F, 1.0F);
      matrixstack.func_227861_a_(0.0, -1.101F, 1.5);
      Impl irendertypebuffer$impl = Minecraft.func_71410_x().func_228019_au_().func_228487_b_();
      IVertexBuilder ivertexbuilder = irendertypebuffer$impl.getBuffer(this.renderType);
      this.model.func_225598_a_(matrixstack, ivertexbuilder, 15728880, OverlayTexture.field_229196_a_, 1.0F, 1.0F, 1.0F, f1);
      irendertypebuffer$impl.func_228461_a_();
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite spriteSet;

      public Factory(IAnimatedSprite spriteSet) {
         this.spriteSet = spriteSet;
      }

      public Particle makeParticle(
         @Nonnull BasicParticleType typeIn, @Nonnull ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed
      ) {
         return new EyesoreAppearanceParticle(worldIn, x, y, z);
      }
   }
}
