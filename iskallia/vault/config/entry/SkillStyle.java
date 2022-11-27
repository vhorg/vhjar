package iskallia.vault.config.entry;

import com.google.gson.annotations.Expose;
import iskallia.vault.client.gui.helper.SkillFrame;
import net.minecraft.resources.ResourceLocation;

public class SkillStyle {
   @Expose
   public int x;
   @Expose
   public int y;
   @Expose
   public SkillFrame frameType;
   @Expose
   public ResourceLocation icon;

   public SkillStyle(int x, int y, ResourceLocation icon) {
      this(x, y, icon, SkillFrame.STAR);
   }

   public SkillStyle(int x, int y, ResourceLocation icon, SkillFrame skillFrame) {
      this.x = x;
      this.y = y;
      this.frameType = skillFrame;
      this.icon = icon;
   }
}
