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
import net.minecraft.util.Mth;

public class HorizontalScrollClipContainer<E extends HorizontalScrollClipContainer<E>> extends ContainerElement<E> {
   private static final double SCROLL_SENSITIVITY = 0.05;
   private static final double DRAG_SENSITIVITY = 0.0015;
   protected static float scrollValue;
   protected boolean dragging = false;
   protected final NineSliceElement<?> backgroundElement;
   protected final ClipContainerElement<?> clipContainerElement;
   protected final ElasticContainerElement<?> innerContainerElement;
   protected final Padding padding;

   public HorizontalScrollClipContainer(ISpatial spatial) {
      this(spatial, Padding.ZERO, ScreenTextures.INSET_GREY_BACKGROUND);
   }

   public HorizontalScrollClipContainer(ISpatial spatial, Padding padding) {
      this(spatial, padding, ScreenTextures.INSET_GREY_BACKGROUND);
   }

   public HorizontalScrollClipContainer(ISpatial spatial, Padding padding, NineSlice.TextureRegion background) {
      super(spatial);
      this.padding = Padding.of(
         padding.left() + background.slices().left(),
         padding.right() + background.slices().right(),
         padding.top() + background.slices().top(),
         padding.bottom() + background.slices().bottom()
      );
      this.elementStore.addElement(this.backgroundElement = new NineSliceElement(Spatials.zero(), background).layout(this::layoutBackground));
      this.elementStore.addElement(this.clipContainerElement = new ClipContainerElement(Spatials.zero()).layout(this::layoutClipContainer));
      this.clipContainerElement
         .addElement(this.innerContainerElement = new ElasticContainerElement(Spatials.zero()).postLayout(this::postLayoutInnerContainer));
   }

   protected void layoutBackground(ISize screen, ISpatial gui, ISpatial parent, IMutableSpatial world) {
      world.width(parent.width());
      world.height(parent);
   }

   protected void layoutClipContainer(ISize screen, ISpatial gui, ISpatial parent, IMutableSpatial world) {
      world.translateX(this.padding.left());
      world.width(this.innerWidth());
      world.translateY(this.padding.top());
      world.height(this.innerHeight());
   }

   protected boolean postLayoutInnerContainer(ISize screen, ISpatial gui, ISpatial parent, IMutableSpatial world) {
      int innerWidth = world.width();
      int outerWidth = this.clipContainerElement.width();
      int diffWidth = innerWidth - outerWidth;
      if (diffWidth <= 0) {
         return false;
      } else {
         world.translateX((int)(-diffWidth * scrollValue));
         return true;
      }
   }

   protected int innerWidth() {
      return this.width() - this.padding.horizontal();
   }

   protected int innerHeight() {
      return this.height() - this.padding.vertical();
   }

   @Override
   public <T extends IElement> T addElement(T element) {
      return this.innerContainerElement.addElement(element);
   }

   @Override
   public void addElements(IElement element, IElement... elements) {
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
   public boolean onMouseClicked(double mouseX, double mouseY, int buttonIndex) {
      super.onMouseClicked(mouseX, mouseY, buttonIndex);
      this.dragging = true;
      ScreenLayout.requestLayout();
      return true;
   }

   @Override
   public boolean mouseDragged(double mouseX, double mouseY, int buttonIndex, double dragX, double dragY) {
      return this.dragging && this.isEnabled() && this.onMouseDragged(mouseX, mouseY, buttonIndex, dragX, dragY);
   }

   @Override
   public boolean onMouseDragged(double mouseX, double mouseY, int buttonIndex, double dragX, double dragY) {
      scrollValue = (float)Mth.clamp(scrollValue - dragX / this.innerWidth() / 2.0, 0.0, 1.0);
      ScreenLayout.requestLayout();
      return super.onMouseDragged(mouseX, mouseY, buttonIndex, dragX, dragY);
   }

   @Override
   public boolean mouseReleased(double mouseX, double mouseY, int buttonIndex) {
      this.dragging = false;
      return super.mouseReleased(mouseX, mouseY, buttonIndex);
   }

   @Override
   public boolean onMouseScrolled(double mouseX, double mouseY, double delta) {
      scrollValue = (float)Mth.clamp(scrollValue - delta / this.innerWidth() * 10.0, 0.0, 1.0);
      ScreenLayout.requestLayout();
      return super.onMouseScrolled(mouseX, mouseY, delta);
   }
}
