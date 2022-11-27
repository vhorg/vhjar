package iskallia.vault.mixin;

import com.google.common.base.Strings;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.entity.ItemRenderer;
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
}
