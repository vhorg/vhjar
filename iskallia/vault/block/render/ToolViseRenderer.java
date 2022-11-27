package iskallia.vault.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import iskallia.vault.block.entity.ToolViseTile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.core.Direction.Axis;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

public class ToolViseRenderer implements BlockEntityRenderer<ToolViseTile> {
   private final ItemRenderer itemRenderer;

   public ToolViseRenderer(Context context) {
      Minecraft minecraft = Minecraft.getInstance();
      this.itemRenderer = minecraft.getItemRenderer();
   }

   public void render(ToolViseTile tile, float partialTicks, PoseStack poseStack, MultiBufferSource bufferIn, int combinedLightIn, int combinedOverlayIn) {
      ItemStack magnet = tile.getPaxel();
      if (!magnet.isEmpty()) {
         poseStack.pushPose();
         poseStack.translate(0.5, 0.5, 0.5);
         if (tile.getBlockState().getValue(BlockStateProperties.AXIS) != Axis.Z) {
            poseStack.mulPose(Vector3f.YP.rotationDegrees(-90.0F));
         }

         poseStack.translate(0.0, 0.5, 0.0);
         poseStack.scale(0.75F, 0.75F, 0.75F);
         this.itemRenderer.renderStatic(magnet, TransformType.FIXED, combinedLightIn, combinedOverlayIn, poseStack, bufferIn, 0);
         poseStack.popPose();
      }
   }
}
