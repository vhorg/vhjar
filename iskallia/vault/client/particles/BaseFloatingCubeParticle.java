package iskallia.vault.client.particles;

import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import iskallia.vault.client.gui.helper.LightmapHelper;
import iskallia.vault.client.util.color.ColorUtil;
import java.awt.Color;
import java.util.Random;
import net.minecraft.client.Camera;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.Particle;
import net.minecraft.client.particle.ParticleRenderType;
import net.minecraft.client.particle.SpriteSet;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.phys.Vec3;

public abstract class BaseFloatingCubeParticle extends Particle {
   private static final Random rand = new Random();
   private final BlockPos originPos;
   private final float size;
   private final SpriteSet spriteSet;
   private float effectPercent = 0.0F;
   private float prevEffectPercent = 0.0F;
   private final Vec3 rotationChange;
   private Vec3 rotationDegreeAxis;
   private Vec3 prevRotationDegreeAxis = Vec3.ZERO;

   protected BaseFloatingCubeParticle(ClientLevel world, SpriteSet spriteSet, double x, double y, double z) {
      super(world, x, y, z);
      this.spriteSet = spriteSet;
      this.originPos = new BlockPos(x, y, z);
      this.size = 0.45F;
      Vec3 change = new Vec3(
         rand.nextFloat() * (rand.nextBoolean() ? 1 : -1), rand.nextFloat() * (rand.nextBoolean() ? 1 : -1), rand.nextFloat() * (rand.nextBoolean() ? 1 : -1)
      );
      this.rotationChange = change.multiply(5.0, 5.0, 5.0);
      Vec3 axis = new Vec3(rand.nextFloat() * (rand.nextBoolean() ? 1 : -1), rand.nextFloat(), rand.nextFloat() * (rand.nextBoolean() ? 1 : -1));
      this.rotationDegreeAxis = axis.multiply(18.0, 18.0, 18.0);
   }

   public void tick() {
      this.xo = this.x;
      this.yo = this.y;
      this.zo = this.z;
      this.oRoll = this.roll;
      if (this.isAlive()) {
         if (!this.isValid()) {
            this.remove();
         } else {
            this.prevEffectPercent = this.effectPercent;
            if (this.isActive()) {
               this.effectPercent = Math.min(this.effectPercent + 0.02F, 1.0F);
            } else {
               this.effectPercent = Math.max(this.effectPercent - 0.01F, 0.0F);
            }

            this.updateRotations();
         }
      }
   }

   private void updateRotations() {
      if (this.effectPercent > 0.0F && this.rotationChange.lengthSqr() > 0.0) {
         Vec3 modify = this.rotationChange.multiply(this.effectPercent, this.effectPercent, this.effectPercent);
         this.prevRotationDegreeAxis = this.rotationDegreeAxis.scale(1.0);
         this.rotationDegreeAxis = this.rotationDegreeAxis.add(modify);
         this.rotationDegreeAxis = new Vec3(this.rotationDegreeAxis.x() % 360.0, this.rotationDegreeAxis.y() % 360.0, this.rotationDegreeAxis.z() % 360.0);
         if (!this.rotationDegreeAxis.add(modify).equals(this.rotationDegreeAxis)) {
            this.prevRotationDegreeAxis = this.rotationDegreeAxis.subtract(modify);
         }
      } else {
         this.prevRotationDegreeAxis = this.rotationDegreeAxis.scale(1.0);
      }
   }

   protected abstract boolean isValid();

   protected abstract boolean isActive();

   private Vec3 getInterpolatedRotation(float partialTicks) {
      return new Vec3(
         Mth.lerp(partialTicks, this.prevRotationDegreeAxis.x(), this.rotationDegreeAxis.x()),
         Mth.lerp(partialTicks, this.prevRotationDegreeAxis.y(), this.rotationDegreeAxis.y()),
         Mth.lerp(partialTicks, this.prevRotationDegreeAxis.z(), this.rotationDegreeAxis.z())
      );
   }

   private double getYOffset(float partialTicks) {
      double offset = (Math.sin(this.effectPercent * Math.PI + (Math.PI * 3.0 / 2.0)) + 1.0) / 2.0;
      double offsetPrev = (Math.sin(this.prevEffectPercent * Math.PI + (Math.PI * 3.0 / 2.0)) + 1.0) / 2.0;
      return Mth.lerp(partialTicks, offsetPrev, offset);
   }

   public void render(VertexConsumer buffer, Camera ari, float partialTicks) {
      float effectPart = Mth.lerp(partialTicks, this.prevEffectPercent, this.effectPercent);
      Color color = new Color(ColorUtil.blendColors(this.getActiveColor(), 5263440, effectPart));
      float x = (float)Mth.lerp(partialTicks, this.xo, this.x);
      float y = (float)Mth.lerp(partialTicks, this.yo, this.y);
      float z = (float)Mth.lerp(partialTicks, this.zo, this.z);
      Vec3 cameraPos = ari.getPosition();
      x = (float)(x - cameraPos.x());
      y = (float)(y - cameraPos.y());
      z = (float)(z - cameraPos.z());
      Vec3 iRotation = this.getInterpolatedRotation(partialTicks);
      Matrix4f offsetMatrix = new Matrix4f();
      offsetMatrix.setIdentity();
      offsetMatrix.multiply(Matrix4f.createTranslateMatrix(x, (float)(y + 1.25 + this.getYOffset(partialTicks) * 0.4), z));
      offsetMatrix.multiply(Vector3f.XP.rotationDegrees((float)iRotation.x()));
      offsetMatrix.multiply(Vector3f.YP.rotationDegrees((float)iRotation.y()));
      offsetMatrix.multiply(Vector3f.ZP.rotationDegrees((float)iRotation.z()));
      offsetMatrix.multiply(Matrix4f.createScaleMatrix(this.size, this.size, this.size));
      this.renderTexturedCube(buffer, offsetMatrix, color.getRed(), color.getGreen(), color.getBlue(), 255);
   }

