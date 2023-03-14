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

public class VeinMinerAdditionalBlocksModification extends IntValueAbilityModification<IntValueConfig> {
   public static final ResourceLocation ID = VaultMod.id("vein_miner_additional_blocks");

   public VeinMinerAdditionalBlocksModification() {
      super(ID);
   }

   public int adjustBlockCount(IntValueConfig config, int count) {
      return count + config.getValue();
   }

   public static SpecialAbilityGearAttribute.SpecialAbilityConfig<?, ?> newConfig(int value) {
      return new SpecialAbilityGearAttribute.SpecialAbilityConfig<>("Vein Miner", ID, new IntValueConfig(value));
   }

   @Override
   public Class<IntValueConfig> getConfigClass() {
      return IntValueConfig.class;
   }

   @Nullable
   public MutableComponent getDisplay(IntValueConfig config, Style style, VaultGearModifier.AffixType type) {
      MutableComponent valueDisplay = this.getValueDisplay(config);
      return valueDisplay == null
         ? null
         : new TextComponent("")
            .withStyle(style)
            .append(type.getAffixPrefixComponent(config.getValue() >= 0).withStyle(getValueStyle()))
            .append(valueDisplay.withStyle(getValueStyle()))
            .append(" additional ")
            .append(new TextComponent("Vein Miner").withStyle(getAbilityStyle()))
            .append(" blocks");
   }

   @Nullable
   public MutableComponent getValueDisplay(IntValueConfig config) {
      return new TextComponent(String.valueOf(config.getValue()));
   }

   public void serializeTextElements(JsonArray out, IntValueConfig config, VaultGearModifier.AffixType type) {
      Component valueDisplay = this.getValueDisplay(config);
      if (valueDisplay != null) {
         out.add(type.getAffixPrefix(config.getValue() >= 0));
         out.add(valueDisplay.getString());
         out.add(" additional Vein Miner blocks");
      }
   }
}