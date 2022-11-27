package iskallia.vault.gear.reader;

import com.google.gson.JsonArray;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.attribute.VaultGearModifier;
import java.text.DecimalFormat;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;

public abstract class DecimalModifierReader<T extends Number> extends VaultGearModifierReader<T> {
   private static final DecimalFormat FORMAT = new DecimalFormat("0.##");

   public DecimalModifierReader(String modifierName, int rgbColor) {
      super(modifierName, rgbColor);
   }

   @Nullable
   @Override
   public MutableComponent getDisplay(VaultGearAttributeInstance<T> instance, VaultGearModifier.AffixType type) {
      Component valueDisplay = this.getValueDisplay(instance.getValue());
      return valueDisplay == null
         ? null
         : new TextComponent(type.getAffixPrefix(instance.getValue().doubleValue() >= 0.0))
            .withStyle(this.getColoredTextStyle())
            .append(valueDisplay)
            .withStyle(this.getColoredTextStyle())
            .append(new TextComponent(" " + this.getModifierName()))
            .withStyle(this.getColoredTextStyle());
   }

   @Override
   protected void serializeTextElements(JsonArray out, VaultGearAttributeInstance<T> instance, VaultGearModifier.AffixType type) {
      Component valueDisplay = this.getValueDisplay(instance.getValue());
      if (valueDisplay != null) {
         out.add(type.getAffixPrefix(instance.getValue().doubleValue() >= 0.0));
         out.add(valueDisplay.getString());
         out.add(" " + this.getModifierName());
      }
   }

   public static class Added<T extends Number> extends DecimalModifierReader<T> {
      public Added(String modifierName, int rgbColor) {
         super(modifierName, rgbColor);
      }

      @Nullable
      public MutableComponent getValueDisplay(T value) {
         return new TextComponent(DecimalModifierReader.FORMAT.format(value));
      }
   }

   public static class Percentage<T extends Number> extends DecimalModifierReader<T> {
      public Percentage(String modifierName, int rgbColor) {
         super(modifierName, rgbColor);
      }

      @Nullable
      public MutableComponent getValueDisplay(T value) {
         return new TextComponent(DecimalModifierReader.FORMAT.format(value.doubleValue() * 100.0) + "%");
      }
   }

   public static class Round<T extends Float> extends DecimalModifierReader<T> {
      public Round(String modifierName, int rgbColor) {
         super(modifierName, rgbColor);
      }

      @Nullable
      public MutableComponent getValueDisplay(T value) {
         return new TextComponent(String.valueOf(Math.round(value)));
      }
   }
}
