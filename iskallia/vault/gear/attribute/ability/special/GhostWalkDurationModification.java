package iskallia.vault.gear.attribute.ability.special;

import com.google.gson.JsonArray;
import iskallia.vault.VaultMod;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.attribute.ability.special.base.SpecialAbilityGearAttribute;
import iskallia.vault.gear.attribute.ability.special.base.template.IntValueAbilityModification;
import iskallia.vault.gear.attribute.ability.special.base.template.IntValueConfig;
import javax.annotation.Nullable;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

public class GhostWalkDurationModification extends IntValueAbilityModification<IntValueConfig> {
   public static final ResourceLocation ID = VaultMod.id("ghost_walk_additional_duration");

   public GhostWalkDurationModification() {
      super(ID);
   }

   public int addDuration(IntValueConfig config, int duration) {
      return duration + config.getValue();
   }

   public static SpecialAbilityGearAttribute.SpecialAbilityConfig<?, ?> newConfig(int value) {
      return new SpecialAbilityGearAttribute.SpecialAbilityConfig<>("Ghost Walk", ID, new IntValueConfig(value));
   }

   @Override
   public Class<IntValueConfig> getConfigClass() {
      return IntValueConfig.class;
   }

   @Nullable
   public MutableComponent getDisplay(IntValueConfig config, Style style, VaultGearModifier.AffixType type) {
      Component valueDisplay = this.getValueDisplay(config);
      return valueDisplay == null
         ? null
         : new TextComponent(type.getAffixPrefix(config.getValue() >= 0))
            .withStyle(style)
            .append(valueDisplay)
            .withStyle(style)
            .append(new TextComponent(" seconds to Ghost Walk"))
            .withStyle(style);
   }

   @Nullable
   public MutableComponent getValueDisplay(IntValueConfig config) {
      float duration = config.getValue() * 0.05F;
      return new TextComponent(FORMAT.format(duration));
   }

   public void serializeTextElements(JsonArray out, IntValueConfig config, VaultGearModifier.AffixType type) {
      Component valueDisplay = this.getValueDisplay(config);
      if (valueDisplay != null) {
         out.add(type.getAffixPrefix(config.getValue() >= 0));
         out.add(valueDisplay.getString());
         out.add(" seconds to Ghost Walk");
      }
   }
}
