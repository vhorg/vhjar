package iskallia.vault.client.gui.framework.element;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.framework.element.spi.IRenderedElement;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.util.function.ObservableSupplier;
import java.util.List;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

public class RenderIndexedElement<E extends RenderIndexedElement<E>> extends ElasticContainerElement<E> {
   private final ObservableSupplier<Integer> renderIndexObserver;
   private final List<IRenderedElement> renderedElementList;

   public RenderIndexedElement(ISpatial spatial, List<IRenderedElement> renderedElementList, Supplier<Integer> renderIndex) {
      super(spatial);
      this.renderedElementList = renderedElementList;

      for (IRenderedElement element : renderedElementList) {
         this.addElement(element);
      }

      this.renderIndexObserver = ObservableSupplier.of(renderIndex, Integer::equals);
   }

   protected void onRenderIndexChanged(int selectedIndex) {
      for (int i = 0; i < this.renderedElementList.size(); i++) {
         IRenderedElement element = this.renderedElementList.get(i);
         element.setEnabled(i == selectedIndex);
         element.setVisible(i == selectedIndex);
      }
   }

   @Override
   public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
      this.renderIndexObserver.ifChanged(this::onRenderIndexChanged);
      super.render(renderer, poseStack, mouseX, mouseY, partialTick);
   }
}
