package iskallia.vault.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Matrix4f;
import com.mojang.math.Quaternion;
import com.mojang.math.Vector3f;
import iskallia.vault.block.entity.ShopPedestalBlockTile;
import iskallia.vault.event.event.ShopPedestalPriceEvent;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction;
import net.minecraft.core.Direction.Plane;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;

public class ShopPedestalBlockTileRenderer implements BlockEntityRenderer<ShopPedestalBlockTile> {
   private final ItemRenderer itemRenderer;
   private final Font font;

   public ShopPedestalBlockTileRenderer(Context context) {
      Minecraft minecraft = Minecraft.getInstance();
      this.itemRenderer = minecraft.getItemRenderer();
      this.font = minecraft.gui.getFont();
   }

   public static void renderName(Component name, float h, PoseStack poseStack, MultiBufferSource bufferIn, int combinedLightIn) {
      Minecraft mc = Minecraft.getInstance();
      int i = 0;
      poseStack.pushPose();
      poseStack.translate(0.0, h, 0.0);
      poseStack.mulPose(mc.getEntityRenderDispatcher().cameraOrientation());
      poseStack.scale(-0.025F, -0.025F, 0.025F);
      Matrix4f matrix4f = poseStack.last().pose();
      float f1 = mc.options.getBackgroundOpacity(0.25F);
      int j = (int)(f1 * 255.0F) << 24;
      float f2 = -mc.font.width(name) / 2;
      mc.font.drawInBatch(name, f2, i, -1, false, matrix4f, bufferIn, false, j, combinedLightIn);
      poseStack.popPose();
   }

   public void render(
      ShopPedestalBlockTile tile, float partialTicks, PoseStack matrixStack, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn
   ) {
      if (tile.isInitialized()) {
         ItemStack offerStack = tile.getOfferStack();
         if (!offerStack.isEmpty()) {
            Player player = Minecraft.getInstance().player;
            ItemStack currency;
            if (player != null) {
               ShopPedestalPriceEvent event = new ShopPedestalPriceEvent(player, offerStack, tile.getCurrencyStack());
               MinecraftForge.EVENT_BUS.post(event);
               currency = event.getCost();
            } else {
               currency = tile.getCurrencyStack();
            }

            matrixStack.pushPose();
            matrixStack.translate(0.5, 0.5, 0.5);
            this.drawPrice(currency, matrixStack, buffer, String.valueOf(currency.getCount()), combinedLightIn, combinedOverlayIn);
            matrixStack.pushPose();
            matrixStack.translate(0.0, 0.625, 0.0);
            renderName(offerStack.getHoverName(), 0.875F, matrixStack, buffer, combinedLightIn);
            matrixStack.scale(0.5F, 0.5F, 0.5F);
            matrixStack.translate(0.0, 0.25, 0.0);
            TransformType transform = TransformType.FIXED;
            matrixStack.translate(0.0, 0.375, 0.0);
            matrixStack.scale(1.5F, 1.5F, 1.5F);
            long time = tile.getLevel().getGameTime();
            int scale = 360;
            float angle = ((float)Math.floorMod(time, (long)scale) + partialTicks) / scale;
            Quaternion rotation = Vector3f.YP.rotation((float)(angle * Math.PI * 10.0));
            matrixStack.mulPose(rotation);
            this.itemRenderer.renderStatic(offerStack, transform, combinedLightIn, combinedOverlayIn, matrixStack, buffer, 0);
            matrixStack.popPose();
            matrixStack.popPose();
         }
      }
   }

   private void drawPrice(ItemStack stack, PoseStack matrixStack, MultiBufferSource buffer, String name, int combinedLight, int combinedOverlay) {
      FormattedCharSequence text = new TextComponent(name).getVisualOrderText();
      Font fr = this.font;
      int xOffset = fr.width(text);

      for (Direction dir : Plane.HORIZONTAL) {
         matrixStack.pushPose();
         matrixStack.mulPose(Vector3f.YP.rotationDegrees(dir.toYRot()));
         matrixStack.translate(0.0, 0.0, 0.4375);
         matrixStack.pushPose();
         matrixStack.translate(0.0, -0.16, 0.075);
         matrixStack.scale(0.6125F, 0.6125F, 0.6125F);
         this.itemRenderer.renderStatic(stack, TransformType.GROUND, combinedLight, combinedOverlay, matrixStack, buffer, 0);
         matrixStack.popPose();
         matrixStack.translate(0.0, -0.25, 0.005);
         float scale = 0.015F;
         matrixStack.scale(scale, -scale, scale);
         fr.drawInBatch(text, 1.0F - xOffset / 2.0F, 1.0F - 9.0F / 2.0F, -16777216, false, matrixStack.last().pose(), buffer, false, 0, combinedLight);
         matrixStack.translate(0.0, 0.0, 0.001);
         fr.drawInBatch(text, -xOffset / 2.0F, -9 / 2.0F, -1, false, matrixStack.last().pose(), buffer, false, 0, combinedLight);
         matrixStack.popPose();
      }
   }
}
