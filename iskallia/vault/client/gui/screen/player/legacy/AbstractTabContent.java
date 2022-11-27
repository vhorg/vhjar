package iskallia.vault.client.gui.screen.player.legacy;

import iskallia.vault.client.gui.screen.player.AbstractSkillTabElementContainerScreen;

public abstract class AbstractTabContent implements TabContent {
   protected final AbstractSkillTabElementContainerScreen<?> parentScreen;

   public AbstractTabContent(AbstractSkillTabElementContainerScreen<?> parentScreen) {
      this.parentScreen = parentScreen;
   }

   @Override
   public void update() {
   }

   @Override
   public void removed() {
   }

   @Override
   public boolean mouseClicked(double mouseX, double mouseY, int button) {
      return false;
   }

   @Override
   public boolean mouseReleased(double mouseX, double mouseY, int button) {
      return false;
   }

   @Override
   public void mouseMoved(double mouseX, double mouseY) {
   }

   @Override
   public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
      return false;
   }
}
