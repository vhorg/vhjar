package iskallia.vault.client.gui.overlay.goal;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import iskallia.vault.client.ClientVaultRaidData;
import iskallia.vault.client.gui.helper.LightmapHelper;
import iskallia.vault.client.vault.goal.VaultGoalData;
import iskallia.vault.client.vault.goal.VaultObeliskData;
import iskallia.vault.network.message.VaultOverlayMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.MultiBufferSource.BufferSource;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class ObeliskGoalOverlay {
   public static final ResourceLocation VAULT_HUD_RESOURCE = new ResourceLocation("the_vault", "textures/gui/vault_hud.png");

   @SubscribeEvent
   public static void onObeliskRender(Post event) {
      VaultOverlayMessage.OverlayType type = ClientVaultRaidData.getOverlayType();
      if (event.getType() == ElementType.ALL && type == VaultOverlayMessage.OverlayType.VAULT) {
         VaultGoalData data = VaultGoalData.CURRENT_DATA;
         if (data != null) {
            if (data instanceof VaultObeliskData displayData) {
               PoseStack renderStack = event.getMatrixStack();
               renderObeliskMessage(renderStack, displayData);
               renderObeliskIndicator(renderStack, displayData);
            }

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
            RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
         }
      }
   }

   private static void renderObeliskMessage(PoseStack matrixStack, VaultObeliskData data) {
      Minecraft mc = Minecraft.getInstance();
      Font fr = mc.font;
      BufferSource buffer = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
      int bottom = mc.getWindow().getGuiScaledHeight();
      FormattedCharSequence bidiText = data.getMessage().getVisualOrderText();
      matrixStack.pushPose();
      matrixStack.translate(15.0, bottom - 34, 0.0);
      fr.drawInBatch(bidiText, 0.0F, 0.0F, -1, true, matrixStack.last().pose(), buffer, false, 0, LightmapHelper.getPackedFullbrightCoords());
      buffer.endBatch();
      matrixStack.popPose();
   }

   private static void renderObeliskIndicator(PoseStack matrixStack, VaultObeliskData data) {
      int maxObelisks = data.getMaxObelisks();
      int touchedObelisks = data.getCurrentObelisks();
      if (maxObelisks > 0) {
         Minecraft mc = Minecraft.getInstance();
         int untouchedObelisks = maxObelisks - touchedObelisks;
         int bottom = mc.getWindow().getGuiScaledHeight();
         float scale = 0.6F;
         int gap = 2;
         int margin = 2;
         RenderSystem.setShader(GameRenderer::getPositionTexShader);
         RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
         RenderSystem.setShaderTexture(0, VAULT_HUD_RESOURCE);
         int iconWidth = 12;
         int iconHeight = 22;
         matrixStack.pushPose();
         matrixStack.translate(15.0, bottom - 34, 0.0);
         matrixStack.translate(0.0, -margin, 0.0);
         matrixStack.translate(0.0, -scale * iconHeight, 0.0);
         matrixStack.scale(scale, scale, scale);

         for (int i = 0; i < touchedObelisks; i++) {
            int u = 77;
            int v = 84;
            GuiComponent.blit(matrixStack, 0, 0, u, v, iconWidth, iconHeight, 256, 256);
            matrixStack.translate(scale * gap + iconWidth, 0.0, 0.0);
         }

         for (int i = 0; i < untouchedObelisks; i++) {
            int u = 64;
            int v = 84;
            GuiComponent.blit(matrixStack, 0, 0, u, v, iconWidth, iconHeight, 256, 256);
            matrixStack.translate(scale * gap + iconWidth, 0.0, 0.0);
         }

         matrixStack.popPose();
      }
   }
}
