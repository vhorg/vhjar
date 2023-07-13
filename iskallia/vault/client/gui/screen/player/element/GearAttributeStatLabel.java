package iskallia.vault.client.gui.screen.player.element;

import iskallia.vault.gear.attribute.VaultGearAttribute;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import java.util.function.Function;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextColor;
import net.minecraft.world.entity.player.Player;

public final class GearAttributeStatLabel {
   public static StatLabelElementBuilder<Integer> ofInteger(Player player, VaultGearAttribute<Integer> attribute) {
      return of(player, attribute, VaultGearAttributeTypeMerger.intSum());
   }

   public static StatLabelElementBuilder<Double> ofDouble(Player player, VaultGearAttribute<Double> attribute) {
      return of(player, attribute, VaultGearAttributeTypeMerger.doubleSum());
   }

   public static StatLabelElementBuilder<Float> ofFloat(Player player, VaultGearAttribute<Float> attribute) {
      return of(player, attribute, VaultGearAttributeTypeMerger.floatSum());
   }

   public static StatLabelElementBuilder<Float> ofFloat(Player player, VaultGearAttribute<Float> attribute, Function<Player, Float> attributeCapSupplier) {
      return of(player, attribute, VaultGearAttributeTypeMerger.floatSum())
         .setValueCap(() -> attributeCapSupplier.apply(player), value -> attribute.getReader().getValueDisplay(value));
   }

   public static StatLabelElementBuilder<Boolean> ofBoolean(Player player, VaultGearAttribute<Boolean> attribute) {
      return of(player, attribute, VaultGearAttributeTypeMerger.anyTrue(), StatLabelElement.BOOLEAN_FORMATTER);
   }

   public static <V extends Comparable<V>> StatLabelElementBuilder<V> of(Player player, VaultGearAttribute<V> attribute, Function<Player, V> valueSupplier) {
      return of(player, attribute, valueSupplier, value -> attribute.getReader().getValueDisplay(value));
   }

   public static StatLabelElementBuilder<Float> ofFloat(
      Player player, VaultGearAttribute<Float> attribute, Function<Player, Float> valueSupplier, Function<Player, Float> attributeCapSupplier
   ) {
      return of(player, attribute, valueSupplier, value -> attribute.getReader().getValueDisplay(value))
         .setValueCap(() -> attributeCapSupplier.apply(player), value -> attribute.getReader().getValueDisplay(value));
   }

   private static <V extends Comparable<V>> StatLabelElementBuilder<V> of(
      Player player, VaultGearAttribute<V> attribute, VaultGearAttributeTypeMerger<V, V> attributeTypeMerger
   ) {
      return of(player, attribute, attributeTypeMerger, value -> attribute.getReader().getValueDisplay(value));
   }

   private static <V extends Comparable<V>> StatLabelElementBuilder<V> of(
      Player player, VaultGearAttribute<V> attribute, VaultGearAttributeTypeMerger<V, V> attributeTypeMerger, Function<V, MutableComponent> valueFormatFunction
   ) {
      return new StatLabelElementBuilder<>(
         () -> attribute.getReader().getModifierName(),
         () -> ModConfigs.MENU_PLAYER_STAT_DESCRIPTIONS.getModGearAttributeDescriptionFor(attribute.getRegistryName()),
         () -> AttributeSnapshotHelper.getInstance().getSnapshot(player).getAttributeValue(attribute, attributeTypeMerger),
         valueFormatFunction,
         TextColor.fromRgb(0)
      );
   }

   private static <V extends Comparable<V>> StatLabelElementBuilder<V> of(
      Player player, VaultGearAttribute<V> attribute, Function<Player, V> valueSupplier, Function<V, MutableComponent> valueFormatFunction
   ) {
      return new StatLabelElementBuilder<>(
         () -> attribute.getReader().getModifierName(),
         () -> ModConfigs.MENU_PLAYER_STAT_DESCRIPTIONS.getModGearAttributeDescriptionFor(attribute.getRegistryName()),
         () -> valueSupplier.apply(player),
         valueFormatFunction,
         TextColor.fromRgb(0)
      );
   }

   private GearAttributeStatLabel() {
   }
}
