package iskallia.vault.gear.attribute.ability.special;

import com.google.gson.JsonArray;
import iskallia.vault.VaultMod;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.attribute.ability.special.base.SpecialAbilityGearAttribute;
import iskallia.vault.gear.attribute.ability.special.base.template.FloatValueAbilityModification;
import iskallia.vault.gear.attribute.ability.special.base.template.FloatValueConfig;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

public class ExecuteHealthModification extends FloatValueAbilityModification<FloatValueConfig> {
   public static final ResourceLocation ID = VaultMod.id("execute_health_percent");

   public ExecuteHealthModification() {
      super(ID);
   }

   public float adjustHealthPercent(FloatValueConfig config, float healthPercent) {
      return healthPercent + config.getValue();
   }

   public static SpecialAbilityGearAttribute.SpecialAbilityConfig<?, ?> newConfig(float value) {
      return new SpecialAbilityGearAttribute.SpecialAbilityConfig<>("Execute", ID, new FloatValueConfig(value));
   }

   @Override
   public Class<FloatValueConfig> getConfigClass() {
      return FloatValueConfig.class;
   }

   @Nullable
   public MutableComponent getDisplay(FloatValueConfig config, Style style, VaultGearModifier.AffixType type) {
      MutableComponent valueDisplay = this.getValueDisplay(config);
      return valueDisplay == null
         ? null
         : new TextComponent("")
            .withStyle(style)
            .append(type.getAffixPrefixComponent(config.getValue() >= 0.0F).withStyle(getValueStyle()))
            .append(valueDisplay.withStyle(getValueStyle()))
            .append(" to ")
            .append(new TextComponent("Execute").withStyle(getAbilityStyle()))
            .append(" health damage percentage");
   }

   @Nullable
   public MutableComponent getValueDisplay(FloatValueConfig config) {
      return new TextComponent(FORMAT.format(config.getValue() * 100.0F) + "%");
   }

   public void serializeTextElements(JsonArray out, FloatValueConfig config, VaultGearModifier.AffixType type) {
      Component valueDisplay = this.getValueDisplay(config);
      if (valueDisplay != null) {
         out.add(type.getAffixPrefix(config.getValue() >= 0.0F));
         out.add(valueDisplay.getString());
         out.add(" Execute health damage percentage");
      }
   }
}
