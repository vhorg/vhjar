package iskallia.vault.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import iskallia.vault.block.ToolStationBlock;
import iskallia.vault.block.entity.ModifierWorkbenchTileEntity;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;

public class ModifierWorkbenchRenderer implements BlockEntityRenderer<ModifierWorkbenchTileEntity> {
   public ModifierWorkbenchRenderer(Context context) {
   }

   public void render(
      ModifierWorkbenchTileEntity tile,
      float partialTicks,
      @Nonnull PoseStack poseStack,
      @Nonnull MultiBufferSource bufferIn,
      int combinedLightIn,
      int combinedOverlayIn
   ) {
      ItemStack craftedTool = tile.getInventory().getItem(0);
      if (!craftedTool.isEmpty()) {
         poseStack.pushPose();
         poseStack.translate(0.5, 1.03, 0.5);
         Direction facing = (Direction)tile.getBlockState().getValue(ToolStationBlock.FACING);
         poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F - facing.toYRot()));
         poseStack.mulPose(Vector3f.XP.rotationDegrees(-90.0F));
         poseStack.mulPose(Vector3f.YP.rotationDegrees(180.0F));
         Minecraft minecraft = Minecraft.getInstance();
         minecraft.getItemRenderer().renderStatic(craftedTool, TransformType.FIXED, combinedLightIn, combinedOverlayIn, poseStack, bufferIn, 0);
         poseStack.popPose();
      }
   }
}
