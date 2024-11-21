package iskallia.vault.gear.attribute.ability;

import com.google.gson.JsonArray;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.attribute.type.VaultGearAttributeType;
import iskallia.vault.init.ModConfigs;
import javax.annotation.Nullable;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;

public class AbilityCooldownPercentAttribute extends AbilityFloatValueAttribute {
   public AbilityCooldownPercentAttribute(String abilityKey, float amount) {
      super(abilityKey, amount);
   }

   public float adjustCooldown(@Nullable String ability, float cooldown) {
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
         MutableComponent cooldownCmp = new TextComponent("Cooldown").withStyle(Style.EMPTY.withColor(ModConfigs.COLORS.getColor("cooldown")));
         String cdInfo;
         if (positive) {
            cdInfo = " more ";
         } else {
            cdInfo = " less ";
         }

         return new TextComponent("")
            .append(type.getAffixPrefixComponent(true).withStyle(this.getValueStyle()))
            .append(valueDisplay.withStyle(this.getValueStyle()))
            .append(cdInfo)
            .append(cooldownCmp)
            .append(" of ")
            .append(this.formatAbilityName(attribute.getAbilityKey()))
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
            cdInfo = " more Cooldown of ";
         } else {
            cdInfo = " less Cooldown of ";
         }

         out.add(type.getAffixPrefix(true));
         out.add(valueDisplay.getString());
         out.add(cdInfo);
         out.add(this.formatAbilityName(attribute.getAbilityKey()).getString());
      }

      private TextComponent getValueComponent(float value) {
         return new TextComponent(this.formatValue(value * 100.0F) + "%");
      }
   }
}
