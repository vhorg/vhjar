package iskallia.vault.gear.reader;

import com.google.gson.JsonArray;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.attribute.VaultGearModifier;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import org.jetbrains.annotations.Nullable;

public class FlagModifierReader extends VaultGearModifierReader<Boolean> {
   public FlagModifierReader(String modifierName, int rgbColor) {
      super(modifierName, rgbColor);
   }

   @Override
   public MutableComponent getDisplay(VaultGearAttributeInstance<Boolean> instance, VaultGearModifier.AffixType type) {
      return new TextComponent(type.getAffixPrefix(true) + this.getModifierName()).withStyle(this.getColoredTextStyle());
   }

   @Nullable
   public MutableComponent getValueDisplay(Boolean value) {
      return null;
   }

   @Override
   protected void serializeTextElements(JsonArray out, VaultGearAttributeInstance<Boolean> instance, VaultGearModifier.AffixType type) {
      out.add(type.getAffixPrefix(true));
      out.add(this.getModifierName());
   }
}
