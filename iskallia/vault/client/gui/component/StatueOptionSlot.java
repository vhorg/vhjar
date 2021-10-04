package iskallia.vault.client.gui.component;

import java.awt.Rectangle;
import net.minecraft.item.ItemStack;

public class StatueOptionSlot {
   private int posX;
   private int posY;
   private int width;
   private int height;
   private ItemStack stack;

   public StatueOptionSlot(int posX, int posY, int width, int height, ItemStack stack) {
      this.posX = posX;
      this.posY = posY;
      this.width = width;
      this.height = height;
      this.stack = stack;
   }

   public int getPosX() {
      return this.posX;
   }

   public int getPosY() {
      return this.posY;
   }

   public ItemStack getStack() {
      return this.stack;
   }

   public boolean contains(double mouseX, double mouseY) {
      return new Rectangle(this.posX, this.posY, this.width, this.height).contains(mouseX, mouseY);
   }

   @Override
   public String toString() {
      return "StatueOptionSlot{posX=" + this.posX + ", posY=" + this.posY + ", stack=" + this.stack.func_200301_q().getString() + '}';
   }
}
