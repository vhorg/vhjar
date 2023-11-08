package iskallia.vault.client.gui.helper;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import java.awt.Rectangle;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.StringSplitter;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.locale.Language;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.lwjgl.opengl.GL11;

public class UIHelper {
   public static final ResourceLocation UI_RESOURCE = new ResourceLocation("the_vault", "textures/gui/ability_tree.png");
   private static final UIHelper.OverflowHiddenMode OVERFLOW_HIDDEN_MODE = UIHelper.OverflowHiddenMode.DEPTH;
   private static final int[] LINE_BREAK_VALUES = new int[]{0, 10, -10, 25, -25};

   public static void renderOverflowHidden(PoseStack matrixStack, Consumer<PoseStack> backgroundRenderer, Consumer<PoseStack> innerRenderer) {
      if (OVERFLOW_HIDDEN_MODE == UIHelper.OverflowHiddenMode.STENCIL) {
         Minecraft.getInstance().getMainRenderTarget().enableStencil();
         GL11.glEnable(2960);
         matrixStack.pushPose();
         RenderSystem.disableDepthTest();
         RenderSystem.clear(1024, false);
         RenderSystem.clearStencil(0);
         RenderSystem.stencilOp(7680, 7680, 7681);
         RenderSystem.stencilFunc(519, 1, 255);
         RenderSystem.stencilMask(255);
         backgroundRenderer.accept(matrixStack);
         RenderSystem.stencilFunc(514, 1, 255);
         RenderSystem.stencilMask(0);
         innerRenderer.accept(matrixStack);
         RenderSystem.disableDepthTest();
         matrixStack.popPose();
         GL11.glDisable(2960);
      } else if (OVERFLOW_HIDDEN_MODE == UIHelper.OverflowHiddenMode.DEPTH) {
         matrixStack.pushPose();
         RenderSystem.enableDepthTest();
         matrixStack.translate(0.0, 0.0, 950.0);
         RenderSystem.colorMask(false, false, false, false);
         GuiComponent.fill(matrixStack, 4680, 2260, -4680, -2260, -16777216);
         RenderSystem.colorMask(true, true, true, true);
         matrixStack.translate(0.0, 0.0, -950.0);
         RenderSystem.depthFunc(518);
         backgroundRenderer.accept(matrixStack);
         RenderSystem.depthFunc(515);
         innerRenderer.accept(matrixStack);
         RenderSystem.depthFunc(518);
         matrixStack.translate(0.0, 0.0, -950.0);
         RenderSystem.colorMask(false, false, false, false);
         GuiComponent.fill(matrixStack, 4680, 2260, -4680, -2260, -16777216);
         RenderSystem.colorMask(true, true, true, true);
         matrixStack.translate(0.0, 0.0, 950.0);
         RenderSystem.depthFunc(515);
         RenderSystem.disableDepthTest();
         matrixStack.popPose();
      }
   }

   public static void drawFacingPlayer(PoseStack renderStack, int containerMouseX, int containerMouseY) {
      Player player = Minecraft.getInstance().player;
      if (player != null) {
         drawFacingEntity(player, renderStack, containerMouseX, containerMouseY);
      }
   }

   public static void drawFacingEntity(LivingEntity entity, PoseStack renderStack, int containerMouseX, int containerMouseY) {
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
   }

   public static void renderContainerBorder(
      GuiComponent gui, PoseStack matrixStack, Rectangle screenBounds, int u, int v, int lw, int rw, int th, int bh, int contentColor
   ) {
      int width = screenBounds.width;
      int height = screenBounds.height;
      renderContainerBorder(gui, matrixStack, screenBounds.x, screenBounds.y, width, height, u, v, lw, rw, th, bh, contentColor);
   }

   public static void renderContainerBorder(
      GuiComponent gui, PoseStack matrixStack, int x, int y, int width, int height, int u, int v, int lw, int rw, int th, int bh, int contentColor
   ) {
      int horizontalGap = width - lw - rw;
      int verticalGap = height - th - bh;
      if (contentColor != 0) {
         GuiComponent.fill(matrixStack, x + lw, y + th, x + lw + horizontalGap, y + th + verticalGap, contentColor);
      }

      gui.blit(matrixStack, x, y, u, v, lw, th);
      gui.blit(matrixStack, x + lw + horizontalGap, y, u + lw + 3, v, rw, th);
      gui.blit(matrixStack, x, y + th + verticalGap, u, v + th + 3, lw, bh);
      gui.blit(matrixStack, x + lw + horizontalGap, y + th + verticalGap, u + lw + 3, v + th + 3, rw, bh);
      matrixStack.pushPose();
      matrixStack.translate(x + lw, y, 0.0);
      matrixStack.scale(horizontalGap, 1.0F, 1.0F);
      gui.blit(matrixStack, 0, 0, u + lw + 1, v, 1, th);
      matrixStack.translate(0.0, th + verticalGap, 0.0);
      gui.blit(matrixStack, 0, 0, u + lw + 1, v + th + 3, 1, bh);
      matrixStack.popPose();
      matrixStack.pushPose();
      matrixStack.translate(x, y + th, 0.0);
      matrixStack.scale(1.0F, verticalGap, 1.0F);
      gui.blit(matrixStack, 0, 0, u, v + th + 1, lw, 1);
      matrixStack.translate(lw + horizontalGap, 0.0, 0.0);
      gui.blit(matrixStack, 0, 0, u + lw + 3, v + th + 1, rw, 1);
      matrixStack.popPose();
   }

