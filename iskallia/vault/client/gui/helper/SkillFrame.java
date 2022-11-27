package iskallia.vault.client.gui.helper;

import iskallia.vault.VaultMod;
import iskallia.vault.util.ResourceBoundary;

public enum SkillFrame {
   STAR(new ResourceBoundary(VaultMod.id("textures/gui/skill_widget.png"), 0, 31, 30, 30)),
   RECTANGULAR(new ResourceBoundary(VaultMod.id("textures/gui/skill_widget.png"), 30, 31, 30, 30));

   private final ResourceBoundary resourceBoundary;

   private SkillFrame(ResourceBoundary resourceBoundary) {
      this.resourceBoundary = resourceBoundary;
   }

   public ResourceBoundary getResourceBoundary() {
      return this.resourceBoundary;
   }
}
