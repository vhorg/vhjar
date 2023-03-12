package iskallia.vault.gear.attribute.ability;

import com.google.gson.JsonArray;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.attribute.type.VaultGearAttributeType;
import javax.annotation.Nullable;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;

public class AbilityManaCostFlatAttribute extends AbilityFloatValueAttribute {
   public AbilityManaCostFlatAttribute(String abilityKey, float amount) {
      super(abilityKey, amount);
   }

   public float adjustManaCost(String ability, float manaCost) {
      return this.affectsAbility(ability) ? manaCost + this.getAmount() : manaCost;
   }

   @Override
   public String toString() {
      return "AbilityManaCostFlatAttribute{abilityKey='" + this.abilityKey + "', amount=" + this.amount + "}";
   }

   public static VaultGearAttributeType<AbilityManaCostFlatAttribute> type() {
      return AbilityFloatValueAttribute.type(AbilityManaCostFlatAttribute::new);
   }

   public static AbilityFloatValueAttribute.Generator<AbilityManaCostFlatAttribute, AbilityFloatValueAttribute.Config> generator() {
      return AbilityFloatValueAttribute.generator(AbilityManaCostFlatAttribute::new, AbilityFloatValueAttribute.Config.class);
   }

   public static AbilityManaCostFlatAttribute.Reader reader() {
      return new AbilityManaCostFlatAttribute.Reader();
   }

   private static class Reader extends AbilityFloatValueAttribute.Reader<AbilityManaCostFlatAttribute> {
      @Nullable
      @Override
      public MutableComponent getDisplay(VaultGearAttributeInstance<AbilityManaCostFlatAttribute> instance, VaultGearModifier.AffixType type) {
         AbilityManaCostFlatAttribute attribute = instance.getValue();
         MutableComponent valueDisplay = this.getValueDisplay(attribute);
         return valueDisplay == null
            ? null
            : new TextComponent(type.getAffixPrefix(attribute.getAmount() >= 0.0F))
               .append(valueDisplay)
               .append(" to Mana Cost of ")
               .append(attribute.getAbilityKey())
               .setStyle(this.getColoredTextStyle());
      }

      @Nullable
      public MutableComponent getValueDisplay(AbilityManaCostFlatAttribute value) {
         return new TextComponent(this.formatValue(value.getAmount()));
      }

      @Override
      protected void serializeTextElements(JsonArray out, VaultGearAttributeInstance<AbilityManaCostFlatAttribute> instance, VaultGearModifier.AffixType type) {
         AbilityManaCostFlatAttribute attribute = instance.getValue();
         MutableComponent valueDisplay = this.getValueDisplay(attribute);
         if (valueDisplay != null) {
            out.add(type.getAffixPrefix(attribute.getAmount() >= 0.0F));
            out.add(valueDisplay.getString());
            out.add(" to Mana Cost of ");
            out.add(attribute.getAbilityKey());
         }
      }
   }
}
