package iskallia.vault.client.particles;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.IVertexBuilder;
import iskallia.vault.block.StabilizerBlock;
import iskallia.vault.block.entity.StabilizerTileEntity;
import iskallia.vault.client.gui.helper.LightmapHelper;
import iskallia.vault.util.MiscUtils;
import java.awt.Color;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.block.BlockState;
import net.minecraft.client.particle.IAnimatedSprite;
import net.minecraft.client.particle.IParticleFactory;
import net.minecraft.client.particle.IParticleRenderType;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.renderer.ActiveRenderInfo;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.particles.BasicParticleType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Matrix4f;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class StabilizerCubeParticle extends Particle {
   private static final Random rand = new Random();
   private final BlockPos originPos;
   private final float size;
   private final IAnimatedSprite spriteSet;
   private float effectPercent = 0.0F;
   private float prevEffectPercent = 0.0F;
   private final Vector3d rotationChange;
   private Vector3d rotationDegreeAxis;
   private Vector3d prevRotationDegreeAxis = Vector3d.field_186680_a;

   private StabilizerCubeParticle(ClientWorld world, IAnimatedSprite spriteSet, double x, double y, double z) {
      super(world, x, y, z);
      this.spriteSet = spriteSet;
      this.originPos = new BlockPos(x, y, z);
      this.size = 0.45F;
      Vector3d change = new Vector3d(
         rand.nextFloat() * (rand.nextBoolean() ? 1 : -1), rand.nextFloat() * (rand.nextBoolean() ? 1 : -1), rand.nextFloat() * (rand.nextBoolean() ? 1 : -1)
      );
      this.rotationChange = change.func_216372_d(5.0, 5.0, 5.0);
      Vector3d axis = new Vector3d(rand.nextFloat() * (rand.nextBoolean() ? 1 : -1), rand.nextFloat(), rand.nextFloat() * (rand.nextBoolean() ? 1 : -1));
      this.rotationDegreeAxis = axis.func_216372_d(18.0, 18.0, 18.0);
   }

   public void func_189213_a() {
      this.field_187123_c = this.field_187126_f;
      this.field_187124_d = this.field_187127_g;
      this.field_187125_e = this.field_187128_h;
      this.field_190015_G = this.field_190014_F;
      if (this.func_187113_k()) {
         StabilizerTileEntity tile = this.getTileRef();
         if (tile == null) {
            this.func_187112_i();
         } else {
            this.prevEffectPercent = this.effectPercent;
            if (tile.isActive()) {
               this.effectPercent = Math.min(this.effectPercent + 0.02F, 1.0F);
            } else {
               this.effectPercent = Math.max(this.effectPercent - 0.01F, 0.0F);
            }

            this.updateRotations();
         }
      }
   }

   private void updateRotations() {
      if (this.effectPercent > 0.0F && this.rotationChange.func_189985_c() > 0.0) {
         Vector3d modify = this.rotationChange.func_216372_d(this.effectPercent, this.effectPercent, this.effectPercent);
         this.prevRotationDegreeAxis = this.rotationDegreeAxis.func_186678_a(1.0);
         this.rotationDegreeAxis = this.rotationDegreeAxis.func_178787_e(modify);
         this.rotationDegreeAxis = new Vector3d(
            this.rotationDegreeAxis.func_82615_a() % 360.0, this.rotationDegreeAxis.func_82617_b() % 360.0, this.rotationDegreeAxis.func_82616_c() % 360.0
         );
         if (!this.rotationDegreeAxis.func_178787_e(modify).equals(this.rotationDegreeAxis)) {
            this.prevRotationDegreeAxis = this.rotationDegreeAxis.func_178788_d(modify);
         }
      } else {
         this.prevRotationDegreeAxis = this.rotationDegreeAxis.func_186678_a(1.0);
      }
   }

   @Nullable
   private StabilizerTileEntity getTileRef() {
      BlockState at = this.field_187122_b.func_180495_p(this.originPos);
      if (!(at.func_177230_c() instanceof StabilizerBlock)) {
         return null;
      } else {
         TileEntity tile = this.field_187122_b.func_175625_s(this.originPos);
         return tile instanceof StabilizerTileEntity ? (StabilizerTileEntity)tile : null;
      }
   }

   private Vector3d getInterpolatedRotation(float partialTicks) {
      return new Vector3d(
         MathHelper.func_219803_d(partialTicks, this.prevRotationDegreeAxis.func_82615_a(), this.rotationDegreeAxis.func_82615_a()),
         MathHelper.func_219803_d(partialTicks, this.prevRotationDegreeAxis.func_82617_b(), this.rotationDegreeAxis.func_82617_b()),
         MathHelper.func_219803_d(partialTicks, this.prevRotationDegreeAxis.func_82616_c(), this.rotationDegreeAxis.func_82616_c())
      );
   }

   private double getYOffset(float partialTicks) {
      double offset = (Math.sin(this.effectPercent * Math.PI + (Math.PI * 3.0 / 2.0)) + 1.0) / 2.0;
      double offsetPrev = (Math.sin(this.prevEffectPercent * Math.PI + (Math.PI * 3.0 / 2.0)) + 1.0) / 2.0;
      return MathHelper.func_219803_d(partialTicks, offsetPrev, offset);
   }

   public void func_225606_a_(IVertexBuilder buffer, ActiveRenderInfo ari, float partialTicks) {
      RenderSystem.disableAlphaTest();
      float effectPart = MathHelper.func_219799_g(partialTicks, this.prevEffectPercent, this.effectPercent);
      Color color = new Color(MiscUtils.blendColors(301982, 5263440, effectPart));
      float x = (float)MathHelper.func_219803_d(partialTicks, this.field_187123_c, this.field_187126_f);
      float y = (float)MathHelper.func_219803_d(partialTicks, this.field_187124_d, this.field_187127_g);
      float z = (float)MathHelper.func_219803_d(partialTicks, this.field_187125_e, this.field_187128_h);
      Vector3d cameraPos = ari.func_216785_c();
      x = (float)(x - cameraPos.func_82615_a());
      y = (float)(y - cameraPos.func_82617_b());
      z = (float)(z - cameraPos.func_82616_c());
      Vector3d iRotation = this.getInterpolatedRotation(partialTicks);
      Matrix4f offsetMatrix = new Matrix4f();
      offsetMatrix.func_226591_a_();
      offsetMatrix.func_226595_a_(Matrix4f.func_226599_b_(x, (float)(y + 1.25 + this.getYOffset(partialTicks) * 0.4), z));
      offsetMatrix.func_226596_a_(Vector3f.field_229179_b_.func_229187_a_((float)iRotation.func_82615_a()));
      offsetMatrix.func_226596_a_(Vector3f.field_229181_d_.func_229187_a_((float)iRotation.func_82617_b()));
      offsetMatrix.func_226596_a_(Vector3f.field_229183_f_.func_229187_a_((float)iRotation.func_82616_c()));
      offsetMatrix.func_226595_a_(Matrix4f.func_226593_a_(this.size, this.size, this.size));
      this.renderTexturedCube(buffer, offsetMatrix, color.getRed(), color.getGreen(), color.getBlue(), 255);
   }

   public IParticleRenderType func_217558_b() {
      return IParticleRenderType.field_217603_c;
   }

   private void renderTexturedCube(IVertexBuilder buf, Matrix4f offset, int r, int g, int b, int a) {
      int combinedLight = LightmapHelper.getPackedFullbrightCoords();
      TextureAtlasSprite tas = this.spriteSet.func_217590_a(rand);
      float minU = tas.func_94209_e();
      float minV = tas.func_94206_g();
      float maxU = tas.func_94212_f();
      float maxV = tas.func_94210_h();
      buf.func_227888_a_(offset, -0.5F, -0.5F, -0.5F).func_225583_a_(minU, minV).func_225586_a_(r, g, b, a).func_227886_a_(combinedLight).func_181675_d();
      buf.func_227888_a_(offset, 0.5F, -0.5F, -0.5F).func_225583_a_(maxU, minV).func_225586_a_(r, g, b, a).func_227886_a_(combinedLight).func_181675_d();
      buf.func_227888_a_(offset, 0.5F, -0.5F, 0.5F).func_225583_a_(maxU, maxV).func_225586_a_(r, g, b, a).func_227886_a_(combinedLight).func_181675_d();
      buf.func_227888_a_(offset, -0.5F, -0.5F, 0.5F).func_225583_a_(minU, maxV).func_225586_a_(r, g, b, a).func_227886_a_(combinedLight).func_181675_d();
      buf.func_227888_a_(offset, -0.5F, 0.5F, 0.5F).func_225583_a_(minU, minV).func_225586_a_(r, g, b, a).func_227886_a_(combinedLight).func_181675_d();
      buf.func_227888_a_(offset, 0.5F, 0.5F, 0.5F).func_225583_a_(maxU, minV).func_225586_a_(r, g, b, a).func_227886_a_(combinedLight).func_181675_d();
      buf.func_227888_a_(offset, 0.5F, 0.5F, -0.5F).func_225583_a_(maxU, maxV).func_225586_a_(r, g, b, a).func_227886_a_(combinedLight).func_181675_d();
      buf.func_227888_a_(offset, -0.5F, 0.5F, -0.5F).func_225583_a_(minU, maxV).func_225586_a_(r, g, b, a).func_227886_a_(combinedLight).func_181675_d();
      buf.func_227888_a_(offset, -0.5F, -0.5F, 0.5F).func_225583_a_(maxU, minV).func_225586_a_(r, g, b, a).func_227886_a_(combinedLight).func_181675_d();
      buf.func_227888_a_(offset, -0.5F, 0.5F, 0.5F).func_225583_a_(maxU, maxV).func_225586_a_(r, g, b, a).func_227886_a_(combinedLight).func_181675_d();
      buf.func_227888_a_(offset, -0.5F, 0.5F, -0.5F).func_225583_a_(minU, maxV).func_225586_a_(r, g, b, a).func_227886_a_(combinedLight).func_181675_d();
      buf.func_227888_a_(offset, -0.5F, -0.5F, -0.5F).func_225583_a_(minU, minV).func_225586_a_(r, g, b, a).func_227886_a_(combinedLight).func_181675_d();
      buf.func_227888_a_(offset, 0.5F, -0.5F, -0.5F).func_225583_a_(maxU, minV).func_225586_a_(r, g, b, a).func_227886_a_(combinedLight).func_181675_d();
      buf.func_227888_a_(offset, 0.5F, 0.5F, -0.5F).func_225583_a_(maxU, maxV).func_225586_a_(r, g, b, a).func_227886_a_(combinedLight).func_181675_d();
      buf.func_227888_a_(offset, 0.5F, 0.5F, 0.5F).func_225583_a_(minU, maxV).func_225586_a_(r, g, b, a).func_227886_a_(combinedLight).func_181675_d();
      buf.func_227888_a_(offset, 0.5F, -0.5F, 0.5F).func_225583_a_(minU, minV).func_225586_a_(r, g, b, a).func_227886_a_(combinedLight).func_181675_d();
      buf.func_227888_a_(offset, 0.5F, -0.5F, -0.5F).func_225583_a_(minU, minV).func_225586_a_(r, g, b, a).func_227886_a_(combinedLight).func_181675_d();
      buf.func_227888_a_(offset, -0.5F, -0.5F, -0.5F).func_225583_a_(maxU, minV).func_225586_a_(r, g, b, a).func_227886_a_(combinedLight).func_181675_d();
      buf.func_227888_a_(offset, -0.5F, 0.5F, -0.5F).func_225583_a_(maxU, maxV).func_225586_a_(r, g, b, a).func_227886_a_(combinedLight).func_181675_d();
      buf.func_227888_a_(offset, 0.5F, 0.5F, -0.5F).func_225583_a_(minU, maxV).func_225586_a_(r, g, b, a).func_227886_a_(combinedLight).func_181675_d();
      buf.func_227888_a_(offset, -0.5F, -0.5F, 0.5F).func_225583_a_(minU, minV).func_225586_a_(r, g, b, a).func_227886_a_(combinedLight).func_181675_d();
      buf.func_227888_a_(offset, 0.5F, -0.5F, 0.5F).func_225583_a_(maxU, minV).func_225586_a_(r, g, b, a).func_227886_a_(combinedLight).func_181675_d();
      buf.func_227888_a_(offset, 0.5F, 0.5F, 0.5F).func_225583_a_(maxU, maxV).func_225586_a_(r, g, b, a).func_227886_a_(combinedLight).func_181675_d();
      buf.func_227888_a_(offset, -0.5F, 0.5F, 0.5F).func_225583_a_(minU, maxV).func_225586_a_(r, g, b, a).func_227886_a_(combinedLight).func_181675_d();
   }

   public boolean shouldCull() {
      return false;
   }

   @OnlyIn(Dist.CLIENT)
   public static class Factory implements IParticleFactory<BasicParticleType> {
      private final IAnimatedSprite spriteSet;

      public Factory(IAnimatedSprite spriteSet) {
         this.spriteSet = spriteSet;
      }

      @Nullable
      public Particle makeParticle(BasicParticleType type, ClientWorld worldIn, double x, double y, double z, double xSpeed, double ySpeed, double zSpeed) {
         return new StabilizerCubeParticle(worldIn, this.spriteSet, x, y, z);
      }
   }
}
