package iskallia.vault.gear.reader;

import com.google.gson.JsonArray;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.attribute.VaultGearModifier;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;

public class SweepingDamageReader extends DecimalModifierReader<Float> {
   public SweepingDamageReader(int rgbColor) {
      super("Sweeping Hit Damage", rgbColor);
   }

   @Nullable
   @Override
   public MutableComponent getDisplay(VaultGearAttributeInstance<Float> instance, VaultGearModifier.AffixType type) {
      Component valueDisplay = this.getValueDisplay(instance.getValue());
      return valueDisplay == null
         ? null
         : new TextComponent(type.getAffixPrefix(instance.getValue().doubleValue() >= 0.0))
            .withStyle(this.getColoredTextStyle())
            .append(valueDisplay)
            .withStyle(this.getColoredTextStyle())
            .append(new TextComponent(" of Attack Damage dealt as Sweeping Hit").withStyle(this.getColoredTextStyle()));
   }

   @Nullable
   public MutableComponent getValueDisplay(Float value) {
      return new TextComponent(FORMAT.format(value.doubleValue() * 100.0) + "%");
   }

   @Override
   protected void serializeTextElements(JsonArray out, VaultGearAttributeInstance<Float> instance, VaultGearModifier.AffixType type) {
      Component valueDisplay = this.getValueDisplay(instance.getValue());
      if (valueDisplay != null) {
         out.add(type.getAffixPrefix(instance.getValue().doubleValue() >= 0.0));
         out.add(valueDisplay.getString());
         out.add(" of Attack Damage dealt as Sweeping Hit");
      }
   }
}
