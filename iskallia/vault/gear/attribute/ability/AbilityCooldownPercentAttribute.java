package iskallia.vault.gear.attribute.ability;

import com.google.gson.JsonArray;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.attribute.type.VaultGearAttributeType;
import javax.annotation.Nullable;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;

public class AbilityCooldownPercentAttribute extends AbilityFloatValueAttribute {
   public AbilityCooldownPercentAttribute(String abilityKey, float amount) {
      super(abilityKey, amount);
   }

   public float adjustCooldown(String ability, float cooldown) {
      return this.affectsAbility(ability) ? cooldown * Math.max(1.0F + this.getAmount(), 0.0F) : cooldown;
   }

   @Override
   public String toString() {
      return "AbilityCooldownPercentAttribute{abilityKey='" + this.abilityKey + "', amount=" + this.amount + "}";
   }

   public static VaultGearAttributeType<AbilityCooldownPercentAttribute> type() {
      return AbilityFloatValueAttribute.type(AbilityCooldownPercentAttribute::new);
   }

   public static AbilityFloatValueAttribute.Generator<AbilityCooldownPercentAttribute, AbilityFloatValueAttribute.Config> generator() {
      return AbilityFloatValueAttribute.generator(AbilityCooldownPercentAttribute::new, AbilityFloatValueAttribute.Config.class);
   }

   public static AbilityCooldownPercentAttribute.Reader reader() {
      return new AbilityCooldownPercentAttribute.Reader();
   }

   private static class Reader extends AbilityFloatValueAttribute.Reader<AbilityCooldownPercentAttribute> {
      @Nullable
      @Override
      public MutableComponent getDisplay(VaultGearAttributeInstance<AbilityCooldownPercentAttribute> instance, VaultGearModifier.AffixType type) {
         AbilityCooldownPercentAttribute attribute = instance.getValue();
         MutableComponent valueDisplay = this.getValueComponent(Math.abs(attribute.getAmount()));
         boolean positive = attribute.getAmount() >= 0.0F;
         String cdInfo;
         if (positive) {
            cdInfo = " increased Cooldown of ";
         } else {
            cdInfo = " reduced Cooldown of ";
         }

         return new TextComponent(type.getAffixPrefix(positive))
            .append(valueDisplay)
            .append(cdInfo)
            .append(attribute.getAbilityKey())
            .setStyle(this.getColoredTextStyle());
      }

      @Nullable
      public MutableComponent getValueDisplay(AbilityCooldownPercentAttribute value) {
         return this.getValueComponent(value.getAmount());
      }

      @Override
      protected void serializeTextElements(
         JsonArray out, VaultGearAttributeInstance<AbilityCooldownPercentAttribute> instance, VaultGearModifier.AffixType type
      ) {
         AbilityCooldownPercentAttribute attribute = instance.getValue();
         MutableComponent valueDisplay = this.getValueComponent(Math.abs(attribute.getAmount()));
         boolean positive = attribute.getAmount() >= 0.0F;
         String cdInfo;
         if (positive) {
            cdInfo = " increased Cooldown of ";
         } else {
            cdInfo = " reduced Cooldown of ";
         }

         out.add(type.getAffixPrefix(positive));
         out.add(valueDisplay.getString());
         out.add(cdInfo);
         out.add(attribute.getAbilityKey());
      }

      private TextComponent getValueComponent(float value) {
         return new TextComponent(this.formatValue(value * 100.0F) + "%");
      }
   }
}
