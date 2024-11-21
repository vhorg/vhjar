package iskallia.vault.gear.attribute.custom.effect;

import com.google.common.collect.Iterables;
import com.google.gson.JsonArray;
import com.google.gson.annotations.Expose;
import io.netty.buffer.ByteBuf;
import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.entity.entity.EffectCloudEntity;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.attribute.config.ConfigurableAttributeGenerator;
import iskallia.vault.gear.attribute.type.VaultGearAttributeType;
import iskallia.vault.gear.reader.VaultGearModifierReader;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.util.NetcodeUtils;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.registries.ForgeRegistries;

public class EffectCloudAttribute {
   private final EffectCloudAttribute.EffectCloud effectCloud;

   public EffectCloudAttribute(EffectCloudAttribute.EffectCloud effectCloud) {
      this.effectCloud = effectCloud;
   }

   public void apply(EffectCloudEntity entity) {
      this.effectCloud.apply(entity);
   }

   public float getTriggerChance() {
      return this.effectCloud.getTriggerChance();
   }

   @Nullable
   public MobEffect getPrimaryEffect() {
      EffectCloudAttribute.AdditionalCloudEffect first = (EffectCloudAttribute.AdditionalCloudEffect)Iterables.getFirst(
         this.effectCloud.additionalEffects, null
      );
      return first == null ? null : first.effect;
   }

   public static VaultGearAttributeType<EffectCloudAttribute> type() {
      return VaultGearAttributeType.of(
         (buf, effect) -> effect.effectCloud.write(buf),
         buf -> new EffectCloudAttribute(EffectCloudAttribute.EffectCloud.read(buf)),
         (buf, effect) -> effect.effectCloud.netWrite(buf),
         buf -> new EffectCloudAttribute(EffectCloudAttribute.EffectCloud.netRead(buf)),
         VaultGearAttributeType.GSON::toJsonTree,
         EffectCloudAttribute::read,
         EffectCloudAttribute::write
      );
   }

   private static EffectCloudAttribute read(Tag nbt) {
      CompoundTag tag = (CompoundTag)nbt;
      EffectCloudAttribute.EffectCloud cloud = new EffectCloudAttribute.EffectCloud();
      cloud.tooltip = tag.getString("tooltip");
      cloud.duration = tag.getInt("duration");
      cloud.radius = tag.getFloat("radius");
      cloud.color = new Color(tag.getInt("color"), true);
      cloud.affectsOwner = tag.getBoolean("affectsOwner");
      cloud.triggerChance = tag.getFloat("triggerChance");
      ListTag effects = tag.getList("effects", 10);

      for (int i = 0; i < effects.size(); i++) {
         CompoundTag effectTag = effects.getCompound(i);
         EffectCloudAttribute.AdditionalCloudEffect effect = new EffectCloudAttribute.AdditionalCloudEffect();
         effect.effect = (MobEffect)ForgeRegistries.MOB_EFFECTS.getValue(new ResourceLocation(effectTag.getString("effect")));
         effect.amplifier = effectTag.getInt("amplifier");
         effect.duration = effectTag.getInt("duration");
         cloud.additionalEffects.add(effect);
      }

      return new EffectCloudAttribute(cloud);
   }

   private static Tag write(EffectCloudAttribute attribute) {
      EffectCloudAttribute.EffectCloud cloud = attribute.effectCloud;
      CompoundTag tag = new CompoundTag();
      tag.putString("tooltip", cloud.tooltip);
      tag.putInt("duration", cloud.duration);
      tag.putFloat("radius", cloud.radius);
      tag.putInt("color", cloud.color.getRGB());
      tag.putBoolean("affectsOwner", cloud.affectsOwner);
      tag.putFloat("triggerChance", cloud.triggerChance);
      ListTag effects = new ListTag();
      cloud.additionalEffects.forEach(effect -> {
         CompoundTag effectTag = new CompoundTag();
         effectTag.putString("effect", effect.effect.getRegistryName().toString());
         effectTag.putInt("amplifier", effect.amplifier);
         effectTag.putInt("duration", effect.duration);
         effects.add(effectTag);
      });
      tag.put("effects", effects);
      return tag;
   }

