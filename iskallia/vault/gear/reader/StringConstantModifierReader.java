package iskallia.vault.gear.reader;

import com.google.gson.JsonArray;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.attribute.VaultGearModifier;
import javax.annotation.Nonnull;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import org.jetbrains.annotations.Nullable;

public class StringConstantModifierReader extends VaultGearModifierReader<String> {
   private final String format;

   public StringConstantModifierReader(String modifierName, int rgbColor, String format) {
      super(modifierName, rgbColor);
      this.format = format;
   }

   @Nullable
   @Override
   public MutableComponent getDisplay(VaultGearAttributeInstance<String> instance, VaultGearModifier.AffixType type) {
      return this.getValueDisplay(instance.getValue()).withStyle(this.getColoredTextStyle());
   }

   @Nonnull
   public MutableComponent getValueDisplay(String value) {
      return new TextComponent(this.format.formatted(this.getModifierName(), value));
   }

   @Override
   protected void serializeTextElements(JsonArray out, VaultGearAttributeInstance<String> instance, VaultGearModifier.AffixType type) {
      out.add(this.format.formatted(this.getModifierName(), instance.getValue()));
   }
}
