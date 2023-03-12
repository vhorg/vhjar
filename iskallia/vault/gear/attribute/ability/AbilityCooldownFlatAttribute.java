package iskallia.vault.gear.attribute.ability;

import com.google.gson.JsonArray;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.attribute.type.VaultGearAttributeType;
import javax.annotation.Nullable;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;

public class AbilityCooldownFlatAttribute extends AbilityFloatValueAttribute {
   public AbilityCooldownFlatAttribute(String abilityKey, float amount) {
      super(abilityKey, amount);
   }

   public float adjustCooldown(String ability, float cooldown) {
      return this.affectsAbility(ability) ? cooldown + this.getAmount() : cooldown;
   }

   @Override
   public String toString() {
      return "AbilityCooldownFlatAttribute{abilityKey='" + this.abilityKey + "', amount=" + this.amount + "}";
   }

   public static VaultGearAttributeType<AbilityCooldownFlatAttribute> type() {
      return AbilityFloatValueAttribute.type(AbilityCooldownFlatAttribute::new);
   }

   public static AbilityFloatValueAttribute.Generator<AbilityCooldownFlatAttribute, AbilityFloatValueAttribute.Config> generator() {
      return AbilityFloatValueAttribute.generator(AbilityCooldownFlatAttribute::new, AbilityFloatValueAttribute.Config.class);
   }

   public static AbilityCooldownFlatAttribute.Reader reader() {
      return new AbilityCooldownFlatAttribute.Reader();
   }

   private static class Reader extends AbilityFloatValueAttribute.Reader<AbilityCooldownFlatAttribute> {
      @Nullable
      @Override
      public MutableComponent getDisplay(VaultGearAttributeInstance<AbilityCooldownFlatAttribute> instance, VaultGearModifier.AffixType type) {
         AbilityCooldownFlatAttribute attribute = instance.getValue();
         MutableComponent valueDisplay = this.getValueDisplay(attribute);
         return valueDisplay == null
            ? null
            : new TextComponent(type.getAffixPrefix(attribute.getAmount() >= 0.0F))
               .append(valueDisplay)
               .append("s to Cooldown of ")
               .append(attribute.getAbilityKey())
               .setStyle(this.getColoredTextStyle());
      }

      @Nullable
      public MutableComponent getValueDisplay(AbilityCooldownFlatAttribute value) {
         return new TextComponent(this.formatValue(value.getAmount()));
      }

      @Override
      protected void serializeTextElements(JsonArray out, VaultGearAttributeInstance<AbilityCooldownFlatAttribute> instance, VaultGearModifier.AffixType type) {
         AbilityCooldownFlatAttribute attribute = instance.getValue();
         MutableComponent valueDisplay = this.getValueDisplay(attribute);
         if (valueDisplay != null) {
            out.add(type.getAffixPrefix(attribute.getAmount() >= 0.0F));
            out.add(valueDisplay.getString());
            out.add("s to Cooldown of ");
            out.add(attribute.getAbilityKey());
         }
      }
   }
}
