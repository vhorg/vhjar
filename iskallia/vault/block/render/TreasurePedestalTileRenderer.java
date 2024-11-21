package iskallia.vault.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import iskallia.vault.block.entity.TreasurePedestalTileEntity;
import iskallia.vault.client.util.ClientScheduler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public class TreasurePedestalTileRenderer implements BlockEntityRenderer<TreasurePedestalTileEntity> {
   private final Minecraft minecraft = Minecraft.getInstance();
   private final ItemRenderer itemRenderer = this.minecraft.getItemRenderer();

   public TreasurePedestalTileRenderer(Context context) {
   }

   public void render(TreasurePedestalTileEntity tile, float pTicks, PoseStack pose, MultiBufferSource buffer, int combinedLightIn, int combinedOverlayIn) {
      if (this.minecraft.level != null) {
         ItemStack contained = tile.getContained();
         if (!contained.isEmpty()) {
            BakedModel bakedmodel = this.itemRenderer.getModel(contained, this.minecraft.level, null, 0);
            int tickPart = (int)ClientScheduler.INSTANCE.getTick();
            float angle = (tickPart + pTicks) / 20.0F;
            float yOffset = Mth.sin((tickPart + pTicks) / 10.0F) * 0.1F + 0.1F;
            yOffset += 0.25F * bakedmodel.getTransforms().getTransform(TransformType.GROUND).scale.y();
            pose.pushPose();
            pose.translate(0.5, 1.0 + yOffset, 0.5);
            pose.mulPose(Vector3f.YP.rotation(angle));
            this.itemRenderer.render(contained, TransformType.GROUND, false, pose, buffer, combinedLightIn, OverlayTexture.NO_OVERLAY, bakedmodel);
            pose.popPose();
         }
      }
   }
}
