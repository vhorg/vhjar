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
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.ResourceLocation;

public class SpecialAbilityGearAttribute<C extends SpecialAbilityModification.Config<C>, T extends SpecialAbilityModification<C>> extends AbilityGearAttribute {
   private final T modification;
   private final C modificationConfig;

   protected SpecialAbilityGearAttribute(String abilityKey, T modification, C modificationConfig) {
      super(abilityKey);
      this.modification = modification;
      this.modificationConfig = modificationConfig;
   }

   public T getModification() {
      return this.modification;
   }

   public C getModificationConfig() {
      return this.modificationConfig;
   }

   public static VaultGearAttributeType<SpecialAbilityGearAttribute> type() {
      return VaultGearAttributeType.of((buf, attribute) -> {
         buf.writeString(attribute.getAbilityKey());
         buf.writeIdentifier(attribute.getModification().getKey());
         attribute.getModificationConfig().write(buf, (C)attribute.getModificationConfig());
      }, buf -> {
         String ability = buf.readString();
         SpecialAbilityModification<?> modification = SpecialAbilityModificationRegistry.getAbilityModification(buf.readIdentifier());
         SpecialAbilityModification.Config cfg = modification.read(buf);
         return new SpecialAbilityGearAttribute<>(ability, modification, cfg);
      }, (buf, attribute) -> {
         NetcodeUtils.writeString(buf, attribute.getAbilityKey());
         NetcodeUtils.writeIdentifier(buf, attribute.getModification().getKey());
         attribute.getModificationConfig().netWrite(buf, (C)attribute.getModificationConfig());
      }, buf -> {
         String ability = NetcodeUtils.readString(buf);
         SpecialAbilityModification<?> modification = SpecialAbilityModificationRegistry.getAbilityModification(NetcodeUtils.readIdentifier(buf));
         SpecialAbilityModification.Config cfg = modification.netRead(buf);
         return new SpecialAbilityGearAttribute<>(ability, modification, cfg);
      }, VaultGearAttributeType.GSON::toJsonTree, SpecialAbilityGearAttribute::read, SpecialAbilityGearAttribute::write);
   }

   private static SpecialAbilityGearAttribute read(Tag tag) {
      CompoundTag nbt = (CompoundTag)tag;
      String ability = nbt.getString("ability");
      String modificationKey = nbt.getString("modification");
      Tag cfgTag = nbt.get("config");
      SpecialAbilityModification modification = SpecialAbilityModificationRegistry.getAbilityModification(new ResourceLocation(modificationKey));
      SpecialAbilityModification.Config cfg = modification.nbtRead(cfgTag);
      return new SpecialAbilityGearAttribute(ability, (T)modification, (C)cfg);
   }

   private static Tag write(SpecialAbilityGearAttribute attribute) {
      CompoundTag nbt = new CompoundTag();
      nbt.putString("ability", attribute.getAbilityKey());
      nbt.putString("modification", attribute.getModification().getKey().toString());
      nbt.put("config", attribute.getModificationConfig().nbtWrite((C)attribute.getModificationConfig()));
      return nbt;
   }

   public static SpecialAbilityGearAttribute.Generator<?, ?> generator() {
      return new SpecialAbilityGearAttribute.Generator();
   }

   public static SpecialAbilityGearAttribute.Reader<?, ?> reader() {
      return new SpecialAbilityGearAttribute.Reader();
   }

   public static class Generator<C extends SpecialAbilityModification.Config<C>, T extends SpecialAbilityModification<C>>
      extends ConfigurableAttributeGenerator<SpecialAbilityGearAttribute<C, T>, SpecialAbilityGearAttribute.SpecialAbilityConfig<C, T>> {
      private Generator() {
      }

      @Nullable
      @Override
      public Class<SpecialAbilityGearAttribute.SpecialAbilityConfig<C, T>> getConfigurationObjectClass() {
         return MiscUtils.cast(SpecialAbilityGearAttribute.SpecialAbilityConfig.class);
      }

      public SpecialAbilityGearAttribute<C, T> generateRandomValue(SpecialAbilityGearAttribute.SpecialAbilityConfig<C, T> cfg, Random random) {
         return new SpecialAbilityGearAttribute<>(cfg.getAbilityKey(), cfg.getModification(), cfg.getConfig());
      }
   }

   public static class Reader<C extends SpecialAbilityModification.Config<C>, T extends SpecialAbilityModification<C>>
      extends VaultGearModifierReader<SpecialAbilityGearAttribute<C, T>> {
      private Reader() {
         super("", 11842740);
      }

      @Nullable
      @Override
      public MutableComponent getDisplay(VaultGearAttributeInstance<SpecialAbilityGearAttribute<C, T>> instance, VaultGearModifier.AffixType type) {
         SpecialAbilityGearAttribute<C, T> attribute = instance.getValue();
         return attribute.getModification().getDisplay(attribute.getModificationConfig(), this.getColoredTextStyle(), type);
      }

      @Nullable
      public MutableComponent getValueDisplay(SpecialAbilityGearAttribute<C, T> value) {
         return value.getModification().getValueDisplay(value.getModificationConfig());
      }

      @Override
      protected void serializeTextElements(
         JsonArray out, VaultGearAttributeInstance<SpecialAbilityGearAttribute<C, T>> instance, VaultGearModifier.AffixType type
      ) {
         SpecialAbilityGearAttribute<C, T> attribute = instance.getValue();
         attribute.getModification().serializeTextElements(out, attribute.getModificationConfig(), type);
      }
   }

   public static class SpecialAbilityConfig<C extends SpecialAbilityModification.Config<C>, T extends SpecialAbilityModification<C>>
      extends AbilityGearAttribute.AbilityAttributeConfig
      implements ConfigurableAttributeGenerator.CustomTierConfig {
      @Expose
      private final ResourceLocation specialModificationKey;
      private C specialConfig;

      public SpecialAbilityConfig(String abilityKey, ResourceLocation specialModificationKey, C config) {
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
