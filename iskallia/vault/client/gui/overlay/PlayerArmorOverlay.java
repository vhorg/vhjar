package iskallia.vault.client.gui.overlay;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import iskallia.vault.client.gui.helper.FontHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class PlayerArmorOverlay {
   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public static void setupHealthTexture(Pre event) {
      if (event.getType() == ElementType.ARMOR) {
         PlayerEntity player = Minecraft.func_71410_x().field_71439_g;
         if (player != null) {
            Minecraft mc = Minecraft.func_71410_x();
            if (mc.field_71442_b.func_78763_f()) {
               int armor = player.func_70658_aO();
               if (armor > 20) {
                  event.setCanceled(true);
                  MatrixStack matrixStack = event.getMatrixStack();
                  RenderSystem.enableBlend();
                  int left = mc.func_228018_at_().func_198107_o() / 2 - 91;
                  int top = mc.func_228018_at_().func_198087_p() - ForgeIngameGui.left_height;

                  for (int i = 0; i < 8; i++) {
                     AbstractGui.func_238464_a_(matrixStack, left, top, 0, 34.0F, 9.0F, 9, 9, 256, 256);
                     left += 8;
                  }

                  FontHelper.drawStringWithBorder(matrixStack, String.valueOf(armor), (float)(left + 2), (float)(top + 1), -4671036, -16777216);
                  ForgeIngameGui.left_height += 10;
                  RenderSystem.disableBlend();
                  mc.func_110434_K().func_110577_a(AbstractGui.field_230665_h_);
               }
            }
         }
      }
   }
}
