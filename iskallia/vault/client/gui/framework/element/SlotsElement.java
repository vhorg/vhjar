package iskallia.vault.client.gui.framework.element;

import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.atlas.TextureAtlasRegion;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.spi.AbstractSpatialElement;
import iskallia.vault.client.gui.framework.element.spi.IRenderedElement;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.client.gui.helper.LightmapHelper;
import iskallia.vault.container.slot.spi.IGhostSlot;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.MenuAccess;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.NotNull;

public class SlotsElement<E extends SlotsElement<E>> extends AbstractSpatialElement<E> implements IRenderedElement {
   protected final List<Slot> slots;
   protected final TextureAtlasRegion background;
   protected boolean visible;

   public SlotsElement(MenuAccess<?> containerScreen) {
      this(Spatials.zero(), containerScreen.getMenu().slots);
   }

   public SlotsElement(IPosition position, List<Slot> slots) {
      this(position, slots, ScreenTextures.INSET_ITEM_SLOT_BACKGROUND);
   }

   public SlotsElement(IPosition position, List<Slot> slots, TextureAtlasRegion background) {
      super(Spatials.positionXYZ(position).size(calculateElementWidth(slots), calculateElementHeight(slots)));
      this.slots = slots;
      this.background = background;
      this.setVisible(true);
   }

   @Override
   public void setVisible(boolean visible) {
      this.visible = visible;
   }

   @Override
   public boolean isVisible() {
      return this.visible;
   }

   @Override
   public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
      for (Slot slot : this.slots) {
         renderer.render(this.background, poseStack, slot.x + this.worldSpatial.x() - 1, slot.y + this.worldSpatial.y() - 1, this.worldSpatial.z());
         if (slot instanceof IGhostSlot) {
            IGhostSlot ghostSlot = (IGhostSlot)slot;
            ItemStack ghostItemStack = ghostSlot.getGhostItemStack();
            if (ghostItemStack != null) {
               this.renderItemStack(ghostItemStack, slot.x + this.worldSpatial.x(), slot.y + this.worldSpatial.y(), this.worldSpatial.z() + 1);
            }
         }
      }
   }

   private void renderItemStack(ItemStack itemStack, float x, float y, float z) {
      Minecraft minecraft = Minecraft.getInstance();
      BakedModel bakedModel = minecraft.getItemRenderer().getModel(itemStack, null, null, 0);
      PoseStack poseStack2 = new PoseStack();
      RenderSystem.setShaderTexture(0, TextureAtlas.LOCATION_BLOCKS);
      RenderSystem.enableBlend();
      RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
      RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 0.1F);
      PoseStack posestack = RenderSystem.getModelViewStack();
      posestack.pushPose();
      posestack.translate(x, y, z);
      posestack.translate(8.0, 8.0, 8.0);
      posestack.scale(16.0F, -16.0F, 16.0F);
      RenderSystem.applyModelViewMatrix();
      BufferSource bufferSource = Minecraft.getInstance().renderBuffers().bufferSource();
      Lighting.setupForFlatItems();
      minecraft.getItemRenderer()
         .render(itemStack, TransformType.GUI, false, poseStack2, bufferSource, LightmapHelper.getPackedLightCoords(5), OverlayTexture.NO_OVERLAY, bakedModel);
      bufferSource.endBatch();
      RenderSystem.enableDepthTest();
      posestack.popPose();
      RenderSystem.applyModelViewMatrix();
      Lighting.setupFor3DItems();
   }

   private static int calculateElementWidth(List<Slot> slots) {
      int result = 0;

      for (Slot slot : slots) {
         if (slot.x + 18 > result) {
            result = slot.x + 18;
         }
      }

      return result;
   }

   private static int calculateElementHeight(List<Slot> slots) {
      int result = 0;

      for (Slot slot : slots) {
         if (slot.y + 18 > result) {
            result = slot.y + 18;
         }
      }

      return result;
   }
}
