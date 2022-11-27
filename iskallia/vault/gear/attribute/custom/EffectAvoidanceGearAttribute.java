package iskallia.vault.gear.attribute.custom;

import com.google.gson.JsonArray;
import com.google.gson.annotations.Expose;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.attribute.config.ConfigurableAttributeGenerator;
import iskallia.vault.gear.attribute.type.VaultGearAttributeType;
import iskallia.vault.gear.reader.VaultGearModifierReader;
import iskallia.vault.util.NetcodeUtils;
import java.text.DecimalFormat;
import java.util.Random;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

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

   public static EffectAvoidanceGearAttribute.Generator generator() {
      return new EffectAvoidanceGearAttribute.Generator();
   }

   public static EffectAvoidanceGearAttribute.Reader reader() {
      return new EffectAvoidanceGearAttribute.Reader();
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
   }

   private static class Generator extends ConfigurableAttributeGenerator<EffectAvoidanceGearAttribute, EffectAvoidanceGearAttribute.Config> {
      @Nullable
      @Override
      public Class<EffectAvoidanceGearAttribute.Config> getConfigurationObjectClass() {
         return EffectAvoidanceGearAttribute.Config.class;
      }

      public MutableComponent getConfigDisplay(VaultGearModifierReader<EffectAvoidanceGearAttribute> reader, EffectAvoidanceGearAttribute.Config object) {
         return this.getChanceDisplay(object.minChance).append("-").append(this.getChanceDisplay(object.maxChance));
      }

      private MutableComponent getChanceDisplay(float value) {
         return new TextComponent(EffectAvoidanceGearAttribute.FORMAT.format(value * 100.0F) + "%");
      }

      public EffectAvoidanceGearAttribute generateRandomValue(EffectAvoidanceGearAttribute.Config object, Random random) {
         MobEffect effect = (MobEffect)ForgeRegistries.MOB_EFFECTS.getValue(object.effectKey);
         int steps = Mth.floor(Math.max(object.maxChance - object.minChance, 0.0F) / object.step) + 1;
         return new EffectAvoidanceGearAttribute(effect, object.minChance + random.nextInt(steps) * object.step);
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
