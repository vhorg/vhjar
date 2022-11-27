package iskallia.vault.client.gui.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.ClientDamageData;
import iskallia.vault.client.gui.helper.ScreenDrawHelper;
import java.text.DecimalFormat;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;

@OnlyIn(Dist.CLIENT)
public class PlayerDamageOverlay implements IIngameOverlay {
   private static final ResourceLocation STRENGTH_ICON = new ResourceLocation("minecraft", "textures/mob_effect/strength.png");

   public void render(ForgeIngameGui gui, PoseStack matrixStack, float partialTick, int width, int height) {
      Player player = Minecraft.getInstance().player;
      if (player != null) {
         Minecraft mc = Minecraft.getInstance();
         if (mc.gameMode.hasExperience()) {
            float multiplier = ClientDamageData.getCurrentDamageMultiplier();
            if (!(Math.abs(multiplier - 1.0F) < 0.001)) {
               DecimalFormat format = new DecimalFormat("0");
               float value = (multiplier - 1.0F) * 100.0F;
               String displayStr = format.format(value);
               if (value >= 0.0F) {
                  displayStr = "+" + displayStr;
               }

               displayStr = displayStr + "%";
               ChatFormatting color = value < 0.0F ? ChatFormatting.RED : ChatFormatting.DARK_GREEN;
               Component display = new TextComponent(displayStr).withStyle(color);
               ((ForgeIngameGui)Minecraft.getInstance().gui).left_height += 6;
               int left = mc.getWindow().getGuiScaledWidth() / 2 - 91;
               int top = mc.getWindow().getGuiScaledHeight() - ((ForgeIngameGui)Minecraft.getInstance().gui).left_height;
               RenderSystem.setShader(GameRenderer::getPositionTexShader);
               RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
               RenderSystem.setShaderTexture(0, STRENGTH_ICON);
               matrixStack.pushPose();
               matrixStack.translate(left, top, 0.0);
               ScreenDrawHelper.drawTexturedQuads(buf -> ScreenDrawHelper.rect(buf, matrixStack).dim(16.0F, 16.0F).draw());
               matrixStack.translate(16.0, 4.0, 0.0);
               mc.font.drawShadow(matrixStack, display, 0.0F, 0.0F, 16777215);
               matrixStack.popPose();
            }
         }
      }
   }
}
