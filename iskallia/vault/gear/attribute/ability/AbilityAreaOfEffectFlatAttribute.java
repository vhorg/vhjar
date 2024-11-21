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

public class AbilityAreaOfEffectFlatAttribute extends AbilityFloatValueAttribute {
   public AbilityAreaOfEffectFlatAttribute(String abilityKey, float amount) {
      super(abilityKey, amount);
   }

   public float adjustAreaOfEffect(@Nullable String ability, float area) {
      return this.affectsAbility(ability) ? area + this.getAmount() : area;
   }

   @Override
   public String toString() {
      return "AbilityAreaOfEffectFlatAttribute{abilityKey='" + this.abilityKey + "', amount=" + this.amount + "}";
   }

   public static VaultGearAttributeType<AbilityAreaOfEffectFlatAttribute> type() {
      return AbilityFloatValueAttribute.type(AbilityAreaOfEffectFlatAttribute::new);
   }

   public static AbilityFloatValueAttribute.Generator<AbilityAreaOfEffectFlatAttribute, AbilityFloatValueAttribute.Config> generator() {
      return AbilityFloatValueAttribute.generator(AbilityAreaOfEffectFlatAttribute::new, AbilityFloatValueAttribute.Config.class);
   }

   public static AbilityAreaOfEffectFlatAttribute.Reader reader() {
      return new AbilityAreaOfEffectFlatAttribute.Reader();
   }

   private static class Reader extends AbilityFloatValueAttribute.Reader<AbilityAreaOfEffectFlatAttribute> {
      @Nullable
      @Override
      public MutableComponent getDisplay(VaultGearAttributeInstance<AbilityAreaOfEffectFlatAttribute> instance, VaultGearModifier.AffixType type) {
         AbilityAreaOfEffectFlatAttribute attribute = instance.getValue();
         MutableComponent valueDisplay = this.getValueDisplay(attribute);
         if (valueDisplay == null) {
            return null;
         } else {
            MutableComponent areaCmp = new TextComponent("Area Of Effect").withStyle(Style.EMPTY.withColor(ModConfigs.COLORS.getColor("areaOfEffect")));
            return new TextComponent("")
               .append(type.getAffixPrefixComponent(attribute.getAmount() >= 0.0F).withStyle(this.getValueStyle()))
               .append(valueDisplay.withStyle(this.getValueStyle()))
               .append(" Blocks ")
               .withStyle(this.getValueStyle())
               .append(" of ")
               .append(areaCmp)
               .append(" of ")
               .append(this.formatAbilityName(attribute.getAbilityKey()))
               .setStyle(this.getColoredTextStyle());
         }
      }

      @Nullable
      public MutableComponent getValueDisplay(AbilityAreaOfEffectFlatAttribute value) {
         return new TextComponent(this.formatValue(value.getAmount()));
      }

      @Override
      protected void serializeTextElements(
         JsonArray out, VaultGearAttributeInstance<AbilityAreaOfEffectFlatAttribute> instance, VaultGearModifier.AffixType type
      ) {
         AbilityAreaOfEffectFlatAttribute attribute = instance.getValue();
         MutableComponent valueDisplay = this.getValueDisplay(attribute);
         if (valueDisplay != null) {
            out.add(type.getAffixPrefix(attribute.getAmount() >= 0.0F));
            out.add(valueDisplay.getString());
            out.add(" Blocks of Area of Effect of ");
            out.add(this.formatAbilityName(attribute.getAbilityKey()).getString());
         }
      }
   }
}
