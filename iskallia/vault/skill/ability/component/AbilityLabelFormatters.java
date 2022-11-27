package iskallia.vault.skill.ability.component;

import java.util.Locale;

public final class AbilityLabelFormatters {
   public static String percent(float value) {
      return Math.round(value * 100.0F) + "%";
   }

   public static String ticks(int value) {
      return seconds(value / 20.0F);
   }

   public static String seconds(int value) {
      return seconds((float)value);
   }

   public static String seconds(float value) {
      return decimal(value) + "s";
   }

   public static String integer(int value) {
      return String.valueOf(value);
   }

   public static String decimal(float value) {
      return value == (int)value ? integer((int)value) : String.format(Locale.US, "%.1f", value);
   }

   public static String decimal(double value) {
      return decimal((float)value);
   }

   private AbilityLabelFormatters() {
   }
}
