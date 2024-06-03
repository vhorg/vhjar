package iskallia.vault.client.gui.framework.element;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.SheetedDecalTextureGenerator;
import com.mojang.blaze3d.vertex.Tesselator;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.blaze3d.vertex.VertexMultiConsumer;
import com.mojang.blaze3d.vertex.PoseStack.Pose;
import com.mojang.blaze3d.vertex.VertexFormat.Mode;
import com.mojang.math.Matrix4f;
import iskallia.vault.client.atlas.TextureAtlasRegion;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.render.spi.ITooltipRenderer;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.client.gui.helper.LightmapHelper;
import iskallia.vault.client.gui.screen.block.VaultJewelApplicationStationScreen;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.GameRenderer;
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
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.RenderProperties;
import org.jetbrains.annotations.NotNull;

public class ClickableItemSlotElement<E extends ClickableItemSlotElement<E>> extends ElasticContainerElement<E> {
   protected TextureAtlasRegion slotTexture;
   protected TextureAtlasRegion overlayTexture;
   protected TextureAtlasRegion disabledSlotTexture;
   protected Supplier<ItemStack> itemStack;
   protected Supplier<Boolean> disabled;
   protected boolean ignoreGlint;
   protected Runnable onClickHandler;
   protected Supplier<Component> labelSupplier = () -> null;

   public ClickableItemSlotElement(ISpatial spatial) {
      this(spatial, () -> ItemStack.EMPTY, () -> false);
   }

   public ClickableItemSlotElement(ISpatial spatial, Supplier<ItemStack> itemStack, Supplier<Boolean> disabled) {
      this(spatial, itemStack, disabled, ScreenTextures.INSET_ITEM_SLOT_BACKGROUND, ScreenTextures.INSET_DISABLED_ITEM_SLOT_BACKGROUND);
   }

   public ClickableItemSlotElement(
      ISpatial spatial, Supplier<ItemStack> itemStack, Supplier<Boolean> disabled, TextureAtlasRegion slotTexture, TextureAtlasRegion disabledSlotTexture
   ) {
      this(spatial, itemStack, disabled, slotTexture, disabledSlotTexture, slotTexture.width(), slotTexture.height());
   }

   public ClickableItemSlotElement(
      ISpatial spatial,
      Supplier<ItemStack> itemStack,
      Supplier<Boolean> disabled,
      TextureAtlasRegion slotTexture,
      TextureAtlasRegion disabledSlotTexture,
      int width,
      int height
   ) {
      super(Spatials.copy(spatial).size(width, height));
      this.itemStack = itemStack;
      this.slotTexture = slotTexture;
      this.disabledSlotTexture = disabledSlotTexture;
      this.disabled = disabled;
   }

   public ClickableItemSlotElement<E> whenClicked(Runnable onClickHandler) {
      this.onClickHandler = onClickHandler;
      return this;
   }

   public ClickableItemSlotElement<E> setLabel(Supplier<Component> countLabelSupplier) {
      this.labelSupplier = countLabelSupplier;
      return this;
   }

   public void setItemStack(Supplier<ItemStack> sup) {
      this.itemStack = sup;
   }

   public ClickableItemSlotElement<E> setLabelStackCount() {
      this.labelSupplier = () -> {
         ItemStack stack = this.getDisplayStack();
         return !stack.isEmpty() && stack.getCount() > 1 ? new TextComponent(String.valueOf(stack.getCount())) : null;
      };
      return this;
   }

   public ClickableItemSlotElement<E> ignoringGlint() {
      this.ignoreGlint = true;
      return this;
   }

   public ClickableItemSlotElement<E> overlayTexture(TextureAtlasRegion overlayTexture) {
      this.overlayTexture = overlayTexture;
      return this;
   }

   public void setDisabled(Supplier<Boolean> disabled) {
      this.disabled = disabled;
   }

   public boolean isDisabled() {
      return this.disabled.get();
   }

   public ItemStack getDisplayStack() {
      return this.itemStack.get();
   }

   @Override
   public boolean onMouseClicked(double mouseX, double mouseY, int buttonIndex) {
      if (!this.isDisabled() && this.onClickHandler != null) {
         if (this.onClickHandler instanceof VaultJewelApplicationStationScreen.MouseClickRunnable mouseClickRunnable) {
            mouseClickRunnable.setType(buttonIndex);
         }

         this.onClickHandler.run();
         return true;
      } else {
         return super.onMouseClicked(mouseX, mouseY, buttonIndex);
      }
   }

