package iskallia.vault.gear.attribute.ability;

import com.google.gson.annotations.Expose;
import iskallia.vault.gear.attribute.config.ConfigurableAttributeGenerator;
import iskallia.vault.gear.attribute.type.VaultGearAttributeType;
import iskallia.vault.gear.reader.VaultGearModifierReader;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.tree.AbilityTree;
import iskallia.vault.util.MiscUtils;
import iskallia.vault.util.NetcodeUtils;
import java.text.DecimalFormat;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextComponent;

public abstract class AbilityFloatValueAttribute extends AbilityGearAttribute {
   protected final float amount;

   public AbilityFloatValueAttribute(String abilityKey, float amount) {
      super(abilityKey);
      this.amount = amount;
   }

   public float getAmount() {
      return this.amount;
   }

   public static <T extends AbilityFloatValueAttribute> VaultGearAttributeType<T> type(BiFunction<String, Float, T> ctor) {
      return VaultGearAttributeType.of((buf, attribute) -> {
         buf.writeString(attribute.getAbilityKey());
         buf.writeFloat(attribute.getAmount());
      }, buf -> ctor.apply(buf.readString(), buf.readFloat()), (buf, attribute) -> {
         NetcodeUtils.writeString(buf, attribute.getAbilityKey());
         buf.writeFloat(attribute.getAmount());
      }, buf -> {
         String abilityKey = NetcodeUtils.readString(buf);
         float amount = buf.readFloat();
         return ctor.apply(abilityKey, amount);
      }, VaultGearAttributeType.GSON::toJsonTree, read(ctor), write());
   }

   private static <T extends AbilityFloatValueAttribute> Function<Tag, T> read(BiFunction<String, Float, T> ctor) {
      return nbt -> {
         CompoundTag tag = (CompoundTag)nbt;
         String ability = tag.getString("ability");
         float amount = tag.getFloat("amount");
         return ctor.apply(ability, amount);
      };
   }

   private static <T extends AbilityFloatValueAttribute> Function<T, Tag> write() {
      return attribute -> {
         CompoundTag tag = new CompoundTag();
         tag.putString("ability", attribute.getAbilityKey());
         tag.putFloat("amount", attribute.getAmount());
         return tag;
      };
   }

   public static <T extends AbilityFloatValueAttribute, C extends AbilityFloatValueAttribute.Config> AbilityFloatValueAttribute.Generator<T, C> generator(
      BiFunction<String, Float, T> ctor, Class<C> configClass
   ) {
      return new AbilityFloatValueAttribute.Generator<>(ctor, configClass);
   }

   public static class Config extends AbilityGearAttribute.AbilityAttributeConfig {
      @Expose
      private float min;
      @Expose
      private float max;
      @Expose
      private float step;

      public Config(String abilityKey, float min, float max, float step) {
         super(abilityKey);
         this.min = min;
         this.max = max;
         this.step = step;
      }

      public float getMin() {
         return this.min;
      }

      public float generateValue(Random rand) {
         int steps = Math.round(Math.max(this.max - this.min, 0.0F) / this.step) + 1;
         return this.min + rand.nextInt(steps) * this.step;
      }

      public float generateMaximumValue() {
         int steps = Math.round(Math.max(this.max - this.min, 0.0F) / this.step);
         return this.min + steps * this.step;
      }
   }

   protected record ConfigRoll(AbilityFloatValueAttribute.Config cfg, Float value) {
   }

   public static class Generator<T extends AbilityFloatValueAttribute, C extends AbilityFloatValueAttribute.Config>
      extends ConfigurableAttributeGenerator<T, C> {
      private final Function<AbilityFloatValueAttribute.ConfigRoll, T> valueConverter;
      private final Class<C> configClass;

      protected Generator(BiFunction<String, Float, T> valueConverter, Class<C> configClass) {
         this(cfg -> valueConverter.apply(cfg.cfg().getAbilityKey(), cfg.value()), configClass);
      }

      protected Generator(Function<AbilityFloatValueAttribute.ConfigRoll, T> valueConverter, Class<C> configClass) {
         this.valueConverter = valueConverter;
         this.configClass = configClass;
      }

      @Nullable
      @Override
      public Class<C> getConfigurationObjectClass() {
         return this.configClass;
      }

      public T generateRandomValue(C object, Random random) {
         return this.valueConverter.apply(new AbilityFloatValueAttribute.ConfigRoll(object, object.generateValue(random)));
      }

      @Override
      public Optional<T> getMinimumValue(List<C> configurations) {
         return configurations.stream()
            .map(cfg -> new AbilityFloatValueAttribute.ConfigRoll(cfg, cfg.getMin()))
            .min(Comparator.comparing(AbilityFloatValueAttribute.ConfigRoll::value))
            .map(this.valueConverter);
      }

      @Override
      public Optional<T> getMaximumValue(List<C> configurations) {
         return configurations.stream()
            .map(cfg -> new AbilityFloatValueAttribute.ConfigRoll(cfg, cfg.generateMaximumValue()))
            .min(Comparator.comparing(AbilityFloatValueAttribute.ConfigRoll::value))
            .map(this.valueConverter);
      }

      public Optional<Float> getRollPercentage(T value, List<C> configurations) {
         return MiscUtils.getFloatValueRange(
            value.getAmount(), this.getMinimumValue(configurations), this.getMaximumValue(configurations), AbilityFloatValueAttribute::getAmount
         );
      }

      @Nullable
      public MutableComponent getConfigRangeDisplay(VaultGearModifierReader<T> reader, C min, C max) {
         MutableComponent minDisplay = reader.getValueDisplay(this.valueConverter.apply(new AbilityFloatValueAttribute.ConfigRoll(min, min.getMin())));
         MutableComponent maxDisplay = reader.getValueDisplay(
            this.valueConverter.apply(new AbilityFloatValueAttribute.ConfigRoll(min, min.generateMaximumValue()))
         );
         return (MutableComponent)(minDisplay != null && maxDisplay != null ? minDisplay.append("-").append(maxDisplay) : new TextComponent(""));
      }
   }

   public abstract static class Reader<T extends AbilityFloatValueAttribute> extends VaultGearModifierReader<T> {
      private static final DecimalFormat FORMAT = new DecimalFormat("0.#");

      protected Reader() {
         this(11842740);
      }

      protected Reader(int rgbColor) {
         super("", rgbColor);
      }

      protected Style getAbilityStyle() {
         return Style.EMPTY.withColor(14076214);
      }

      protected Style getValueStyle() {
         return Style.EMPTY.withColor(6082075);
      }

      protected String formatValue(float value) {
         return FORMAT.format(value);
      }

      protected MutableComponent formatAbilityName(String abilityKey) {
         return ModConfigs.ABILITIES.getAbilityById(abilityKey).filter(skill -> skill.getName() != null).map(skill -> {
            String name = skill.getName();
            if (skill.getParent() instanceof AbilityTree) {
               name = String.format("all %s skills", name);
            }

            return new TextComponent(name).withStyle(this.getAbilityStyle());
         }).orElseGet(() -> new TextComponent(abilityKey).withStyle(this.getAbilityStyle()));
      }
   }
}
