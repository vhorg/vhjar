package iskallia.vault.client.gui.overlay;

import com.mojang.blaze3d.matrix.MatrixStack;
import iskallia.vault.client.ClientVaultRaidData;
import iskallia.vault.client.gui.overlay.goal.BossBarOverlay;
import iskallia.vault.client.vault.goal.VaultGoalData;
import iskallia.vault.network.message.VaultOverlayMessage;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.RenderGameOverlayEvent.ElementType;
import net.minecraftforge.client.event.RenderGameOverlayEvent.Pre;
import net.minecraftforge.eventbus.api.SubscribeEvent;

@OnlyIn(Dist.CLIENT)
public class VaultGoalBossBarOverlay {
   @SubscribeEvent
   public static void onBossBarRender(Pre event) {
      VaultOverlayMessage.OverlayType type = ClientVaultRaidData.getOverlayType();
      if (event.getType() == ElementType.BOSSHEALTH) {
         if (type == VaultOverlayMessage.OverlayType.VAULT) {
            VaultGoalData data = VaultGoalData.CURRENT_DATA;
            if (data != null) {
               BossBarOverlay overlay = data.getBossBarOverlay();
               if (overlay != null && overlay.shouldDisplay()) {
                  MatrixStack renderStack = event.getMatrixStack();
                  overlay.drawOverlay(renderStack, event.getPartialTicks());
               }
            }
         }
      }
   }
}
