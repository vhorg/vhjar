package iskallia.vault.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import iskallia.vault.block.entity.PylonTileEntity;
import iskallia.vault.block.model.PylonCrystalModel;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;

public class PylonRenderer implements BlockEntityRenderer<PylonTileEntity> {
   protected final PylonCrystalModel crystalModel;

   public PylonRenderer(Context context) {
      this.crystalModel = new PylonCrystalModel(context.bakeLayer(PylonCrystalModel.MODEL_LOCATION));
   }

   public void render(
      @Nonnull PylonTileEntity tileEntity,
      float partialTick,
      @Nonnull PoseStack poseStack,
      @Nonnull MultiBufferSource bufferSource,
      int packetLight,
      int packetOverlay
   ) {
      int color = tileEntity.config == null ? -1 : tileEntity.config.getColor();
      int a = tileEntity.isConsumed() ? 32 : color >>> 24 & 0xFF;
      int r = color >>> 16 & 0xFF;
      int g = color >>> 8 & 0xFF;
      int b = color & 0xFF;
      VertexConsumer vertexConsumer = PylonCrystalModel.MATERIAL.buffer(bufferSource, RenderType::entityTranslucent);
      poseStack.pushPose();
      poseStack.translate(0.5, 1.5, 0.5);
      poseStack.mulPose(Vector3f.ZP.rotation((float) Math.PI));
      this.crystalModel.setupAnimations();
      this.crystalModel.renderToBuffer(poseStack, vertexConsumer, packetLight, packetOverlay, r / 255.0F, g / 255.0F, b / 255.0F, a / 255.0F);
      poseStack.popPose();
      Minecraft minecraft = Minecraft.getInstance();
      if (!tileEntity.isConsumed() && minecraft.hitResult != null && minecraft.hitResult.getType() == Type.BLOCK) {
         BlockHitResult result = (BlockHitResult)minecraft.hitResult;
         if (tileEntity.getBlockPos().equals(result.getBlockPos()) && tileEntity.config != null) {
            String text = tileEntity.config.getDescription();
            Component progressText = new TextComponent(text == null ? "" : text)
               .setStyle(Style.EMPTY.withColor(TextColor.fromRgb(tileEntity.config.getColor())));
            this.renderLabel(poseStack, 0.5F, 1.5F, 0.5F, bufferSource, packetLight, progressText);
         }
      }
   }

   public void renderLabel(PoseStack matrixStack, float x, float y, float z, MultiBufferSource buffer, int lightLevel, Component text) {
      Minecraft minecraft = Minecraft.getInstance();
      Font fontRenderer = minecraft.font;
      matrixStack.pushPose();
      float scale = 0.02F;
      int opacity = 1711276032;
      float offset = -fontRenderer.width(text) / 2;
      Matrix4f matrix4f = matrixStack.last().pose();
      matrixStack.translate(x, y, z);
      matrixStack.scale(scale, scale, scale);
      matrixStack.mulPose(minecraft.getEntityRenderDispatcher().cameraOrientation());
      matrixStack.mulPose(Vector3f.ZP.rotationDegrees(180.0F));
      fontRenderer.drawInBatch(text, offset, 0.0F, -1, false, matrix4f, buffer, true, opacity, lightLevel);
      fontRenderer.drawInBatch(text, offset, 0.0F, -1, false, matrix4f, buffer, false, 0, lightLevel);
      matrixStack.popPose();
   }
}
