package iskallia.vault.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import iskallia.vault.block.entity.VaultDiffuserTileEntity;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class VaultDiffuserRenderer implements BlockEntityRenderer<VaultDiffuserTileEntity> {
   public VaultDiffuserRenderer(Context context) {
   }

   public void render(
      VaultDiffuserTileEntity diffuser,
      float partialTicks,
      @Nonnull PoseStack matrixStack,
      @Nonnull MultiBufferSource buffer,
      int combinedLight,
      int combinedOverlay
   ) {
      Level world = diffuser.getLevel();
      if (world != null) {
         float lerp = Mth.lerp(partialTicks, diffuser.getProgressLastPercent(), diffuser.getProgressPercent());
         if (lerp < 0.4) {
            ItemStack itemStack = diffuser.getInputInv().getItem(0);
            matrixStack.pushPose();
            this.renderInputItem(matrixStack, buffer, combinedLight, combinedOverlay, 0.6F, 0.35F - lerp / 2.0F, itemStack, diffuser, partialTicks);
            matrixStack.popPose();
         }

         ItemStack itemStack = diffuser.getOutputInv().getItem(0);
         matrixStack.pushPose();
         this.renderOutputItem(matrixStack, buffer, combinedLight, combinedOverlay, 1.4F, 0.35F, itemStack, diffuser, partialTicks);
         matrixStack.popPose();
      }
   }

   private void renderInputItem(
      PoseStack matrixStack,
      MultiBufferSource buffer,
      int lightLevel,
      int overlay,
      float yOffset,
      float scale,
      ItemStack itemStack,
      VaultDiffuserTileEntity diffuser,
      float partialTicks
   ) {
      Minecraft minecraft = Minecraft.getInstance();
      matrixStack.pushPose();
      matrixStack.translate(0.5, yOffset, 0.5);
      matrixStack.scale(scale, scale, scale);
      float lerp = Mth.lerp(partialTicks, diffuser.getProgressLastPercent(), diffuser.getProgressPercent());
      float ticksToConsumedAnimated = lerp * lerp * lerp;
      double rotation = -10.0 * (System.currentTimeMillis() / 400.0 + 180.0F * ticksToConsumedAnimated) % 360.0 * (Math.PI / 180.0);
      matrixStack.mulPose(Quaternion.fromXYZ(0.0F, (float)rotation, 0.0F));
      BakedModel bakedModel = minecraft.getItemRenderer().getModel(itemStack, null, null, 0);
      minecraft.getItemRenderer().render(itemStack, TransformType.FIXED, true, matrixStack, buffer, lightLevel, overlay, bakedModel);
      matrixStack.popPose();
   }

   private void renderOutputItem(
      PoseStack matrixStack,
      MultiBufferSource buffer,
      int lightLevel,
      int overlay,
      float yOffset,
      float scale,
      ItemStack itemStack,
      VaultDiffuserTileEntity diffuser,
      float partialTicks
   ) {
      Minecraft minecraft = Minecraft.getInstance();
      matrixStack.pushPose();
      matrixStack.translate(0.5, yOffset, 0.5);
      matrixStack.scale(scale, scale, scale);
      double rotation = -10.0 * (System.currentTimeMillis() / 200.0) % 360.0 * (Math.PI / 180.0);
      matrixStack.mulPose(Quaternion.fromXYZ(0.0F, (float)rotation, 0.0F));
      BakedModel bakedModel = minecraft.getItemRenderer().getModel(itemStack, null, null, 0);
      minecraft.getItemRenderer().render(itemStack, TransformType.FIXED, true, matrixStack, buffer, lightLevel, overlay, bakedModel);
      matrixStack.popPose();
   }
}