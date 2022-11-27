package iskallia.vault.client.gui.framework.element;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.client.gui.helper.LightmapHelper;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.ItemModelShaper;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.RenderProperties;
import org.jetbrains.annotations.NotNull;

public class ScalableItemElement<E extends ScalableItemElement<E>> extends ElasticContainerElement<E> {
   protected Supplier<ItemStack> itemStack;
   protected boolean ignoreGlint;
   protected float scale;
   protected Runnable onClickHandler;

   public ScalableItemElement(ISpatial spatial, Supplier<ItemStack> itemStack) {
      super(Spatials.copy(spatial).width(16).height(16));
      this.scale = 1.0F;
      this.itemStack = itemStack;
   }

   public ScalableItemElement(ISpatial spatial, Supplier<ItemStack> itemStack, float scale) {
      super(Spatials.copy(spatial).width(16).height(16));
      this.scale = scale;
      this.itemStack = itemStack;
   }

   public ScalableItemElement<E> whenClicked(Runnable onClickHandler) {
      this.onClickHandler = onClickHandler;
      return this;
   }

   public ScalableItemElement<E> ignoringGlint() {
      this.ignoreGlint = true;
      return this;
   }

   public ItemStack getDisplayStack() {
      return this.itemStack.get();
   }

   @Override
   public boolean onMouseClicked(double mouseX, double mouseY, int buttonIndex) {
      if (this.onClickHandler != null) {
         this.onClickHandler.run();
      }

      return super.onMouseClicked(mouseX, mouseY, buttonIndex);
   }

