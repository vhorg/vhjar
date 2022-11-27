package iskallia.vault.client.gui.screen.player.legacy.tab.split.spi;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.component.ScrollableContainer;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.screen.player.legacy.ILegacySkillTreeScreen;
import iskallia.vault.client.render.TextureRegion;
import iskallia.vault.client.render.TextureRegionRenderer;
import java.awt.Rectangle;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.Button;

public abstract class AbstractDialog<S extends ILegacySkillTreeScreen> extends GuiComponent {
   protected final S skillTreeScreen;
   protected Rectangle bounds;
   protected ScrollableContainer descriptionComponent;
   protected Button regretButton;
   protected Button learnButton;
   private static final TextureRegion CONTAINER_BORDER_CORNER_TOP_LEFT = new TextureRegion(0, 44, 5, 5);
   private static final TextureRegion CONTAINER_BORDER_CORNER_TOP_RIGHT = new TextureRegion(8, 44, 5, 5);
   private static final TextureRegion CONTAINER_BORDER_CORNER_BOTTOM_LEFT = new TextureRegion(0, 52, 5, 5);
   private static final TextureRegion CONTAINER_BORDER_CORNER_BOTTOM_RIGHT = new TextureRegion(8, 52, 5, 5);
   private static final TextureRegion CONTAINER_BORDER_TOP = new TextureRegion(6, 44, 1, 5);
   private static final TextureRegion CONTAINER_BORDER_BOTTOM = new TextureRegion(6, 52, 1, 5);
   private static final TextureRegion CONTAINER_BORDER_LEFT = new TextureRegion(0, 50, 5, 1);
   private static final TextureRegion CONTAINER_BORDER_RIGHT = new TextureRegion(8, 50, 5, 1);

   protected AbstractDialog(S skillTreeScreen) {
      this.skillTreeScreen = skillTreeScreen;
   }

   public abstract void update();

   public void setBounds(Rectangle bounds) {
      this.bounds = bounds;
   }

   public void mouseMoved(double screenX, double screenY) {
      if (this.bounds != null) {
         double containerX = screenX - this.bounds.x;
         double containerY = screenY - this.bounds.y;
         if (this.learnButton != null) {
            this.learnButton.mouseMoved(containerX, containerY);
         }

         if (this.regretButton != null) {
            this.regretButton.mouseMoved(containerX, containerY);
         }
      }
   }

   public boolean mouseClicked(double screenX, double screenY, int button) {
      if (this.bounds == null) {
         return false;
      } else {
         if (this.learnButton != null) {
            double containerX = screenX - this.bounds.x - 5.0;
            double containerY = screenY - this.bounds.y - 5.0;
            if (this.learnButton.mouseClicked(containerX, containerY, button)) {
               return true;
            }
         }

         if (this.regretButton != null) {
            double containerX = screenX - this.bounds.x - 5.0;
            double containerY = screenY - this.bounds.y - 5.0;
            if (this.regretButton.mouseClicked(containerX, containerY, button)) {
               return true;
            }
         }

         return false;
      }
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double delta) {
      if (this.bounds != null
         && this.descriptionComponent != null
         && this.descriptionComponent.getBounds() != null
         && this.descriptionComponent.getBounds().contains((int)mouseX - this.bounds.x, (int)mouseY - this.bounds.y)) {
         this.descriptionComponent.mouseScrolled(mouseX, mouseY, delta);
         return true;
      } else {
         return false;
      }
   }

   public Rectangle getHeadingBounds() {
      int widgetHeight = 32;
      return new Rectangle(5, 5, this.bounds.width - 20, widgetHeight + 5);
   }

   public Rectangle getDescriptionsBounds() {
      Rectangle headingBounds = this.getHeadingBounds();
      int topOffset = headingBounds.y + headingBounds.height + 10;
      int descriptionHeight = this.bounds.height - 50 - topOffset;
      return new Rectangle(headingBounds.x, topOffset, headingBounds.width, descriptionHeight);
   }

   public void render(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      int width = this.bounds.width - 20;
      if (this.learnButton != null && this.regretButton != null) {
         this.learnButton.y = this.bounds.height - 40;
         this.learnButton.setHeight(20);
         this.regretButton.y = this.bounds.height - 40;
         this.regretButton.setHeight(20);
         int elementWidth = width / 2 - 4;
         this.learnButton.x = 5;
         this.learnButton.setWidth(elementWidth);
         this.regretButton.x = 5 + elementWidth + 8;
         this.regretButton.setWidth(elementWidth);
      } else {
         if (this.learnButton != null) {
            this.learnButton.x = 5;
            this.learnButton.y = this.bounds.height - 40;
            this.learnButton.setHeight(20);
            this.learnButton.setWidth(width);
         }

         if (this.regretButton != null) {
            this.regretButton.x = 5;
            this.regretButton.y = this.bounds.height - 40;
            this.regretButton.setHeight(20);
            this.regretButton.setWidth(width);
         }
      }
   }

   protected void renderBackground(PoseStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      fill(matrixStack, this.bounds.x + 5, this.bounds.y + 5, this.bounds.x + this.bounds.width - 5, this.bounds.y + this.bounds.height - 5, -3750202);
      TextureRegionRenderer.getInstance()
         .begin(ScreenTextures.UI_RESOURCE)
         .with(matrixStack)
         .draw(this.bounds.x, this.bounds.y, CONTAINER_BORDER_CORNER_TOP_LEFT)
         .draw(this.bounds.x + this.bounds.width - 5, this.bounds.y, CONTAINER_BORDER_CORNER_TOP_RIGHT)
         .draw(this.bounds.x, this.bounds.y + this.bounds.height - 5, CONTAINER_BORDER_CORNER_BOTTOM_LEFT)
         .draw(this.bounds.x + this.bounds.width - 5, this.bounds.y + this.bounds.height - 5, CONTAINER_BORDER_CORNER_BOTTOM_RIGHT)
         .push()
         .translateXY(this.bounds.x + 5, this.bounds.y)
         .scaleX(this.bounds.width - 10)
         .draw(CONTAINER_BORDER_TOP)
         .translateY(this.bounds.getHeight() - 5.0)
         .draw(CONTAINER_BORDER_BOTTOM)
         .pop()
         .push()
         .translateXY(this.bounds.x, this.bounds.y + 5)
         .scaleY(this.bounds.height - 10)
         .draw(CONTAINER_BORDER_LEFT)
         .translateX(this.bounds.getWidth() - 5.0)
         .draw(CONTAINER_BORDER_RIGHT)
         .pop()
         .end();
   }
}
