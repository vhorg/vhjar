package iskallia.vault.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Matrix4f;
import com.mojang.math.Vector3f;
import iskallia.vault.block.base.FillableAltarTileEntity;
import java.awt.Color;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.inventory.InventoryMenu;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;

public class FillableAltarRenderer implements BlockEntityRenderer<FillableAltarTileEntity> {
   private static final Vector3f FLUID_LOWER_POS = new Vector3f(2.25F, 2.0F, 2.25F);
   private static final Vector3f FLUID_UPPER_POS = new Vector3f(13.75F, 11.0F, 13.75F);

   public FillableAltarRenderer(Context context) {
   }

   public void render(
      FillableAltarTileEntity tileEntity, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLight, int combinedOverlayIn
   ) {
      VertexConsumer builder = buffer.getBuffer(RenderType.translucent());
      float progressPercentage = (float)tileEntity.getCurrentProgress() / tileEntity.getMaxProgress();
      if (progressPercentage > 0.0F) {
         float fluidMaxHeight = FLUID_UPPER_POS.y() - FLUID_LOWER_POS.y();
         Vector3f upperPos = new Vector3f(FLUID_UPPER_POS.x(), FLUID_LOWER_POS.y() + fluidMaxHeight * progressPercentage, FLUID_UPPER_POS.z());
         this.renderCuboid(builder, matrixStack, FLUID_LOWER_POS, upperPos, tileEntity.getFillColor());
         if (buffer instanceof BufferSource) {
            ((BufferSource)buffer).endBatch(RenderType.translucent());
         }
      }

      Minecraft minecraft = Minecraft.getInstance();
      if (minecraft.hitResult != null && minecraft.hitResult.getType() == Type.BLOCK) {
         BlockHitResult result = (BlockHitResult)minecraft.hitResult;
         if (tileEntity.getBlockPos().equals(result.getBlockPos())) {
            Component progressText = null;
            if (tileEntity.isCompleted() && !tileEntity.isConsumed()) {
               progressText = new TextComponent("Right Click to Loot!").setStyle(Style.EMPTY.withColor(TextColor.fromRgb(-1313364)));
            } else if (!tileEntity.isCompleted()) {
               progressText = new TextComponent(tileEntity.getCurrentProgress() + " / " + tileEntity.getMaxProgress() + " ")
                  .append(tileEntity.getRequirementUnit());
            }

            if (progressText != null) {
               this.renderLabel(matrixStack, 0.5F, 2.3F, 0.5F, buffer, combinedLight, tileEntity.getRequirementName());
               this.renderLabel(matrixStack, 0.5F, 2.1F, 0.5F, buffer, combinedLight, progressText);
            }
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

   public void renderCuboid(VertexConsumer builder, PoseStack matrixStack, Vector3f v1, Vector3f v2, Color tint) {
      TextureAtlasSprite sprite = (TextureAtlasSprite)Minecraft.getInstance()
         .getTextureAtlas(InventoryMenu.BLOCK_ATLAS)
         .apply(Fluids.WATER.getAttributes().getStillTexture());
      float minU = sprite.getU(0.0);
      float maxU = sprite.getU(16.0);
      float minV = sprite.getV(0.0);
      float maxV = sprite.getV(16.0);
      matrixStack.pushPose();
      this.addVertex(builder, matrixStack, v1.x(), v2.y(), v1.z(), tint, minU, maxV);
      this.addVertex(builder, matrixStack, v1.x(), v2.y(), v2.z(), tint, minU, minV);
      this.addVertex(builder, matrixStack, v2.x(), v2.y(), v2.z(), tint, maxU, minV);
      this.addVertex(builder, matrixStack, v2.x(), v2.y(), v1.z(), tint, maxU, maxV);
      this.addVertex(builder, matrixStack, v1.x(), v1.y(), v1.z(), tint, minU, maxV);
      this.addVertex(builder, matrixStack, v1.x(), v2.y(), v1.z(), tint, minU, minV);
      this.addVertex(builder, matrixStack, v2.x(), v2.y(), v1.z(), tint, maxU, minV);
      this.addVertex(builder, matrixStack, v2.x(), v1.y(), v1.z(), tint, maxU, maxV);
      this.addVertex(builder, matrixStack, v2.x(), v1.y(), v1.z(), tint, minU, maxV);
      this.addVertex(builder, matrixStack, v2.x(), v2.y(), v1.z(), tint, minU, minV);
      this.addVertex(builder, matrixStack, v2.x(), v2.y(), v2.z(), tint, maxU, minV);
      this.addVertex(builder, matrixStack, v2.x(), v1.y(), v2.z(), tint, maxU, maxV);
      this.addVertex(builder, matrixStack, v1.x(), v1.y(), v2.z(), tint, minU, maxV);
      this.addVertex(builder, matrixStack, v1.x(), v2.y(), v2.z(), tint, minU, minV);
      this.addVertex(builder, matrixStack, v1.x(), v2.y(), v1.z(), tint, maxU, minV);
      this.addVertex(builder, matrixStack, v1.x(), v1.y(), v1.z(), tint, maxU, maxV);
      this.addVertex(builder, matrixStack, v2.x(), v1.y(), v2.z(), tint, minU, maxV);
      this.addVertex(builder, matrixStack, v2.x(), v2.y(), v2.z(), tint, minU, minV);
      this.addVertex(builder, matrixStack, v1.x(), v2.y(), v2.z(), tint, maxU, minV);
      this.addVertex(builder, matrixStack, v1.x(), v1.y(), v2.z(), tint, maxU, maxV);
      matrixStack.popPose();
   }

   public void addVertex(VertexConsumer builder, PoseStack matrixStack, float x, float y, float z, Color tint, float u, float v) {
      builder.vertex(matrixStack.last().pose(), x / 16.0F, y / 16.0F, z / 16.0F)
         .color(tint.getRed() / 255.0F, tint.getGreen() / 255.0F, tint.getBlue() / 255.0F, 0.8F)
         .uv(u, v)
         .uv2(0, 240)
         .normal(1.0F, 0.0F, 0.0F)
         .endVertex();
   }
}
