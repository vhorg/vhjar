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
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextComponent;

public class BountyProgressScreen extends AbstractElementScreen {
   private final Bounty active;

   public BountyProgressScreen(Bounty active) {
      super(new TextComponent(""), ScreenRenderers.getBuffered(), ScreenTooltipRenderer::create);
      this.active = active;
      this.setGuiSize(Spatials.size(183, 224));
      if (active == null) {
         this.addElement(
            new LabelElement(
               Spatials.positionXY(this.getGuiSpatial().width() / 2, this.getGuiSpatial().height() / 2 - 10).size(100, 20),
               new TextComponent("You must activate a bounty to view its progress.").withStyle(ChatFormatting.WHITE),
               LabelTextStyle.center().shadow().wrap()
            )
         );
      } else {
         this.initializeElements();
      }
   }

   private void initializeElements() {
      BountyProgressElement element = this.addElement(
         new BountyProgressElement(Spatials.positionXY(0, 0).size(this.getGuiSpatial().width() - 5, this.getGuiSpatial().height() - 5), this.active)
            .layout((screen, gui, parent, world) -> world.translateXY(gui))
      );
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
      if (keyCode == ModKeybinds.bountyStatusKey.getKey().getValue()) {
         this.onClose();
         return true;
      } else {
         return super.keyReleased(keyCode, scanCode, modifiers);
      }
   }
}
