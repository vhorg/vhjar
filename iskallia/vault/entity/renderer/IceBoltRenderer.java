package iskallia.vault.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.math.Matrix3f;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import iskallia.vault.VaultMod;
import iskallia.vault.entity.entity.IceBoltEntity;
import net.minecraft.client.model.Model;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.client.model.geom.PartPose;
import net.minecraft.client.model.geom.builders.CubeDeformation;
import net.minecraft.client.model.geom.builders.CubeListBuilder;
import net.minecraft.client.model.geom.builders.LayerDefinition;
import net.minecraft.client.model.geom.builders.MeshDefinition;
import net.minecraft.client.model.geom.builders.PartDefinition;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class IceBoltRenderer extends EntityRenderer<IceBoltEntity> {
   public static final ResourceLocation ARROW_TEXTURE = VaultMod.id("textures/entity/blizzard_shard.png");
   public static final ResourceLocation CHUNK_TEXTURE = VaultMod.id("textures/entity/ice_bolt_chunk.png");
   private final IceBoltRenderer.ChunkModel chunk = new IceBoltRenderer.ChunkModel(IceBoltRenderer.ChunkModel.getMesh().bakeRoot());

   public IceBoltRenderer(Context context) {
      super(context);
   }

   public ResourceLocation getTextureLocation(IceBoltEntity entity) {
      return null;
   }

   public void render(IceBoltEntity entity, float yaw, float partialTicks, PoseStack matrices, MultiBufferSource buffers, int light) {
      if (entity.getModel() == IceBoltEntity.Model.ARROW) {
         this.renderArrow(entity, partialTicks, matrices, buffers, light);
      } else if (entity.getModel() == IceBoltEntity.Model.CHUNK) {
         matrices.pushPose();
         matrices.mulPose(Vector3f.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) - 90.0F));
         matrices.mulPose(Vector3f.ZP.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot()) + 90.0F));
         matrices.mulPose(Vector3f.YP.rotationDegrees(Mth.lerp(partialTicks, (entity.tickCount - 1) * 15.0F, entity.tickCount * 15.0F)));
         VertexConsumer buffer = buffers.getBuffer(RenderType.entityTranslucent(CHUNK_TEXTURE));
         this.chunk.renderToBuffer(matrices, buffer, 15728880, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 0.4F);
         matrices.popPose();
      }

      super.render(entity, yaw, partialTicks, matrices, buffers, light);
   }

   private void renderArrow(IceBoltEntity entity, float partialTicks, PoseStack matrices, MultiBufferSource buffers, int light) {
      matrices.pushPose();
      matrices.mulPose(Vector3f.YP.rotationDegrees(Mth.lerp(partialTicks, entity.yRotO, entity.getYRot()) - 90.0F));
      matrices.mulPose(Vector3f.ZP.rotationDegrees(Mth.lerp(partialTicks, entity.xRotO, entity.getXRot())));
      float elapsed = entity.shakeTime - partialTicks;
      if (elapsed > 0.0F) {
         float f10 = -Mth.sin(elapsed * 3.0F) * elapsed;
         matrices.mulPose(Vector3f.ZP.rotationDegrees(f10));
      }

      matrices.mulPose(Vector3f.XP.rotationDegrees(45.0F));
      matrices.scale(0.05625F, 0.05625F, 0.05625F);
      matrices.translate(-4.0, 0.0, 0.0);
      VertexConsumer buffer = buffers.getBuffer(RenderType.entityCutout(ARROW_TEXTURE));
      Pose last = matrices.last();
      Matrix4f matrix4f = last.pose();
      Matrix3f matrix3f = last.normal();
      this.vertex(matrix4f, matrix3f, buffer, -7, -2, -2, 0.0F, 0.15625F, -1, 0, 0, light);
      this.vertex(matrix4f, matrix3f, buffer, -7, -2, 2, 0.15625F, 0.15625F, -1, 0, 0, light);
      this.vertex(matrix4f, matrix3f, buffer, -7, 2, 2, 0.15625F, 0.3125F, -1, 0, 0, light);
      this.vertex(matrix4f, matrix3f, buffer, -7, 2, -2, 0.0F, 0.3125F, -1, 0, 0, light);
      this.vertex(matrix4f, matrix3f, buffer, -7, 2, -2, 0.0F, 0.15625F, 1, 0, 0, light);
      this.vertex(matrix4f, matrix3f, buffer, -7, 2, 2, 0.15625F, 0.15625F, 1, 0, 0, light);
      this.vertex(matrix4f, matrix3f, buffer, -7, -2, 2, 0.15625F, 0.3125F, 1, 0, 0, light);
      this.vertex(matrix4f, matrix3f, buffer, -7, -2, -2, 0.0F, 0.3125F, 1, 0, 0, light);

      for (int j = 0; j < 4; j++) {
         matrices.mulPose(Vector3f.XP.rotationDegrees(90.0F));
         this.vertex(matrix4f, matrix3f, buffer, -8, -2, 0, 0.0F, 0.0F, 0, 1, 0, light);
         this.vertex(matrix4f, matrix3f, buffer, 8, -2, 0, 0.5F, 0.0F, 0, 1, 0, light);
         this.vertex(matrix4f, matrix3f, buffer, 8, 2, 0, 0.5F, 0.15625F, 0, 1, 0, light);
         this.vertex(matrix4f, matrix3f, buffer, -8, 2, 0, 0.0F, 0.15625F, 0, 1, 0, light);
      }

      matrices.popPose();
   }

   public void vertex(
      Matrix4f pose, Matrix3f normal, VertexConsumer buffer, int x, int y, int z, float u, float v, int normalX, int normalZ, int normalY, int light
   ) {
      buffer.vertex(pose, x, y, z)
         .color(255, 255, 255, 255)
         .uv(u, v)
         .overlayCoords(OverlayTexture.NO_OVERLAY)
         .uv2(light)
         .normal(normal, normalX, normalY, normalZ)
         .endVertex();
   }

   public static class ChunkModel extends Model {
      protected final ModelPart main;

      public ChunkModel(ModelPart root) {
         super(RenderType::entityCutoutNoCull);
         this.main = root.getChild("main");
      }

      public void renderToBuffer(PoseStack matrices, VertexConsumer buffer, int light, int overlay, float red, float green, float blue, float alpha) {
         this.main.render(matrices, buffer, light, overlay, red, green, blue, alpha);
      }

      public static LayerDefinition getMesh() {
         MeshDefinition data = new MeshDefinition();
         PartDefinition root = data.getRoot();
         root.addOrReplaceChild(
            "main",
            CubeListBuilder.create()
               .texOffs(0, 16)
               .addBox(-4.0F, -12.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(0, 36)
               .addBox(-4.0F, -4.0F, -4.0F, 8.0F, 8.0F, 8.0F, new CubeDeformation(0.0F))
               .texOffs(0, 34)
               .addBox(-5.0F, -9.0F, -5.0F, 10.0F, 8.0F, 10.0F, new CubeDeformation(0.0F))
               .texOffs(26, 10)
               .addBox(-3.0F, -11.0F, -3.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(40, 38)
               .addBox(-3.0F, 1.0F, -3.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F))
               .texOffs(40, 26)
               .addBox(-3.0F, -5.0F, -3.0F, 6.0F, 6.0F, 6.0F, new CubeDeformation(0.0F)),
            PartPose.offset(0.0F, 7.0F, 0.0F)
         );
         return LayerDefinition.create(data, 64, 64);
      }
   }
}
