package iskallia.vault.client.gui.widget;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.render.NineSlice;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.IMutablePosition;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.client.gui.helper.UIHelper;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec2;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractPanWidget extends AbstractWidget implements IMutablePosition {
   protected static Map<Class<?>, Vec2> persistedTranslations = new HashMap<>();
   protected Vec2 translation;
   private final List<BaseWidget> internalWidgets = new ArrayList<>();
   protected boolean dragging;
   protected Vec2 clickedPos;

   public AbstractPanWidget(int x, int y, int width, int height, Component message) {
      super(x, y, width, height, message);
      persistedTranslations.clear();
   }

   @NotNull
   protected NineSlice.TextureRegion getBackground() {
      return ScreenTextures.INSET_GREY_BACKGROUND;
   }

   protected Vec2 getDefaultCentered() {
      int centerX = this.width / 2;
      int centerY = this.height / 2;
      int minX = Integer.MAX_VALUE;
      int minY = Integer.MAX_VALUE;
      int width = 0;
      int height = 0;

      for (BaseWidget internalWidget : this.internalWidgets) {
         int x = internalWidget.x - this.x;
         if (x < minX) {
            minX = x;
            width = internalWidget.width;
         }

         int y = internalWidget.y - this.y;
         if (y < minY) {
            minY = y;
            height = internalWidget.height;
         }
      }

      if (minX == Integer.MAX_VALUE) {
         minX = 0;
      }

      if (minY == Integer.MAX_VALUE) {
         minY = 0;
      }

      return new Vec2(centerX - minX - width / 2.0F, centerY - minY - height / 2.0F);
   }

   protected <W extends BaseWidget> void addWidget(W widget) {
      widget.x = widget.x + this.x;
      widget.y = widget.y + this.y;
      this.internalWidgets.add(widget);
   }

   protected boolean contains(double mouseX, double mouseY) {
      return !(mouseX < this.x) && !(mouseX > this.x + this.width) ? mouseY >= this.y && mouseY <= this.y + this.height : false;
   }

   private void renderInnerForeground(PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
      poseStack.pushPose();
      poseStack.translate(this.translation.x, this.translation.y, 0.0);
      int translatedMouseX = (int)(mouseX - this.translation.x);
      int translatedMouseY = (int)(mouseY - this.translation.y);

      for (BaseWidget widget : this.internalWidgets) {
         widget.render(poseStack, translatedMouseX, translatedMouseY, partialTick);
      }

      poseStack.popPose();
   }

   private void renderInnerBackground(PoseStack poseStack) {
      GuiComponent.fill(poseStack, this.x, this.y, this.x + this.width, this.y + this.height, 33554431);
   }

   private void moveTranslation(float x, float y) {
      this.translation = new Vec2(x + this.translation.x, y + this.translation.y);
      persistedTranslations.put(this.getClass(), this.translation);
   }

   public void render(@NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
      this.getBackground().blit(poseStack, this.x - 1, this.y - 1, 0, this.width + 2, this.height + 2);
      if (this.translation == null) {
         Vec2 defaultCentered = this.getDefaultCentered();
         this.translation = persistedTranslations.getOrDefault(this.getClass(), defaultCentered);
      }

      UIHelper.renderOverflowHidden(poseStack, this::renderInnerBackground, ps -> this.renderInnerForeground(ps, mouseX, mouseY, partialTick));
   }

   public boolean mouseClicked(double mouseX, double mouseY, int pButton) {
      if (this.contains(mouseX, mouseY)) {
         this.dragging = true;
         this.clickedPos = new Vec2((float)mouseX, (float)mouseY);
         int translatedMouseX = (int)(mouseX - this.translation.x);
         int translatedMouseY = (int)(mouseY - this.translation.y);

         for (BaseWidget internalWidget : this.internalWidgets) {
            if (internalWidget.isMouseOver(translatedMouseX, translatedMouseY) && internalWidget.mouseClicked(translatedMouseX, translatedMouseY, pButton)) {
               this.dragging = false;
               return true;
            }
         }

         return true;
      } else {
         return false;
      }
   }

   public boolean mouseReleased(double mouseX, double mouseY, int pButton) {
      this.dragging = false;
      int translatedMouseX = (int)(mouseX - this.translation.x);
      int translatedMouseY = (int)(mouseY - this.translation.y);

      for (BaseWidget internalWidget : this.internalWidgets) {
         internalWidget.mouseReleased(translatedMouseX, translatedMouseY, pButton);
      }

      return super.mouseReleased(mouseX, mouseY, pButton);
   }

   public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
      int translatedMouseX = (int)(mouseX - this.translation.x);
      int translatedMouseY = (int)(mouseY - this.translation.y);

      for (BaseWidget internalWidget : this.internalWidgets) {
         if (internalWidget.isMouseOver(translatedMouseX, translatedMouseY)
            && internalWidget.mouseDragged(translatedMouseX, translatedMouseY, button, dragX, dragY)) {
            return true;
         }
      }

      if (!this.dragging) {
         return false;
      } else {
         float deltaX = (float)(mouseX - this.clickedPos.x);
         float deltaY = (float)(mouseY - this.clickedPos.y);
         this.moveTranslation(deltaX, deltaY);
         this.clickedPos = new Vec2((float)mouseX, (float)mouseY);
         return true;
      }
   }

   public boolean mouseScrolled(double mouseX, double mouseY, double pDelta) {
      return super.mouseScrolled(mouseX, mouseY, pDelta);
   }

   public boolean isMouseOver(double mouseX, double mouseY) {
      return this.dragging ? true : super.isMouseOver(mouseX, mouseY);
   }

   public void updateNarration(@NotNull NarrationElementOutput output) {
   }

   @Override
   public IMutablePosition positionX(int x) {
      this.x = x;
      return Spatials.positionXY(this.x, this.y).size(this.width, this.height);
   }

   @Override
   public IMutablePosition positionX(IPosition position) {
      return this.positionX(position.x());
   }

   @Override
   public IMutablePosition positionY(int y) {
      this.y = y;
      return Spatials.positionXY(this.x, this.y).size(this.width, this.height);
   }

   @Override
   public IMutablePosition positionY(IPosition position) {
      return this.positionY(position.y());
   }

   @Override
   public IMutablePosition positionZ(int z) {
      return Spatials.positionXY(this.x, this.y).size(this.width, this.height);
   }

   @Override
   public IMutablePosition positionZ(IPosition position) {
      return Spatials.positionXY(this.x, this.y).size(this.width, this.height);
   }

   @Override
   public IMutablePosition positionXY(int x, int y) {
      this.x = x;
      this.y = y;
      return Spatials.positionXY(this.x, this.y).size(this.width, this.height);
   }

   @Override
   public IMutablePosition positionXY(IPosition position) {
      return this.positionXY(position.x(), position.y());
   }

   @Override
   public IMutablePosition positionXYZ(int x, int y, int z) {
      this.x = x;
      this.y = y;
      return Spatials.positionXY(this.x, this.y).size(this.width, this.height);
   }

   @Override
   public IMutablePosition positionXYZ(IPosition position) {
      return this.positionXYZ(position.x(), position.y(), position.z());
   }

   @Override
   public IMutablePosition translateX(int x) {
      this.x += x;
      return Spatials.positionXY(this.x, this.y).size(this.width, this.height);
   }

   @Override
   public IMutablePosition translateX(IPosition position) {
      return this.translateX(position.x());
   }

   @Override
   public IMutablePosition translateY(int y) {
      this.y += y;
      return Spatials.positionXY(this.x, this.y).size(this.width, this.height);
   }

   @Override
   public IMutablePosition translateY(IPosition position) {
      return this.translateY(position.y());
   }

   @Override
   public IMutablePosition translateZ(int z) {
      return Spatials.positionXY(this.x, this.y).size(this.width, this.height);
   }

   @Override
   public IMutablePosition translateZ(IPosition position) {
      return Spatials.positionXY(this.x, this.y).size(this.width, this.height);
   }

   @Override
   public IMutablePosition translateXY(int x, int y) {
      this.x += x;
      this.y += y;
      return Spatials.positionXY(this.x, this.y).size(this.width, this.height);
   }

   @Override
   public IMutablePosition translateXY(IPosition position) {
      return this.translateXY(position.x(), position.y());
   }

   @Override
   public IMutablePosition translateXYZ(int x, int y, int z) {
      this.x += x;
      this.y += y;
      return Spatials.positionXYZ(this.x, this.y, z).size(this.width, this.height);
   }

   @Override
   public IMutablePosition translateXYZ(IPosition position) {
      return this.translateXYZ(position.x(), position.y(), position.z());
   }

   @Override
   public int x() {
      return this.x;
   }

   @Override
   public int y() {
      return this.y;
   }

   @Override
   public int z() {
      return 0;
   }
}
