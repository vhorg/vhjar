package iskallia.vault.client.gui.screen.summary.element;

import iskallia.vault.client.gui.framework.element.ElasticContainerElement;
import iskallia.vault.client.gui.framework.render.Tooltips;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;

public class StatLabelListElement<E extends StatLabelListElement<E>> extends ElasticContainerElement<E> {
   public static final int LABEL_HEIGHT = 11;

   public StatLabelListElement(ISpatial spatial, TextColor textColor, List<StatLabelListElement.Stat<?>> statList) {
      super(spatial);

      for (int i = 0; i < statList.size(); i++) {
         this.add(statList.get(i), spatial.width(), textColor, i);
      }
   }

   protected <V> void add(StatLabelListElement.Stat<V> type, int width, TextColor textColor, int index) {
      this.addElement(
         new StatLabelElement<>(ISpatial.ZERO, Spatials.size(width, 11), type.name, type.value, type.valueFormatter, LabelTextStyle.defaultStyle(), textColor)
            .layout((screen, gui, parent, world) -> world.translateY(index * 11).width(parent))
            .tooltip(
               Tooltips.shift(
                  Tooltips.multi(() -> List.of(new TextComponent(type.name.get()), Tooltips.DEFAULT_HOLD_SHIFT_COMPONENT)),
                  Tooltips.multi(() -> List.of(new TextComponent(type.name.get()), new TextComponent(type.description.get()).withStyle(ChatFormatting.GRAY)))
               )
            )
      );
   }

   public record Stat<V>(Supplier<String> name, Supplier<String> description, Supplier<V> value, Function<V, String> valueFormatter) {
      public static StatLabelListElement.Stat<Integer> ofInteger(Supplier<String> name, Supplier<String> description, Supplier<Integer> value) {
         return new StatLabelListElement.Stat<>(name, description, value, StatLabelElement.INTEGER_FORMATTER);
      }

      public static StatLabelListElement.Stat<Long> ofLong(Supplier<String> name, Supplier<String> description, Supplier<Long> value) {
         return new StatLabelListElement.Stat<>(name, description, value, StatLabelElement.LONG_FORMATTER);
      }

      public static StatLabelListElement.Stat<Float> ofFloat(Supplier<String> name, Supplier<String> description, Supplier<Float> value) {
         return new StatLabelListElement.Stat<>(name, description, value, StatLabelElement.FLOAT_FORMATTER);
      }

      public static StatLabelListElement.Stat<Double> ofDouble(Supplier<String> name, Supplier<String> description, Supplier<Double> value) {
         return new StatLabelListElement.Stat<>(name, description, value, StatLabelElement.DOUBLE_FORMATTER);
      }

      public static StatLabelListElement.Stat<Float> ofFloatPercent(Supplier<String> name, Supplier<String> description, Supplier<Float> value) {
         return new StatLabelListElement.Stat<>(name, description, value, StatLabelElement.FLOAT_PERCENT_FORMATTER);
      }

      public static StatLabelListElement.Stat<Double> ofDoublePercent(Supplier<String> name, Supplier<String> description, Supplier<Double> value) {
         return new StatLabelListElement.Stat<>(name, description, value, StatLabelElement.DOUBLE_PERCENT_FORMATTER);
      }

      public static StatLabelListElement.Stat<Integer> ofSeconds(Supplier<String> name, Supplier<String> description, Supplier<Integer> value) {
         return new StatLabelListElement.Stat<>(name, description, value, StatLabelElement.SECONDS_TO_HOURS_MINUTES_SECONDS_FORMATTER);
      }

      public static StatLabelListElement.Stat<Boolean> ofBoolean(Supplier<String> name, Supplier<String> description, Supplier<Boolean> value) {
         return new StatLabelListElement.Stat<>(name, description, value, StatLabelElement.BOOLEAN_FORMATTER);
      }
   }
}
