package iskallia.vault.client.gui.screen.player.element;

import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;

public final class StatLabel {
   public static StatLabelElementBuilder<Integer> ofInteger(
      Supplier<String> labelSupplier, Supplier<String> descriptionSupplier, Supplier<Integer> valueSupplier
   ) {
      return of(labelSupplier, descriptionSupplier, valueSupplier, StatLabelElement.INTEGER_FORMATTER);
   }

   public static StatLabelElementBuilder<Double> ofDouble(Supplier<String> labelSupplier, Supplier<String> descriptionSupplier, Supplier<Double> valueSupplier) {
      return of(labelSupplier, descriptionSupplier, valueSupplier, StatLabelElement.DOUBLE_FORMATTER);
   }

   public static StatLabelElementBuilder<Double> ofDoublePercent(
      Supplier<String> labelSupplier, Supplier<String> descriptionSupplier, Supplier<Double> valueSupplier
   ) {
      return of(labelSupplier, descriptionSupplier, valueSupplier, StatLabelElement.DOUBLE_PERCENT_FORMATTER);
   }

   public static StatLabelElementBuilder<Float> ofFloat(Supplier<String> labelSupplier, Supplier<String> descriptionSupplier, Supplier<Float> valueSupplier) {
      return of(labelSupplier, descriptionSupplier, valueSupplier, StatLabelElement.FLOAT_FORMATTER);
   }

   public static StatLabelElementBuilder<Float> ofFloatPercent(
      Supplier<String> labelSupplier, Supplier<String> descriptionSupplier, Supplier<Float> valueSupplier
   ) {
      return of(labelSupplier, descriptionSupplier, valueSupplier, StatLabelElement.FLOAT_PERCENT_FORMATTER);
   }

   public static StatLabelElementBuilder<Boolean> ofBoolean(
      Supplier<String> labelSupplier, Supplier<String> descriptionSupplier, Supplier<Boolean> valueSupplier
   ) {
      return of(labelSupplier, descriptionSupplier, valueSupplier, StatLabelElement.BOOLEAN_FORMATTER);
   }

   public static StatLabelElementBuilder<Integer> ofSeconds(
      Supplier<String> labelSupplier, Supplier<String> descriptionSupplier, Supplier<Integer> valueSupplier
   ) {
      return of(labelSupplier, descriptionSupplier, valueSupplier, StatLabelElement.SECONDS_TO_HOURS_MINUTES_SECONDS_FORMATTER);
   }

   private static <V extends Comparable<V>> StatLabelElementBuilder<V> of(
      Supplier<String> labelSupplier, Supplier<String> descriptionSupplier, Supplier<V> valueSupplier, Function<V, MutableComponent> valueFormatter
   ) {
      return new StatLabelElementBuilder<>(labelSupplier, descriptionSupplier, valueSupplier, valueFormatter, TextColor.fromRgb(0));
   }

   private StatLabel() {
   }
}
