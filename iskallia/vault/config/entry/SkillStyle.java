package iskallia.vault.config.entry;

import com.google.gson.annotations.Expose;
import iskallia.vault.client.gui.helper.SkillFrame;

public class SkillStyle {
   @Expose
   public int x;
   @Expose
   public int y;
   @Expose
   public SkillFrame frameType;
   @Expose
   public int u;
   @Expose
   public int v;

   public SkillStyle(int x, int y, int u, int v) {
      this(x, y, u, v, SkillFrame.STAR);
   }

   public SkillStyle(int x, int y, int u, int v, SkillFrame skillFrame) {
      this.x = x;
      this.y = y;
      this.frameType = skillFrame;
      this.u = u;
      this.v = v;
   }
}
