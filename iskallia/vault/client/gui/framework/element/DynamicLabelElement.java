package iskallia.vault.client.gui.framework.element;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.client.gui.framework.spatial.spi.ISize;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.util.function.ObservableSupplier;
import java.util.Objects;
import java.util.function.Supplier;
import org.jetbrains.annotations.NotNull;

public abstract class DynamicLabelElement<T, E extends DynamicLabelElement<T, E>> extends LabelElement<E> {
   protected final ObservableSupplier<T> valueSupplier;

   public DynamicLabelElement(IPosition position, Supplier<T> valueSupplier, LabelTextStyle.Builder labelTextStyle) {
      super(position, labelTextStyle);
      this.valueSupplier = ObservableSupplier.of(valueSupplier, Objects::equals);
   }

   public DynamicLabelElement(IPosition position, ISize size, Supplier<T> valueSupplier, LabelTextStyle.Builder labelTextStyle) {
      super(position, size, labelTextStyle);
      this.valueSupplier = ObservableSupplier.of(valueSupplier, Objects::equals);
   }

   protected abstract void onValueChanged(T var1);

   @Override
   public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
      this.valueSupplier.ifChanged(this::onValueChanged);
      super.render(renderer, poseStack, mouseX, mouseY, partialTick);
   }
}
