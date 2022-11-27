package iskallia.vault.client.gui.framework.element;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.atlas.TextureAtlasRegion;
import iskallia.vault.client.gui.framework.ScreenTextures;
import iskallia.vault.client.gui.framework.element.spi.IElement;
import iskallia.vault.client.gui.framework.element.spi.IGuiEventElement;
import iskallia.vault.client.gui.framework.render.NineSlice;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.IMutableSpatial;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.client.gui.framework.spatial.spi.ISize;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.util.function.ObservableSupplier;
import java.util.function.Consumer;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class VerticalScrollbarElement<E extends VerticalScrollbarElement<E>> extends ContainerElement<E> implements IGuiEventElement {
   private static final double SCROLL_SENSITIVITY = 0.05;
   private final TextureAtlasElement<?> handle;
   private final TextureAtlasElement<?> handleDisabled;
   private final NineSliceElement<?> background;
   private final ObservableSupplier<Float> changeObserver;
   private final Consumer<Float> changeObserverAction;
   private final int topPadding;
   private final int bottomPadding;
   private final float handleOffset;
   private boolean dragging;
   private float value;

   public VerticalScrollbarElement(IPosition position, int height, Consumer<Float> changeObserverAction) {
      this(
         position,
         height,
         ScreenTextures.SCROLLBAR_HANDLE,
         ScreenTextures.SCROLLBAR_HANDLE_DISABLED,
         ScreenTextures.INSET_GREY_BACKGROUND,
         changeObserverAction
      );
   }

   public VerticalScrollbarElement(
      IPosition position,
      int height,
      TextureAtlasRegion handleTextureRegion,
      TextureAtlasRegion handleDisabledTextureRegion,
      NineSlice.TextureRegion backgroundTextureRegion,
      Consumer<Float> changeObserverAction
   ) {
      super(Spatials.positionXYZ(position).width(calculateWidth(handleTextureRegion, backgroundTextureRegion)).height(height));
      this.handleOffset = handleTextureRegion.height() / 2.0F;
      this.topPadding = backgroundTextureRegion.slices().top();
      this.bottomPadding = backgroundTextureRegion.slices().bottom();
      this.addElements(
         this.background = new NineSliceElement(
               Spatials.width(calculateWidth(handleTextureRegion, backgroundTextureRegion)).height(height), backgroundTextureRegion
            )
            .layout(this::layoutBackground),
         new IElement[]{
            this.handle = new TextureAtlasElement(Spatials.positionX(1).positionZ(1), handleTextureRegion),
            this.handleDisabled = new TextureAtlasElement(Spatials.positionX(1).positionZ(1), handleDisabledTextureRegion)
         }
      );
      this.changeObserver = ObservableSupplier.of(() -> this.value, Mth::equal);
      this.changeObserverAction = changeObserverAction;
      this.value = 0.0F;
      this.changeObserver.ifChanged(this.changeObserverAction);
   }

   public void setValue(float value) {
      this.value = value;
   }

   public float getValue() {
      return this.value;
   }

   private void layoutBackground(ISize screen, ISpatial gui, ISpatial parent, IMutableSpatial world) {
      world.height(parent);
   }

   private static int calculateWidth(TextureAtlasRegion handleTextureRegion, NineSlice.TextureRegion backgroundTextureRegion) {
      int handleWidth = handleTextureRegion.width();
      int leftPadding = backgroundTextureRegion.slices().left();
      int rightPadding = backgroundTextureRegion.slices().right();
      return handleWidth + leftPadding + rightPadding;
   }

   private int calculateScrollableRangeMin() {
      return (int)Math.floor(this.topPadding + this.handleOffset);
   }

   private int calculateScrollableRangeMax() {
      return (int)Math.floor(this.worldSpatial.height() - this.bottomPadding - this.handleOffset);
   }

   private float calculateValue(float mouseY) {
      int scrollableRangeMin = this.worldSpatial.y() + this.calculateScrollableRangeMin();
      int scrollableRangeMax = this.worldSpatial.y() + this.calculateScrollableRangeMax();
      return Mth.clamp((mouseY - scrollableRangeMin) / (scrollableRangeMax - scrollableRangeMin), 0.0F, 1.0F);
   }

   public boolean isDragging() {
      return this.dragging;
   }

   @Override
   public boolean onMouseClicked(double mouseX, double mouseY, int buttonIndex) {
      this.dragging = true;
      this.value = this.calculateValue((float)mouseY);
      this.changeObserver.ifChanged(this.changeObserverAction);
      return true;
   }

   @Override
   public boolean mouseDragged(double mouseX, double mouseY, int buttonIndex, double dragX, double dragY) {
      return this.dragging && this.isEnabled() && this.onMouseDragged(mouseX, mouseY, buttonIndex, dragX, dragY);
   }

   @Override
   public boolean onMouseDragged(double mouseX, double mouseY, int buttonIndex, double dragX, double dragY) {
      this.value = this.calculateValue((float)mouseY);
      this.changeObserver.ifChanged(this.changeObserverAction);
      return true;
   }

   @Override
   public boolean mouseReleased(double mouseX, double mouseY, int buttonIndex) {
      this.dragging = false;
      return super.mouseReleased(mouseX, mouseY, buttonIndex);
   }

   @Override
   public boolean onMouseScrolled(double mouseX, double mouseY, double delta) {
      this.value = (float)Mth.clamp(this.value - delta * 0.05, 0.0, 1.0);
      this.changeObserver.ifChanged(this.changeObserverAction);
      return true;
   }

   @Override
   public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
      this.background.render(renderer, poseStack, mouseX, mouseY, partialTick);
      int scrollableRangeMin = this.calculateScrollableRangeMin();
      int scrollableRangeMax = this.calculateScrollableRangeMax();
      double y = this.topPadding + (scrollableRangeMax - scrollableRangeMin) * this.value;
      poseStack.pushPose();
      poseStack.translate(0.0, y, 0.0);
      if (this.isEnabled()) {
         this.handle.render(renderer, poseStack, mouseX, mouseY, partialTick);
      } else {
         this.handleDisabled.render(renderer, poseStack, mouseX, mouseY, partialTick);
      }

      poseStack.popPose();
   }
}
