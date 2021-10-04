package iskallia.vault.client.gui.overlay;

import com.mojang.blaze3d.matrix.MatrixStack;
import iskallia.vault.Vault;
import iskallia.vault.util.PlayerRageHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;

@OnlyIn(Dist.CLIENT)
public class PlayerRageOverlay {
   private static final ResourceLocation OVERLAY_ICONS = Vault.id("textures/gui/overlay_icons.png");

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public static void setupHealthTexture(Post event) {
      if (event.getType() == ElementType.EXPERIENCE) {
         PlayerEntity player = Minecraft.func_71410_x().field_71439_g;
         if (player != null) {
            Minecraft mc = Minecraft.func_71410_x();
            if (mc.field_71442_b.func_78763_f()) {
               int rage = PlayerRageHelper.getCurrentRage(player, LogicalSide.CLIENT);
               if (rage > 0) {
                  int scaledWidth = event.getWindow().func_198107_o();
                  int scaledHeight = event.getWindow().func_198087_p();
                  MatrixStack matrixStack = event.getMatrixStack();
                  int offsetX = scaledWidth / 2 - 91;
                  int offsetY = scaledHeight - 32 + 3;
                  int width = Math.round(182.0F * (rage / 100.0F));
                  int height = 5;
                  int uOffset = 0;
                  int vOffset = 64;
                  mc.func_110434_K().func_110577_a(OVERLAY_ICONS);
                  AbstractGui.func_238464_a_(matrixStack, offsetX, offsetY, 0, uOffset, vOffset, width, height, 256, 256);
                  mc.func_110434_K().func_110577_a(AbstractGui.field_230665_h_);
               }
            }
         }
      }
   }
}
