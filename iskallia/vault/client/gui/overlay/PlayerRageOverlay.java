package iskallia.vault.client.gui.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.VaultMod;
import iskallia.vault.util.PlayerRageHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;

@OnlyIn(Dist.CLIENT)
public class PlayerRageOverlay implements IIngameOverlay {
   private static final ResourceLocation OVERLAY_ICONS = VaultMod.id("textures/gui/overlay_icons.png");

   public void render(ForgeIngameGui gui, PoseStack matrixStack, float partialTick, int scaledWidth, int scaledHeight) {
      Player player = Minecraft.getInstance().player;
      if (player != null) {
         Minecraft mc = Minecraft.getInstance();
         if (mc.gameMode.hasExperience()) {
            int rage = PlayerRageHelper.getCurrentRage(player);
            if (rage != 0) {
               int offsetX = scaledWidth / 2 - 91;
               int offsetY = scaledHeight - 32 + 3;
               int width = Math.round(182.0F * (rage / 100.0F));
               int height = 5;
               int uOffset = 0;
               int vOffset = 64;
               RenderSystem.setShader(GameRenderer::getPositionTexShader);
               RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
               RenderSystem.setShaderTexture(0, OVERLAY_ICONS);
               GuiComponent.blit(matrixStack, offsetX, offsetY, 0, uOffset, vOffset, width, height, 256, 256);
               RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
            }
         }
      }
   }
}
