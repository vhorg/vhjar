package iskallia.vault.mixin;

import com.google.common.base.Strings;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.bottle.BottleItem;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.block.model.ItemTransforms.TransformType;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin({ItemRenderer.class})
public class MixinItemRenderer {
   @Inject(
      method = {"renderGuiItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V"},
      at = {@At(
         value = "INVOKE",
         target = "Lnet/minecraft/client/gui/Font;drawInBatch(Ljava/lang/String;FFIZLcom/mojang/math/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;ZII)I"
      )},
      locals = LocalCapture.CAPTURE_FAILSOFT
   )
   public void preStackCount(Font font, ItemStack stack, int x, int y, String text, CallbackInfo ci, PoseStack matrixStack) {
      if (text == null && stack.getCount() >= 1000) {
         String countStr = String.valueOf(stack.getCount());
         String countLengthStr = Strings.repeat("8", countStr.length());
         String comparisonLength = Strings.repeat("8", 3);
         float width = font.width(countLengthStr);
         float compareWidth = font.width(comparisonLength);
         float posX = x + 19 - 2;
         float posY = y + 6 + 3 + 9;
         matrixStack.translate(posX, posY, 0.0);
         float scale = compareWidth / width;
         matrixStack.scale(scale, scale, 1.0F);
         matrixStack.translate(-posX, -posY - 1.0F, 0.0);
      }
   }

   @Inject(
      method = {"Lnet/minecraft/client/renderer/entity/ItemRenderer;render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/renderer/block/model/ItemTransforms$TransformType;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V"},
      at = {@At("HEAD")}
   )
   public void animateBottle(
      ItemStack stack,
      TransformType transformType,
      boolean leftHand,
      PoseStack poseStack,
      MultiBufferSource buffer,
      int combinedLight,
      int combinedOverlay,
      BakedModel model,
      CallbackInfo ci
   ) {
      if (transformType == TransformType.GUI && stack.getItem() == ModItems.BOTTLE) {
         BottleItem.onBeforeGuiItemRender(stack, poseStack);
      }
   }

   @Inject(
      method = {"Lnet/minecraft/client/renderer/entity/ItemRenderer;render(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/client/renderer/block/model/ItemTransforms$TransformType;ZLcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/client/renderer/MultiBufferSource;IILnet/minecraft/client/resources/model/BakedModel;)V"},
      at = {@At("TAIL")}
   )
   public void animateBottlePop(
      ItemStack stack,
      TransformType transformType,
      boolean leftHand,
      PoseStack poseStack,
      MultiBufferSource buffer,
      int combinedLight,
      int combinedOverlay,
      BakedModel model,
      CallbackInfo ci
   ) {
      if (transformType == TransformType.GUI && stack.getItem() == ModItems.BOTTLE) {
         BottleItem.onAfterGuiItemRender(stack, poseStack);
      }
   }

   @Inject(
      method = {"Lnet/minecraft/client/renderer/entity/ItemRenderer;renderGuiItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V"},
      at = {@At("TAIL")}
   )
   public void renderBottleProgressNotification(Font font, ItemStack stack, int xPosition, int yPosition, String text, CallbackInfo ci) {
      if (stack.getItem() == ModItems.BOTTLE) {
         BottleItem.renderBottleProgressNotification(font, stack, xPosition, yPosition);
      }
   }
}
