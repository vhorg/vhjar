package iskallia.vault.client.gui.overlay.goal;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import iskallia.vault.VaultMod;
import iskallia.vault.client.ClientVaultRaidData;
import iskallia.vault.client.gui.helper.LightmapHelper;
import iskallia.vault.client.gui.helper.ScreenDrawHelper;
import iskallia.vault.client.vault.goal.CakeHuntData;
import iskallia.vault.client.vault.goal.VaultGoalData;
import iskallia.vault.network.message.VaultOverlayMessage;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;

public class CakeHuntOverlay implements IIngameOverlay {
   private static final ResourceLocation ARCHITECT_HUD = VaultMod.id("textures/gui/architect_event_bar.png");

   public void render(ForgeIngameGui gui, PoseStack renderStack, float partialTick, int width, int height) {
      VaultOverlayMessage.OverlayType type = ClientVaultRaidData.getOverlayType();
      if (type == VaultOverlayMessage.OverlayType.VAULT) {
         Minecraft mc = Minecraft.getInstance();
         if (VaultGoalData.CURRENT_DATA instanceof CakeHuntData displayData) {
            BufferSource buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
            Font fr = mc.font;
            float part = displayData.getCompletePercent();
            Component txt = new TextComponent("Find the cakes!").withStyle(ChatFormatting.AQUA).withStyle(ChatFormatting.BOLD);
            fr.drawInBatch(
               txt.getVisualOrderText(), 8.0F, height - 54, -1, true, renderStack.last().pose(), buffer, false, 0, LightmapHelper.getPackedFullbrightCoords()
            );
            txt = new TextComponent(displayData.getFoundCakes() + " / " + displayData.getTotalCakes())
               .withStyle(ChatFormatting.AQUA)
               .withStyle(ChatFormatting.BOLD);
            fr.drawInBatch(
               txt.getVisualOrderText(), 12.0F, height - 44, -1, true, renderStack.last().pose(), buffer, false, 0, LightmapHelper.getPackedFullbrightCoords()
            );
            buffer.endBatch();
            RenderSystem.enableBlend();
            RenderSystem.defaultBlendFunc();
            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, ARCHITECT_HUD);
            ScreenDrawHelper.drawTexturedQuads(buf -> {
               ScreenDrawHelper.rect(buf, renderStack).at(15.0F, height - 31).dim(54.0F, 7.0F).texVanilla(0.0F, 105.0F, 54.0F, 7.0F).draw();
               ScreenDrawHelper.rect(buf, renderStack).at(16.0F, height - 30).dim(52.0F * part, 5.0F).texVanilla(0.0F, 113.0F, 52.0F * part, 5.0F).draw();
            });
         }

         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
      }
   }
}