   @Override
   public boolean onHoverTooltip(ITooltipRenderer tooltipRenderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, TooltipFlag tooltipFlag) {
      return !this.containsMouse(mouseX, mouseY) ? false : super.onHoverTooltip(tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag);
   }

   @Override
   public boolean onMouseReleased(double mouseX, double mouseY, int buttonIndex) {
      return true;
   }

   @Override
   public void render(IElementRenderer renderer, @Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
      super.render(renderer, poseStack, mouseX, mouseY, partialTick);
      if (this.isDisabled()) {
         renderer.render(this.disabledSlotTexture, poseStack, this.worldSpatial);
      } else {
         renderer.render(this.slotTexture, poseStack, this.worldSpatial);
      }

      if (this.overlayTexture != null) {
         renderer.render(this.overlayTexture, poseStack, this.worldSpatial.copy().translateXY(1, 1));
      }

      this.renderItemStack(this.getDisplayStack(), this.worldSpatial.x() + 1, this.worldSpatial.y() + 1, this.isDisabled());
      this.renderLabel(poseStack);
      if (this.containsMouse(mouseX, mouseY)) {
         renderSlotHighlight(poseStack, this.worldSpatial.x() + 1, this.worldSpatial.y() + 1, 20, -2130706433);
      }
   }

   @Override
   public boolean containsMouse(double x, double y) {
      return x < this.right() && x >= this.left() && y >= this.top() && y < this.bottom();
   }

   public static void renderSlotHighlight(PoseStack pPoseStack, int pX, int pY, int pBlitOffset, int slotColor) {
      RenderSystem.disableDepthTest();
      RenderSystem.colorMask(true, true, true, false);
      fillGradient(pPoseStack, pX, pY, pX + 16, pY + 16, slotColor, slotColor, pBlitOffset);
      RenderSystem.colorMask(true, true, true, true);
      RenderSystem.enableDepthTest();
   }

   protected static void fillGradient(PoseStack pPoseStack, int pX1, int pY1, int pX2, int pY2, int pColorFrom, int pColorTo, int pBlitOffset) {
      RenderSystem.disableTexture();
      RenderSystem.enableBlend();
      RenderSystem.defaultBlendFunc();
      RenderSystem.setShader(GameRenderer::getPositionColorShader);
      Tesselator tesselator = Tesselator.getInstance();
      BufferBuilder bufferbuilder = tesselator.getBuilder();
      bufferbuilder.begin(Mode.QUADS, DefaultVertexFormat.POSITION_COLOR);
      fillGradient(pPoseStack.last().pose(), bufferbuilder, pX1, pY1, pX2, pY2, pBlitOffset, pColorFrom, pColorTo);
      tesselator.end();
      RenderSystem.disableBlend();
      RenderSystem.enableTexture();
   }

   protected static void fillGradient(Matrix4f pMatrix, BufferBuilder pBuilder, int pX1, int pY1, int pX2, int pY2, int pBlitOffset, int pColorA, int pColorB) {
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

   protected void renderLabel(PoseStack poseStack) {
      Component label = this.labelSupplier.get();
      if (label != null && !label.getString().isEmpty()) {
         poseStack.pushPose();
         poseStack.translate(0.0, 0.0, this.worldSpatial.z() + 370);
         BufferSource buffers = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
         Font defaultFont = Minecraft.getInstance().font;
         int xOffset = defaultFont.width(label);
         defaultFont.drawInBatch(
            label,
            this.worldSpatial.x() + 18 - xOffset,
            this.worldSpatial.y() + 10,
            -1,
            true,
            poseStack.last().pose(),
            buffers,
            false,
            0,
            LightmapHelper.getPackedFullbrightCoords()
         );
         buffers.endBatch();
         poseStack.popPose();
         RenderSystem.enableDepthTest();
      }
   }

   protected void renderItemStack(ItemStack itemStack, float x, float y, boolean disabled) {
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
      posestack.scale(16.0F, -16.0F, 16.0F);
      RenderSystem.applyModelViewMatrix();
      BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
      if (disabled) {
         Lighting.setupFor3DItems();
      } else {
         Lighting.setupForFlatItems();
      }

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
