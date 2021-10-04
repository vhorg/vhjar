package iskallia.vault.client.gui.component;

import com.mojang.blaze3d.matrix.MatrixStack;
import iskallia.vault.client.gui.screen.SkillTreeScreen;
import iskallia.vault.client.gui.tab.SkillTab;
import java.awt.Point;
import java.awt.Rectangle;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.widget.button.Button;

public abstract class ComponentDialog extends AbstractGui {
   private final SkillTreeScreen skillTreeScreen;
   protected Rectangle bounds;
   protected ScrollableContainer descriptionComponent;
   protected Button selectButton;

   protected ComponentDialog(SkillTreeScreen skillTreeScreen) {
      this.skillTreeScreen = skillTreeScreen;
   }

   public abstract void refreshWidgets();

   public abstract int getHeaderHeight();

   public abstract SkillTab createTab();

   public abstract Point getIconUV();

   protected final SkillTreeScreen getSkillTreeScreen() {
      return this.skillTreeScreen;
   }

   public void setBounds(Rectangle bounds) {
      this.bounds = bounds;
   }

   public void mouseMoved(double screenX, double screenY) {
      if (this.bounds != null) {
         double containerX = screenX - this.bounds.x;
         double containerY = screenY - this.bounds.y;
         if (this.selectButton != null) {
            this.selectButton.func_212927_b(containerX, containerY);
         }
      }
   }

   public void mouseClicked(double screenX, double screenY, int button) {
      if (this.bounds != null) {
         double containerX = screenX - this.bounds.x;
         double containerY = screenY - this.bounds.y;
         if (this.selectButton != null) {
            this.selectButton.func_231044_a_(containerX, containerY, button);
         }
      }
   }

   public void mouseScrolled(double mouseX, double mouseY, double delta) {
      if (this.bounds != null
         && this.descriptionComponent != null
         && this.descriptionComponent.bounds != null
         && this.descriptionComponent.bounds.contains((int)mouseX - this.bounds.x, (int)mouseY - this.bounds.y)) {
         this.descriptionComponent.mouseScrolled(mouseX, mouseY, delta);
      }
   }

   public Rectangle getHeadingBounds() {
      int widgetHeight = this.getHeaderHeight();
      return new Rectangle(5, 5, this.bounds.width - 20, widgetHeight + 5);
   }

   public Rectangle getDescriptionsBounds() {
      Rectangle headingBounds = this.getHeadingBounds();
      int topOffset = headingBounds.y + headingBounds.height + 10;
      int descriptionHeight = this.bounds.height - 50 - topOffset;
      return new Rectangle(headingBounds.x, topOffset, headingBounds.width, descriptionHeight);
   }

   public abstract void render(MatrixStack var1, int var2, int var3, float var4);

   protected void renderBackground(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {
      Minecraft.func_71410_x().func_110434_K().func_110577_a(SkillTreeScreen.UI_RESOURCE);
      func_238467_a_(matrixStack, this.bounds.x + 5, this.bounds.y + 5, this.bounds.x + this.bounds.width - 5, this.bounds.y + this.bounds.height - 5, -3750202);
      this.func_238474_b_(matrixStack, this.bounds.x, this.bounds.y, 0, 44, 5, 5);
      this.func_238474_b_(matrixStack, this.bounds.x + this.bounds.width - 5, this.bounds.y, 8, 44, 5, 5);
      this.func_238474_b_(matrixStack, this.bounds.x, this.bounds.y + this.bounds.height - 5, 0, 52, 5, 5);
      this.func_238474_b_(matrixStack, this.bounds.x + this.bounds.width - 5, this.bounds.y + this.bounds.height - 5, 8, 52, 5, 5);
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(this.bounds.x + 5, this.bounds.y, 0.0);
      matrixStack.func_227862_a_(this.bounds.width - 10, 1.0F, 1.0F);
      this.func_238474_b_(matrixStack, 0, 0, 6, 44, 1, 5);
      matrixStack.func_227861_a_(0.0, this.bounds.getHeight() - 5.0, 0.0);
      this.func_238474_b_(matrixStack, 0, 0, 6, 52, 1, 5);
      matrixStack.func_227865_b_();
      matrixStack.func_227860_a_();
      matrixStack.func_227861_a_(this.bounds.x, this.bounds.y + 5, 0.0);
      matrixStack.func_227862_a_(1.0F, this.bounds.height - 10, 1.0F);
      this.func_238474_b_(matrixStack, 0, 0, 0, 50, 5, 1);
      matrixStack.func_227861_a_(this.bounds.getWidth() - 5.0, 0.0, 0.0);
      this.func_238474_b_(matrixStack, 0, 0, 8, 50, 5, 1);
      matrixStack.func_227865_b_();
   }
}
