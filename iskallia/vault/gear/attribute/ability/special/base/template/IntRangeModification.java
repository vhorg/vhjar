package iskallia.vault.gear.attribute.ability.special.base.template;

import io.netty.buffer.ByteBuf;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.gear.attribute.ability.special.base.SpecialAbilityGearAttribute;
import iskallia.vault.gear.attribute.ability.special.base.SpecialAbilityModification;
import iskallia.vault.gear.attribute.ability.special.base.template.config.IntRangeConfig;
import iskallia.vault.gear.attribute.ability.special.base.template.value.IntValue;
import iskallia.vault.gear.reader.VaultGearModifierReader;
import iskallia.vault.util.MiscUtils;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;

public abstract class IntRangeModification extends SpecialAbilityModification<IntRangeConfig, IntValue> {
   protected IntRangeModification(ResourceLocation key) {
      super(key);
   }

   @Override
   public Class<IntRangeConfig> getConfigClass() {
      return IntRangeConfig.class;
   }

   @Override
   public Function<BitBuffer, IntValue> readValue() {
      return IntValue::new;
   }

   @Override
   public Function<ByteBuf, IntValue> netReadValue() {
      return IntValue::new;
   }

   @Override
   public Function<Tag, IntValue> nbtReadValue() {
      return IntValue::new;
   }

   @Nullable
   public MutableComponent getValueDisplay(IntValue value) {
      return new TextComponent(String.valueOf(value.getValue()));
   }

   @Nullable
   @Override
   public <T extends SpecialAbilityModification<IntRangeConfig, IntValue>> MutableComponent getConfigRangeDisplay(
      VaultGearModifierReader<SpecialAbilityGearAttribute<T, IntValue>> reader,
      SpecialAbilityGearAttribute.SpecialAbilityTierConfig<T, IntRangeConfig, IntValue> min,
      SpecialAbilityGearAttribute.SpecialAbilityTierConfig<T, IntRangeConfig, IntValue> max
   ) {
      MutableComponent minDisplay = reader.getValueDisplay(this.of(min.getAbilityKey(), min.getModification(), new IntValue(min.getConfig().getMin())));
      MutableComponent maxDisplay = reader.getValueDisplay(
         this.of(min.getAbilityKey(), min.getModification(), new IntValue(min.getConfig().generateMaximumValue()))
      );
      return (MutableComponent)(minDisplay != null && maxDisplay != null ? minDisplay.append("-").append(maxDisplay) : new TextComponent(""));
   }

   @Override
   public <T extends SpecialAbilityModification<IntRangeConfig, IntValue>> Optional<SpecialAbilityGearAttribute<T, IntValue>> getMinimumValue(
      List<SpecialAbilityGearAttribute.SpecialAbilityTierConfig<T, IntRangeConfig, IntValue>> configurations
   ) {
      return configurations.stream()
         .filter(cfg -> cfg.getModification() != null && cfg.getConfig() != null)
         .map(cfg -> new IntRangeModification.ComparingConfig(cfg.getAbilityKey(), cfg.getModification(), cfg.getConfig().getMin()))
         .min(Comparator.comparing(IntRangeModification.ComparingConfig::value))
         .map(cfg -> this.of(cfg.abilityKey(), (T)cfg.modification(), new IntValue(cfg.value())));
   }

   @Override
   public <T extends SpecialAbilityModification<IntRangeConfig, IntValue>> Optional<SpecialAbilityGearAttribute<T, IntValue>> getMaximumValue(
      List<SpecialAbilityGearAttribute.SpecialAbilityTierConfig<T, IntRangeConfig, IntValue>> configurations
   ) {
      return configurations.stream()
         .filter(cfg -> cfg.getModification() != null && cfg.getConfig() != null)
         .map(cfg -> new IntRangeModification.ComparingConfig(cfg.getAbilityKey(), cfg.getModification(), cfg.getConfig().generateMaximumValue()))
         .max(Comparator.comparing(IntRangeModification.ComparingConfig::value))
         .map(cfg -> this.of(cfg.abilityKey(), (T)cfg.modification(), new IntValue(cfg.value())));
   }

   @Override
   public <T extends SpecialAbilityModification<IntRangeConfig, IntValue>> Optional<Float> getRollPercentage(
      SpecialAbilityGearAttribute<T, IntValue> value, List<SpecialAbilityGearAttribute.SpecialAbilityTierConfig<T, IntRangeConfig, IntValue>> configurations
   ) {
      Optional<SpecialAbilityGearAttribute<T, IntValue>> min = this.getMinimumValue(configurations);
      Optional<SpecialAbilityGearAttribute<T, IntValue>> max = this.getMaximumValue(configurations);
      return MiscUtils.getIntValueRange(
         value.getValue().getValue(), min.map(attr -> attr.getValue().getValue()), max.map(attr -> attr.getValue().getValue()), i -> i
      );
   }

   private record ComparingConfig<T extends SpecialAbilityModification<IntRangeConfig, IntValue>>(String abilityKey, T modification, int value) {
   }
}
