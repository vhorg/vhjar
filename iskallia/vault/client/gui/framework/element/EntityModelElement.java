package iskallia.vault.client.gui.framework.element;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.client.gui.framework.spatial.spi.ISize;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import java.util.function.Supplier;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class EntityModelElement<E extends EntityModelElement<E>> extends ElasticContainerElement<E> {
   protected final Supplier<LivingEntity> livingEntitySupplier;
   protected final IPosition entityOffset;
   protected final float entityScale;

   public EntityModelElement(IPosition position, ISize size, Supplier<LivingEntity> livingEntitySupplier) {
      this(position, size, livingEntitySupplier, IPosition.ZERO, 1.0F);
   }

   public EntityModelElement(IPosition position, ISize size, Supplier<LivingEntity> livingEntitySupplier, IPosition entityOffset, float entityScale) {
      super(Spatials.positionXYZ(position));
      this.livingEntitySupplier = livingEntitySupplier;
      this.entityOffset = entityOffset;
      this.entityScale = entityScale;
      this.addElement(new NineSliceElement(Spatials.size(size), ScreenTextures.INSET_BLACK_BACKGROUND));
   }

   @Override
   public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
      super.render(renderer, poseStack, mouseX, mouseY, partialTick);
      LivingEntity livingEntity = this.livingEntitySupplier.get();
      if (livingEntity != null) {
         ISpatial worldSpatial = this.getWorldSpatial();
         int playerRenderX = worldSpatial.x() + worldSpatial.width() / 2 + this.entityOffset.x();
         int playerRenderY = worldSpatial.y() + (worldSpatial.height() - 16) + this.entityOffset.y();
         poseStack.pushPose();
         poseStack.translate(playerRenderX, playerRenderY, this.entityOffset.z());
         poseStack.scale(this.entityScale, this.entityScale, this.entityScale);
         this.renderEntity(livingEntity, poseStack, playerRenderX - mouseX, playerRenderY - mouseY - 50);
         poseStack.popPose();
      }
   }

   public void renderEntity(LivingEntity entity, PoseStack renderStack, int containerMouseX, int containerMouseY) {
      float xYaw = (float)Math.atan(containerMouseX / 40.0F);
      float yPitch = (float)Math.atan(containerMouseY / 40.0F);
      PoseStack modelViewStack = RenderSystem.getModelViewStack();
      modelViewStack.pushPose();
      modelViewStack.translate(0.0, 0.0, 350.0);
      modelViewStack.scale(1.0F, 1.0F, -1.0F);
      RenderSystem.applyModelViewMatrix();
      renderStack.pushPose();
      renderStack.scale(30.0F, 30.0F, 30.0F);
      Quaternion rotationZ = Vector3f.ZP.rotationDegrees(180.0F);
      Quaternion rotationX = Vector3f.XP.rotationDegrees(yPitch * 20.0F);
      rotationZ.mul(rotationX);
      renderStack.mulPose(rotationZ);
      float yBodyRot = entity.yBodyRot;
      float yRot = entity.getYRot();
      float xRot = entity.getXRot();
      float yHeadRotO = entity.yHeadRotO;
      float yHeadRot = entity.yHeadRot;
      entity.yBodyRot = 180.0F + xYaw * 20.0F;
      entity.setYRot(180.0F + xYaw * 40.0F);
      entity.setXRot(-yPitch * 20.0F);
      entity.yHeadRot = entity.getYRot();
      entity.yHeadRotO = entity.getYRot();
      RenderSystem.setShaderLights(
         (Vector3f)Util.make(new Vector3f(0.2F, -1.0F, -1.0F), Vector3f::normalize), (Vector3f)Util.make(new Vector3f(0.0F, -0.5F, 1.0F), Vector3f::normalize)
      );
      EntityRenderDispatcher entityRenderDispatcher = Minecraft.getInstance().getEntityRenderDispatcher();
      rotationX.conj();
      entityRenderDispatcher.overrideCameraOrientation(rotationX);
      entityRenderDispatcher.setRenderShadow(false);
      BufferSource multiBufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
      RenderSystem.runAsFancy(() -> entityRenderDispatcher.render(entity, 0.0, 0.0, 0.0, 0.0F, 1.0F, renderStack, multiBufferSource, 15728880));
      multiBufferSource.endBatch();
      entityRenderDispatcher.setRenderShadow(true);
      entity.yBodyRot = yBodyRot;
      entity.setYRot(yRot);
      entity.setXRot(xRot);
      entity.yHeadRotO = yHeadRotO;
      entity.yHeadRot = yHeadRot;
      renderStack.popPose();
      modelViewStack.popPose();
      RenderSystem.applyModelViewMatrix();
      RenderSystem.enableDepthTest();
      Lighting.setupFor3DItems();
   }
}
