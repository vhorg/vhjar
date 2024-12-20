package iskallia.vault.gear.attribute.custom.effect;

import com.google.gson.JsonArray;
import com.google.gson.annotations.Expose;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.attribute.config.ConfigurableAttributeGenerator;
import iskallia.vault.gear.attribute.type.VaultGearAttributeType;
import iskallia.vault.gear.reader.VaultGearModifierReader;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.util.NetcodeUtils;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.ForgeRegistries;

public class EffectGearAttribute {
   private final MobEffect effect;
   private final int amplifier;

   public EffectGearAttribute(MobEffect effect, int amplifier) {
      this.effect = effect;
      this.amplifier = amplifier;
   }

   public MobEffect getEffect() {
      return this.effect;
   }

   public int getAmplifier() {
      return this.amplifier;
   }

   @Override
   public String toString() {
      return "EffectGearAttribute{effect=" + (this.effect == null ? "null" : this.effect.getRegistryName().toString()) + ", amplifier=" + this.amplifier + "}";
   }

   public static VaultGearAttributeType<EffectGearAttribute> type() {
      return VaultGearAttributeType.of((buf, attribute) -> {
         buf.writeIdentifier(attribute.getEffect().getRegistryName());
         buf.writeInt(attribute.getAmplifier());
      }, buf -> {
         MobEffect effect = (MobEffect)ForgeRegistries.MOB_EFFECTS.getValue(buf.readIdentifier());
         return new EffectGearAttribute(effect, buf.readInt());
      }, (buf, attribute) -> {
         NetcodeUtils.writeIdentifier(buf, attribute.getEffect().getRegistryName());
         buf.writeInt(attribute.getAmplifier());
      }, buf -> {
         MobEffect effect = (MobEffect)ForgeRegistries.MOB_EFFECTS.getValue(NetcodeUtils.readIdentifier(buf));
         return new EffectGearAttribute(effect, buf.readInt());
      }, VaultGearAttributeType.GSON::toJsonTree, EffectGearAttribute::read, EffectGearAttribute::write);
   }

   private static EffectGearAttribute read(Tag nbt) {
      CompoundTag tag = (CompoundTag)nbt;
      MobEffect effect = (MobEffect)ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(tag.getString("effect")));
      int amplifier = tag.getInt("amplifier");
      return new EffectGearAttribute(effect, amplifier);
   }

   private static Tag write(EffectGearAttribute attribute) {
      CompoundTag tag = new CompoundTag();
      tag.putString("effect", attribute.getEffect().getRegistryName().toString());
      tag.putInt("amplifier", attribute.getAmplifier());
      return tag;
   }

   public static EffectGearAttribute.Generator generator() {
      return new EffectGearAttribute.Generator();
   }

   public static EffectGearAttribute.Reader reader() {
      return new EffectGearAttribute.Reader();
   }

   public static class Config {
      @Expose
      private final ResourceLocation effectKey;
      @Expose
      private final int amplifier;

      public Config(MobEffect effect, int amplifier) {
         this(effect.getRegistryName(), amplifier);
      }

      public Config(ResourceLocation effectKey, int amplifier) {
         this.effectKey = effectKey;
         this.amplifier = amplifier;
      }
   }

   private static class Generator extends ConfigurableAttributeGenerator<EffectGearAttribute, EffectGearAttribute.Config> {
      @Nullable
      @Override
      public Class<EffectGearAttribute.Config> getConfigurationObjectClass() {
         return EffectGearAttribute.Config.class;
      }

      public EffectGearAttribute generateRandomValue(EffectGearAttribute.Config object, Random random) {
         MobEffect effect = (MobEffect)ForgeRegistries.MOB_EFFECTS.getValue(object.effectKey);
         return new EffectGearAttribute(effect, object.amplifier);
      }

      @Nullable
      public MutableComponent getConfigDisplay(VaultGearModifierReader<EffectGearAttribute> reader, EffectGearAttribute.Config object) {
         MobEffect effect = (MobEffect)ForgeRegistries.MOB_EFFECTS.getValue(object.effectKey);
         return effect == null
            ? null
            : new TextComponent(String.valueOf(object.amplifier)).withStyle(reader.getColoredTextStyle()).append(" ").append(effect.getDisplayName());
      }

      @Override
      public Optional<EffectGearAttribute> getMinimumValue(List<EffectGearAttribute.Config> configurations) {
         return configurations.stream()
            .min(Comparator.comparing(config -> config.amplifier))
            .map(config -> new EffectGearAttribute((MobEffect)ForgeRegistries.MOB_EFFECTS.getValue(config.effectKey), config.amplifier));
      }

      @Override
      public Optional<EffectGearAttribute> getMaximumValue(List<EffectGearAttribute.Config> configurations) {
         return configurations.stream()
            .max(Comparator.comparing(config -> config.amplifier))
            .map(config -> new EffectGearAttribute((MobEffect)ForgeRegistries.MOB_EFFECTS.getValue(config.effectKey), config.amplifier));
      }

      public Optional<Float> getRollPercentage(EffectGearAttribute value, List<EffectGearAttribute.Config> configurations) {
         return MiscUtils.getIntValueRange(
            value.getAmplifier(), this.getMinimumValue(configurations), this.getMaximumValue(configurations), EffectGearAttribute::getAmplifier
         );
      }
   }

   private static class Reader extends VaultGearModifierReader<EffectGearAttribute> {
      private Reader() {
         super("", 14111487);
      }

      @Nullable
      @Override
      public MutableComponent getDisplay(VaultGearAttributeInstance<EffectGearAttribute> instance, VaultGearModifier.AffixType type) {
         EffectGearAttribute effect = instance.getValue();
         MutableComponent valueDisplay = this.getValueDisplay(effect);
         return valueDisplay == null
            ? null
            : new TextComponent(type.getAffixPrefix(true))
               .append(valueDisplay)
               .append(new TextComponent(" "))
               .append(effect.getEffect().getDisplayName())
               .setStyle(this.getColoredTextStyle());
      }

      @Nullable
      public MutableComponent getValueDisplay(EffectGearAttribute value) {
         return new TextComponent(String.valueOf(value.getAmplifier()));
      }

      @Override
      protected void serializeTextElements(JsonArray out, VaultGearAttributeInstance<EffectGearAttribute> instance, VaultGearModifier.AffixType type) {
         EffectGearAttribute effect = instance.getValue();
         MutableComponent valueDisplay = this.getValueDisplay(effect);
         if (valueDisplay != null) {
            out.add(type.getAffixPrefix(true));
            out.add(valueDisplay.getString());
            out.add(" ");
            out.add(effect.getEffect().getDescriptionId());
         }
      }
   }
}
