package iskallia.vault.client.gui.framework.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.framework.ScreenRenderers;
import iskallia.vault.client.gui.framework.element.spi.ElementStore;
import iskallia.vault.client.gui.framework.element.spi.IElement;
import iskallia.vault.client.gui.framework.element.spi.IGuiEventElement;
import iskallia.vault.client.gui.framework.element.spi.ILayoutElement;
import iskallia.vault.client.gui.framework.element.spi.IRenderedElement;
import iskallia.vault.client.gui.framework.element.spi.ISpatialElement;
import iskallia.vault.client.gui.framework.element.spi.ITooltipElement;
import iskallia.vault.client.gui.framework.render.spi.IDebugRenderer;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.render.spi.ITooltipRenderer;
import iskallia.vault.client.gui.framework.render.spi.ITooltipRendererFactory;
import iskallia.vault.client.gui.framework.screen.layout.ILayoutScreen;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.ISize;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import java.util.List;
import java.util.Optional;
import javax.annotation.Nonnull;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraftforge.client.event.ScreenEvent.BackgroundDrawnEvent;
import net.minecraftforge.common.MinecraftForge;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractElementContainerScreen<C extends AbstractContainerMenu> extends AbstractContainerScreen<C> implements ILayoutScreen {
   protected final ElementStore elementStore;
   protected final IElementRenderer elementRenderer;
   protected final ITooltipRenderer tooltipRenderer;
   protected IDebugRenderer debugRenderer;
   protected boolean needsLayout;

   public AbstractElementContainerScreen(
      C container,
      Inventory inventory,
      Component title,
      IElementRenderer elementRenderer,
      ITooltipRendererFactory<AbstractElementContainerScreen<C>> tooltipRendererFactory
   ) {
      super(container, inventory, title);
      this.elementRenderer = elementRenderer;
      this.elementStore = new ElementStore();
      this.tooltipRenderer = tooltipRendererFactory.create(this);
      this.debugRenderer = ScreenRenderers.getDebugNone();
   }

   protected void setGuiSize(ISize size) {
      this.imageWidth = size.width();
      this.imageHeight = size.height();
   }

   protected ISpatial getGuiSpatial() {
      return Spatials.positionXY(this.getGuiLeft(), this.getGuiTop()).size(this.getXSize(), this.getYSize()).unmodifiableView();
   }

   protected ISize getScreenSize() {
      return Spatials.size(this.width, this.height).unmodifiableView();
   }

   public ITooltipRenderer getTooltipRenderer() {
      return this.tooltipRenderer;
   }

   protected <E extends IElement> E addElement(E element) {
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

   @Override
   public void requestLayout() {
      this.needsLayout = true;
   }

   protected void init() {
      super.init();
      this.requestLayout();
   }

   protected void layout(ISpatial parentSpatial) {
      for (ILayoutElement<?> element : this.elementStore.getLayoutElementList()) {
         element.onLayout(this.getScreenSize(), this.getGuiSpatial(), parentSpatial);
      }
   }

   protected void renderBg(@Nonnull PoseStack matrixStack, float partialTicks, int x, int y) {
   }

   protected void renderLabels(@Nonnull PoseStack matrixStack, int x, int y) {
   }

   public void render(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
      if (this.needsLayout) {
         this.layout(Spatials.zero());
         this.needsLayout = false;
      }

      this.renderBackgroundFill(poseStack);
      this.renderElements(poseStack, mouseX, mouseY, partialTick);
      this.renderSlotItems(poseStack, mouseX, mouseY, partialTick);
      this.renderDebug(poseStack);
      this.renderTooltips(poseStack, mouseX, mouseY);
   }

   protected void renderBackgroundFill(@Nonnull PoseStack poseStack) {
      RenderSystem.disableDepthTest();
      this.fillGradient(poseStack, 0, 0, this.width, this.height, -1072689136, -804253680);
      MinecraftForge.EVENT_BUS.post(new BackgroundDrawnEvent(this, poseStack));
   }

   protected void renderElements(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
      this.elementRenderer.beginFrame();
      RenderSystem.enableDepthTest();
      this.elementRenderer.begin();

      for (IRenderedElement element : this.elementStore.getRenderedElementList()) {
         if (element.isVisible()) {
            element.render(this.elementRenderer, poseStack, mouseX, mouseY, partialTick);
         }
      }

      this.elementRenderer.end();
      RenderSystem.disableDepthTest();
      this.elementRenderer.endFrame();
   }

   protected void renderSlotItems(@Nonnull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
      super.render(poseStack, mouseX, mouseY, partialTick);
   }

   protected void enableDebugRendering() {
      this.debugRenderer = ScreenRenderers.getDebugBuffered();
   }

   protected void renderDebug(@Nonnull PoseStack poseStack) {
      this.debugRenderer.begin();
      RenderSystem.disableDepthTest();

      for (ISpatialElement element : this.elementStore.getSpatialElementList()) {
         element.renderDebug(this.debugRenderer, poseStack);
      }

      this.debugRenderer.end();
   }

   protected void renderTooltips(@Nonnull PoseStack poseStack, int mouseX, int mouseY) {
      if (this.menu.getCarried().isEmpty()) {
         if (!this.renderHoveredSlotTooltips(poseStack, mouseX, mouseY)) {
            this.renderElementTooltips(poseStack, mouseX, mouseY);
         }
      }
   }

   protected boolean renderHoveredSlotTooltips(@Nonnull PoseStack poseStack, int mouseX, int mouseY) {
      if (this.hoveredSlot != null && this.hoveredSlot.hasItem()) {
         this.renderTooltip(poseStack, this.hoveredSlot.getItem(), mouseX, mouseY);
         return true;
      } else {
         return false;
      }
   }

   protected boolean renderElementTooltips(@NotNull PoseStack poseStack, int mouseX, int mouseY) {
      for (ITooltipElement element : this.elementStore.getTooltipElementList()) {
         if (element.renderTooltip(this.tooltipRenderer, poseStack, mouseX, mouseY)) {
            return true;
         }
      }

      return false;
   }

   @Nonnull
   public List<? extends GuiEventListener> children() {
      return this.elementStore.getGuiEventElementList();
   }

   @Nonnull
   public Optional<GuiEventListener> getChildAt(double mouseX, double mouseY) {
      for (GuiEventListener listener : this.children()) {
         if (listener instanceof IElement element && element.isEnabled() && listener.isMouseOver(mouseX, mouseY)) {
            return Optional.of(listener);
         }
      }

      return Optional.empty();
   }

   public void mouseMoved(double mouseX, double mouseY) {
      for (IGuiEventElement element : this.elementStore.getGuiEventElementList()) {
         element.mouseMoved(mouseX, mouseY);
      }
   }

   public boolean mouseDragged(double mouseX, double mouseY, int button, double dragX, double dragY) {
      super.mouseDragged(mouseX, mouseY, button, dragX, dragY);
      return this.getFocused() != null && this.isDragging() && button == 0 && this.getFocused().mouseDragged(mouseX, mouseY, button, dragX, dragY);
   }
}
