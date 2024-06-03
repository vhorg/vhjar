package iskallia.vault.block.render;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import iskallia.vault.block.entity.CrystalWorkbenchTileEntity;
import iskallia.vault.container.oversized.OverSizedInventory;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider.Context;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;

public class CrystalWorkbenchRenderer implements BlockEntityRenderer<CrystalWorkbenchTileEntity> {
   private final Minecraft mc = Minecraft.getInstance();

   public CrystalWorkbenchRenderer(Context context) {
   }

   public void render(
      CrystalWorkbenchTileEntity entity, float partialTicks, @Nonnull PoseStack poseStack, @Nonnull MultiBufferSource buffer, int light, int overlay
   ) {
      poseStack.pushPose();
      poseStack.translate(0.5, 1.0, 0.5);
      poseStack.mulPose(Vector3f.YP.rotation((float)(Math.PI * (System.currentTimeMillis() / 10000.0) % (Math.PI * 2))));
      ItemStack inputStack = entity.getInput().getItem(0);
      if (!inputStack.isEmpty()) {
         this.renderItem(inputStack, poseStack, buffer, overlay, light);
      }

      OverSizedInventory uniqueIngredients = entity.getUniqueIngredients();

      for (int i = 0; i < uniqueIngredients.getContainerSize(); i++) {
         ItemStack uniqueIngredient = uniqueIngredients.getItem(i);
         if (!uniqueIngredient.isEmpty()) {
            poseStack.pushPose();
            poseStack.scale(0.5F, 0.5F, 0.5F);
            double angle = i / 3.0F * 2.0F * Math.PI + Math.PI * (System.currentTimeMillis() / 5000.0) % (Math.PI * 2);
            double y = Math.sin(angle) * 0.75;
            double x = Math.cos(angle) * 0.75;
            poseStack.translate(x, 0.2 + Math.sin(angle * 2.0) * 0.1, y);
            poseStack.mulPose(Vector3f.YN.rotation((float)(angle + (Math.PI / 2))));
            this.renderItem(uniqueIngredient, poseStack, buffer, overlay, light);
            poseStack.popPose();
         }
      }

      poseStack.popPose();
   }

   private void renderItem(ItemStack stack, PoseStack matrixStack, MultiBufferSource buffer, int combinedOverlay, int lightLevel) {
      BakedModel ibakedmodel = this.mc.getItemRenderer().getModel(stack, null, null, 0);
      this.mc.getItemRenderer().render(stack, TransformType.GROUND, true, matrixStack, buffer, lightLevel, combinedOverlay, ibakedmodel);
   }
}
