package iskallia.vault.gear.attribute.custom.effect;

import com.google.gson.JsonArray;
import com.google.gson.annotations.Expose;
import io.netty.buffer.ByteBuf;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.attribute.config.ConfigurableAttributeGenerator;
import iskallia.vault.gear.attribute.type.VaultGearAttributeType;
import iskallia.vault.gear.comparator.VaultGearAttributeComparator;
import iskallia.vault.gear.reader.VaultGearModifierReader;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.util.NetcodeUtils;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.ForgeRegistryEntry;

public class EffectAvoidanceListGearAttribute implements IEffectAvoidanceChanceAttribute {
   private static final DecimalFormat FORMAT = new DecimalFormat("0.##");
   private final List<MobEffect> effects;
   private final String name;
   private final float chance;

   public EffectAvoidanceListGearAttribute(List<MobEffect> effects, String name, float chance) {
      this.effects = effects;
      this.name = name;
      this.chance = chance;
   }

   @Override
   public List<MobEffect> getEffects() {
      return Collections.unmodifiableList(this.effects);
   }

   @Override
   public float getChance() {
      return this.chance;
   }

   @Override
   public String toString() {
      return "EffectAvoidanceListGearAttribute{effects=" + this.effects + ", name='" + this.name + "', chance=" + this.chance + "}";
   }

   public static VaultGearAttributeType<EffectAvoidanceListGearAttribute> type() {
      return VaultGearAttributeType.of(
         EffectAvoidanceListGearAttribute::write,
         EffectAvoidanceListGearAttribute::read,
         EffectAvoidanceListGearAttribute::write,
         EffectAvoidanceListGearAttribute::read,
         VaultGearAttributeType.GSON::toJsonTree,
         EffectAvoidanceListGearAttribute::read,
         EffectAvoidanceListGearAttribute::write
      );
   }

   private static EffectAvoidanceListGearAttribute read(ByteBuf buf) {
      int size = buf.readInt();
      List<MobEffect> effects = new ArrayList<>(size);

      for (int i = 0; i < size; i++) {
         effects.add((MobEffect)ForgeRegistries.MOB_EFFECTS.getValue(NetcodeUtils.readIdentifier(buf)));
      }

      effects.removeIf(Objects::isNull);
      String name = NetcodeUtils.readString(buf);
      float chance = buf.readFloat();
      return new EffectAvoidanceListGearAttribute(effects, name, chance);
   }

   private static void write(ByteBuf buf, EffectAvoidanceListGearAttribute attribute) {
      buf.writeInt(attribute.effects.size());

      for (MobEffect effect : attribute.effects) {
         NetcodeUtils.writeIdentifier(buf, effect.getRegistryName());
      }

      NetcodeUtils.writeString(buf, attribute.name);
      buf.writeFloat(attribute.chance);
   }

   private static EffectAvoidanceListGearAttribute read(BitBuffer buf) {
      int size = buf.readInt();
      List<MobEffect> effects = new ArrayList<>(size);

      for (int i = 0; i < size; i++) {
         effects.add((MobEffect)ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(buf.readString())));
      }

