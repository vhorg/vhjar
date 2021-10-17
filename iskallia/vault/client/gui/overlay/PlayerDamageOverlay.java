package iskallia.vault.client.gui.overlay;

import com.mojang.blaze3d.matrix.MatrixStack;
import iskallia.vault.client.ClientDamageData;
import iskallia.vault.client.gui.helper.ScreenDrawHelper;
import java.text.DecimalFormat;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class PlayerDamageOverlay {
   private static final ResourceLocation STRENGTH_ICON = new ResourceLocation("minecraft", "textures/mob_effect/strength.png");

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public static void setupHealthTexture(Post event) {
      if (event.getType() == ElementType.FOOD) {
         PlayerEntity player = Minecraft.func_71410_x().field_71439_g;
         if (player != null) {
            Minecraft mc = Minecraft.func_71410_x();
            if (mc.field_71442_b.func_78763_f()) {
               float multiplier = ClientDamageData.getCurrentDamageMultiplier();
               if (!(Math.abs(multiplier - 1.0F) < 0.001)) {
                  DecimalFormat format = new DecimalFormat("0");
                  float value = (multiplier - 1.0F) * 100.0F;
                  String displayStr = format.format(value);
                  if (value >= 0.0F) {
                     displayStr = "+" + displayStr;
                  }

                  displayStr = displayStr + "%";
                  TextFormatting color = value < 0.0F ? TextFormatting.RED : TextFormatting.DARK_GREEN;
                  ITextComponent display = new StringTextComponent(displayStr).func_240699_a_(color);
                  ForgeIngameGui.left_height += 6;
                  int left = mc.func_228018_at_().func_198107_o() / 2 - 91;
                  int top = mc.func_228018_at_().func_198087_p() - ForgeIngameGui.left_height;
                  MatrixStack matrixStack = event.getMatrixStack();
                  mc.func_110434_K().func_110577_a(STRENGTH_ICON);
                  matrixStack.func_227860_a_();
                  matrixStack.func_227861_a_(left, top, 0.0);
                  ScreenDrawHelper.drawQuad(buf -> ScreenDrawHelper.rect(buf, matrixStack).dim(16.0F, 16.0F).draw());
                  matrixStack.func_227861_a_(16.0, 4.0, 0.0);
                  mc.field_71466_p.func_243246_a(matrixStack, display, 0.0F, 0.0F, 16777215);
                  matrixStack.func_227865_b_();
                  mc.func_110434_K().func_110577_a(AbstractGui.field_230665_h_);
               }
            }
         }
      }
   }
}