   public static EffectCloudAttribute.Generator generator() {
      return new EffectCloudAttribute.Generator();
   }

   public static EffectCloudAttribute.Reader reader(boolean whenHit) {
      return new EffectCloudAttribute.Reader(whenHit);
   }

   public static class AdditionalCloudEffect {
      private MobEffect effect;
      private int duration;
      private int amplifier;

      private AdditionalCloudEffect() {
      }

      public AdditionalCloudEffect(MobEffect effect, int duration, int amplifier) {
         this.effect = effect;
         this.duration = duration;
         this.amplifier = amplifier;
      }

      private static EffectCloudAttribute.AdditionalCloudEffect fromConfig(EffectCloudAttribute.CloudEffectConfig config) {
         return new EffectCloudAttribute.AdditionalCloudEffect(
            (MobEffect)ForgeRegistries.MOB_EFFECTS.getValue(config.effect), config.duration, config.amplifier
         );
      }

      public MobEffectInstance makeEffect() {
         return new MobEffectInstance(this.effect, this.duration, this.amplifier, false, false, true);
      }

      public void write(BitBuffer buffer) {
         buffer.writeIdentifier(this.effect.getRegistryName());
         buffer.writeInt(this.duration);
         buffer.writeInt(this.amplifier);
      }

      private static EffectCloudAttribute.AdditionalCloudEffect read(BitBuffer buffer) {
         EffectCloudAttribute.AdditionalCloudEffect effect = new EffectCloudAttribute.AdditionalCloudEffect();
         effect.effect = (MobEffect)ForgeRegistries.MOB_EFFECTS.getValue(buffer.readIdentifier());
         effect.duration = buffer.readInt();
         effect.amplifier = buffer.readInt();
         return effect;
      }

      public void netWrite(ByteBuf buffer) {
         NetcodeUtils.writeIdentifier(buffer, this.effect.getRegistryName());
         buffer.writeInt(this.duration);
         buffer.writeInt(this.amplifier);
      }

      private static EffectCloudAttribute.AdditionalCloudEffect netRead(ByteBuf buffer) {
         EffectCloudAttribute.AdditionalCloudEffect effect = new EffectCloudAttribute.AdditionalCloudEffect();
         effect.effect = (MobEffect)ForgeRegistries.MOB_EFFECTS.getValue(NetcodeUtils.readIdentifier(buffer));
         effect.duration = buffer.readInt();
         effect.amplifier = buffer.readInt();
         return effect;
      }
   }

   public static class CloudConfig {
      @Expose
      private String tooltipDisplayName;
      @Expose
      private ResourceLocation potion;
      @Expose
      private List<EffectCloudAttribute.CloudEffectConfig> additionalEffects = new ArrayList<>();
      @Expose
      private int duration;
      @Expose
      private float radius;
      @Expose
      private int color;
      @Expose
      private boolean affectsOwner;
      @Expose
      private float triggerChance;

      public CloudConfig(String tooltipDisplayName, ResourceLocation potion, int duration, float radius, int color, boolean affectsOwner, float triggerChance) {
         this.tooltipDisplayName = tooltipDisplayName;
         this.potion = potion;
         this.duration = duration;
         this.radius = radius;
         this.color = color;
         this.affectsOwner = affectsOwner;
         this.triggerChance = triggerChance;
      }

      public void setAdditionalEffect(EffectCloudAttribute.CloudEffectConfig config) {
         this.additionalEffects.add(config);
      }
   }

   public static class CloudEffectConfig {
      @Expose
      private ResourceLocation effect;
      @Expose
      private int duration;
      @Expose
      private int amplifier;

      public CloudEffectConfig(ResourceLocation effect, int duration, int amplifier) {
         this.effect = effect;
         this.duration = duration;
         this.amplifier = amplifier;
      }
   }