   @Override
   public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
      super.render(renderer, poseStack, mouseX, mouseY, partialTick);
      this.renderItemStack(this.getDisplayStack(), this.worldSpatial.x() + 1, this.worldSpatial.y() + 1);
   }

   private void renderItemStack(ItemStack itemStack, float x, float y) {
      Minecraft minecraft = Minecraft.getInstance();
      BakedModel bakedModel = minecraft.getItemRenderer().getModel(itemStack, null, null, 0);
      PoseStack poseStack2 = new PoseStack();
      RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      PoseStack posestack = RenderSystem.getModelViewStack();
      posestack.pushPose();
      posestack.translate(x, y, 350.0);
      posestack.translate(8.0, 8.0, 8.0);
      posestack.scale(16.0F * this.scale, -16.0F * this.scale, 16.0F * this.scale);
      RenderSystem.applyModelViewMatrix();
      BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
      Lighting.setupForFlatItems();
      this.renderItemStack(
         itemStack, TransformType.GUI, false, poseStack2, bufferSource, LightmapHelper.getPackedFullbrightCoords(), OverlayTexture.NO_OVERLAY, bakedModel
      );
      bufferSource.endBatch();
      RenderSystem.enableDepthTest();
      posestack.popPose();
      RenderSystem.applyModelViewMatrix();
      Lighting.setupFor3DItems();
   }

   private void renderItemStack(
      ItemStack pItemStack,
      TransformType pTransformType,
      boolean pLeftHand,
      PoseStack pMatrixStack,
      MultiBufferSource pBuffer,
      int pCombinedLight,
      int pCombinedOverlay,
      BakedModel pModel
   ) {
      Minecraft minecraft = Minecraft.getInstance();
      ItemRenderer itemRenderer = minecraft.getItemRenderer();
      ItemModelShaper itemModelShaper = itemRenderer.getItemModelShaper();
      if (!pItemStack.isEmpty()) {
         pMatrixStack.pushPose();
         boolean flag = pTransformType == TransformType.GUI || pTransformType == TransformType.GROUND || pTransformType == TransformType.FIXED;
         if (flag) {
            if (pItemStack.is(Items.TRIDENT)) {
               pModel = itemModelShaper.getModelManager().getModel(new ModelResourceLocation("minecraft:trident#inventory"));
            } else if (pItemStack.is(Items.SPYGLASS)) {
               pModel = itemModelShaper.getModelManager().getModel(new ModelResourceLocation("minecraft:spyglass#inventory"));
            }
         }

         pModel = ForgeHooksClient.handleCameraTransforms(pMatrixStack, pModel, pTransformType, pLeftHand);
         pMatrixStack.translate(-0.5, -0.5, -0.5);
         if (!pModel.isCustomRenderer() && (!pItemStack.is(Items.TRIDENT) || flag)) {
            boolean flag1;
            if (pTransformType != TransformType.GUI && !pTransformType.firstPerson() && pItemStack.getItem() instanceof BlockItem) {
               Block block = ((BlockItem)pItemStack.getItem()).getBlock();
               flag1 = !(block instanceof HalfTransparentBlock) && !(block instanceof StainedGlassPaneBlock);
            } else {
               flag1 = true;
            }

            if (pModel.isLayered()) {
               ForgeHooksClient.drawItemLayered(itemRenderer, pModel, pItemStack, pMatrixStack, pBuffer, pCombinedLight, pCombinedOverlay, flag1);
            } else {
               RenderType rendertype = ItemBlockRenderTypes.getRenderType(pItemStack, flag1);
               VertexConsumer vertexconsumer;
               if (pItemStack.is(Items.COMPASS) && pItemStack.hasFoil()) {
                  pMatrixStack.pushPose();
                  Pose posestack$pose = pMatrixStack.last();
                  if (pTransformType == TransformType.GUI) {
                     posestack$pose.pose().multiply(0.5F);
                  } else if (pTransformType.firstPerson()) {
                     posestack$pose.pose().multiply(0.75F);
                  }

                  if (flag1) {
                     vertexconsumer = getCompassFoilBufferDirect(pBuffer, rendertype, posestack$pose);
                  } else {
                     vertexconsumer = getCompassFoilBuffer(pBuffer, rendertype, posestack$pose);
                  }

                  pMatrixStack.popPose();
               } else if (flag1) {
                  vertexconsumer = getFoilBufferDirect(pBuffer, rendertype, true, !this.ignoreGlint && pItemStack.hasFoil());
               } else {
                  vertexconsumer = getFoilBuffer(pBuffer, rendertype, true, !this.ignoreGlint && pItemStack.hasFoil());
               }

               itemRenderer.renderModelLists(pModel, pItemStack, pCombinedLight, pCombinedOverlay, pMatrixStack, vertexconsumer);
            }
         } else {
            RenderProperties.get(pItemStack)
               .getItemStackRenderer()
               .renderByItem(pItemStack, pTransformType, pMatrixStack, pBuffer, pCombinedLight, pCombinedOverlay);
         }

         pMatrixStack.popPose();
      }
   }

   public static VertexConsumer getCompassFoilBuffer(MultiBufferSource pBuffer, RenderType pRenderType, Pose pMatrixEntry) {
      return VertexMultiConsumer.create(
         new SheetedDecalTextureGenerator(pBuffer.getBuffer(RenderType.glint()), pMatrixEntry.pose(), pMatrixEntry.normal()), pBuffer.getBuffer(pRenderType)
      );
   }

   public static VertexConsumer getCompassFoilBufferDirect(MultiBufferSource pBuffer, RenderType pRenderType, Pose pMatrixEntry) {
      return VertexMultiConsumer.create(
         new SheetedDecalTextureGenerator(pBuffer.getBuffer(RenderType.glintDirect()), pMatrixEntry.pose(), pMatrixEntry.normal()),
         pBuffer.getBuffer(pRenderType)
      );
   }

   public static VertexConsumer getFoilBuffer(MultiBufferSource pBuffer, RenderType pRenderType, boolean pIsItem, boolean pGlint) {
      if (!pGlint) {
         return pBuffer.getBuffer(pRenderType);
      } else {
         return Minecraft.useShaderTransparency() && pRenderType == Sheets.translucentItemSheet()
            ? VertexMultiConsumer.create(pBuffer.getBuffer(RenderType.glintTranslucent()), pBuffer.getBuffer(pRenderType))
            : VertexMultiConsumer.create(pBuffer.getBuffer(pIsItem ? RenderType.glint() : RenderType.entityGlint()), pBuffer.getBuffer(pRenderType));
      }
   }

   public static VertexConsumer getFoilBufferDirect(MultiBufferSource pBuffer, RenderType pRenderType, boolean pNoEntity, boolean pWithGlint) {
      return pWithGlint
         ? VertexMultiConsumer.create(pBuffer.getBuffer(pNoEntity ? RenderType.glintDirect() : RenderType.entityGlintDirect()), pBuffer.getBuffer(pRenderType))
         : pBuffer.getBuffer(pRenderType);
   }
}
