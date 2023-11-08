package iskallia.vault.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Quaternion;
import iskallia.vault.block.entity.DivineAltarTileEntity;
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

public class DivineAltarRenderer implements BlockEntityRenderer<DivineAltarTileEntity> {
   public DivineAltarRenderer(Context context) {
   }

   public void render(
      DivineAltarTileEntity scavengerAltarTileEntity,
      float partialTicks,
      @Nonnull PoseStack matrixStack,
      @Nonnull MultiBufferSource buffer,
      int combinedLight,
      int combinedOverlay
   ) {
      Level world = scavengerAltarTileEntity.getLevel();
      if (world != null) {
         float lerp = Mth.lerp(partialTicks, scavengerAltarTileEntity.ticksToConsumeOld, scavengerAltarTileEntity.ticksToConsume);
         float percentConsumed = 1.0F - lerp / 40.0F;
         ItemStack itemStack = scavengerAltarTileEntity.getHeldItem();
         matrixStack.pushPose();
         this.renderItem(
            matrixStack,
            buffer,
            combinedLight,
            combinedOverlay,
            1.5F - percentConsumed * percentConsumed * percentConsumed,
            (1.0F - percentConsumed * percentConsumed) * 0.4F + 0.1F,
            itemStack,
            scavengerAltarTileEntity,
            partialTicks
         );
         matrixStack.popPose();
      }
   }

   private void renderItem(
      PoseStack matrixStack,
      MultiBufferSource buffer,
      int lightLevel,
      int overlay,
      float yOffset,
      float scale,
      ItemStack itemStack,
      DivineAltarTileEntity scavengerAltarTileEntity,
      float partialTicks
   ) {
      Minecraft minecraft = Minecraft.getInstance();
      matrixStack.pushPose();
      matrixStack.translate(0.5, yOffset, 0.5);
      matrixStack.scale(scale, scale, scale);
      float lerp = Mth.lerp(partialTicks, scavengerAltarTileEntity.ticksToConsumeOld, scavengerAltarTileEntity.ticksToConsume);
      float ticksToConsumedAnimated = (40.0F - lerp) * (40.0F - lerp);
      double rotation = -10.0 * (System.currentTimeMillis() / 1000.0 + ticksToConsumedAnimated / 25.0F) % 360.0 * (Math.PI / 180.0);
      matrixStack.mulPose(Quaternion.fromXYZ(0.0F, (float)rotation, 0.0F));
      BakedModel bakedModel = minecraft.getItemRenderer().getModel(itemStack, null, null, 0);
      minecraft.getItemRenderer().render(itemStack, TransformType.FIXED, true, matrixStack, buffer, lightLevel, overlay, bakedModel);
      matrixStack.popPose();
   }
}
