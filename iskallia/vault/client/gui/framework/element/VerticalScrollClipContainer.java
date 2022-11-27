package iskallia.vault.client.gui.framework.element;

import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.spi.IElement;
import iskallia.vault.client.gui.framework.render.NineSlice;
import iskallia.vault.client.gui.framework.screen.layout.ScreenLayout;
import iskallia.vault.client.gui.framework.spatial.Padding;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.IMutableSpatial;
import iskallia.vault.client.gui.framework.spatial.spi.ISize;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;

public class VerticalScrollClipContainer<E extends VerticalScrollClipContainer<E>> extends ContainerElement<E> {
   protected final VerticalScrollbarElement<?> verticalScrollBarElement;
   protected final NineSliceElement<?> backgroundElement;
   protected final ClipContainerElement<?> clipContainerElement;
   protected final ElasticContainerElement<?> innerContainerElement;
   protected final Padding padding;

   public VerticalScrollClipContainer(ISpatial spatial) {
      this(spatial, Padding.ZERO, ScreenTextures.INSET_GREY_BACKGROUND);
   }

   public VerticalScrollClipContainer(ISpatial spatial, Padding padding) {
      this(spatial, padding, ScreenTextures.INSET_GREY_BACKGROUND);
   }

   public VerticalScrollClipContainer(ISpatial spatial, Padding padding, NineSlice.TextureRegion background) {
      super(spatial);
      this.padding = Padding.of(
         padding.left() + background.slices().left(),
         padding.right() + background.slices().right(),
         padding.top() + background.slices().top(),
         padding.bottom() + background.slices().bottom()
      );
      this.elementStore
         .addElement(
            this.verticalScrollBarElement = new VerticalScrollbarElement(Spatials.zero(), 0, this::onScrollbarValueChanged).layout(this::layoutScrollbar)
         );
      this.elementStore.addElement(this.backgroundElement = new NineSliceElement(Spatials.zero(), background).layout(this::layoutBackground));
      this.elementStore.addElement(this.clipContainerElement = new ClipContainerElement(Spatials.zero()).layout(this::layoutClipContainer));
      this.clipContainerElement
         .addElement(this.innerContainerElement = new ElasticContainerElement(Spatials.zero()).postLayout(this::postLayoutInnerContainer));
   }

   protected void layoutScrollbar(ISize screen, ISpatial gui, ISpatial parent, IMutableSpatial world) {
      world.translateX(parent.width() - this.verticalScrollBarElement.width());
      world.height(parent);
   }

   protected void layoutBackground(ISize screen, ISpatial gui, ISpatial parent, IMutableSpatial world) {
      world.width(parent.width() - this.verticalScrollBarElement.width() - 1);
      world.height(parent);
   }

   protected void layoutClipContainer(ISize screen, ISpatial gui, ISpatial parent, IMutableSpatial world) {
      world.translateX(this.padding.left());
      world.width(this.innerWidth());
      world.translateY(this.padding.top());
      world.height(this.innerHeight());
   }

   protected boolean postLayoutInnerContainer(ISize screen, ISpatial gui, ISpatial parent, IMutableSpatial world) {
      int innerHeight = world.height();
      int outerHeight = this.clipContainerElement.height();
      int diffHeight = innerHeight - outerHeight;
      if (diffHeight <= 0) {
         this.verticalScrollBarElement.setEnabled(false);
         return false;
      } else {
         this.verticalScrollBarElement.setEnabled(true);
         world.translateY((int)(-diffHeight * this.verticalScrollBarElement.getValue()));
         return true;
      }
   }

   protected int innerWidth() {
      return this.width() - this.verticalScrollBarElement.width() - 1 - this.padding.horizontal();
   }

   protected int innerHeight() {
      return this.height() - this.padding.vertical();
   }

   @Override
   protected <T extends IElement> T addElement(T element) {
      return this.innerContainerElement.addElement(element);
   }

   @Override
   protected void addElements(IElement element, IElement... elements) {
      this.innerContainerElement.addElements(element, elements);
   }

   @Override
   protected void removeElement(IElement element) {
      super.removeElement(element);
      this.innerContainerElement.removeElement(element);
   }

   protected void onScrollbarValueChanged(float value) {
      ScreenLayout.requestLayout();
   }

   @Override
   public boolean mouseDragged(double mouseX, double mouseY, int buttonIndex, double dragX, double dragY) {
      return this.verticalScrollBarElement.isDragging()
         && this.isEnabled()
         && this.verticalScrollBarElement.isEnabled()
         && this.onMouseDragged(mouseX, mouseY, buttonIndex, dragX, dragY);
   }

   @Override
   public boolean onMouseScrolled(double mouseX, double mouseY, double delta) {
      return this.verticalScrollBarElement.isEnabled() ? this.verticalScrollBarElement.onMouseScrolled(mouseX, mouseY, delta) : false;
   }
}