   public static class EffectCloud {
      private String tooltip;
      private List<EffectCloudAttribute.AdditionalCloudEffect> additionalEffects = new ArrayList<>();
      private int duration;
      private float radius;
      private Color color;
      private boolean affectsOwner;
      private float triggerChance;

      private EffectCloud() {
      }

      public EffectCloud(String tooltip, int duration, float radius, Color color, boolean affectsOwner, float triggerChance) {
         this.tooltip = tooltip;
         this.duration = duration;
         this.radius = radius;
         this.color = color;
         this.affectsOwner = affectsOwner;
         this.triggerChance = triggerChance;
      }

      public static EffectCloudAttribute.EffectCloud fromConfig(EffectCloudAttribute.CloudConfig config) {
         EffectCloudAttribute.EffectCloud cloud = new EffectCloudAttribute.EffectCloud(
            config.tooltipDisplayName, config.duration, config.radius, new Color(config.color, true), config.affectsOwner, config.triggerChance
         );

         for (EffectCloudAttribute.CloudEffectConfig effectConfig : config.additionalEffects) {
            cloud.addEffect(EffectCloudAttribute.AdditionalCloudEffect.fromConfig(effectConfig));
         }

         return cloud;
      }

      public EffectCloudAttribute.EffectCloud addEffect(EffectCloudAttribute.AdditionalCloudEffect additionalEffect) {
         this.additionalEffects.add(additionalEffect);
         return this;
      }

      public float getTriggerChance() {
         return this.triggerChance;
      }

      public void apply(EffectCloudEntity entity) {
         entity.setDuration(this.duration);
         entity.setRadius(this.radius);
         entity.setColor(this.color.getRGB());
         entity.setAffectsOwner(this.affectsOwner);
         this.additionalEffects.forEach(additionalCloudEffect -> entity.addEffect(additionalCloudEffect.makeEffect()));
      }

      private void write(BitBuffer buffer) {
         buffer.writeString(this.tooltip);
         buffer.writeInt(this.duration);
         buffer.writeFloat(this.radius);
         buffer.writeInt(this.color.getRGB());
         buffer.writeBoolean(this.affectsOwner);
         buffer.writeFloat(this.triggerChance);
         buffer.writeCollection(this.additionalEffects, EffectCloudAttribute.AdditionalCloudEffect::write);
      }

      private static EffectCloudAttribute.EffectCloud read(BitBuffer buffer) {
         EffectCloudAttribute.EffectCloud cloud = new EffectCloudAttribute.EffectCloud();
         cloud.tooltip = buffer.readString();
         cloud.duration = buffer.readInt();
         cloud.radius = buffer.readFloat();
         cloud.color = new Color(buffer.readInt(), true);
         cloud.affectsOwner = buffer.readBoolean();
         cloud.triggerChance = buffer.readFloat();
         cloud.additionalEffects = buffer.readCollection(ArrayList::new, EffectCloudAttribute.AdditionalCloudEffect::read);
         return cloud;
      }

      private void netWrite(ByteBuf buffer) {
         NetcodeUtils.writeString(buffer, this.tooltip);
         buffer.writeInt(this.duration);
         buffer.writeFloat(this.radius);
         buffer.writeInt(this.color.getRGB());
         buffer.writeBoolean(this.affectsOwner);
         buffer.writeFloat(this.triggerChance);
         NetcodeUtils.writeCollection(buffer, this.additionalEffects, EffectCloudAttribute.AdditionalCloudEffect::netWrite);
      }

      private static EffectCloudAttribute.EffectCloud netRead(ByteBuf buffer) {
         EffectCloudAttribute.EffectCloud cloud = new EffectCloudAttribute.EffectCloud();
         cloud.tooltip = NetcodeUtils.readString(buffer);
         cloud.duration = buffer.readInt();
         cloud.radius = buffer.readFloat();
         cloud.color = new Color(buffer.readInt(), true);
         cloud.affectsOwner = buffer.readBoolean();
         cloud.triggerChance = buffer.readFloat();
         cloud.additionalEffects = NetcodeUtils.readCollection(buffer, ArrayList::new, EffectCloudAttribute.AdditionalCloudEffect::netRead);
         return cloud;
      }
   }

