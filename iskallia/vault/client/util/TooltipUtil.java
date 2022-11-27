package iskallia.vault.client.util;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.BufferUploader;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.math.Matrix4f;
import java.util.List;
import java.util.stream.Collectors;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.RenderTooltipEvent.Color;
import net.minecraftforge.client.event.RenderTooltipEvent.Pre;

public final class TooltipUtil {
   public static void renderTooltip(
      PoseStack poseStack, List<? extends FormattedCharSequence> tooltips, int mouseX, int mouseY, int screenWidth, int screenHeight
   ) {
      renderTooltip(
         poseStack,
         tooltips.stream().<ClientTooltipComponent>map(ClientTooltipComponent::create).collect(Collectors.toList()),
         mouseX,
         mouseY,
         screenWidth,
         screenHeight,
         ItemStack.EMPTY,
         Minecraft.getInstance().font,
         false
      );
   }

   public static void renderTooltip(PoseStack pPoseStack, List<Component> pTooltips, int pMouseX, int pMouseY, Screen screen, boolean onLeft) {
      int width = screen.width;
      int height = screen.height;
      Font font = screen.getMinecraft().font;
      List<ClientTooltipComponent> components = ForgeHooksClient.gatherTooltipComponents(ItemStack.EMPTY, pTooltips, pMouseX, width, height, font, font);
      renderTooltip(pPoseStack, components, pMouseX, pMouseY, width, height, ItemStack.EMPTY, font, onLeft);
   }

