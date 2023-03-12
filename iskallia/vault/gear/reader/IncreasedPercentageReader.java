package iskallia.vault.gear.reader;

import com.google.gson.JsonArray;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.attribute.VaultGearModifier;
import java.text.DecimalFormat;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;

public class IncreasedPercentageReader<T extends Number> extends VaultGearModifierReader<T> {
   private static final DecimalFormat FORMAT = new DecimalFormat("0.##");

   public IncreasedPercentageReader(String modifierName, int rgbColor) {
      super(modifierName, rgbColor);
   }

   @Nullable
   @Override
   public MutableComponent getDisplay(VaultGearAttributeInstance<T> instance, VaultGearModifier.AffixType type) {
      Component valueDisplay = this.getValueDisplay(instance.getValue());
      if (valueDisplay == null) {
         return null;
      } else {
         boolean positive = instance.getValue().doubleValue() >= 0.0;
         String incInfo;
         if (positive) {
            incInfo = "increased";
         } else {
            incInfo = "reduced";
         }

         return new TextComponent(type.getAffixPrefix(positive))
            .withStyle(this.getColoredTextStyle())
            .append(valueDisplay)
            .withStyle(this.getColoredTextStyle())
            .append(" ")
            .append(incInfo)
            .withStyle(this.getColoredTextStyle())
            .append(" ")
            .append(new TextComponent(this.getModifierName()))
            .withStyle(this.getColoredTextStyle());
      }
   }

   @Nullable
   public MutableComponent getValueDisplay(T value) {
      return new TextComponent(FORMAT.format(value.doubleValue() * 100.0) + "%");
   }

   @Override
   protected void serializeTextElements(JsonArray out, VaultGearAttributeInstance<T> instance, VaultGearModifier.AffixType type) {
      Component valueDisplay = this.getValueDisplay(instance.getValue());
      if (valueDisplay != null) {
         boolean positive = instance.getValue().doubleValue() >= 0.0;
         String incInfo;
         if (positive) {
            incInfo = "increased";
         } else {
            incInfo = "reduced";
         }

         out.add(type.getAffixPrefix(instance.getValue().doubleValue() >= 0.0));
         out.add(valueDisplay.getString());
         out.add(" ");
         out.add(incInfo);
         out.add(" ");
         out.add(this.getModifierName());
      }
   }
}