   private static class Generator extends ConfigurableAttributeGenerator<EffectCloudAttribute, EffectCloudAttribute.CloudConfig> {
      @Nullable
      @Override
      public Class<EffectCloudAttribute.CloudConfig> getConfigurationObjectClass() {
         return EffectCloudAttribute.CloudConfig.class;
      }

      public EffectCloudAttribute generateRandomValue(EffectCloudAttribute.CloudConfig object, Random random) {
         return new EffectCloudAttribute(EffectCloudAttribute.EffectCloud.fromConfig(object));
      }

      @Nullable
      public MutableComponent getConfigDisplay(VaultGearModifierReader<EffectCloudAttribute> reader, EffectCloudAttribute.CloudConfig object) {
         return reader.getValueDisplay(new EffectCloudAttribute(EffectCloudAttribute.EffectCloud.fromConfig(object)));
      }

      @Override
      public Optional<EffectCloudAttribute> getMinimumValue(List<EffectCloudAttribute.CloudConfig> configurations) {
         return configurations.stream()
            .min(Comparator.comparing(config -> config.additionalEffects.stream().mapToInt(effect -> effect.amplifier).sum()))
            .map(EffectCloudAttribute.EffectCloud::fromConfig)
            .map(EffectCloudAttribute::new);
      }

      @Override
      public Optional<EffectCloudAttribute> getMaximumValue(List<EffectCloudAttribute.CloudConfig> configurations) {
         return configurations.stream()
            .max(Comparator.comparing(config -> config.additionalEffects.stream().mapToInt(effect -> effect.amplifier).sum()))
            .map(EffectCloudAttribute.EffectCloud::fromConfig)
            .map(EffectCloudAttribute::new);
      }

      public Optional<Float> getRollPercentage(EffectCloudAttribute value, List<EffectCloudAttribute.CloudConfig> configurations) {
         return MiscUtils.getIntValueRange(
            this.getAmplifierSum(value), this.getMinimumValue(configurations), this.getMaximumValue(configurations), this::getAmplifierSum
         );
      }

      private int getAmplifierSum(EffectCloudAttribute attribute) {
         return attribute.effectCloud.additionalEffects.stream().mapToInt(effect -> effect.amplifier).sum();
      }
   }

   private static class Reader extends VaultGearModifierReader<EffectCloudAttribute> {
      private final boolean isWhenHit;

      protected Reader(boolean isWhenHit) {
         super(isWhenHit ? "Effect Cloud when Hit" : "Effect Cloud", 15007916);
         this.isWhenHit = isWhenHit;
      }

      @Nullable
      @Override
      public MutableComponent getDisplay(VaultGearAttributeInstance<EffectCloudAttribute> instance, VaultGearModifier.AffixType type) {
         MutableComponent valueDisplay = this.getValueDisplay(instance.getValue());
         return valueDisplay == null ? null : new TextComponent(type.getAffixPrefix(true)).append(valueDisplay).setStyle(this.getColoredTextStyle());
      }

      @Nullable
      public MutableComponent getValueDisplay(EffectCloudAttribute value) {
         return new TextComponent(value.effectCloud.tooltip + " Cloud" + (this.isWhenHit ? " when Hit" : ""));
      }

      @Override
      protected void serializeTextElements(JsonArray out, VaultGearAttributeInstance<EffectCloudAttribute> instance, VaultGearModifier.AffixType type) {
         MutableComponent valueDisplay = this.getValueDisplay(instance.getValue());
         if (valueDisplay != null) {
            out.add(type.getAffixPrefix(true));
            out.add(valueDisplay.getString());
         }
      }
   }
}
