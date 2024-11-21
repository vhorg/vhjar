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

public class AbilityCooldownFlatAttribute extends AbilityFloatValueAttribute {
   public AbilityCooldownFlatAttribute(String abilityKey, float amount) {
      super(abilityKey, amount);
   }

   public float adjustCooldown(@Nullable String ability, float cooldown) {
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
         if (valueDisplay == null) {
            return null;
         } else {
            MutableComponent cooldownCmp = new TextComponent("Cooldown").withStyle(Style.EMPTY.withColor(ModConfigs.COLORS.getColor("cooldown")));
            return new TextComponent("")
               .append(type.getAffixPrefixComponent(attribute.getAmount() >= 0.0F).withStyle(this.getValueStyle()))
               .append(valueDisplay.withStyle(this.getValueStyle()))
               .append("s")
               .withStyle(this.getValueStyle())
               .append(" to ")
               .append(cooldownCmp)
               .append(" of ")
               .append(this.formatAbilityName(attribute.getAbilityKey()))
               .setStyle(this.getColoredTextStyle());
         }
      }

      @Nullable
      public MutableComponent getValueDisplay(AbilityCooldownFlatAttribute value) {
         float amtTicks = value.getAmount();
         return new TextComponent(this.formatValue(amtTicks / 20.0F));
      }

      @Override
      protected void serializeTextElements(JsonArray out, VaultGearAttributeInstance<AbilityCooldownFlatAttribute> instance, VaultGearModifier.AffixType type) {
         AbilityCooldownFlatAttribute attribute = instance.getValue();
         MutableComponent valueDisplay = this.getValueDisplay(attribute);
         if (valueDisplay != null) {
            out.add(type.getAffixPrefix(attribute.getAmount() >= 0.0F));
            out.add(valueDisplay.getString());
            out.add("s to Cooldown of ");
            out.add(this.formatAbilityName(attribute.getAbilityKey()).getString());
         }
      }
   }
}
