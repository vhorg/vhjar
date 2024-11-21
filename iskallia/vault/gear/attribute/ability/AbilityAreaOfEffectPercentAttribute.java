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

public class AbilityAreaOfEffectPercentAttribute extends AbilityFloatValueAttribute {
   public AbilityAreaOfEffectPercentAttribute(String abilityKey, float amount) {
      super(abilityKey, amount);
   }

   public float adjustAreaOfEffect(@Nullable String ability, float cooldown) {
      return this.affectsAbility(ability) ? cooldown * Math.max(1.0F + this.getAmount(), 0.0F) : cooldown;
   }

   @Override
   public String toString() {
      return "AbilityAreaOfEffectPercentAttribute{abilityKey='" + this.abilityKey + "', amount=" + this.amount + "}";
   }

   public static VaultGearAttributeType<AbilityAreaOfEffectPercentAttribute> type() {
      return AbilityFloatValueAttribute.type(AbilityAreaOfEffectPercentAttribute::new);
   }

   public static AbilityFloatValueAttribute.Generator<AbilityAreaOfEffectPercentAttribute, AbilityFloatValueAttribute.Config> generator() {
      return AbilityFloatValueAttribute.generator(AbilityAreaOfEffectPercentAttribute::new, AbilityFloatValueAttribute.Config.class);
   }

   public static AbilityAreaOfEffectPercentAttribute.Reader reader() {
      return new AbilityAreaOfEffectPercentAttribute.Reader();
   }

   private static class Reader extends AbilityFloatValueAttribute.Reader<AbilityAreaOfEffectPercentAttribute> {
      @Nullable
      @Override
      public MutableComponent getDisplay(VaultGearAttributeInstance<AbilityAreaOfEffectPercentAttribute> instance, VaultGearModifier.AffixType type) {
         AbilityAreaOfEffectPercentAttribute attribute = instance.getValue();
         MutableComponent valueDisplay = this.getValueComponent(Math.abs(attribute.getAmount()));
         boolean positive = attribute.getAmount() >= 0.0F;
         MutableComponent areaCmp = new TextComponent("Area Of Effect").withStyle(Style.EMPTY.withColor(ModConfigs.COLORS.getColor("areaOfEffect")));
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
            .append(areaCmp)
            .append(" of ")
            .append(this.formatAbilityName(attribute.getAbilityKey()))
            .setStyle(this.getColoredTextStyle());
      }

      @Nullable
      public MutableComponent getValueDisplay(AbilityAreaOfEffectPercentAttribute value) {
         return this.getValueComponent(value.getAmount());
      }

      @Override
      protected void serializeTextElements(
         JsonArray out, VaultGearAttributeInstance<AbilityAreaOfEffectPercentAttribute> instance, VaultGearModifier.AffixType type
      ) {
         AbilityAreaOfEffectPercentAttribute attribute = instance.getValue();
         MutableComponent valueDisplay = this.getValueComponent(Math.abs(attribute.getAmount()));
         boolean positive = attribute.getAmount() >= 0.0F;
         String cdInfo;
         if (positive) {
            cdInfo = " more Area Of Effect of ";
         } else {
            cdInfo = " less Area Of Effect of ";
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
