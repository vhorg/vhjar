package iskallia.vault.client.gui.framework.element;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.client.gui.framework.spatial.spi.ISize;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.dynamodel.DynamicModelItem;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModRelics;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class RelicPedestalPreviewElement<E extends RelicPedestalPreviewElement<E>> extends ElasticContainerElement<E> {
   protected Supplier<ResourceLocation> relicId;

   public RelicPedestalPreviewElement(IPosition position, ISize size, Supplier<ResourceLocation> relicId) {
      super(Spatials.positionXYZ(position).size(size));
      this.relicId = relicId;
      this.addElement(new NineSliceElement(Spatials.size(size), ScreenTextures.INSET_BLACK_BACKGROUND));
   }

   @Override
   public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
      super.render(renderer, poseStack, mouseX, mouseY, partialTick);
      PoseStack matrixStack = new PoseStack();
      matrixStack.translate(0.0, -0.2F, 0.0);
      matrixStack.mulPose(Quaternion.fromXYZ(0.5233334F, 0.0F, 0.0F));
      float scale = 0.8F;
      matrixStack.scale(scale, scale, scale);
      this.renderItemStack(matrixStack, new ItemStack(ModBlocks.RELIC_PEDESTAL.asItem()));
      if (this.relicId.get() != ModRelics.EMPTY.getResultingRelic()) {
         matrixStack.translate(0.0, 1.0, 0.0);
         ItemStack itemStack = new ItemStack(ModItems.RELIC);
         DynamicModelItem.setGenericModelId(itemStack, this.relicId.get());
         this.renderItemStack(matrixStack, itemStack);
      }
   }

   private void renderItemStack(PoseStack matrixStack, ItemStack itemStack) {
      Minecraft minecraft = Minecraft.getInstance();
      ISpatial worldSpatial = this.getWorldSpatial();
      int playerRenderX = worldSpatial.x() + worldSpatial.width() / 2;
      int playerRenderY = worldSpatial.y() + worldSpatial.height() / 2;
      BakedModel bakedModel = minecraft.getItemRenderer().getModel(itemStack, null, null, 0);
      RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      PoseStack posestack = RenderSystem.getModelViewStack();
      posestack.pushPose();
      posestack.translate(playerRenderX, playerRenderY, 350.0);
      posestack.scale(32.0F, -32.0F, 32.0F);
      RenderSystem.applyModelViewMatrix();
      BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
      matrixStack.pushPose();
      double rotat = -90.0 * (System.currentTimeMillis() / 1000.0) % 360.0 * (float) (Math.PI / 180.0);
      matrixStack.mulPose(Quaternion.fromXYZ(0.0F, (float)rotat, 0.0F));
      Lighting.setupForFlatItems();
      minecraft.getItemRenderer().render(itemStack, TransformType.FIXED, false, matrixStack, bufferSource, 15728880, OverlayTexture.NO_OVERLAY, bakedModel);
      matrixStack.popPose();
      bufferSource.endBatch();
      RenderSystem.enableDepthTest();
      posestack.popPose();
      RenderSystem.applyModelViewMatrix();
   }
}
