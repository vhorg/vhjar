package iskallia.vault.client.gui.overlay;

import com.mojang.blaze3d.matrix.MatrixStack;
import iskallia.vault.init.ModEffects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class AbilityVignetteOverlay {
   @SubscribeEvent
   public static void onPreRender(Pre event) {
      if (event.getType() == ElementType.ALL) {
         MatrixStack matrixStack = event.getMatrixStack();
         Minecraft minecraft = Minecraft.func_71410_x();
         int width = minecraft.func_228018_at_().func_198107_o();
         int height = minecraft.func_228018_at_().func_198087_p();
         if (minecraft.field_71439_g != null) {
            if (minecraft.field_71439_g.func_70660_b(ModEffects.RAMPAGE) != null) {
               int alpha = (int)(21.0 * (Math.sin(System.currentTimeMillis() / 250.0) + 2.0)) << 24;
               AbstractGui.func_238467_a_(matrixStack, 0, 0, width, height, alpha | 0xFF0000);
            } else if (minecraft.field_71439_g.func_70660_b(ModEffects.GHOST_WALK) != null) {
               AbstractGui.func_238467_a_(matrixStack, 0, 0, width, height, 548137662);
            }
         }
      }
   }
}
