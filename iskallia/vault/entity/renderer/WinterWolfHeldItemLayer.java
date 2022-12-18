package iskallia.vault.entity.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Vector3f;
import iskallia.vault.entity.entity.WinterWolfEntity;
import iskallia.vault.entity.model.WinterWolfModel;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ItemStack;

public class WinterWolfHeldItemLayer extends RenderLayer<WinterWolfEntity, WinterWolfModel> {
   public WinterWolfHeldItemLayer(RenderLayerParent<WinterWolfEntity, WinterWolfModel> renderer) {
      super(renderer);
   }

   public void render(
      @Nonnull PoseStack matrixStack,
      @Nonnull MultiBufferSource bufferSource,
      int packedLight,
      @Nonnull WinterWolfEntity entity,
      float pLimbSwing,
      float pLimbSwingAmount,
      float partialTicks,
      float pAgeInTicks,
      float pNetHeadYaw,
      float pHeadPitch
   ) {
      boolean isSleeping = entity.isSleeping();
      boolean isBaby = entity.isBaby();
      matrixStack.pushPose();
      if (isBaby) {
         float f = 0.75F;
         matrixStack.scale(0.75F, 0.75F, 0.75F);
         matrixStack.translate(0.0, 0.5, 0.209375F);
      }

      matrixStack.translate(
         ((WinterWolfModel)this.getParentModel()).getHead().x / 16.0F,
         ((WinterWolfModel)this.getParentModel()).getHead().y / 16.0F,
         ((WinterWolfModel)this.getParentModel()).getHead().z / 16.0F
      );
      float f1 = entity.getHeadRollAngle(partialTicks);
      matrixStack.mulPose(Vector3f.ZP.rotation(f1));
      matrixStack.mulPose(Vector3f.YP.rotationDegrees(pNetHeadYaw));
      matrixStack.mulPose(Vector3f.XP.rotationDegrees(pHeadPitch));
      if (entity.isBaby()) {
         if (isSleeping) {
            matrixStack.translate(0.4F, 0.26F, 0.15F);
         } else {
            matrixStack.translate(0.06F, 0.26F, -0.5);
         }
      } else if (isSleeping) {
         matrixStack.translate(0.46F, 0.26F, 0.22F);
      } else {
         matrixStack.translate(0.06F, 0.27F, -0.5);
      }

      matrixStack.mulPose(Vector3f.XP.rotationDegrees(90.0F));
      if (isSleeping) {
         matrixStack.mulPose(Vector3f.ZP.rotationDegrees(90.0F));
      }

      ItemStack itemstack = entity.getItemBySlot(EquipmentSlot.MAINHAND);
      Minecraft.getInstance().getItemInHandRenderer().renderItem(entity, itemstack, TransformType.GROUND, false, matrixStack, bufferSource, packedLight);
      matrixStack.popPose();
   }
}
