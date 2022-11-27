package iskallia.vault.client.gui.overlay.goal;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import iskallia.vault.client.ClientVaultRaidData;
import iskallia.vault.client.gui.helper.LightmapHelper;
import iskallia.vault.client.vault.goal.AncientGoalData;
import iskallia.vault.client.vault.goal.VaultGoalData;
import iskallia.vault.network.message.VaultOverlayMessage;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.gui.ForgeIngameGui;
import net.minecraftforge.client.gui.IIngameOverlay;

@OnlyIn(Dist.CLIENT)
public class AncientGoalOverlay implements IIngameOverlay {
   public static final ResourceLocation VAULT_HUD_RESOURCE = new ResourceLocation("the_vault", "textures/gui/vault_hud.png");

   public void render(ForgeIngameGui gui, PoseStack renderStack, float partialTick, int width, int height) {
      VaultOverlayMessage.OverlayType type = ClientVaultRaidData.getOverlayType();
      if (type == VaultOverlayMessage.OverlayType.VAULT) {
         VaultGoalData data = VaultGoalData.CURRENT_DATA;
         if (data != null) {
            if (data instanceof AncientGoalData displayData) {
               renderAncientsMessage(renderStack, displayData);
               renderAncientIndicator(renderStack, displayData);
            }

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
         }
      }
   }

   private static void renderAncientsMessage(PoseStack matrixStack, AncientGoalData data) {
      Minecraft mc = Minecraft.getInstance();
      Font fr = mc.font;
      BufferSource buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
      int bottom = mc.getWindow().getGuiScaledHeight();
      int offsetY = 54;
      List<FormattedCharSequence> msg = new ArrayList<>();
      if (data.getTotalAncients() <= 0) {
         msg.add(new TextComponent("Hunt and escape").withStyle(ChatFormatting.DARK_AQUA).withStyle(ChatFormatting.BOLD).getVisualOrderText());
         msg.add(new TextComponent("the Vault!").withStyle(ChatFormatting.DARK_AQUA).withStyle(ChatFormatting.BOLD).getVisualOrderText());
         offsetY = 24;
      } else {
         String eternalPart = data.getTotalAncients() > 1 ? "eternals" : "eternal";
         msg.add(new TextComponent("Find your " + eternalPart).withStyle(ChatFormatting.DARK_AQUA).withStyle(ChatFormatting.BOLD).getVisualOrderText());
         msg.add(new TextComponent("and escape the Vault!").withStyle(ChatFormatting.DARK_AQUA).withStyle(ChatFormatting.BOLD).getVisualOrderText());
      }

      matrixStack.pushPose();
      matrixStack.translate(12.0, bottom - offsetY - msg.size() * 10, 0.0);

      for (int i = 0; i < msg.size(); i++) {
         FormattedCharSequence txt = msg.get(i);
         fr.drawInBatch(txt, 0.0F, i * 10, -1, true, matrixStack.last().pose(), buffer, false, 0, LightmapHelper.getPackedFullbrightCoords());
      }

      buffer.endBatch();
      matrixStack.popPose();
   }

   private static void renderAncientIndicator(PoseStack matrixStack, AncientGoalData data) {
      int totalAncients = data.getTotalAncients();
      int foundAncients = data.getFoundAncients();
      if (totalAncients > 0) {
         Minecraft mc = Minecraft.getInstance();
         int untouchedObelisks = totalAncients - foundAncients;
         int bottom = mc.getWindow().getGuiScaledHeight();
         float scale = 1.0F;
         int gap = 2;
         int margin = 2;
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.setShaderTexture(0, VAULT_HUD_RESOURCE);
         int iconWidth = 15;
         int iconHeight = 27;
         matrixStack.pushPose();
         matrixStack.translate(12.0, bottom - 24, 0.0);
         matrixStack.translate(0.0, -margin, 0.0);
         matrixStack.translate(0.0, -scale * iconHeight, 0.0);
         matrixStack.scale(scale, scale, scale);

         for (int i = 0; i < foundAncients; i++) {
            int u = 81;
            int v = 109;
            GuiComponent.blit(matrixStack, 0, 0, u, v, iconWidth, iconHeight, 256, 256);
            matrixStack.translate(scale * gap + iconWidth, 0.0, 0.0);
         }

         for (int i = 0; i < untouchedObelisks; i++) {
            int u = 64;
            int v = 109;
            GuiComponent.blit(matrixStack, 0, 0, u, v, iconWidth, iconHeight, 256, 256);
            matrixStack.translate(scale * gap + iconWidth, 0.0, 0.0);
         }

         matrixStack.popPose();
      }
   }
}
