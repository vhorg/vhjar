package iskallia.vault.gear.reader;

import com.google.gson.JsonArray;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.attribute.VaultGearModifier;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import org.jetbrains.annotations.Nullable;

public class IntegerModifierReader extends VaultGearModifierReader<Integer> {
   public IntegerModifierReader(String modifierName, int rgbColor) {
      super(modifierName, rgbColor);
   }

   @Override
   public MutableComponent getDisplay(VaultGearAttributeInstance<Integer> instance, VaultGearModifier.AffixType type) {
      MutableComponent valueDisplay = this.getValueDisplay(instance.getValue());
      return valueDisplay == null
         ? null
         : new TextComponent(type.getAffixPrefix(instance.getValue() >= 0))
            .append(valueDisplay)
            .append(" " + this.getModifierName())
            .withStyle(this.getColoredTextStyle());
   }

   @Nullable
   public MutableComponent getValueDisplay(Integer value) {
      return new TextComponent(String.valueOf(value));
   }

   @Override
   protected void serializeTextElements(JsonArray out, VaultGearAttributeInstance<Integer> instance, VaultGearModifier.AffixType type) {
      Component valueDisplay = this.getValueDisplay(instance.getValue());
      if (valueDisplay != null) {
         out.add(type.getAffixPrefix(instance.getValue() >= 0));
         out.add(valueDisplay.getString());
         out.add(" " + this.getModifierName());
      }
   }
}
