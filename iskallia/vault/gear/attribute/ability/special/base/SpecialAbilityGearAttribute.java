package iskallia.vault.gear.attribute.ability.special.base;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.annotations.Expose;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.attribute.ability.AbilityGearAttribute;
import iskallia.vault.gear.attribute.config.ConfigurableAttributeGenerator;
import iskallia.vault.gear.attribute.type.VaultGearAttributeType;
import iskallia.vault.gear.reader.VaultGearModifierReader;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.util.NetcodeUtils;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;

public class SpecialAbilityGearAttribute<T extends SpecialAbilityModification<? extends SpecialAbilityConfig<V>, V>, V extends SpecialAbilityConfigValue>
   extends AbilityGearAttribute {
   private final T modification;
   private final V value;
   private int textColor = 14076214;
   private int highlightColor = 6082075;

   protected SpecialAbilityGearAttribute(String abilityKey, T modification, V value) {
      super(abilityKey);
      this.modification = modification;
      this.value = value;
   }

   public T getModification() {
      return this.modification;
   }

   public V getValue() {
      return this.value;
   }

   public <M extends SpecialAbilityGearAttribute<?, ?>> M setTextColor(int textColor) {
      this.textColor = textColor;
      return (M)this;
   }

   public <M extends SpecialAbilityGearAttribute<?, ?>> M setHighlightColor(int highlightColor) {
      this.highlightColor = highlightColor;
      return (M)this;
   }

   public Style getTextStyle() {
      return Style.EMPTY.withColor(TextColor.fromRgb(this.textColor));
   }

   public Style getHighlightStyle() {
      return Style.EMPTY.withColor(TextColor.fromRgb(this.highlightColor));
   }

   public static VaultGearAttributeType<SpecialAbilityGearAttribute> type() {
      return VaultGearAttributeType.of((buf, attribute) -> {
         buf.writeString(attribute.getAbilityKey());
         buf.writeIdentifier(attribute.getModification().getKey());
         buf.writeInt(attribute.textColor);
         buf.writeInt(attribute.highlightColor);
         attribute.getValue().write(buf);
      }, buf -> {
         String ability = buf.readString();
         SpecialAbilityModification<?, ?> modification = SpecialAbilityModificationRegistry.getAbilityModification(buf.readIdentifier());
         int textColor = buf.readInt();
         int highlightColor = buf.readInt();
         SpecialAbilityConfigValue value = (SpecialAbilityConfigValue)modification.readValue().apply(buf);
         return new SpecialAbilityGearAttribute<>(ability, (T)modification, value).setTextColor(textColor).setHighlightColor(highlightColor);
      }, (buf, attribute) -> {
         NetcodeUtils.writeString(buf, attribute.getAbilityKey());
         NetcodeUtils.writeIdentifier(buf, attribute.getModification().getKey());
         buf.writeInt(attribute.textColor);
         buf.writeInt(attribute.highlightColor);
         attribute.getValue().netWrite(buf);
      }, buf -> {
         String ability = NetcodeUtils.readString(buf);
         SpecialAbilityModification<?, ?> modification = SpecialAbilityModificationRegistry.getAbilityModification(NetcodeUtils.readIdentifier(buf));
         int textColor = buf.readInt();
         int highlightColor = buf.readInt();
         SpecialAbilityConfigValue value = (SpecialAbilityConfigValue)modification.netReadValue().apply(buf);
         return new SpecialAbilityGearAttribute<>(ability, (T)modification, value).setTextColor(textColor).setHighlightColor(highlightColor);
      }, VaultGearAttributeType.GSON::toJsonTree, SpecialAbilityGearAttribute::read, SpecialAbilityGearAttribute::write);
   }

   private static SpecialAbilityGearAttribute read(Tag tag) {
      CompoundTag nbt = (CompoundTag)tag;
      String ability = nbt.getString("ability");
      String modificationKey = nbt.getString("modification");
      int textColor = nbt.getInt("textColor");
      int highlightColor = nbt.getInt("highlightColor");
      Tag valueTag = nbt.get("value");
      SpecialAbilityModification<?, ?> modification = SpecialAbilityModificationRegistry.getAbilityModification(new ResourceLocation(modificationKey));
      SpecialAbilityConfigValue value = (SpecialAbilityConfigValue)modification.nbtReadValue().apply(valueTag);
      return new SpecialAbilityGearAttribute<>(ability, (T)modification, value).setTextColor(textColor).setHighlightColor(highlightColor);
   }

   private static Tag write(SpecialAbilityGearAttribute attribute) {
      CompoundTag nbt = new CompoundTag();
      nbt.putString("ability", attribute.getAbilityKey());
      nbt.putString("modification", attribute.getModification().getKey().toString());
      nbt.putInt("textColor", attribute.textColor);
      nbt.putInt("highlightColor", attribute.highlightColor);
      nbt.put("value", attribute.getValue().nbtWrite());
      return nbt;
   }

   public static SpecialAbilityGearAttribute.Generator<?, ?, ?> generator() {
      return new SpecialAbilityGearAttribute.Generator();
   }

   public static SpecialAbilityGearAttribute.Reader<?, ?, ?> reader() {
      return new SpecialAbilityGearAttribute.Reader();
   }

   public static class Generator<T extends SpecialAbilityModification<C, V>, C extends SpecialAbilityConfig<V>, V extends SpecialAbilityConfigValue>
      extends ConfigurableAttributeGenerator<SpecialAbilityGearAttribute<T, V>, SpecialAbilityGearAttribute.SpecialAbilityTierConfig<T, C, V>> {
      private Generator() {
      }

      @Nullable
      @Override
      public Class<SpecialAbilityGearAttribute.SpecialAbilityTierConfig<T, C, V>> getConfigurationObjectClass() {
         return MiscUtils.cast(SpecialAbilityGearAttribute.SpecialAbilityTierConfig.class);
      }

      public SpecialAbilityGearAttribute<T, V> generateRandomValue(SpecialAbilityGearAttribute.SpecialAbilityTierConfig<T, C, V> cfgContainer, Random random) {
         C config = (C)Optional.ofNullable(cfgContainer.getConfig()).orElseThrow();
         SpecialAbilityGearAttribute<T, V> attr = new SpecialAbilityGearAttribute<>(
            cfgContainer.getAbilityKey(), cfgContainer.getModification(), config.generateValue(random)
         );
         attr.setTextColor(config.getTextColor());
         attr.setHighlightColor(config.getHighlightColor());
         return attr;
      }

      @Override
      public Optional<SpecialAbilityGearAttribute<T, V>> getMinimumValue(List<SpecialAbilityGearAttribute.SpecialAbilityTierConfig<T, C, V>> configurations) {
         return configurations.stream()
            .findFirst()
            .map(SpecialAbilityGearAttribute.SpecialAbilityTierConfig::getModification)
            .flatMap(mod -> mod.getMinimumValue(configurations));
      }

      @Override
      public Optional<SpecialAbilityGearAttribute<T, V>> getMaximumValue(List<SpecialAbilityGearAttribute.SpecialAbilityTierConfig<T, C, V>> configurations) {
         return configurations.stream()
            .findFirst()
            .map(SpecialAbilityGearAttribute.SpecialAbilityTierConfig::getModification)
            .flatMap(mod -> mod.getMaximumValue(configurations));
      }

      public Optional<Float> getRollPercentage(
         SpecialAbilityGearAttribute<T, V> value, List<SpecialAbilityGearAttribute.SpecialAbilityTierConfig<T, C, V>> configurations
      ) {
         return configurations.stream()
            .findFirst()
            .map(SpecialAbilityGearAttribute.SpecialAbilityTierConfig::getModification)
            .flatMap(mod -> mod.getRollPercentage(value, configurations));
      }

      @Nullable
      public MutableComponent getConfigRangeDisplay(
         VaultGearModifierReader<SpecialAbilityGearAttribute<T, V>> reader,
         SpecialAbilityGearAttribute.SpecialAbilityTierConfig<T, C, V> min,
         SpecialAbilityGearAttribute.SpecialAbilityTierConfig<T, C, V> max
      ) {
         return min.getModification() == null ? null : min.getModification().getConfigRangeDisplay(reader, min, max);
      }
   }

   public static class Reader<T extends SpecialAbilityModification<C, V>, C extends SpecialAbilityConfig<V>, V extends SpecialAbilityConfigValue>
      extends VaultGearModifierReader<SpecialAbilityGearAttribute<T, V>> {
      private Reader() {
         super("", 11842740);
      }

      @Nullable
      @Override
      public MutableComponent getDisplay(VaultGearAttributeInstance<SpecialAbilityGearAttribute<T, V>> instance, VaultGearModifier.AffixType type) {
         SpecialAbilityGearAttribute<T, V> attr = instance.getValue();
         return attr.getModification().getDisplay(attr, this.getColoredTextStyle(), type);
      }

      @Nullable
      public MutableComponent getValueDisplay(SpecialAbilityGearAttribute<T, V> value) {
         return value.getModification().getValueDisplay(value.getValue());
      }

      @Override
      protected void serializeTextElements(
         JsonArray out, VaultGearAttributeInstance<SpecialAbilityGearAttribute<T, V>> instance, VaultGearModifier.AffixType type
      ) {
         SpecialAbilityGearAttribute<T, V> attr = instance.getValue();
         attr.getModification().serializeTextElements(out, attr, type);
      }
   }

   public static class SpecialAbilityTierConfig<T extends SpecialAbilityModification<C, V>, C extends SpecialAbilityConfig<V>, V extends SpecialAbilityConfigValue>
      extends AbilityGearAttribute.AbilityAttributeConfig
      implements ConfigurableAttributeGenerator.CustomTierConfig {
      @Expose
      private final ResourceLocation specialModificationKey;
      private C specialConfig;

      public SpecialAbilityTierConfig(String abilityKey, ResourceLocation specialModificationKey, C config) {
         super(abilityKey);
         this.specialModificationKey = specialModificationKey;
         this.specialConfig = config;
      }

      @Nullable
      public C getConfig() {
         return this.specialConfig;
      }

      @Nullable
      public T getModification() {
         return SpecialAbilityModificationRegistry.getAbilityModification(this.specialModificationKey);
      }

      @Override
      public void deserializeAdditional(JsonObject configObject, JsonDeserializationContext ctx) {
         T modification = this.getModification();
         if (modification != null) {
            this.specialConfig = (C)ctx.deserialize(configObject, modification.getConfigClass());
         }
      }

      @Override
      public void serializeAdditional(JsonObject configObject, JsonSerializationContext ctx) {
         T modification = this.getModification();
         if (modification != null && this.specialConfig != null) {
            JsonObject obj = ctx.serialize(this.specialConfig, modification.getConfigClass()).getAsJsonObject();

            for (String key : obj.keySet()) {
               configObject.add(key, obj.get(key));
            }
         }
      }
   }
}
