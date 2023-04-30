package iskallia.vault.client.gui.screen.player.legacy.widget;

import java.awt.Color;
import net.minecraft.resources.ResourceLocation;

public class TalentGroupStyle {
   protected String group = "";
   protected int headerColor = Color.GRAY.getRGB();
   protected int headerTextColor = Color.WHITE.getRGB();
   protected int x = 0;
   protected int y = 0;
   protected int boxWidth = 0;
   protected int boxHeight = 0;
   protected ResourceLocation icon = null;

   public TalentGroupStyle(String group, int x, int y, int boxWidth, int boxHeight) {
      this.group = group;
      this.x = x - 25;
      this.y = y - 34;
      this.boxWidth = boxWidth + 50;
      this.boxHeight = boxHeight + 89;
   }

   public String getGroup() {
      return this.group;
   }

   public int getHeaderColor() {
      return this.headerColor;
   }

   public int getHeaderTextColor() {
      return this.headerTextColor;
   }

   public int getX() {
      return this.x;
   }

   public int getY() {
      return this.y;
   }

   public int getBoxWidth() {
      return this.boxWidth;
   }

   public int getBoxHeight() {
      return this.boxHeight;
   }
}
