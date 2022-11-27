package iskallia.vault.client.gui.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import iskallia.vault.block.render.CryoChamberRenderer;
import iskallia.vault.client.gui.helper.AnimationManyPhased;
import iskallia.vault.client.gui.helper.Easing;
import iskallia.vault.entity.model.StatuePlayerModel;
import iskallia.vault.util.SkinProfile;
import java.util.LinkedList;
import java.util.Queue;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@Deprecated
@OnlyIn(Dist.CLIENT)
public class CheerOverlay {
   public static final int DISPLAY_DURATION = 6000;
   private static long prevUpdateTime = System.currentTimeMillis();
   private static AnimationManyPhased alphaAnimation = new AnimationManyPhased(6000)
      .withRange(0.0F, 0.2F, Easing.EASE_IN_OUT_SINE)
      .withRange(0.2F, 0.8F, Easing.CONSTANT_ONE)
      .withRange(0.8F, 1.0F, Easing.EASE_IN_OUT_SINE, true);
   public static Queue<CheerOverlay.Cheer> queuedCheers = new LinkedList<>();
   public static CheerOverlay.Cheer currentCheer;

   public static void receiveCheer(String nickname, boolean megahead) {
      CheerOverlay.Cheer cheer = new CheerOverlay.Cheer();
      cheer.skin.updateSkin(nickname);
      cheer.megahead = megahead;
      queuedCheers.add(cheer);
   }

   private static void drawSkin(int posX, int posY, int yRotation, SkinProfile skin, boolean megahead, float alpha) {
      Minecraft minecraft = Minecraft.getInstance();
      float scaleFactor = (float)minecraft.getWindow().getGuiScale();
      float scale = (megahead ? 180 : 205) / scaleFactor;
      float headScale = megahead ? 1.75F : 1.0F;
      PoseStack matrixStack = new PoseStack();
      matrixStack.translate(0.0, 0.0, 1000.0);
      matrixStack.scale(scale, scale, scale);
      Quaternion quaternion = Vector3f.ZP.rotationDegrees(200.0F);
      Quaternion quaternion1 = Vector3f.XP.rotationDegrees(45.0F);
      quaternion.mul(quaternion1);
      EntityRenderDispatcher entityrenderermanager = minecraft.getEntityRenderDispatcher();
      quaternion1.conj();
      entityrenderermanager.overrideCameraOrientation(quaternion1);
      entityrenderermanager.setRenderShadow(false);
      BufferSource irendertypebuffer$impl = minecraft.renderBuffers().bufferSource();
      StatuePlayerModel model = CryoChamberRenderer.PLAYER_MODEL;
      RenderSystem.enableBlend();
      int lighting = 15728640;
      int overlay = 983040;
      RenderSystem.runAsFancy(() -> {
         matrixStack.mulPose(Vector3f.XP.rotationDegrees(20.0F));
         matrixStack.mulPose(Vector3f.YN.rotationDegrees(yRotation));
         RenderType renderType = model.renderType(skin.getLocationSkin());
         VertexConsumer vertexBuilder = irendertypebuffer$impl.getBuffer(renderType);
         model.body.render(matrixStack, vertexBuilder, lighting, overlay, 1.0F, 1.0F, 1.0F, alpha);
         model.leftLeg.render(matrixStack, vertexBuilder, lighting, overlay, 1.0F, 1.0F, 1.0F, alpha);
         model.rightLeg.render(matrixStack, vertexBuilder, lighting, overlay, 1.0F, 1.0F, 1.0F, alpha);
         model.leftArm.render(matrixStack, vertexBuilder, lighting, overlay, 1.0F, 1.0F, 1.0F, alpha);
         model.rightArm.render(matrixStack, vertexBuilder, lighting, overlay, 1.0F, 1.0F, 1.0F, alpha);
         model.jacket.render(matrixStack, vertexBuilder, lighting, overlay, 1.0F, 1.0F, 1.0F, alpha);
         model.leftPants.render(matrixStack, vertexBuilder, lighting, overlay, 1.0F, 1.0F, 1.0F, alpha);
         model.rightPants.render(matrixStack, vertexBuilder, lighting, overlay, 1.0F, 1.0F, 1.0F, alpha);
         model.leftSleeve.render(matrixStack, vertexBuilder, lighting, overlay, 1.0F, 1.0F, 1.0F, alpha);
         matrixStack.pushPose();
         matrixStack.translate(0.0, 0.0, -0.62F);
         model.rightSleeve.render(matrixStack, vertexBuilder, lighting, overlay, 1.0F, 1.0F, 1.0F, alpha);
         matrixStack.popPose();
         matrixStack.scale(headScale, headScale, headScale);
         model.hat.render(matrixStack, vertexBuilder, lighting, overlay, 1.0F, 1.0F, 1.0F, alpha);
         model.head.render(matrixStack, vertexBuilder, lighting, overlay, 1.0F, 1.0F, 1.0F, alpha);
         matrixStack.popPose();
      });
      irendertypebuffer$impl.endBatch();
      entityrenderermanager.setRenderShadow(true);
   }

   private static void drawLabel(PoseStack matrixStack, int x, int y, String text, int color) {
      Minecraft minecraft = Minecraft.getInstance();
      Font fontRenderer = minecraft.font;
      float scaleFactor = (float)minecraft.getWindow().getGuiScale();
      float textScale = 3.0F / scaleFactor;
      float textWidth = fontRenderer.width(text);
      float textHeight = 7.0F;
      float skinOffsetY = 220.0F / scaleFactor;
      matrixStack.pushPose();
      matrixStack.translate(x, y - skinOffsetY, 0.0);
      matrixStack.scale(textScale, textScale, textScale);
      float horizontalGap = 5.0F;
      float verticalGap = 4.0F;
      int rectX = (int)(-textWidth / 2.0F - horizontalGap);
      int rectY = (int)(-textHeight / 2.0F - verticalGap);
      int rectWidth = (int)(textWidth + 2.0F * horizontalGap);
      int rectHeight = (int)(textHeight + 2.0F * verticalGap);
      GuiComponent.fill(matrixStack, rectX, rectY, rectX + rectWidth, rectY + rectHeight, -1442840576);
      fontRenderer.draw(matrixStack, text, -textWidth / 2.0F, -textHeight / 2.0F, color);
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
      matrixStack.popPose();
   }

   public static class Cheer {
      public SkinProfile skin = new SkinProfile();
      public boolean megahead;
   }
}