   public static void renderTooltip(
      PoseStack poseStack,
      List<ClientTooltipComponent> clientTooltipComponents,
      int mouseX,
      int mouseY,
      int screenWidth,
      int screenHeight,
      ItemStack itemStack,
      Font tooltipFont,
      boolean flippedToTheLeft
   ) {
      if (!clientTooltipComponents.isEmpty()) {
         Pre preEvent = ForgeHooksClient.onRenderTooltipPre(
            itemStack, poseStack, mouseX, mouseY, screenWidth, screenHeight, clientTooltipComponents, tooltipFont, Minecraft.getInstance().font
         );
         if (preEvent.isCanceled()) {
            return;
         }

         int maxWidth = 0;
         int maxHeight = clientTooltipComponents.size() == 1 ? -2 : 0;

         for (ClientTooltipComponent clienttooltipcomponent : clientTooltipComponents) {
            int k = clienttooltipcomponent.getWidth(preEvent.getFont());
            if (k > maxWidth) {
               maxWidth = k;
            }

            maxHeight += clienttooltipcomponent.getHeight();
         }

         int x = preEvent.getX() + (flippedToTheLeft ? -12 : 12);
         int y = preEvent.getY() - 12;
         if (flippedToTheLeft) {
            x -= maxWidth;
         }

         if (x + maxWidth > screenWidth) {
            x -= 28 + maxWidth;
         }

         if (y + maxHeight + 6 > screenHeight) {
            y = screenHeight - maxHeight - 6;
         }

         int zOffset = 400;
         poseStack.pushPose();
         ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
         float originalBlitOffset = itemRenderer.blitOffset;
         itemRenderer.blitOffset = zOffset;
         Tesselator tesselator = Tesselator.getInstance();
         BufferBuilder bufferbuilder = tesselator.getBuilder();
         RenderSystem.setShader(GameRenderer::getPositionColorShader);
         bufferbuilder.begin(Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
         Matrix4f matrix4f = poseStack.last().pose();
         Color colorEvent = ForgeHooksClient.onRenderTooltipColor(itemStack, poseStack, x, y, preEvent.getFont(), clientTooltipComponents);
         fillGradient(matrix4f, bufferbuilder, x - 3, y - 4, x + maxWidth + 3, y - 3, zOffset, colorEvent.getBackgroundStart(), colorEvent.getBackgroundStart());
         fillGradient(
            matrix4f,
            bufferbuilder,
            x - 3,
            y + maxHeight + 3,
            x + maxWidth + 3,
            y + maxHeight + 4,
            zOffset,
            colorEvent.getBackgroundEnd(),
            colorEvent.getBackgroundEnd()
         );
         fillGradient(
            matrix4f, bufferbuilder, x - 3, y - 3, x + maxWidth + 3, y + maxHeight + 3, zOffset, colorEvent.getBackgroundStart(), colorEvent.getBackgroundEnd()
         );
         fillGradient(matrix4f, bufferbuilder, x - 4, y - 3, x - 3, y + maxHeight + 3, zOffset, colorEvent.getBackgroundStart(), colorEvent.getBackgroundEnd());
         fillGradient(
            matrix4f,
            bufferbuilder,
            x + maxWidth + 3,
            y - 3,
            x + maxWidth + 4,
            y + maxHeight + 3,
            zOffset,
            colorEvent.getBackgroundStart(),
            colorEvent.getBackgroundEnd()
         );
         fillGradient(
            matrix4f, bufferbuilder, x - 3, y - 3 + 1, x - 3 + 1, y + maxHeight + 3 - 1, zOffset, colorEvent.getBorderStart(), colorEvent.getBorderEnd()
         );
         fillGradient(
            matrix4f,
            bufferbuilder,
            x + maxWidth + 2,
            y - 3 + 1,
            x + maxWidth + 3,
            y + maxHeight + 3 - 1,
            zOffset,
            colorEvent.getBorderStart(),
            colorEvent.getBorderEnd()
         );
         fillGradient(matrix4f, bufferbuilder, x - 3, y - 3, x + maxWidth + 3, y - 3 + 1, zOffset, colorEvent.getBorderStart(), colorEvent.getBorderStart());
         fillGradient(
            matrix4f,
            bufferbuilder,
            x - 3,
            y + maxHeight + 2,
            x + maxWidth + 3,
            y + maxHeight + 3,
            zOffset,
            colorEvent.getBorderEnd(),
            colorEvent.getBorderEnd()
         );
         RenderSystem.enableDepthTest();
         RenderSystem.disableTexture();
         RenderSystem.enableBlend();
         RenderSystem.defaultBlendFunc();
         bufferbuilder.end();
         BufferUploader.end(bufferbuilder);
         RenderSystem.disableBlend();
         RenderSystem.enableTexture();
         BufferSource bufferSource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
         poseStack.translate(0.0, 0.0, zOffset);
         int l1 = y;

         for (int i2 = 0; i2 < clientTooltipComponents.size(); i2++) {
            ClientTooltipComponent component = clientTooltipComponents.get(i2);
            component.renderText(preEvent.getFont(), x, l1, matrix4f, bufferSource);
            l1 += component.getHeight() + (i2 == 0 ? 2 : 0);
         }

         bufferSource.endBatch();
         poseStack.popPose();
         l1 = y;

         for (int l2 = 0; l2 < clientTooltipComponents.size(); l2++) {
            ClientTooltipComponent component = clientTooltipComponents.get(l2);
            component.renderImage(preEvent.getFont(), x, l1, poseStack, itemRenderer, zOffset);
            l1 += component.getHeight() + (l2 == 0 ? 2 : 0);
         }

         itemRenderer.blitOffset = originalBlitOffset;
      }
   }

   private static void fillGradient(Matrix4f pMatrix, BufferBuilder pBuilder, int pX1, int pY1, int pX2, int pY2, int pBlitOffset, int pColorA, int pColorB) {
      float f = (pColorA >> 24 & 0xFF) / 255.0F;
      float f1 = (pColorA >> 16 & 0xFF) / 255.0F;
      float f2 = (pColorA >> 8 & 0xFF) / 255.0F;
      float f3 = (pColorA & 0xFF) / 255.0F;
      float f4 = (pColorB >> 24 & 0xFF) / 255.0F;
      float f5 = (pColorB >> 16 & 0xFF) / 255.0F;
      float f6 = (pColorB >> 8 & 0xFF) / 255.0F;
      float f7 = (pColorB & 0xFF) / 255.0F;
      pBuilder.vertex(pMatrix, pX2, pY1, pBlitOffset).color(f1, f2, f3, f).endVertex();
      pBuilder.vertex(pMatrix, pX1, pY1, pBlitOffset).color(f1, f2, f3, f).endVertex();
      pBuilder.vertex(pMatrix, pX1, pY2, pBlitOffset).color(f5, f6, f7, f4).endVertex();
      pBuilder.vertex(pMatrix, pX2, pY2, pBlitOffset).color(f5, f6, f7, f4).endVertex();
   }
}
