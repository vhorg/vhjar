package iskallia.vault.client.gui.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.helper.FontHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class PlayerArmorOverlay implements IIngameOverlay {
   public void render(ForgeIngameGui gui, PoseStack poseStack, float partialTick, int width, int height) {
      Minecraft mc = Minecraft.getInstance();
      if (!mc.options.hideGui && gui.shouldDrawSurvivalElements()) {
         gui.setupOverlayRenderState(true, false);
         this.renderArmor(gui, poseStack, partialTick, width, height);
      }
   }

   private void renderArmor(ForgeIngameGui gui, PoseStack poseStack, float partialTick, int width, int height) {
      Player player = Minecraft.getInstance().player;
      if (player != null) {
         int armor = player.getArmorValue();
         if (armor <= 20) {
            ForgeIngameGui.ARMOR_LEVEL_ELEMENT.render(gui, poseStack, partialTick, width, height);
         } else {
            Minecraft mc = Minecraft.getInstance();
            RenderSystem.enableBlend();
            int left = mc.getWindow().getGuiScaledWidth() / 2 - 91;
            int top = mc.getWindow().getGuiScaledHeight() - ((ForgeIngameGui)Minecraft.getInstance().gui).left_height;

            for (int i = 0; i < 8; i++) {
               GuiComponent.blit(poseStack, left, top, 0, 34.0F, 9.0F, 9, 9, 256, 256);
               left += 8;
            }

            FontHelper.drawStringWithBorder(poseStack, String.valueOf(armor), (float)(left + 2), (float)(top + 1), -4671036, -16777216);
            ((ForgeIngameGui)Minecraft.getInstance().gui).left_height += 10;
            gui.setupOverlayRenderState(true, false);
         }
      }
   }

   @SubscribeEvent(
      priority = EventPriority.LOW
   )
   public static void setupHealthTexture(Pre event) {
      if (event.getType() == ElementType.ALL) {
         Player player = Minecraft.getInstance().player;
         if (player != null) {
            Minecraft mc = Minecraft.getInstance();
            if (mc.gameMode.hasExperience()) {
               int armor = player.getArmorValue();
               if (armor > 20) {
                  PoseStack matrixStack = event.getMatrixStack();
                  RenderSystem.enableBlend();
                  int left = mc.getWindow().getGuiScaledWidth() / 2 - 91;
                  int top = mc.getWindow().getGuiScaledHeight() - ((ForgeIngameGui)Minecraft.getInstance().gui).left_height;

                  for (int i = 0; i < 8; i++) {
                     GuiComponent.blit(matrixStack, left, top, 0, 34.0F, 9.0F, 9, 9, 256, 256);
                     left += 8;
                  }

                  FontHelper.drawStringWithBorder(matrixStack, String.valueOf(armor), (float)(left + 2), (float)(top + 1), -4671036, -16777216);
                  ((ForgeIngameGui)Minecraft.getInstance().gui).left_height += 10;
                  RenderSystem.disableBlend();
                  RenderSystem.setShader(GameRenderer::getPositionTexShader);
                  RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                  RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
               }
            }
         }
      }
   }
}
