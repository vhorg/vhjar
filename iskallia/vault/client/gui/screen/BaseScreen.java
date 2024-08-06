package iskallia.vault.client.gui.screen;

import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public abstract class BaseScreen extends Screen implements IPosition {
   protected final int guiWidth;
   protected final int guiHeight;

   protected BaseScreen(Component title, int width, int height) {
      super(title);
      this.guiWidth = width;
      this.guiHeight = height;
   }

   protected int getPadding() {
      return 7;
   }

   protected int getCenterX() {
      return this.width / 2;
   }

   protected int getCenterY() {
      return this.height / 2;
   }

   protected int getGuiLeft() {
      return this.getCenterX() - this.guiWidth / 2;
   }

   protected int getGuiTop() {
      return this.getCenterY() - this.guiHeight / 2;
   }

   @Override
   public int x() {
      return this.getGuiLeft();
   }

   @Override
   public int y() {
      return this.getGuiTop();
   }

   @Override
   public int z() {
      return 0;
   }
}
