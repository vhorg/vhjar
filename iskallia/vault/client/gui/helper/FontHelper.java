package iskallia.vault.client.gui.helper;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.math.Matrix4f;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.FormattedCharSequence;

public class FontHelper {
   public static float drawStringWithBorder(PoseStack matrixStack, String text, float x, float y, int color, int borderColor) {
      return drawStringWithBorder(matrixStack, new TextComponent(text), x, y, color, borderColor);
   }

   public static float drawStringWithBorder(PoseStack matrixStack, Component text, float x, float y, int color, int borderColor) {
      FormattedCharSequence formattedCharSequence = text.getVisualOrderText();
      Minecraft minecraft = Minecraft.getInstance();
      Matrix4f matrix = matrixStack.last().pose();
      BufferSource bufferSource = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
      minecraft.font.drawInBatch(formattedCharSequence, x - 1.0F, y, borderColor, false, matrix, bufferSource, false, 0, 15728880);
      minecraft.font.drawInBatch(formattedCharSequence, x + 1.0F, y, borderColor, false, matrix, bufferSource, false, 0, 15728880);
      minecraft.font.drawInBatch(formattedCharSequence, x, y - 1.0F, borderColor, false, matrix, bufferSource, false, 0, 15728880);
      minecraft.font.drawInBatch(formattedCharSequence, x, y + 1.0F, borderColor, false, matrix, bufferSource, false, 0, 15728880);
      bufferSource.endBatch();
      return minecraft.font.draw(matrixStack, text, x, y, color) + 1;
   }

   public static int drawStringWithBorder(
      float x,
      float y,
      int color,
      int borderColor,
      FormattedCharSequence formattedCharSequence,
      Minecraft minecraft,
      PoseStack matrixStack,
      BufferSource bufferSource
   ) {
      Matrix4f matrix = matrixStack.last().pose();
      minecraft.font.drawInBatch(formattedCharSequence, x - 1.0F, y, borderColor, false, matrix, bufferSource, false, 0, 15728880);
      minecraft.font.drawInBatch(formattedCharSequence, x + 1.0F, y, borderColor, false, matrix, bufferSource, false, 0, 15728880);
      minecraft.font.drawInBatch(formattedCharSequence, x, y - 1.0F, borderColor, false, matrix, bufferSource, false, 0, 15728880);
      minecraft.font.drawInBatch(formattedCharSequence, x, y + 1.0F, borderColor, false, matrix, bufferSource, false, 0, 15728880);
      matrixStack.pushPose();
      matrixStack.translate(0.0, 0.0, 0.1);
      int result = minecraft.font.drawInBatch(formattedCharSequence, x, y, color, false, matrixStack.last().pose(), bufferSource, false, 0, 15728880) + 1;
      matrixStack.popPose();
      return result;
   }

   public static int drawTextComponent(PoseStack matrixStack, Component component, boolean rightAligned) {
      Font fontRenderer = Minecraft.getInstance().font;
      int width = fontRenderer.width(component);
      fontRenderer.drawShadow(matrixStack, component, rightAligned ? -width : 0.0F, 0.0F, -1052689);
      return width;
   }
}
