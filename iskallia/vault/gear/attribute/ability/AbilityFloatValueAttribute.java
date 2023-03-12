package iskallia.vault.gear.attribute.ability;

import com.google.gson.annotations.Expose;
import iskallia.vault.gear.attribute.config.ConfigurableAttributeGenerator;
import iskallia.vault.gear.attribute.type.VaultGearAttributeType;
import iskallia.vault.gear.reader.VaultGearModifierReader;
import iskallia.vault.util.NetcodeUtils;
import java.text.DecimalFormat;
import java.util.Random;
import java.util.function.BiFunction;
import java.util.function.Function;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;

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

   public static <T extends AbilityFloatValueAttribute, C extends AbilityFloatValueAttribute.Config> AbilityFloatValueAttribute.Generator<T, C> generator(
      Function<C, T> cfgConverter, Class<C> configClass
   ) {
      return new AbilityFloatValueAttribute.Generator<>(cfgConverter, configClass);
   }

   public static class Config extends AbilityGearAttribute.AbilityAttributeConfig {
      @Expose
      private float amount;

      public Config(String abilityKey, float amount) {
         super(abilityKey);
         this.amount = amount;
      }

      public float getAmount() {
         return this.amount;
      }
   }

   public static class Generator<T extends AbilityFloatValueAttribute, C extends AbilityFloatValueAttribute.Config>
      extends ConfigurableAttributeGenerator<T, C> {
      private final Function<C, T> cfgConverter;
      private final Class<C> configClass;

      protected Generator(BiFunction<String, Float, T> ctor, Class<C> configClass) {
         this(cfg -> ctor.apply(cfg.getAbilityKey(), cfg.getAmount()), configClass);
      }

      protected Generator(Function<C, T> cfgConverter, Class<C> configClass) {
         this.cfgConverter = cfgConverter;
         this.configClass = configClass;
      }

      @Nullable
      @Override
      public Class<C> getConfigurationObjectClass() {
         return this.configClass;
      }

      public T generateRandomValue(C object, Random random) {
         return this.cfgConverter.apply(object);
      }
   }

   public abstract static class Reader<T extends AbilityFloatValueAttribute> extends VaultGearModifierReader<T> {
      private static final DecimalFormat FORMAT = new DecimalFormat("0.#");

      protected Reader() {
         this(9020415);
      }

      protected Reader(int rgbColor) {
         super("", rgbColor);
      }

      protected String formatValue(float value) {
         return FORMAT.format(value);
      }
   }
}
