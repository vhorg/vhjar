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

public class MegaJumpVelocityModification extends IntValueAbilityModification<IntValueConfig> {
   public static final ResourceLocation ID = VaultMod.id("mega_jump_velocity");

   public MegaJumpVelocityModification() {
      super(ID);
   }

   public int adjustHeightConfig(IntValueConfig config, int height) {
      return config.getValue() != 0 && height != 0 ? height + config.getValue() : 0;
   }

   public static SpecialAbilityGearAttribute.SpecialAbilityConfig<?, ?> newConfig(int value) {
      return new SpecialAbilityGearAttribute.SpecialAbilityConfig<>("Mega Jump", ID, new IntValueConfig(value));
   }

   @Override
   public Class<IntValueConfig> getConfigClass() {
      return IntValueConfig.class;
   }

   @Nullable
   public MutableComponent getDisplay(IntValueConfig config, Style style, VaultGearModifier.AffixType type) {
      if (config.getValue() == 0) {
         return new TextComponent(type.getAffixPrefix(config.getValue() >= 0))
            .withStyle(style)
            .append(new TextComponent("Mega Jump no longer jumps"))
            .withStyle(style);
      } else {
         Component valueDisplay = this.getValueDisplay(config);
         return valueDisplay == null
            ? null
            : new TextComponent(type.getAffixPrefix(config.getValue() >= 0))
               .withStyle(style)
               .append(valueDisplay)
               .withStyle(style)
               .append(new TextComponent(" additional Mega Jump height"))
               .withStyle(style);
      }
   }

   @Nullable
   public MutableComponent getValueDisplay(IntValueConfig config) {
      return new TextComponent(String.valueOf(config.getValue()));
   }

   public void serializeTextElements(JsonArray out, IntValueConfig config, VaultGearModifier.AffixType type) {
      if (config.getValue() == 0) {
         out.add(type.getAffixPrefix(config.getValue() >= 0));
         out.add("Mega Jump no longer jumps");
      } else {
         Component valueDisplay = this.getValueDisplay(config);
         if (valueDisplay != null) {
            out.add(type.getAffixPrefix(config.getValue() >= 0));
            out.add(valueDisplay.getString());
            out.add(" additional Mega Jump height");
         }
      }
   }
}
