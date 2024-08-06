package iskallia.vault.gear.attribute.custom;

import com.google.gson.JsonArray;
import com.google.gson.annotations.Expose;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.attribute.config.ConfigurableAttributeGenerator;
import iskallia.vault.gear.attribute.type.VaultGearAttributeType;
import iskallia.vault.gear.comparator.VaultGearAttributeComparator;
import iskallia.vault.gear.reader.VaultGearModifierReader;
import iskallia.vault.util.NetcodeUtils;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.ForgeRegistries;

public class EffectAvoidanceGearAttribute {
   private static final DecimalFormat FORMAT = new DecimalFormat("0.##");
   private final MobEffect effect;
   private final float chance;

   public EffectAvoidanceGearAttribute(MobEffect effect, float chance) {
      this.effect = effect;
      this.chance = chance;
   }

   public MobEffect getEffect() {
      return this.effect;
   }

   public float getChance() {
      return this.chance;
   }

   @Override
   public String toString() {
      return "EffectAvoidanceGearAttribute{effect="
         + (this.effect == null ? "null" : this.effect.getRegistryName().toString())
         + ", chance="
         + this.chance
         + "}";
   }

   public static VaultGearAttributeType<EffectAvoidanceGearAttribute> type() {
      return VaultGearAttributeType.of((buf, attribute) -> {
         buf.writeIdentifier(attribute.getEffect().getRegistryName());
         buf.writeFloat(attribute.getChance());
      }, buf -> {
         MobEffect effect = (MobEffect)ForgeRegistries.MOB_EFFECTS.getValue(buf.readIdentifier());
         return new EffectAvoidanceGearAttribute(effect, buf.readFloat());
      }, (buf, attribute) -> {
         NetcodeUtils.writeIdentifier(buf, attribute.getEffect().getRegistryName());
         buf.writeFloat(attribute.getChance());
      }, buf -> {
         MobEffect effect = (MobEffect)ForgeRegistries.MOB_EFFECTS.getValue(NetcodeUtils.readIdentifier(buf));
         return new EffectAvoidanceGearAttribute(effect, buf.readFloat());
      }, VaultGearAttributeType.GSON::toJsonTree, EffectAvoidanceGearAttribute::read, EffectAvoidanceGearAttribute::write);
   }

