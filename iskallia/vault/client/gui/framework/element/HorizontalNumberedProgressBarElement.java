package iskallia.vault.client.gui.framework.element;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.atlas.TextureAtlasRegion;
import iskallia.vault.client.gui.framework.element.spi.ISpatialElement;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.IMutableSpatial;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.client.gui.framework.text.LabelAutoResize;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.util.function.ObservableSupplier;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import org.jetbrains.annotations.NotNull;

public class HorizontalNumberedProgressBarElement<N extends Number, E extends HorizontalNumberedProgressBarElement<N, E>> extends ElasticContainerElement<E> {
   private final LabelElement<?> labelElement;
   private final ObservableSupplier<N> numberSupplier;
   private final Function<N, String> numberFormatter;
   private final TextColor textColor;

   public HorizontalNumberedProgressBarElement(
      IPosition position,
      TextureAtlasRegion background,
      TextureAtlasRegion foreground,
      Supplier<Float> percentageSupplier,
      HorizontalProgressBarElement.Direction direction,
      Supplier<N> numberSupplier,
      Function<N, String> numberFormatter,
      TextColor textColor,
      LabelTextStyle.Builder numberTextStyle,
      HorizontalNumberedProgressBarElement.Style style,
      IPosition numberOffset
   ) {
      super(Spatials.positionXYZ(position));
      this.numberSupplier = ObservableSupplier.of(numberSupplier, Objects::equals);
      this.numberFormatter = numberFormatter;
      this.textColor = textColor;
      this.addElement(new HorizontalProgressBarElement(IPosition.ZERO, background, foreground, percentageSupplier, direction));
      this.labelElement = this.addElement(
         new LabelElement(style.transform(Spatials.positionXYZ(numberOffset), background.width()), background.size(), style.apply(numberTextStyle))
      );
      this.labelElement.setAutoResize(LabelAutoResize.HEIGHT);
   }

   @Override
   protected void layoutIncludeChildren() {
      for (ISpatialElement element : this.elementStore.getSpatialElementList()) {
         if (element != this.labelElement) {
            this.worldSpatial.include(element.getWorldSpatial());
            this.layoutDebugLogger
               .out(
                  "[{}: world.include(child.world)] world = {}, child.world={}", this.getClass().getSimpleName(), this.worldSpatial, element.getWorldSpatial()
               );
         }
      }
   }

   protected void onNumberChanged(N value) {
      this.labelElement.set(new TextComponent(this.numberFormatter.apply(value)).withStyle(net.minecraft.network.chat.Style.EMPTY.withColor(this.textColor)));
   }

   @Override
   public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
      this.numberSupplier.ifChanged(this::onNumberChanged);
      super.render(renderer, poseStack, mouseX, mouseY, partialTick);
   }

   public static enum Style {
      LEFT(builder -> builder.right(), (spatial, width) -> spatial.translateX(-(width + 3)).translateY(-1)),
      CENTER(builder -> builder.center(), (spatial, width) -> spatial.translateY(-6)),
      RIGHT(builder -> builder.left(), (spatial, width) -> spatial.translateX(width + 2).translateY(-1));

      private final Consumer<LabelTextStyle.Builder> textStyle;
      private final BiConsumer<IMutableSpatial, Integer> spatialTransform;

      private Style(Consumer<LabelTextStyle.Builder> textStyle, BiConsumer<IMutableSpatial, Integer> spatialTransform) {
         this.textStyle = textStyle;
         this.spatialTransform = spatialTransform;
      }

      LabelTextStyle.Builder apply(LabelTextStyle.Builder labelTextStyle) {
         this.textStyle.accept(labelTextStyle);
         return labelTextStyle;
      }

      IMutableSpatial transform(IMutableSpatial spatial, int width) {
         this.spatialTransform.accept(spatial, width);
         return spatial;
      }
   }
}