      effects.removeIf(Objects::isNull);
      String name = buf.readString();
      float chance = buf.readFloat();
      return new EffectAvoidanceListGearAttribute(effects, name, chance);
   }

   private static void write(BitBuffer buf, EffectAvoidanceListGearAttribute attribute) {
      buf.writeInt(attribute.effects.size());

      for (MobEffect effect : attribute.effects) {
         buf.writeString(effect.getRegistryName().toString());
      }

      buf.writeString(attribute.name);
      buf.writeFloat(attribute.chance);
   }

   private static EffectAvoidanceListGearAttribute read(Tag nbt) {
      CompoundTag tag = (CompoundTag)nbt;
      List<MobEffect> effects = new ArrayList<>();

      for (Tag effectTag : tag.getList("effects", 8)) {
         effects.add((MobEffect)ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(effectTag.getAsString())));
      }

      String name = tag.getString("name");
      float chance = tag.getFloat("chance");
      return new EffectAvoidanceListGearAttribute(effects, name, chance);
   }

   private static Tag write(EffectAvoidanceListGearAttribute attribute) {
      CompoundTag tag = new CompoundTag();
      ListTag effectList = new ListTag();

      for (MobEffect effect : attribute.getEffects()) {
         effectList.add(StringTag.valueOf(effect.getRegistryName().toString()));
      }

      tag.put("effects", effectList);
      tag.putString("name", attribute.name);
      tag.putFloat("chance", attribute.getChance());
      return tag;
   }

   public static EffectAvoidanceListGearAttribute.AttributeComparator comparator() {
      return new EffectAvoidanceListGearAttribute.AttributeComparator();
   }

   public static EffectAvoidanceListGearAttribute.Generator generator() {
      return new EffectAvoidanceListGearAttribute.Generator();
   }

   public static EffectAvoidanceListGearAttribute.Reader reader() {
      return new EffectAvoidanceListGearAttribute.Reader();
   }

   private static class AttributeComparator extends VaultGearAttributeComparator<EffectAvoidanceListGearAttribute> {
      public Optional<EffectAvoidanceListGearAttribute> merge(EffectAvoidanceListGearAttribute thisValue, EffectAvoidanceListGearAttribute thatValue) {
         Set<MobEffect> thisSet = new HashSet<>(thisValue.getEffects());
         Set<MobEffect> thatSet = new HashSet<>(thatValue.getEffects());
         if (!thisSet.containsAll(thatSet) && !thatSet.containsAll(thisSet)) {
            return Optional.empty();
         } else {
            return !thisValue.name.equals(thatValue.name)
               ? Optional.empty()
               : Optional.of(new EffectAvoidanceListGearAttribute(thisValue.getEffects(), thisValue.name, thisValue.getChance() + thatValue.getChance()));
         }
      }

      @Deprecated
      public Optional<EffectAvoidanceListGearAttribute> difference(EffectAvoidanceListGearAttribute thisValue, EffectAvoidanceListGearAttribute thatValue) {
         return Optional.empty();
      }

      @Nonnull
      @Override
      public Comparator<EffectAvoidanceListGearAttribute> getComparator() {
         return Comparator.comparing(EffectAvoidanceListGearAttribute::getChance);
      }
   }

   public static class Config {
      @Expose
      private final List<ResourceLocation> effectKeys;
      @Expose
      private final String name;
      @Expose
      private final float minChance;
      @Expose
      private final float maxChance;
      @Expose
      private final float step;

      public Config(List<MobEffect> effects, String name, float minChance, float maxChance) {
         this(keys(effects), name, minChance, maxChance, 0.05F);
      }

      public Config(List<ResourceLocation> effectKeys, String name, float minChance, float maxChance, float step) {
         this.effectKeys = effectKeys;
         this.name = name;
         this.minChance = minChance;
         this.maxChance = maxChance;
         this.step = step;
      }

      private static List<ResourceLocation> keys(List<MobEffect> effects) {
         return effects.stream().<ResourceLocation>map(ForgeRegistryEntry::getRegistryName).collect(Collectors.toList());
      }

      private List<MobEffect> getEffects() {
         return this.effectKeys.stream().<MobEffect>map(ForgeRegistries.MOB_EFFECTS::getValue).filter(Objects::nonNull).collect(Collectors.toList());
      }
   }

   private static class Generator extends ConfigurableAttributeGenerator<EffectAvoidanceListGearAttribute, EffectAvoidanceListGearAttribute.Config> {
      @Nullable
      @Override
      public Class<EffectAvoidanceListGearAttribute.Config> getConfigurationObjectClass() {
         return EffectAvoidanceListGearAttribute.Config.class;
      }

      @Nullable
      public MutableComponent getConfigRangeDisplay(
         VaultGearModifierReader<EffectAvoidanceListGearAttribute> reader,
         EffectAvoidanceListGearAttribute.Config min,
         EffectAvoidanceListGearAttribute.Config max
      ) {
         return this.getChanceDisplay(min.minChance).append("-").append(this.getChanceDisplay(max.maxChance));
      }

      private MutableComponent getChanceDisplay(float value) {
         return new TextComponent(EffectAvoidanceListGearAttribute.FORMAT.format(value * 100.0F) + "%");
      }

      @Nullable
      public MutableComponent getConfigDisplay(VaultGearModifierReader<EffectAvoidanceListGearAttribute> reader, EffectAvoidanceListGearAttribute.Config object) {
         MutableComponent range = this.getConfigRangeDisplay(reader, object);
         return new TextComponent("")
            .withStyle(reader.getColoredTextStyle())
            .append(range.withStyle(reader.getColoredTextStyle()))
            .append(" ")
            .append(new TranslatableComponent("the_vault.gear_attribute.effect_avoidance.avoidance", new Object[]{new TranslatableComponent(object.name)}));
      }

      public EffectAvoidanceListGearAttribute generateRandomValue(EffectAvoidanceListGearAttribute.Config config, Random random) {
         int steps = Math.round(Math.max(config.maxChance - config.minChance, 0.0F) / config.step) + 1;
         return new EffectAvoidanceListGearAttribute(config.getEffects(), config.name, config.minChance + random.nextInt(steps) * config.step);
      }

      @Override
      public Optional<EffectAvoidanceListGearAttribute> getMinimumValue(List<EffectAvoidanceListGearAttribute.Config> configurations) {
         return configurations.stream()
            .min(Comparator.comparing(config -> config.minChance))
            .map(config -> new EffectAvoidanceListGearAttribute(config.getEffects(), config.name, config.minChance));
      }

      private float getMaximumChance(EffectAvoidanceListGearAttribute.Config config) {
         int steps = Math.round(Math.max(config.maxChance - config.minChance, 0.0F) / config.step);
         return config.minChance + steps * config.step;
      }

      @Override
      public Optional<EffectAvoidanceListGearAttribute> getMaximumValue(List<EffectAvoidanceListGearAttribute.Config> configurations) {
         return configurations.stream()
            .max(Comparator.comparing(this::getMaximumChance))
            .map(config -> new EffectAvoidanceListGearAttribute(config.getEffects(), config.name, config.maxChance));
      }

      public Optional<Float> getRollPercentage(EffectAvoidanceListGearAttribute value, List<EffectAvoidanceListGearAttribute.Config> configurations) {
         return MiscUtils.getFloatValueRange(
            value.getChance(), this.getMinimumValue(configurations), this.getMaximumValue(configurations), EffectAvoidanceListGearAttribute::getChance
         );
      }
   }

   private static class Reader extends VaultGearModifierReader<EffectAvoidanceListGearAttribute> {
      protected Reader() {
         super("", 9561049);
      }

      @Nullable
      @Override
      public MutableComponent getDisplay(VaultGearAttributeInstance<EffectAvoidanceListGearAttribute> instance, VaultGearModifier.AffixType type) {
         EffectAvoidanceListGearAttribute effectAvoidance = instance.getValue();
         MutableComponent valueDisplay = this.getValueDisplay(effectAvoidance);
         return valueDisplay == null
            ? null
            : new TextComponent(type.getAffixPrefix(effectAvoidance.getChance() >= 0.0F))
               .append(valueDisplay)
               .append(" ")
               .append(
                  new TranslatableComponent(
                     "the_vault.gear_attribute.effect_avoidance.avoidance", new Object[]{new TranslatableComponent(effectAvoidance.name)}
                  )
               )
               .setStyle(this.getColoredTextStyle());
      }

      @Nullable
      public MutableComponent getValueDisplay(EffectAvoidanceListGearAttribute value) {
         return new TextComponent(EffectAvoidanceListGearAttribute.FORMAT.format(value.getChance() * 100.0F) + "%");
      }

      @Override
      protected void serializeTextElements(
         JsonArray out, VaultGearAttributeInstance<EffectAvoidanceListGearAttribute> instance, VaultGearModifier.AffixType type
      ) {
         EffectAvoidanceListGearAttribute effectAvoidance = instance.getValue();
         MutableComponent valueDisplay = this.getValueDisplay(effectAvoidance);
         if (valueDisplay != null) {
            out.add(type.getAffixPrefix(effectAvoidance.getChance() >= 0.0F));
            out.add(valueDisplay.getString());
            out.add(" ");
            out.add(effectAvoidance.name);
            out.add(" Avoidance");
         }
      }
   }
}
