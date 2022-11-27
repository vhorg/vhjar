package iskallia.vault.client.gui.screen.player.element;

import com.mojang.blaze3d.vertex.PoseStack;
import iskallia.vault.client.gui.framework.element.DynamicLabelElement;
import iskallia.vault.client.gui.framework.element.ElasticContainerElement;
import iskallia.vault.client.gui.framework.element.spi.IElement;
import iskallia.vault.client.gui.framework.render.spi.IElementRenderer;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.client.gui.framework.spatial.spi.ISize;
import iskallia.vault.client.gui.framework.text.LabelAutoResize;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import iskallia.vault.util.function.ObservableSupplier;
import java.util.Objects;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import org.jetbrains.annotations.NotNull;

public class StatLabelElement<V> extends ElasticContainerElement<StatLabelElement<V>> {
   public static final Function<Float, MutableComponent> FLOAT_FORMATTER = value -> new TextComponent(String.format("%.01f", value));
   public static final Function<Float, MutableComponent> FLOAT_ROUNDED_FORMATTER = value -> new TextComponent(String.valueOf(Math.round(value)));
   public static final Function<Double, MutableComponent> DOUBLE_FORMATTER = value -> new TextComponent(String.format("%.01f", value));
   public static final Function<Integer, MutableComponent> INTEGER_FORMATTER = value -> new TextComponent(String.valueOf(value));
   public static final Function<Float, MutableComponent> FLOAT_PERCENT_FORMATTER = value -> new TextComponent(String.format("%.00f%%", value * 100.0F));
   public static final Function<Double, MutableComponent> DOUBLE_PERCENT_FORMATTER = value -> new TextComponent(String.format("%.00f%%", value * 100.0));
   public static final Function<Boolean, MutableComponent> BOOLEAN_FORMATTER = value -> new TextComponent(value ? "Yes" : "No");
   public static final Function<Integer, MutableComponent> SECONDS_TO_HOURS_MINUTES_SECONDS_FORMATTER = value -> {
      int hours = value / 3600;
      int minutes = value % 3600 / 60;
      int seconds = value % 60;
      return new TextComponent(hours > 0 ? "%02d:%02d:%02d".formatted(hours, minutes, seconds) : "%02d:%02d".formatted(minutes, seconds));
   };

   public StatLabelElement(
      IPosition position,
      ISize size,
      Supplier<String> labelSupplier,
      Supplier<V> valueSupplier,
      Function<V, MutableComponent> valueFormatter,
      LabelTextStyle.Builder labelTextStyle,
      TextColor textColor,
      Supplier<TextColor> valueColor
   ) {
      super(Spatials.positionXYZ(position));
      this.addElements(
         new StatLabelElement.NameElement(IPosition.ZERO, size, labelSupplier, textColor, labelTextStyle.left())
            .layout((screen, gui, parent, world) -> world.width(parent)),
         new IElement[]{
            new StatLabelElement.ValueElement<>(IPosition.ZERO, size, valueSupplier, valueFormatter, labelTextStyle.right(), valueColor)
               .layout((screen, gui, parent, world) -> world.width(parent))
         }
      );
   }

   private static class NameElement extends DynamicLabelElement<String, StatLabelElement.NameElement> {
      private final TextColor textColor;

      public NameElement(IPosition position, ISize size, Supplier<String> valueSupplier, TextColor textColor, LabelTextStyle.Builder labelTextStyle) {
         super(position, size, valueSupplier, labelTextStyle);
         this.textColor = textColor;
         this.setAutoResize(LabelAutoResize.NONE);
      }

      protected void onValueChanged(String value) {
         this.set(new TextComponent(value).withStyle(Style.EMPTY.withColor(this.textColor)));
      }
   }

   private static class ValueElement<T> extends DynamicLabelElement<T, StatLabelElement.ValueElement<T>> {
      private final Function<T, MutableComponent> valueFormatter;
      private final ObservableSupplier<TextColor> textColorSupplier;

      public ValueElement(
         IPosition position,
         ISize size,
         Supplier<T> valueSupplier,
         Function<T, MutableComponent> valueFormatter,
         LabelTextStyle.Builder labelTextStyle,
         Supplier<TextColor> textColorSupplier
      ) {
         super(position, size, valueSupplier, labelTextStyle);
         this.valueFormatter = valueFormatter;
         this.textColorSupplier = ObservableSupplier.of(textColorSupplier, Objects::equals);
         this.setAutoResize(LabelAutoResize.NONE);
      }

      @Override
      protected void onValueChanged(T value) {
         this.set(this.createTextComponent(value, this.textColorSupplier.get()));
      }

      @NotNull
      private MutableComponent createTextComponent(T value, TextColor textColor) {
         return this.valueFormatter.apply(value).withStyle(Style.EMPTY.withColor(textColor));
      }

      @Override
      public void render(IElementRenderer renderer, @NotNull PoseStack poseStack, int mouseX, int mouseY, float partialTick) {
         if (this.textColorSupplier.hasChanged()) {
            this.set(this.createTextComponent(this.valueSupplier.get(), this.textColorSupplier.get()));
         }

         super.render(renderer, poseStack, mouseX, mouseY, partialTick);
      }
   }
}
