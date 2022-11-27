package iskallia.vault.client.gui.overlay;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.ClientVaultRaidData;
import iskallia.vault.client.gui.overlay.goal.BossBarOverlay;
import iskallia.vault.client.vault.goal.VaultGoalData;
import iskallia.vault.network.message.VaultOverlayMessage;
import net.minecraft.client.gui.GuiComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Post;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class VaultGoalBossBarOverlay {
   @SubscribeEvent
   public static void onBossBarRender(Pre event) {
      VaultOverlayMessage.OverlayType type = ClientVaultRaidData.getOverlayType();
      if (event.getType() == ElementType.BOSSINFO) {
         SandEventOverlay.overlayYOffset = 0;
         if (type == VaultOverlayMessage.OverlayType.VAULT) {
            VaultGoalData data = VaultGoalData.CURRENT_DATA;
            if (data != null) {
               BossBarOverlay overlay = data.getBossBarOverlay();
               if (overlay != null && overlay.shouldDisplay()) {
                  PoseStack renderStack = event.getMatrixStack();
                  int yOffset = overlay.drawOverlay(renderStack, event.getPartialTicks());
                  SandEventOverlay.overlayYOffset = yOffset + 3;
                  renderStack.pushPose();
                  renderStack.translate(0.0, SandEventOverlay.overlayYOffset, 0.0);
                  RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
                  RenderSystem.setShaderTexture(0, GuiComponent.GUI_ICONS_LOCATION);
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onBossBarRenderPost(Post event) {
      VaultOverlayMessage.OverlayType type = ClientVaultRaidData.getOverlayType();
      if (event.getType() == ElementType.BOSSINFO && type == VaultOverlayMessage.OverlayType.VAULT) {
         VaultGoalData data = VaultGoalData.CURRENT_DATA;
         if (data != null) {
            BossBarOverlay overlay = data.getBossBarOverlay();
            if (overlay != null && overlay.shouldDisplay()) {
               event.getMatrixStack().popPose();
            }
         }
      }
   }
}