   public static void renderLabelAtRight(GuiComponent gui, PoseStack matrixStack, String text, int x, int y) {
      Minecraft minecraft = Minecraft.getInstance();
      RenderSystem.setShader(GameRenderer::getPositionTexShader);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      RenderSystem.setShaderTexture(0, UI_RESOURCE);
      Font fontRenderer = minecraft.font;
      int textWidth = fontRenderer.width(text);
      matrixStack.pushPose();
      matrixStack.translate(x, y, 0.0);
      float scale = 0.75F;
      matrixStack.scale(scale, scale, scale);
      matrixStack.translate(-9.0, 0.0, 0.0);
      gui.blit(matrixStack, 0, 0, 143, 36, 9, 24);
      int gap = 5;
      int remainingWidth = textWidth + 2 * gap;
      matrixStack.translate(-remainingWidth, 0.0, 0.0);

      while (remainingWidth > 0) {
         gui.blit(matrixStack, 0, 0, 136, 36, 6, 24);
         remainingWidth -= 6;
         matrixStack.translate(Math.min(6, remainingWidth), 0.0, 0.0);
      }

      matrixStack.translate(-textWidth - 2 * gap - 6, 0.0, 0.0);
      gui.blit(matrixStack, 0, 0, 121, 36, 14, 24);
      fontRenderer.draw(matrixStack, text, 14 + gap, 9.0F, -12305893);
      matrixStack.popPose();
   }

   public static int renderCenteredWrappedText(PoseStack matrixStack, Component text, int maxWidth, int padding) {
      Minecraft minecraft = Minecraft.getInstance();
      Font fontRenderer = minecraft.font;
      List<FormattedText> lines = getLines(ComponentUtils.mergeStyles(text.copy(), text.getStyle()), maxWidth - 3 * padding);
      int length = lines.stream().mapToInt(fontRenderer::width).max().orElse(0);
      List<FormattedCharSequence> processors = Language.getInstance().getVisualOrder(lines);
      matrixStack.pushPose();
      matrixStack.translate(-length / 2.0F, 0.0, 0.0);

      for (int i = 0; i < processors.size(); i++) {
         fontRenderer.draw(matrixStack, processors.get(i), padding, 10 * i + padding, -15130590);
      }

      matrixStack.popPose();
      return processors.size();
   }

   public static int renderWrappedText(PoseStack matrixStack, Component text, int maxWidth, int padding) {
      return renderWrappedText(matrixStack, text, maxWidth, padding, -15130590);
   }

   public static int renderWrappedText(PoseStack matrixStack, Component text, int maxWidth, int padding, int color) {
      Minecraft minecraft = Minecraft.getInstance();
      Font fontRenderer = minecraft.font;
      List<FormattedText> lines = getLines(ComponentUtils.mergeStyles(text.copy(), text.getStyle()), maxWidth - 3 * padding);
      List<FormattedCharSequence> processors = Language.getInstance().getVisualOrder(lines);

      for (int i = 0; i < processors.size(); i++) {
         fontRenderer.draw(matrixStack, processors.get(i), padding, 10 * i + padding, color);
      }

      return processors.size();
   }

   private static List<FormattedText> getLines(Component component, int maxWidth) {
      Minecraft minecraft = Minecraft.getInstance();
      StringSplitter charactermanager = minecraft.font.getSplitter();
      List<FormattedText> list = null;
      float f = Float.MAX_VALUE;

      for (int i : LINE_BREAK_VALUES) {
         List<FormattedText> list1 = charactermanager.splitLines(component, maxWidth - i, Style.EMPTY);
         float f1 = Math.abs(getTextWidth(charactermanager, list1) - maxWidth);
         if (f1 <= 10.0F) {
            return list1;
         }

         if (f1 < f) {
            f = f1;
            list = list1;
         }
      }

      return list;
   }

   private static float getTextWidth(StringSplitter manager, List<FormattedText> text) {
      return (float)text.stream().mapToDouble(manager::stringWidth).max().orElse(0.0);
   }

   public static String formatTimeString(long remainingTicks) {
      long seconds = remainingTicks / 20L % 60L;
      long minutes = remainingTicks / 20L / 60L % 60L;
      long hours = remainingTicks / 20L / 60L / 60L;
      return hours > 0L ? String.format("%02d:%02d:%02d", hours, minutes, seconds) : String.format("%02d:%02d", minutes, seconds);
   }

   private static enum OverflowHiddenMode {
      DEPTH,
      STENCIL;
   }
}
