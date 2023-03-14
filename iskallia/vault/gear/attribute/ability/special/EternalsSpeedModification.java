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

public class EternalsSpeedModification extends FloatValueAbilityModification<FloatValueConfig> {
   public static final ResourceLocation ID = VaultMod.id("eternal_speed_increase");

   public EternalsSpeedModification() {
      super(ID);
   }

   public float adjustEternalSpeed(FloatValueConfig config, float speedIncrease) {
      return speedIncrease + config.getValue();
   }

   public static SpecialAbilityGearAttribute.SpecialAbilityConfig<?, ?> newConfig(float value) {
      return new SpecialAbilityGearAttribute.SpecialAbilityConfig<>("Summon Eternal", ID, new FloatValueConfig(value));
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
            .append(" increased ")
            .append(new TextComponent("Eternal").withStyle(getAbilityStyle()))
            .append(" movement speed");
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
         out.add(" increased Eternal movement speed");
      }
   }
}
