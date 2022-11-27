package iskallia.vault.client.gui.screen.summary.element;

import iskallia.vault.client.gui.framework.element.DynamicLabelElement;
import iskallia.vault.client.gui.framework.element.ElasticContainerElement;
import iskallia.vault.client.gui.framework.element.spi.IElement;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.IPosition;
import iskallia.vault.client.gui.framework.spatial.spi.ISize;
import iskallia.vault.client.gui.framework.text.LabelAutoResize;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;

public class StatLabelElement<V> extends ElasticContainerElement<StatLabelElement<V>> {
   public static final Function<Float, String> FLOAT_FORMATTER = value -> String.format("%.01f", value);
   public static final Function<Double, String> DOUBLE_FORMATTER = value -> String.format("%.01f", value);
   public static final Function<Integer, String> INTEGER_FORMATTER = String::valueOf;
   public static final Function<Long, String> LONG_FORMATTER = String::valueOf;
   public static final Function<Float, String> FLOAT_PERCENT_FORMATTER = value -> String.format("%.00f%%", value * 100.0F);
   public static final Function<Double, String> DOUBLE_PERCENT_FORMATTER = value -> String.format("%.00f%%", value * 100.0);
   public static final Function<Boolean, String> BOOLEAN_FORMATTER = value -> value ? "Yes" : "No";
   public static final Function<Integer, String> SECONDS_TO_HOURS_MINUTES_SECONDS_FORMATTER = value -> {
      int hours = value / 3600;
      int minutes = value % 3600 / 60;
      int seconds = value % 60;
      return hours > 0 ? "%02d:%02d:%02d".formatted(hours, minutes, seconds) : "%02d:%02d".formatted(minutes, seconds);
   };

   public StatLabelElement(
      IPosition position,
      ISize size,
      Supplier<String> labelSupplier,
      Supplier<V> valueSupplier,
      Function<V, String> valueFormatter,
      LabelTextStyle.Builder labelTextStyle,
      TextColor textColor
   ) {
      super(Spatials.positionXYZ(position));
      this.addElements(
         new StatLabelElement.NameElement(IPosition.ZERO, size, labelSupplier, textColor, labelTextStyle.left())
            .layout((screen, gui, parent, world) -> world.width(parent)),
         new IElement[]{
            new StatLabelElement.ValueElement<>(IPosition.ZERO, size, valueSupplier, valueFormatter, labelTextStyle.right(), textColor)
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
      private final Function<T, String> valueFormatter;
      private final TextColor textColor;

      public ValueElement(
         IPosition position,
         ISize size,
         Supplier<T> valueSupplier,
         Function<T, String> valueFormatter,
         LabelTextStyle.Builder labelTextStyle,
         TextColor textColor
      ) {
         super(position, size, valueSupplier, labelTextStyle);
         this.valueFormatter = valueFormatter;
         this.textColor = textColor;
         this.setAutoResize(LabelAutoResize.NONE);
      }

      @Override
      protected void onValueChanged(T value) {
         this.set(new TextComponent(this.valueFormatter.apply(value)).withStyle(Style.EMPTY.withColor(this.textColor)));
      }
   }
}
