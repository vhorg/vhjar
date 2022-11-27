package iskallia.vault.config.entry;

import com.google.gson.annotations.Expose;
import java.awt.Color;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;

public class ResearchGroupStyle {
   @Expose
   protected String group = "";
   @Expose
   protected int headerColor = Color.YELLOW.getRGB();
   @Expose
   protected int headerTextColor = Color.BLACK.getRGB();
   @Expose
   protected int x = 0;
   @Expose
   protected int y = 0;
   @Expose
   protected int boxWidth = 0;
   @Expose
   protected int boxHeight = 0;
   @Expose
   protected ResourceLocation icon = null;

   public static ResearchGroupStyle.Builder builder(String group) {
      return new ResearchGroupStyle.Builder(group);
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

   @Nullable
   public ResourceLocation getIcon() {
      return this.icon;
   }

   public static class Builder {
      private final ResearchGroupStyle groupStyle = new ResearchGroupStyle();

      private Builder(String group) {
         this.groupStyle.group = group;
      }

      public ResearchGroupStyle.Builder withPosition(int x, int y) {
         this.groupStyle.x = x;
         this.groupStyle.y = y;
         return this;
      }

      public ResearchGroupStyle.Builder withBoxSize(int width, int height) {
         this.groupStyle.boxWidth = width;
         this.groupStyle.boxHeight = height;
         return this;
      }

      public ResearchGroupStyle.Builder withHeaderColor(int color) {
         this.groupStyle.headerColor = color;
         return this;
      }

      public ResearchGroupStyle.Builder withHeaderTextColor(int color) {
         this.groupStyle.headerTextColor = color;
         return this;
      }

      public ResearchGroupStyle.Builder withIcon(ResourceLocation icon) {
         this.groupStyle.icon = icon;
         return this;
      }

      public ResearchGroupStyle build() {
         return this.groupStyle;
      }
   }
}
