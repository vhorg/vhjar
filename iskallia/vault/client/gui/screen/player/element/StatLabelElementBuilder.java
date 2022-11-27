package iskallia.vault.client.gui.screen.player.element;

import iskallia.vault.client.gui.framework.render.Tooltips;
import iskallia.vault.client.gui.framework.spatial.Spatials;
import iskallia.vault.client.gui.framework.spatial.spi.ISpatial;
import iskallia.vault.client.gui.framework.text.LabelTextStyle;
import java.util.Comparator;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;

public class StatLabelElementBuilder<V extends Comparable<V>> {
   public static final Comparator<StatLabelElementBuilder<?>> COMPARATOR = Comparator.comparing(b -> b.labelSupplier.get());
   private static final int LABEL_HEIGHT = 11;
   private final Supplier<String> labelSupplier;
   private final Supplier<String> descriptionSupplier;
   private final Supplier<V> valueSupplier;
   private final Function<V, MutableComponent> valueFormatFunction;
   private final TextColor textColor;
   private Supplier<V> valueCapSupplier;
   private Function<V, MutableComponent> valueCapFormatFunction;
   private Supplier<V> finalValueSupplier;

   public StatLabelElementBuilder(
      Supplier<String> labelSupplier,
      Supplier<String> descriptionSupplier,
      Supplier<V> valueSupplier,
      Function<V, MutableComponent> valueFormatFunction,
      TextColor textColor
   ) {
      this.labelSupplier = labelSupplier;
      this.descriptionSupplier = descriptionSupplier;
      this.valueSupplier = valueSupplier;
      this.valueFormatFunction = valueFormatFunction;
      this.textColor = textColor;
      this.finalValueSupplier = this.valueSupplier;
   }

   public StatLabelElementBuilder<V> setValueCap(Supplier<V> valueCapSupplier, Function<V, MutableComponent> valueCapFormatFunction) {
      this.valueCapSupplier = valueCapSupplier;
      this.valueCapFormatFunction = valueCapFormatFunction;
      this.finalValueSupplier = () -> this.valueCapSupplier.get().compareTo(this.valueSupplier.get()) < 0
         ? this.valueCapSupplier.get()
         : this.valueSupplier.get();
      return this;
   }

   private boolean isValueCapped() {
      return this.valueCapSupplier != null && this.valueCapSupplier.get().compareTo(this.valueSupplier.get()) < 0;
   }

   public StatLabelElement<V> build(int width, int index) {
      return new StatLabelElement<>(
            ISpatial.ZERO,
            Spatials.size(width, 11),
            this.labelSupplier,
            this.finalValueSupplier,
            this.valueFormatFunction,
            LabelTextStyle.defaultStyle(),
            this.textColor,
            () -> this.isValueCapped() ? TextColor.fromRgb(14898260) : this.textColor
         )
         .layout((screen, gui, parent, world) -> world.translateY(index * 11).width(parent))
         .tooltip(
            Tooltips.shift(
               Tooltips.multi(() -> List.of(new TextComponent(this.labelSupplier.get()), Tooltips.DEFAULT_HOLD_SHIFT_COMPONENT)),
               Tooltips.multi(
                  () -> this.isValueCapped()
                     ? List.of(
                        new TextComponent(this.labelSupplier.get()),
                        this.buildCapDescription(
                           this.valueFormatFunction.apply(this.valueSupplier.get()), this.valueCapFormatFunction.apply(this.valueCapSupplier.get())
                        ),
                        new TextComponent(this.descriptionSupplier.get()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11184810)))
                     )
                     : List.of(
                        new TextComponent(this.labelSupplier.get()),
                        new TextComponent(this.descriptionSupplier.get()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11184810)))
                     )
               )
            )
         );
   }

   private Component buildCapDescription(MutableComponent value, MutableComponent valueCap) {
      return new TextComponent("Stat value of ")
         .withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11184810)))
         .append(value.withStyle(Style.EMPTY.withColor(TextColor.fromRgb(4705351))))
         .append(new TextComponent(" capped at ").withStyle(Style.EMPTY.withColor(TextColor.fromRgb(11184810))))
         .append(valueCap.withStyle(Style.EMPTY.withColor(TextColor.fromRgb(14898260))));
   }
}
