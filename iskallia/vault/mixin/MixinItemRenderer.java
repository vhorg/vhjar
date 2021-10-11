package iskallia.vault.mixin;

import com.mojang.blaze3d.systems.RenderSystem;
import iskallia.vault.init.ModAttributes;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

public abstract class MixinItemRenderer {
   private void render(FontRenderer fr, ItemStack stack, int xPosition, int yPosition, String text, CallbackInfo ci) {
      if (ModAttributes.GEAR_MAX_LEVEL.exists(stack)) {
         RenderSystem.disableDepthTest();
         RenderSystem.disableTexture();
         RenderSystem.disableAlphaTest();
         RenderSystem.disableBlend();
         Tessellator tessellator = Tessellator.func_178181_a();
         BufferBuilder bufferbuilder = tessellator.func_178180_c();
         float progress = ModAttributes.GEAR_MAX_LEVEL.getOrDefault(stack, 1).getValue(stack).intValue();
         progress = (progress - ModAttributes.GEAR_LEVEL.getOrDefault(stack, 0.0F).getValue(stack)) / progress;
         progress = MathHelper.func_76131_a(progress, 0.0F, 1.0F);
         if (progress != 0.0F && progress != 1.0F) {
            int i = Math.round(13.0F - progress * 13.0F);
            int var11 = MathHelper.func_181758_c(Math.max(0.0F, 1.0F - progress) / 3.0F, 1.0F, 1.0F);
         }

         RenderSystem.enableBlend();
         RenderSystem.enableAlphaTest();
         RenderSystem.enableTexture();
         RenderSystem.enableDepthTest();
      }
   }
}