package iskallia.vault.client.gui.framework.element;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.framework.element.spi.AbstractSpatialElement;
import iskallia.vault.client.gui.framework.element.spi.ElementStore;
import iskallia.vault.client.gui.framework.element.spi.IElement;
import iskallia.vault.client.gui.framework.element.spi.IGuiEventElement;
import iskallia.vault.client.gui.framework.element.spi.ILayoutElement;
import iskallia.vault.client.gui.framework.element.spi.IPostLayoutElement;
import iskallia.vault.client.gui.framework.element.spi.IPostLayoutStrategy;
import iskallia.vault.client.gui.framework.element.spi.IRenderedElement;
import iskallia.vault.client.gui.framework.element.spi.ISpatialElement;
import iskallia.vault.client.gui.framework.element.spi.ITooltipElement;
import iskallia.vault.client.gui.framework.render.spi.IDebugRenderer;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.render.spi.ITooltipRenderer;
import iskallia.vault.client.gui.framework.spatial.spi.ISize;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import javax.annotation.Nonnull;
import net.minecraft.world.item.TooltipFlag;

public class ContainerElement<E extends ContainerElement<E>>
   extends AbstractSpatialElement<E>
   implements IGuiEventElement,
   IRenderedElement,
   IPostLayoutElement<E> {
   protected final ElementStore elementStore = new ElementStore();
   protected boolean visible;
   protected IPostLayoutStrategy postLayoutStrategy;

   public ContainerElement(ISpatial spatial) {
      super(spatial);
      this.setVisible(true);
      this.postLayoutStrategy = IPostLayoutStrategy.NONE;
   }

   protected <T extends IElement> T addElement(T element) {
      return this.elementStore.addElement(element);
   }

   protected void addElements(IElement element, IElement... elements) {
      this.addElement(element);

      for (IElement e : elements) {
         this.addElement(e);
      }
   }

   protected void removeElement(IElement element) {
      this.elementStore.removeElement(element);
   }

   protected void removeAllElements() {
      this.elementStore.removeAllElements();
   }

   public void enableSpatialDebugLogging(boolean enableForChildren) {
      super.enableLayoutDebugLogging();
      if (enableForChildren) {
         for (ISpatialElement element : this.elementStore.getSpatialElementList()) {
            if (element instanceof ContainerElement containerElement) {
               containerElement.enableSpatialDebugLogging(true);
            } else {
               element.enableLayoutDebugLogging();
            }
         }
      }
   }

   @Override
   public void renderDebug(IDebugRenderer debugRenderer, PoseStack poseStack) {
      super.renderDebug(debugRenderer, poseStack);

      for (ISpatialElement element : this.elementStore.getSpatialElementList()) {
         element.renderDebug(debugRenderer, poseStack);
      }
   }

   public E postLayout(IPostLayoutStrategy layoutStrategy) {
      this.postLayoutStrategy = layoutStrategy;
      return (E)this;
   }

   @Override
   public void onLayout(ISize screen, ISpatial gui, ISpatial parent) {
      this.layoutSelf(screen, gui, parent);
      this.layoutChildren(screen, gui, this.getWorldSpatial());
      if (this.layoutSelfPost(screen, gui, parent)) {
         this.layoutChildrenPost(gui, screen, this.getWorldSpatial());
      }
   }

   protected void layoutChildren(ISize screen, ISpatial gui, ISpatial parent) {
      for (ILayoutElement<?> element : this.elementStore.getLayoutElementList()) {
         element.onLayout(screen, gui, parent);
      }
   }

   protected boolean layoutSelfPost(ISize screen, ISpatial gui, ISpatial parent) {
      boolean result = this.postLayoutStrategy.apply(screen, gui, parent, this.worldSpatial);
      this.layoutDebugLogger.out("[{}: post-layout strategy] -> world = {}", this.getClass().getSimpleName(), this.worldSpatial);
      return result;
   }

   protected void layoutChildrenPost(ISpatial gui, ISize screen, ISpatial parent) {
      for (ILayoutElement<?> element : this.elementStore.getLayoutElementList()) {
         element.onLayout(screen, gui, parent);
      }
   }

   @Override
   public boolean onHoverTooltip(ITooltipRenderer tooltipRenderer, @Nonnull PoseStack poseStack, int mouseX, int mouseY, TooltipFlag tooltipFlag) {
      if (super.onHoverTooltip(tooltipRenderer, poseStack, mouseX, mouseY, tooltipFlag)) {
         return true;
      } else {
         for (ITooltipElement element : this.elementStore.getTooltipElementList()) {
            if (element.renderTooltip(tooltipRenderer, poseStack, mouseX, mouseY)) {
               return true;
            }
         }

         return false;
      }
   }

   @Override
   public void onMouseMoved(double mouseX, double mouseY) {
      for (IGuiEventElement element : this.elementStore.getGuiEventElementList()) {
         element.mouseMoved(mouseX, mouseY);
      }
   }

   @Override
   public boolean onMouseClicked(double mouseX, double mouseY, int buttonIndex) {
      for (IGuiEventElement element : this.elementStore.getGuiEventElementList()) {
         if (element.mouseClicked(mouseX, mouseY, buttonIndex)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean onMouseReleased(double mouseX, double mouseY, int buttonIndex) {
      for (IGuiEventElement element : this.elementStore.getGuiEventElementList()) {
         if (element.mouseReleased(mouseX, mouseY, buttonIndex)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean onMouseDragged(double mouseX, double mouseY, int buttonIndex, double dragX, double dragY) {
      for (IGuiEventElement element : this.elementStore.getGuiEventElementList()) {
         if (element.mouseDragged(mouseX, mouseY, buttonIndex, dragX, dragY)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean onMouseScrolled(double mouseX, double mouseY, double delta) {
      for (IGuiEventElement element : this.elementStore.getGuiEventElementList()) {
         if (element.mouseScrolled(mouseX, mouseY, delta)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean onKeyPressed(int keyCode, int scanCode, int modifiers) {
      for (IGuiEventElement element : this.elementStore.getGuiEventElementList()) {
         if (element.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean onKeyReleased(int keyCode, int scanCode, int modifiers) {
      for (IGuiEventElement element : this.elementStore.getGuiEventElementList()) {
         if (element.keyReleased(keyCode, scanCode, modifiers)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean onCharTyped(char codePoint, int modifiers) {
      for (IGuiEventElement element : this.elementStore.getGuiEventElementList()) {
         if (element.charTyped(codePoint, modifiers)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean onChangeFocus(boolean focus) {
      for (IGuiEventElement element : this.elementStore.getGuiEventElementList()) {
         if (element.changeFocus(focus)) {
            return true;
         }
      }

      return false;
   }

   @Override
   public boolean isMouseOver(double mouseX, double mouseY) {
      if (super.contains(mouseX, mouseY)) {
         for (ISpatialElement element : this.elementStore.getSpatialElementList()) {
            if (element.contains(mouseX, mouseY)) {
               return true;
            }
         }
      }

      return false;
   }

   @Override
   public void setVisible(boolean visible) {
      this.visible = visible;
   }

   @Override
   public boolean isVisible() {
      return this.visible;
   }

   @Override
   public void render(IElementRenderer renderer, @Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
      for (IRenderedElement element : this.elementStore.getRenderedElementList()) {
         if (element.isVisible()) {
            element.render(renderer, poseStack, mouseX, mouseY, partialTick);
         }
      }
   }
}
