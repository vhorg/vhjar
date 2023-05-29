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
import iskallia.vault.client.gui.screen.block.VaultJewelApplicationStationScreen;
import java.util.function.Supplier;
import javax.annotation.Nonnull;
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
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HalfTransparentBlock;
import net.minecraft.world.level.block.StainedGlassPaneBlock;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.RenderProperties;

public class ToolItemSlotElement<E extends ToolItemSlotElement<E>> extends ElasticContainerElement<E> {
   protected Supplier<ItemStack> itemStack;
   protected Supplier<Boolean> disabled;
   protected boolean ignoreGlint;
   protected Runnable onClickHandler;
   protected Supplier<Component> labelSupplier = () -> null;
   protected float scaleWidth = 1.0F;
   protected float scaleHeight = 1.0F;

   public ToolItemSlotElement(ISpatial spatial, Supplier<ItemStack> itemStack, Supplier<Boolean> disabled, int width, int height) {
      super(Spatials.copy(spatial).size(width, height));
      this.itemStack = itemStack;
      this.disabled = disabled;
      this.scaleWidth = width / 16.0F;
      this.scaleHeight = height / 16.0F;
   }

   public ToolItemSlotElement<E> whenClicked(Runnable onClickHandler) {
      this.onClickHandler = onClickHandler;
      return this;
   }

   public ToolItemSlotElement<E> setLabel(Supplier<Component> countLabelSupplier) {
      this.labelSupplier = countLabelSupplier;
      return this;
   }

   @Override
   public boolean mouseClicked(double mouseX, double mouseY, int buttonIndex) {
      return super.mouseClicked(mouseX, mouseY, buttonIndex);
   }

   public void setItemStack(Supplier<ItemStack> sup) {
      this.itemStack = sup;
   }

   public ToolItemSlotElement<E> setLabelStackCount() {
      this.labelSupplier = () -> {
         ItemStack stack = this.getDisplayStack();
         return !stack.isEmpty() && stack.getCount() > 1 ? new TextComponent(String.valueOf(stack.getCount())) : null;
      };
      return this;
   }

   public ToolItemSlotElement<E> ignoringGlint() {
      this.ignoreGlint = true;
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
      }

      return super.onMouseClicked(mouseX, mouseY, buttonIndex);
   }

   @Override
   public void render(IElementRenderer renderer, @Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
      this.renderItemStack(this.getDisplayStack(), this.worldSpatial.x() + 1, this.worldSpatial.y() + 1, this.isDisabled());
   }

   private void renderItemStack(ItemStack itemStack, float x, float y, boolean disabled) {
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
      posestack.translate(8.0F * this.scaleWidth, 8.0F * this.scaleHeight, 8.0F * this.scaleWidth);
      posestack.scale(16.0F * this.scaleWidth, -16.0F * this.scaleHeight, 16.0F * this.scaleWidth);
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