   private static EffectAvoidanceGearAttribute read(Tag nbt) {
      CompoundTag tag = (CompoundTag)nbt;
      MobEffect effect = (MobEffect)ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(tag.getString("effect")));
      float chance = tag.getFloat("chance");
      return new EffectAvoidanceGearAttribute(effect, chance);
   }

   private static Tag write(EffectAvoidanceGearAttribute attribute) {
      CompoundTag tag = new CompoundTag();
      tag.putString("effect", attribute.getEffect().getRegistryName().toString());
      tag.putFloat("chance", attribute.getChance());
      return tag;
   }

   public static EffectAvoidanceGearAttribute.AttributeComparator comparator() {
      return new EffectAvoidanceGearAttribute.AttributeComparator();
   }

   public static EffectAvoidanceGearAttribute.Generator generator() {
      return new EffectAvoidanceGearAttribute.Generator();
   }

   public static EffectAvoidanceGearAttribute.Reader reader() {
      return new EffectAvoidanceGearAttribute.Reader();
   }

   private static class AttributeComparator extends VaultGearAttributeComparator<EffectAvoidanceGearAttribute> {
      public Optional<EffectAvoidanceGearAttribute> merge(EffectAvoidanceGearAttribute thisValue, EffectAvoidanceGearAttribute thatValue) {
         return thisValue.getEffect() != thatValue.getEffect()
            ? Optional.empty()
            : Optional.of(new EffectAvoidanceGearAttribute(thisValue.getEffect(), thisValue.getChance() + thatValue.getChance()));
      }

      @Deprecated
      public Optional<EffectAvoidanceGearAttribute> difference(EffectAvoidanceGearAttribute thisValue, EffectAvoidanceGearAttribute thatValue) {
         return Optional.empty();
      }

      @Nonnull
      @Override
      public Comparator<EffectAvoidanceGearAttribute> getComparator() {
         return Comparator.comparing(EffectAvoidanceGearAttribute::getChance);
      }
   }

   public static class Config {
      @Expose
      private final ResourceLocation effectKey;
      @Expose
      private final float minChance;
      @Expose
      private final float maxChance;
      @Expose
      private final float step;

      public Config(MobEffect effect, float minChance, float maxChance) {
         this(effect.getRegistryName(), minChance, maxChance, 0.05F);
      }

      public Config(ResourceLocation effectKey, float minChance, float maxChance, float step) {
         this.effectKey = effectKey;
         this.minChance = minChance;
         this.maxChance = maxChance;
         this.step = step;
      }

      private MobEffect getEffect() {
         return (MobEffect)ForgeRegistries.MOB_EFFECTS.getValue(this.effectKey);
      }
   }

   private static class Generator extends ConfigurableAttributeGenerator<EffectAvoidanceGearAttribute, EffectAvoidanceGearAttribute.Config> {
      @Nullable
      @Override
      public Class<EffectAvoidanceGearAttribute.Config> getConfigurationObjectClass() {
         return EffectAvoidanceGearAttribute.Config.class;
      }

      @Nullable
      public MutableComponent getConfigRangeDisplay(
         VaultGearModifierReader<EffectAvoidanceGearAttribute> reader, EffectAvoidanceGearAttribute.Config min, EffectAvoidanceGearAttribute.Config max
      ) {
         return this.getChanceDisplay(min.minChance).append("-").append(this.getChanceDisplay(max.maxChance));
      }

      private MutableComponent getChanceDisplay(float value) {
         return new TextComponent(EffectAvoidanceGearAttribute.FORMAT.format(value * 100.0F) + "%");
      }

      @Nullable
      public MutableComponent getConfigDisplay(VaultGearModifierReader<EffectAvoidanceGearAttribute> reader, EffectAvoidanceGearAttribute.Config object) {
         MutableComponent range = this.getConfigRangeDisplay(reader, object);
         MobEffect effect = object.getEffect();
         return new TextComponent("")
            .withStyle(reader.getColoredTextStyle())
            .append(range.withStyle(reader.getColoredTextStyle()))
            .append(" ")
            .append(effect.getDisplayName())
            .append(new TextComponent(" Avoidance"));
      }

      public EffectAvoidanceGearAttribute generateRandomValue(EffectAvoidanceGearAttribute.Config config, Random random) {
         int steps = Math.round(Math.max(config.maxChance - config.minChance, 0.0F) / config.step) + 1;
         return new EffectAvoidanceGearAttribute(config.getEffect(), config.minChance + random.nextInt(steps) * config.step);
      }

      @Override
      public Optional<EffectAvoidanceGearAttribute> getMinimumValue(List<EffectAvoidanceGearAttribute.Config> configurations) {
         return configurations.stream()
            .min(Comparator.comparing(config -> config.minChance))
            .map(config -> new EffectAvoidanceGearAttribute(config.getEffect(), config.minChance));
      }

      private float getMaximumChance(EffectAvoidanceGearAttribute.Config config) {
         int steps = Math.round(Math.max(config.maxChance - config.minChance, 0.0F) / config.step);
         return config.minChance + steps * config.step;
      }

      @Override
      public Optional<EffectAvoidanceGearAttribute> getMaximumValue(List<EffectAvoidanceGearAttribute.Config> configurations) {
         return configurations.stream()
            .max(Comparator.comparing(this::getMaximumChance))
            .map(config -> new EffectAvoidanceGearAttribute(config.getEffect(), config.maxChance));
      }
   }

   private static class Reader extends VaultGearModifierReader<EffectAvoidanceGearAttribute> {
      protected Reader() {
         super("", 9561049);
      }

      @Nullable
      @Override
      public MutableComponent getDisplay(VaultGearAttributeInstance<EffectAvoidanceGearAttribute> instance, VaultGearModifier.AffixType type) {
         EffectAvoidanceGearAttribute effectAvoidance = instance.getValue();
         MutableComponent valueDisplay = this.getValueDisplay(effectAvoidance);
         return valueDisplay == null
            ? null
            : new TextComponent(type.getAffixPrefix(effectAvoidance.getChance() >= 0.0F))
               .append(valueDisplay)
               .append(" ")
               .append(effectAvoidance.getEffect().getDisplayName())
               .append(new TextComponent(" Avoidance"))
               .setStyle(this.getColoredTextStyle());
      }

      @Nullable
      public MutableComponent getValueDisplay(EffectAvoidanceGearAttribute value) {
         return new TextComponent(EffectAvoidanceGearAttribute.FORMAT.format(value.getChance() * 100.0F) + "%");
      }

      @Override
      protected void serializeTextElements(JsonArray out, VaultGearAttributeInstance<EffectAvoidanceGearAttribute> instance, VaultGearModifier.AffixType type) {
         EffectAvoidanceGearAttribute effectAvoidance = instance.getValue();
         MutableComponent valueDisplay = this.getValueDisplay(effectAvoidance);
         if (valueDisplay != null) {
            out.add(type.getAffixPrefix(effectAvoidance.getChance() >= 0.0F));
            out.add(valueDisplay.getString());
            out.add(" ");
            out.add(effectAvoidance.getEffect().getDescriptionId());
            out.add(" Avoidance");
         }
      }
   }
}
