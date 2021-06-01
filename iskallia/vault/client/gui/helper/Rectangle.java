package iskallia.vault.client.gui.helper;

import net.minecraft.util.math.vector.Vector2f;

public class Rectangle {
   public int x0;
   public int y0;
   public int x1;
   public int y1;

   public Rectangle() {
   }

   public Rectangle(Rectangle other) {
      this.x0 = other.x0;
      this.y0 = other.y0;
      this.x1 = other.x1;
      this.y1 = other.y1;
   }

   public int getWidth() {
      return this.x1 - this.x0;
   }

   public int getHeight() {
      return this.y1 - this.y0;
   }

   public void setWidth(int width) {
      this.x1 = this.x0 + width;
   }

   public void setHeight(int height) {
      this.y1 = this.y0 + height;
   }

   public boolean contains(int x, int y) {
      return this.x0 <= x && x <= this.x1 && this.y0 <= y && y <= this.y1;
   }

   public Vector2f midpoint() {
      return new Vector2f((this.x1 + this.x0) / 2.0F, (this.y1 + this.y0) / 2.0F);
   }
}
