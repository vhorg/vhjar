package iskallia.vault.gear.attribute.ability.special;

import com.google.gson.JsonArray;
import iskallia.vault.VaultMod;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.attribute.ability.special.base.SpecialAbilityGearAttribute;
import iskallia.vault.gear.attribute.ability.special.base.template.IntRangeModification;
import iskallia.vault.gear.attribute.ability.special.base.template.value.IntValue;
import javax.annotation.Nullable;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

public class EntropyPoisonModification extends IntRangeModification {
   public static final ResourceLocation ID = VaultMod.id("entropic_bind_poison");

   public EntropyPoisonModification() {
      super(ID);
   }

   @Nullable
   @Override
   public MutableComponent getDisplay(SpecialAbilityGearAttribute<?, IntValue> instance, Style style, VaultGearModifier.AffixType type) {
      MutableComponent valueDisplay = this.getValueDisplay(instance.getValue());
      return valueDisplay == null
         ? null
         : new TextComponent("")
            .append(type.getAffixPrefixComponent(true))
            .append("Entropic Bind also applies Poison ")
            .append(valueDisplay.withStyle(instance.getHighlightStyle()))
            .setStyle(instance.getTextStyle());
   }

   @Override
   public void serializeTextElements(JsonArray out, SpecialAbilityGearAttribute<?, IntValue> instance, VaultGearModifier.AffixType type) {
      MutableComponent valueDisplay = this.getValueDisplay(instance.getValue());
      if (valueDisplay != null) {
         out.add(type.getAffixPrefix(true));
         out.add("Entropic Bind also applies Poison ");
         out.add(valueDisplay.getString());
      }
   }
}
