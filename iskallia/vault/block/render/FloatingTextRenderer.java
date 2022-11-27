package iskallia.vault.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import iskallia.vault.block.entity.FloatingTextTileEntity;
import java.util.List;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component.Serializer;

public class FloatingTextRenderer implements BlockEntityRenderer<FloatingTextTileEntity> {
   public FloatingTextRenderer(Context context) {
   }

   public void render(
      @Nonnull FloatingTextTileEntity tileEntity,
      float partialTicks,
      @Nonnull PoseStack matrixStack,
      @Nonnull MultiBufferSource buffer,
      int combinedLightIn,
      int combinedOverlayIn
   ) {
      List<String> lines = tileEntity.getLines();
      int length = lines.size();
      Minecraft minecraft = Minecraft.getInstance();
      Font fontRenderer = minecraft.font;

      for (int i = length - 1; i >= 0; i--) {
         String line = lines.get(i);
         MutableComponent text = Serializer.fromJsonLenient(line);
         if (text != null) {
            float scale = 0.02F;
            int color = -1;
            int opacity = 1711276032;
            int lightLevel = 1;
            matrixStack.pushPose();
            Matrix4f matrix4f = matrixStack.last().pose();
            float offset = -fontRenderer.width(text) / 2;
            matrixStack.translate(0.5, 1.7F + 0.25F * (length - i), 0.5);
            matrixStack.scale(scale, scale, scale);
            matrixStack.mulPose(minecraft.getEntityRenderDispatcher().cameraOrientation());
            matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
            fontRenderer.drawInBatch(text, offset, 0.0F, color, false, matrix4f, buffer, true, opacity, lightLevel);
            fontRenderer.drawInBatch(text, offset, 0.0F, -1, false, matrix4f, buffer, false, 0, lightLevel);
            matrixStack.popPose();
         }
      }
   }
}
