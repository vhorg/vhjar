package iskallia.vault.client.gui.framework.element;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Quaternion;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.client.gui.framework.spatial.spi.ISize;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.dynamodel.model.armor.ArmorLayers;
import iskallia.vault.dynamodel.model.armor.ArmorPieceModel;
import iskallia.vault.dynamodel.registry.DynamicModelRegistry;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.renderer.VaultArmorRenderProperties;
import iskallia.vault.init.ModDynamicModels;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.item.gear.VaultArmorItem;
import java.util.Optional;
import java.util.function.Supplier;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class GearModelPreviewElement<E extends GearModelPreviewElement<E>> extends ElasticContainerElement<E> {
   protected Supplier<ItemStack> gearStack;

   public GearModelPreviewElement(IPosition position, ISize size, Supplier<ItemStack> gearStack) {
      super(Spatials.positionXYZ(position).size(size));
      this.gearStack = gearStack;
      this.addElement(new NineSliceElement(Spatials.size(size), ScreenTextures.INSET_BLACK_BACKGROUND));
   }

   @Override
   public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
      super.render(renderer, poseStack, mouseX, mouseY, partialTick);
      ItemStack itemStack = this.gearStack.get();
      Item item = itemStack.getItem();
      if (item instanceof VaultArmorItem) {
         poseStack.pushPose();
         this.renderArmorPiece(itemStack);
         poseStack.popPose();
      } else {
         this.renderItemStack(itemStack);
      }
   }

   private void renderArmorPiece(ItemStack armorStack) {
      Minecraft minecraft = Minecraft.getInstance();
      BufferSource multiBufferSource = minecraft.renderBuffers().bufferSource();
      ISpatial worldSpatial = this.getWorldSpatial();
      int playerRenderX = worldSpatial.x() + worldSpatial.width() / 2;
      int playerRenderY = worldSpatial.y() + (worldSpatial.height() - 16);
      PoseStack poseStack2 = new PoseStack();
      RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
      PoseStack modelPoseStack = RenderSystem.getModelViewStack();
      modelPoseStack.pushPose();
      modelPoseStack.translate(playerRenderX, playerRenderY, 350.0);
      modelPoseStack.scale(16.0F, 16.0F, 16.0F);
      RenderSystem.applyModelViewMatrix();
      BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
      double rotation = -90.0 * (System.currentTimeMillis() / 1000.0) % 360.0 * (Math.PI / 180.0);
      poseStack2.mulPose(Quaternion.fromXYZ(-0.3925F, (float)rotation, 0.0F));
      Optional<DynamicModelRegistry<?>> modelRegistry = ModDynamicModels.REGISTRIES.getAssociatedRegistry(armorStack.getItem());
      if (modelRegistry.isPresent()) {
         VaultGearData gearData = VaultGearData.read(armorStack);
         ArmorPieceModel armorPiece = gearData.getFirstValue(ModGearAttributes.GEAR_MODEL)
            .flatMap(modelId -> (Optional<? extends ArmorPieceModel>)modelRegistry.get().get(modelId))
            .filter(gearModel -> gearModel instanceof ArmorPieceModel)
            .orElse(null);
         if (armorPiece != null) {
            VaultArmorItem vaultArmorItem = VaultArmorItem.forSlot(armorPiece.getEquipmentSlot());
            ArmorLayers.BaseLayer baseLayer = VaultArmorRenderProperties.BAKED_LAYERS.get(armorPiece.getId());
            String baseTexture = vaultArmorItem.getArmorTexture(armorStack, null, armorPiece.getEquipmentSlot(), null);
            String overlayTexture = vaultArmorItem.getArmorTexture(armorStack, null, armorPiece.getEquipmentSlot(), "overlay");
            poseStack2.translate(0.0, -2.0, 0.0);
            poseStack2.scale(1.5F, 1.5F, 1.5F);
            EquipmentSlot intendedSlot = vaultArmorItem.getIntendedSlot(armorStack);
            if (intendedSlot == EquipmentSlot.HEAD) {
               poseStack2.translate(0.0, 0.65, 0.0);
            } else if (intendedSlot == EquipmentSlot.LEGS) {
               poseStack2.translate(0.0, -0.3, 0.0);
            } else if (intendedSlot == EquipmentSlot.FEET) {
               poseStack2.translate(0.0, -0.9, 0.0);
            }

            if (baseTexture != null) {
               VertexConsumer baseVertexConsumer = multiBufferSource.getBuffer(baseLayer.renderType(new ResourceLocation(baseTexture)));
               baseLayer.renderToBuffer(poseStack2, baseVertexConsumer, 15728880, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }

            if (overlayTexture != null) {
               VertexConsumer overlayVertexConsumer = multiBufferSource.getBuffer(baseLayer.renderType(new ResourceLocation(overlayTexture)));
               baseLayer.renderToBuffer(poseStack2, overlayVertexConsumer, 15728880, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);
            }
         }
      }

      bufferSource.endBatch();
      RenderSystem.enableDepthTest();
      modelPoseStack.popPose();
      RenderSystem.applyModelViewMatrix();
   }

   private void renderItemStack(ItemStack itemStack) {
      Minecraft minecraft = Minecraft.getInstance();
      ISpatial worldSpatial = this.getWorldSpatial();
      int playerRenderX = worldSpatial.x() + worldSpatial.width() / 2;
      int playerRenderY = worldSpatial.y() + worldSpatial.height() / 2;
      BakedModel bakedModel = minecraft.getItemRenderer().getModel(itemStack, null, null, 0);
      PoseStack poseStack2 = new PoseStack();
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
      double rotat = -90.0 * (System.currentTimeMillis() / 1000.0) % 360.0 * (float) (Math.PI / 180.0);
      poseStack2.mulPose(Quaternion.fromXYZ(0.3925F, (float)rotat, 0.0F));
      Lighting.setupForFlatItems();
      minecraft.getItemRenderer().render(itemStack, TransformType.GUI, false, poseStack2, bufferSource, 15728880, OverlayTexture.NO_OVERLAY, bakedModel);
      bufferSource.endBatch();
      RenderSystem.enableDepthTest();
      posestack.popPose();
      RenderSystem.applyModelViewMatrix();
   }
}
