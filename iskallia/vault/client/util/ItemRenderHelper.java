package iskallia.vault.client.util;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;

public class ItemRenderHelper {
   public static void renderGuiItem(ItemStack itemStack, int x, int y, Consumer<PoseStack> transformAdjuster) {
      Minecraft minecraft = Minecraft.getInstance();
      ItemRenderer itemRenderer = minecraft.getItemRenderer();
      TextureManager textureManager = minecraft.getTextureManager();
      BakedModel bakedModel = itemRenderer.getModel(itemStack, null, null, 0);
      textureManager.getTexture(TextureAtlas.LOCATION_BLOCKS).setFilter(false, false);
      RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      PoseStack posestack = RenderSystem.getModelViewStack();
      posestack.pushPose();
      posestack.translate(x, y, 100.0F + itemRenderer.blitOffset);
      posestack.translate(8.0, 8.0, 0.0);
      transformAdjuster.accept(posestack);
      posestack.scale(1.0F, -1.0F, 1.0F);
      posestack.scale(16.0F, 16.0F, 16.0F);
      RenderSystem.applyModelViewMatrix();
      PoseStack posestack1 = new PoseStack();
      BufferSource multibuffersource$buffersource = Minecraft.getInstance().renderBuffers().bufferSource();
      boolean flag = !bakedModel.usesBlockLight();
      if (flag) {
         Lighting.setupForFlatItems();
      }

      itemRenderer.render(itemStack, TransformType.GUI, false, posestack1, multibuffersource$buffersource, 15728880, OverlayTexture.NO_OVERLAY, bakedModel);
      multibuffersource$buffersource.endBatch();
      RenderSystem.enableDepthTest();
      if (flag) {
         Lighting.setupFor3DItems();
      }

      posestack.popPose();
      RenderSystem.applyModelViewMatrix();
   }
}
