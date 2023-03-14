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

public class AbilityManaCostPercentAttribute extends AbilityFloatValueAttribute {
   public AbilityManaCostPercentAttribute(String abilityKey, float amount) {
      super(abilityKey, amount);
   }

   public float adjustManaCost(String ability, float manaCost) {
      return this.affectsAbility(ability) ? manaCost * Math.max(1.0F + this.getAmount(), 0.0F) : manaCost;
   }

   @Override
   public String toString() {
      return "AbilityManaCostPercentAttribute{abilityKey='" + this.abilityKey + "', amount=" + this.amount + "}";
   }

   public static VaultGearAttributeType<AbilityManaCostPercentAttribute> type() {
      return AbilityFloatValueAttribute.type(AbilityManaCostPercentAttribute::new);
   }

   public static AbilityFloatValueAttribute.Generator<AbilityManaCostPercentAttribute, AbilityFloatValueAttribute.Config> generator() {
      return AbilityFloatValueAttribute.generator(AbilityManaCostPercentAttribute::new, AbilityFloatValueAttribute.Config.class);
   }

   public static AbilityManaCostPercentAttribute.Reader reader() {
      return new AbilityManaCostPercentAttribute.Reader();
   }

   private static class Reader extends AbilityFloatValueAttribute.Reader<AbilityManaCostPercentAttribute> {
      @Nullable
      @Override
      public MutableComponent getDisplay(VaultGearAttributeInstance<AbilityManaCostPercentAttribute> instance, VaultGearModifier.AffixType type) {
         AbilityManaCostPercentAttribute attribute = instance.getValue();
         MutableComponent valueDisplay = this.getValueComponent(Math.abs(attribute.getAmount()));
         boolean positive = attribute.getAmount() >= 0.0F;
         MutableComponent manaCostCmp = new TextComponent("Mana Cost").withStyle(Style.EMPTY.withColor(ModConfigs.COLORS.getColor("manaCost")));
         String cdInfo;
         if (positive) {
            cdInfo = " increased ";
         } else {
            cdInfo = " reduced ";
         }

         return new TextComponent("")
            .append(type.getAffixPrefixComponent(positive).withStyle(this.getValueStyle()))
            .append(valueDisplay.withStyle(this.getValueStyle()))
            .append(cdInfo)
            .append(manaCostCmp)
            .append(" of ")
            .append(new TextComponent(attribute.getAbilityKey()).withStyle(this.getAbilityStyle()))
            .setStyle(this.getColoredTextStyle());
      }

      @Nullable
      public MutableComponent getValueDisplay(AbilityManaCostPercentAttribute value) {
         return this.getValueComponent(value.getAmount());
      }

      @Override
      protected void serializeTextElements(
         JsonArray out, VaultGearAttributeInstance<AbilityManaCostPercentAttribute> instance, VaultGearModifier.AffixType type
      ) {
         AbilityManaCostPercentAttribute attribute = instance.getValue();
         MutableComponent valueDisplay = this.getValueComponent(Math.abs(attribute.getAmount()));
         boolean positive = attribute.getAmount() >= 0.0F;
         String cdInfo;
         if (positive) {
            cdInfo = " increased Mana Cost of ";
         } else {
            cdInfo = " reduced Mana Cost of ";
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
