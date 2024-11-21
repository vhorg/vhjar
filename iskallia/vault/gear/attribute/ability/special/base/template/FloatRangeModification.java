package iskallia.vault.gear.attribute.ability.special.base.template;

import io.netty.buffer.ByteBuf;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.gear.attribute.ability.special.base.SpecialAbilityGearAttribute;
import iskallia.vault.gear.attribute.ability.special.base.SpecialAbilityModification;
import iskallia.vault.gear.attribute.ability.special.base.template.config.FloatRangeConfig;
import iskallia.vault.gear.attribute.ability.special.base.template.value.FloatValue;
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

public abstract class FloatRangeModification extends SpecialAbilityModification<FloatRangeConfig, FloatValue> {
   protected FloatRangeModification(ResourceLocation key) {
      super(key);
   }

   @Override
   public Class<FloatRangeConfig> getConfigClass() {
      return FloatRangeConfig.class;
   }

   @Override
   public Function<BitBuffer, FloatValue> readValue() {
      return FloatValue::new;
   }

   @Override
   public Function<ByteBuf, FloatValue> netReadValue() {
      return FloatValue::new;
   }

   @Override
   public Function<Tag, FloatValue> nbtReadValue() {
      return FloatValue::new;
   }

   @Nullable
   public MutableComponent getValueDisplay(FloatValue value) {
      return new TextComponent(String.valueOf(value.getValue()));
   }

   @Nullable
   @Override
   public <T extends SpecialAbilityModification<FloatRangeConfig, FloatValue>> MutableComponent getConfigRangeDisplay(
      VaultGearModifierReader<SpecialAbilityGearAttribute<T, FloatValue>> reader,
      SpecialAbilityGearAttribute.SpecialAbilityTierConfig<T, FloatRangeConfig, FloatValue> min,
      SpecialAbilityGearAttribute.SpecialAbilityTierConfig<T, FloatRangeConfig, FloatValue> max
   ) {
      MutableComponent minDisplay = reader.getValueDisplay(this.of(min.getAbilityKey(), min.getModification(), new FloatValue(min.getConfig().getMin())));
      MutableComponent maxDisplay = reader.getValueDisplay(
         this.of(min.getAbilityKey(), min.getModification(), new FloatValue(min.getConfig().generateMaximumValue()))
      );
      return (MutableComponent)(minDisplay != null && maxDisplay != null ? minDisplay.append("-").append(maxDisplay) : new TextComponent(""));
   }

   @Override
   public <T extends SpecialAbilityModification<FloatRangeConfig, FloatValue>> Optional<SpecialAbilityGearAttribute<T, FloatValue>> getMinimumValue(
      List<SpecialAbilityGearAttribute.SpecialAbilityTierConfig<T, FloatRangeConfig, FloatValue>> configurations
   ) {
      return configurations.stream()
         .filter(cfg -> cfg.getModification() != null && cfg.getConfig() != null)
         .map(cfg -> new FloatRangeModification.ComparingConfig(cfg.getAbilityKey(), cfg.getModification(), cfg.getConfig().getMin()))
         .min(Comparator.comparing(FloatRangeModification.ComparingConfig::value))
         .map(cfg -> this.of(cfg.abilityKey(), (T)cfg.modification(), new FloatValue(cfg.value())));
   }

   @Override
   public <T extends SpecialAbilityModification<FloatRangeConfig, FloatValue>> Optional<SpecialAbilityGearAttribute<T, FloatValue>> getMaximumValue(
      List<SpecialAbilityGearAttribute.SpecialAbilityTierConfig<T, FloatRangeConfig, FloatValue>> configurations
   ) {
      return configurations.stream()
         .filter(cfg -> cfg.getModification() != null && cfg.getConfig() != null)
         .map(cfg -> new FloatRangeModification.ComparingConfig(cfg.getAbilityKey(), cfg.getModification(), cfg.getConfig().generateMaximumValue()))
         .max(Comparator.comparing(FloatRangeModification.ComparingConfig::value))
         .map(cfg -> this.of(cfg.abilityKey(), (T)cfg.modification(), new FloatValue(cfg.value())));
   }

   @Override
   public <T extends SpecialAbilityModification<FloatRangeConfig, FloatValue>> Optional<Float> getRollPercentage(
      SpecialAbilityGearAttribute<T, FloatValue> value,
      List<SpecialAbilityGearAttribute.SpecialAbilityTierConfig<T, FloatRangeConfig, FloatValue>> configurations
   ) {
      Optional<SpecialAbilityGearAttribute<T, FloatValue>> min = this.getMinimumValue(configurations);
      Optional<SpecialAbilityGearAttribute<T, FloatValue>> max = this.getMaximumValue(configurations);
      return MiscUtils.getFloatValueRange(
         value.getValue().getValue(), min.map(attr -> attr.getValue().getValue()), max.map(attr -> attr.getValue().getValue()), i -> i
      );
   }

   private record ComparingConfig<T extends SpecialAbilityModification<FloatRangeConfig, FloatValue>>(String abilityKey, T modification, float value) {
   }
}
