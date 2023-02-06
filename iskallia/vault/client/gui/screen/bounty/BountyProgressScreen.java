package iskallia.vault.client.gui.screen.bounty;

import iskallia.vault.bounty.Bounty;
import iskallia.vault.client.gui.framework.ScreenRenderers;
import iskallia.vault.client.gui.framework.element.LabelElement;
import iskallia.vault.client.gui.framework.render.ScreenTooltipRenderer;
import iskallia.vault.client.gui.framework.screen.AbstractElementScreen;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.client.gui.screen.bounty.element.BountyProgressElement;
import iskallia.vault.init.ModKeybinds;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;

public class BountyProgressScreen extends AbstractElementScreen {
   private List<Bounty> bounties;
   private Bounty active;

   public BountyProgressScreen(List<Bounty> bounties) {
      super(new TextComponent(""), ScreenRenderers.getBuffered(), ScreenTooltipRenderer::create);
      this.bounties = bounties;
      if (!this.bounties.isEmpty()) {
         this.active = this.bounties.get(0);
      }

      this.setGuiSize(Spatials.size(183, 224));
      this.initializeElements();
   }

   private void initializeElements() {
      this.elementStore.removeAllElements();
      if (this.bounties.isEmpty()) {
         this.addElement(
            new LabelElement(
               Spatials.positionXY(this.getGuiSpatial().width() / 2, this.getGuiSpatial().height() / 2 - 10).size(100, 20),
               new TextComponent("You must activate a bounty to view its progress.").withStyle(ChatFormatting.WHITE),
               LabelTextStyle.center().shadow().wrap()
            )
         );
      } else {
         BountyProgressElement var1 = this.addElement(
            new BountyProgressElement(Spatials.positionXY(0, 0).size(this.getGuiSpatial().width() - 5, this.getGuiSpatial().height() - 5), this.active)
               .layout((screen, gui, parent, world) -> world.translateXY(gui))
         );
      }
   }

   private void getNextBounty() {
      if (this.bounties.size() > 1) {
         this.active = this.bounties.stream().filter(bounty -> !this.active.getId().equals(bounty.getId())).findFirst().orElse(null);
      }
   }

   public boolean shouldCloseOnEsc() {
      return false;
   }

   public boolean isPauseScreen() {
      return false;
   }

   public boolean mouseReleased(double mouseX, double mouseY, int button) {
      this.onClose();
      return super.mouseReleased(mouseX, mouseY, button);
   }

   public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
      if (keyCode == 257) {
         this.getNextBounty();
         this.initializeElements();
      }

      if (keyCode == ModKeybinds.bountyStatusKey.getKey().getValue()) {
         this.onClose();
         return true;
      } else {
         return super.keyReleased(keyCode, scanCode, modifiers);
      }
   }
}