   protected abstract int getActiveColor();

   public ParticleRenderType getRenderType() {
      return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
   }

   private void renderTexturedCube(VertexConsumer buf, Matrix4f offset, int r, int g, int b, int a) {
      int combinedLight = LightmapHelper.getPackedFullbrightCoords();
      TextureAtlasSprite tas = this.spriteSet.get(rand);
      float minU = tas.getU0();
      float minV = tas.getV0();
      float maxU = tas.getU1();
      float maxV = tas.getV1();
      buf.vertex(offset, -0.5F, -0.5F, -0.5F).uv(minU, minV).color(r, g, b, a).uv2(combinedLight).endVertex();
      buf.vertex(offset, 0.5F, -0.5F, -0.5F).uv(maxU, minV).color(r, g, b, a).uv2(combinedLight).endVertex();
      buf.vertex(offset, 0.5F, -0.5F, 0.5F).uv(maxU, maxV).color(r, g, b, a).uv2(combinedLight).endVertex();
      buf.vertex(offset, -0.5F, -0.5F, 0.5F).uv(minU, maxV).color(r, g, b, a).uv2(combinedLight).endVertex();
      buf.vertex(offset, -0.5F, 0.5F, 0.5F).uv(minU, minV).color(r, g, b, a).uv2(combinedLight).endVertex();
      buf.vertex(offset, 0.5F, 0.5F, 0.5F).uv(maxU, minV).color(r, g, b, a).uv2(combinedLight).endVertex();
      buf.vertex(offset, 0.5F, 0.5F, -0.5F).uv(maxU, maxV).color(r, g, b, a).uv2(combinedLight).endVertex();
      buf.vertex(offset, -0.5F, 0.5F, -0.5F).uv(minU, maxV).color(r, g, b, a).uv2(combinedLight).endVertex();
      buf.vertex(offset, -0.5F, -0.5F, 0.5F).uv(maxU, minV).color(r, g, b, a).uv2(combinedLight).endVertex();
      buf.vertex(offset, -0.5F, 0.5F, 0.5F).uv(maxU, maxV).color(r, g, b, a).uv2(combinedLight).endVertex();
      buf.vertex(offset, -0.5F, 0.5F, -0.5F).uv(minU, maxV).color(r, g, b, a).uv2(combinedLight).endVertex();
      buf.vertex(offset, -0.5F, -0.5F, -0.5F).uv(minU, minV).color(r, g, b, a).uv2(combinedLight).endVertex();
      buf.vertex(offset, 0.5F, -0.5F, -0.5F).uv(maxU, minV).color(r, g, b, a).uv2(combinedLight).endVertex();
      buf.vertex(offset, 0.5F, 0.5F, -0.5F).uv(maxU, maxV).color(r, g, b, a).uv2(combinedLight).endVertex();
      buf.vertex(offset, 0.5F, 0.5F, 0.5F).uv(minU, maxV).color(r, g, b, a).uv2(combinedLight).endVertex();
      buf.vertex(offset, 0.5F, -0.5F, 0.5F).uv(minU, minV).color(r, g, b, a).uv2(combinedLight).endVertex();
      buf.vertex(offset, 0.5F, -0.5F, -0.5F).uv(minU, minV).color(r, g, b, a).uv2(combinedLight).endVertex();
      buf.vertex(offset, -0.5F, -0.5F, -0.5F).uv(maxU, minV).color(r, g, b, a).uv2(combinedLight).endVertex();
      buf.vertex(offset, -0.5F, 0.5F, -0.5F).uv(maxU, maxV).color(r, g, b, a).uv2(combinedLight).endVertex();
      buf.vertex(offset, 0.5F, 0.5F, -0.5F).uv(minU, maxV).color(r, g, b, a).uv2(combinedLight).endVertex();
      buf.vertex(offset, -0.5F, -0.5F, 0.5F).uv(minU, minV).color(r, g, b, a).uv2(combinedLight).endVertex();
      buf.vertex(offset, 0.5F, -0.5F, 0.5F).uv(maxU, minV).color(r, g, b, a).uv2(combinedLight).endVertex();
      buf.vertex(offset, 0.5F, 0.5F, 0.5F).uv(maxU, maxV).color(r, g, b, a).uv2(combinedLight).endVertex();
      buf.vertex(offset, -0.5F, 0.5F, 0.5F).uv(minU, maxV).color(r, g, b, a).uv2(combinedLight).endVertex();
   }

   public boolean shouldCull() {
      return false;
   }
}
