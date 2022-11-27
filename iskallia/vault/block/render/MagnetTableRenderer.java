package iskallia.vault.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import iskallia.vault.block.entity.MagnetTableTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;

public class MagnetTableRenderer implements BlockEntityRenderer<MagnetTableTile> {
   private final ItemRenderer itemRenderer;

   public MagnetTableRenderer(Context context) {
      Minecraft minecraft = Minecraft.getInstance();
      this.itemRenderer = minecraft.getItemRenderer();
   }

   public void render(MagnetTableTile tile, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
      ItemStack magnet = tile.getMagnet();
      if (!magnet.isEmpty()) {
         poseStack.pushPose();
         poseStack.translate(0.5, 0.5, 0.5);
         poseStack.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
         poseStack.translate(0.0, 0.0, 0.515);
         poseStack.scale(0.75F, 0.75F, 0.75F);
         this.itemRenderer.renderStatic(magnet, TransformType.FIXED, combinedLightIn, combinedOverlayIn, poseStack, bufferIn, 0);
         poseStack.popPose();
      }
   }
}
