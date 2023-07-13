package iskallia.vault.client.gui.framework.element;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.spi.IGuiEventElement;
import iskallia.vault.client.gui.framework.render.NineSlice;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.client.gui.framework.spatial.spi.ISize;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public class EntityModelElement<E extends EntityModelElement<E>> extends ElasticContainerElement<E> implements IGuiEventElement {
   protected final Supplier<LivingEntity> livingEntitySupplier;
   protected Vector3f entityOffset;
   protected float entityScale;
   protected boolean advancedControl;
   protected boolean dragging;
   protected int draggedButton;
   private float xYaw = 0.0F;
   private float yPitch = 0.0F;
   private double mouseX;
   private double mouseY;
   private double prevMouseX;
   private double prevMouseY;

   private double mouseDeltaX() {
      return this.mouseX - this.prevMouseX;
   }

   private double mouseDeltaY() {
      return this.mouseY - this.prevMouseY;
   }

   public EntityModelElement(IPosition position, ISize size, Supplier<LivingEntity> livingEntitySupplier) {
      this(position, size, livingEntitySupplier, IPosition.ZERO, 1.0F);
   }

   public EntityModelElement(IPosition position, ISize size, Supplier<LivingEntity> livingEntitySupplier, IPosition entityOffset, float entityScale) {
      this(position, size, livingEntitySupplier, entityOffset, entityScale, ScreenTextures.INSET_BLACK_BACKGROUND);
   }

   public EntityModelElement(
      IPosition position,
      ISize size,
      Supplier<LivingEntity> livingEntitySupplier,
      IPosition entityOffset,
      float entityScale,
      NineSlice.TextureRegion background
   ) {
      super(Spatials.positionXYZ(position));
      this.livingEntitySupplier = livingEntitySupplier;
      this.entityOffset = new Vector3f(entityOffset.x(), entityOffset.y(), entityOffset.z());
      this.entityScale = entityScale;
      this.addElement(new NineSliceElement(Spatials.size(size), background));
   }

   public EntityModelElement(IPosition position, ISize size, Supplier<LivingEntity> livingEntitySupplier, Vector3f entityOffset, float entityScale) {
      this(position, size, livingEntitySupplier, entityOffset, entityScale, ScreenTextures.INSET_BLACK_BACKGROUND);
   }

   public EntityModelElement(
      IPosition position, ISize size, Supplier<LivingEntity> livingEntitySupplier, Vector3f entityOffset, float entityScale, NineSlice.TextureRegion background
   ) {
      super(Spatials.positionXYZ(position));
      this.livingEntitySupplier = livingEntitySupplier;
      this.entityOffset = new Vector3f(entityOffset.x(), entityOffset.y(), entityOffset.z());
      this.entityScale = entityScale;
      this.addElement(new NineSliceElement(Spatials.size(size), background));
   }

   public EntityModelElement<?> advancedControl() {
      this.advancedControl = true;
      this.addElement(
         new LabelElement(
            Spatials.positionXY(2, 2).width(this.width()),
            new TextComponent("Scroll: Zoom").withStyle(ChatFormatting.DARK_GRAY),
            LabelTextStyle.defaultStyle().wrap()
         )
      );
      this.addElement(
         new LabelElement(
            Spatials.positionXY(2, 26).width(this.width()),
            new TextComponent("Right Click: Move").withStyle(ChatFormatting.DARK_GRAY),
            LabelTextStyle.defaultStyle().wrap()
         )
      );
      this.addElement(
         new LabelElement(
            Spatials.positionXY(2, 14).width(this.width()),
            new TextComponent("Left Click: Rotate").withStyle(ChatFormatting.DARK_GRAY),
            LabelTextStyle.defaultStyle().wrap()
         )
      );
      return this;
   }

   public boolean isDragging() {
      return this.dragging;
   }

   @Override
   public boolean onMouseClicked(double mouseX, double mouseY, int buttonIndex) {
      if (this.advancedControl && (buttonIndex == 0 || buttonIndex == 1)) {
         this.dragging = true;
         this.draggedButton = buttonIndex;
      }

      return true;
   }

   @Override
   public boolean mouseReleased(double mouseX, double mouseY, int buttonIndex) {
      if (this.advancedControl && (buttonIndex == 0 || buttonIndex == 1)) {
         this.dragging = false;
         this.draggedButton = -1;
      }

      return super.mouseReleased(mouseX, mouseY, buttonIndex);
   }

   @Override
   public boolean mouseDragged(double mouseX, double mouseY, int buttonIndex, double dragX, double dragY) {
      return !this.advancedControl
         ? super.mouseDragged(mouseX, mouseY, buttonIndex, dragX, dragY)
         : this.dragging && this.isEnabled() && this.onMouseDragged(mouseX, mouseY, buttonIndex, dragX, dragY);
   }

   @Override
   public boolean onMouseDragged(double mouseX, double mouseY, int buttonIndex, double dragX, double dragY) {
      if (this.advancedControl && buttonIndex == 1) {
         this.entityOffset = new Vector3f((float)(this.entityOffset.x() + dragX), (float)(this.entityOffset.y() + dragY), this.entityOffset.z());
         return true;
      } else {
         return super.onMouseDragged(mouseX, mouseY, buttonIndex, dragX, dragY);
      }
   }

   @Override
   public boolean onMouseScrolled(double mouseX, double mouseY, double delta) {
      if (this.advancedControl) {
         float increment = (float)(delta * 0.1F);
         float newScale = this.entityScale + increment;
         this.entityScale = Mth.clamp(newScale, 0.1F, 5.0F);
      }

      return super.onMouseScrolled(mouseX, mouseY, delta);
   }

   @Override
   public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
      super.render(renderer, poseStack, mouseX, mouseY, partialTick);
      this.prevMouseX = this.mouseX;
      this.prevMouseY = this.mouseY;
      this.mouseX = mouseX;
      this.mouseY = mouseY;
      ISpatial bounds = Spatials.copy(this.getWorldSpatial()).add(Spatials.positionXY(1, 1).size(-2, -2));
      renderer.beginClipRegion(bounds);
      LivingEntity livingEntity = this.livingEntitySupplier.get();
      if (livingEntity != null) {
         ISpatial worldSpatial = this.getWorldSpatial();
         float entityRenderX = worldSpatial.x() + worldSpatial.width() / 2.0F + this.entityOffset.x();
         float entityRenderY = worldSpatial.y() + (worldSpatial.height() - 16) + this.entityOffset.y();
         poseStack.pushPose();
         poseStack.translate(entityRenderX, entityRenderY, this.entityOffset.z());
         poseStack.scale(this.entityScale, this.entityScale, this.entityScale);
         this.renderEntity(livingEntity, poseStack, entityRenderX - mouseX, entityRenderY - mouseY - 50.0F);
         poseStack.popPose();
      }

      renderer.endClipRegion();
   }

   public void renderEntity(LivingEntity entity, PoseStack renderStack, float containerMouseX, float containerMouseY) {
      if (this.advancedControl) {
         boolean advancedRotation = this.dragging && this.draggedButton == 0;
         if (advancedRotation) {
            this.xYaw = (float)(this.xYaw - this.mouseDeltaX());
            this.yPitch = (float)(this.yPitch - this.mouseDeltaY());
         }
      } else {
         this.xYaw = (float)Math.atan(containerMouseX / 40.0F);
         this.yPitch = (float)Math.atan(containerMouseY / 40.0F);
      }

      PoseStack modelViewStack = RenderSystem.getModelViewStack();
      modelViewStack.pushPose();
      modelViewStack.translate(0.0, 0.0, 350.0);
      modelViewStack.scale(1.0F, 1.0F, -1.0F);
      RenderSystem.applyModelViewMatrix();
      renderStack.pushPose();
      renderStack.scale(30.0F, 30.0F, 30.0F);
      Quaternion rotationZ = Vector3f.ZP.rotationDegrees(180.0F);
      Quaternion rotationX = Vector3f.XP.rotationDegrees(this.advancedControl ? this.yPitch * 2.0F : this.yPitch * 20.0F);
      rotationZ.mul(rotationX);
      if (this.advancedControl) {
         renderStack.translate(0.0, -entity.getBbHeight(), 0.0);
         renderStack.translate(0.0, entity.getBbHeight() / 2.0F, 0.0);
      }

      renderStack.mulPose(rotationZ);
      if (this.advancedControl) {
         renderStack.translate(0.0, -(entity.getBbHeight() / 2.0F), 0.0);
      }

      float yBodyRot = entity.yBodyRot;
      float yRot = entity.getYRot();
      float xRot = entity.getXRot();
      float yHeadRotO = entity.yHeadRotO;
      float yHeadRot = entity.yHeadRot;
      entity.yBodyRot = 180.0F + this.xYaw * (this.advancedControl ? 3.0F : 20.0F);
      entity.setYRot(180.0F + this.xYaw * (this.advancedControl ? 3.0F : 40.0F));
      if (!this.advancedControl) {
         entity.setXRot(-this.yPitch * 20.0F);
         entity.yHeadRot = entity.getYRot();
         entity.yHeadRotO = entity.getYRot();
      } else {
         entity.yHeadRot = entity.yBodyRot;
         entity.yHeadRotO = entity.yBodyRot;
      }

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
      if (!this.advancedControl) {
         entity.yBodyRot = yBodyRot;
         entity.setYRot(yRot);
         entity.setXRot(xRot);
         entity.yHeadRotO = yHeadRotO;
         entity.yHeadRot = yHeadRot;
      }

      renderStack.popPose();
      modelViewStack.popPose();
      RenderSystem.applyModelViewMatrix();
      RenderSystem.enableDepthTest();
      Lighting.setupFor3DItems();
   }
}
