package iskallia.vault.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import iskallia.vault.block.ToolStationBlock;
import iskallia.vault.block.entity.VaultJewelCuttingStationTileEntity;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

public class JewelCuttingStationRenderer implements BlockEntityRenderer<VaultJewelCuttingStationTileEntity> {
   public JewelCuttingStationRenderer(Context context) {
   }

   public void render(
      VaultJewelCuttingStationTileEntity tile,
      float partialTicks,
      @Nonnull PoseStack poseStack,
      @Nonnull MultiBufferSource bufferIn,
      int combinedLightIn,
      int combinedOverlayIn
   ) {
      ItemStack jewel = tile.getJewelInput();
      if (!jewel.isEmpty()) {
         poseStack.pushPose();
         poseStack.translate(0.5, 0.83, 0.5);
         Direction facing = (Direction)tile.getBlockState().getValue(ToolStationBlock.FACING);
         poseStack.mulPose(Vector3f.YP.rotationDegrees(90.0F - facing.toYRot()));
         poseStack.scale(0.5F, 0.5F, 0.5F);
         poseStack.translate(0.0, 0.0, -0.075);
         poseStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
         Minecraft minecraft = Minecraft.getInstance();
         minecraft.getItemRenderer().renderStatic(jewel, TransformType.FIXED, combinedLightIn, combinedOverlayIn, poseStack, bufferIn, 0);
         poseStack.popPose();
      }
   }
}
