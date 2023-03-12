package iskallia.vault.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import iskallia.vault.block.CrakePedestalBlock;
import iskallia.vault.block.entity.CrakePedestalTileEntity;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;

public class CrakePedestalRenderer implements BlockEntityRenderer<CrakePedestalTileEntity> {
   public CrakePedestalRenderer(Context context) {
   }

   public void render(
      @Nonnull CrakePedestalTileEntity tileEntity,
      float partialTick,
      @Nonnull PoseStack poseStack,
      @Nonnull MultiBufferSource bufferSource,
      int packetLight,
      int packetOverlay
   ) {
      Minecraft minecraft = Minecraft.getInstance();
      if (!(Boolean)tileEntity.getBlockState().getValue(CrakePedestalBlock.CONSUMED)
         && minecraft.hitResult != null
         && minecraft.hitResult.getType() == Type.BLOCK) {
         BlockHitResult result = (BlockHitResult)minecraft.hitResult;
         if (tileEntity.getBlockPos().equals(result.getBlockPos())) {
            String text = "Consume to complete the Vault!";
            this.renderLabel(poseStack, 0.5F, 1.5F, 0.5F, bufferSource, packetLight, new TextComponent(text));
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
