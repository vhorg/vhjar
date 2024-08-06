package iskallia.vault.gear.attribute.talent;

import com.google.gson.JsonArray;
import com.google.gson.annotations.Expose;
import iskallia.vault.core.vault.modifier.registry.VaultModifierRegistry;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.attribute.config.ConfigurableAttributeGenerator;
import iskallia.vault.gear.attribute.config.IntegerAttributeGenerator;
import iskallia.vault.gear.attribute.type.VaultGearAttributeType;
import iskallia.vault.gear.comparator.VaultGearAttributeComparator;
import iskallia.vault.gear.reader.VaultGearModifierReader;
import iskallia.vault.util.NetcodeUtils;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

public class RandomVaultModifierAttribute {
   protected final ResourceLocation modifier;
   protected final int count;
   protected final int time;

   public RandomVaultModifierAttribute(ResourceLocation modifier, int count, int time) {
      this.modifier = modifier;
      this.count = count;
      this.time = time;
   }

   public ResourceLocation getModifier() {
      return this.modifier;
   }

   public int getCount() {
      return this.count;
   }

   public int getTime() {
      return this.time;
   }

   public static VaultGearAttributeType<RandomVaultModifierAttribute> type() {
      return VaultGearAttributeType.of((buf, attribute) -> {
         buf.writeIdentifier(attribute.getModifier());
         buf.writeInt(attribute.getCount());
         buf.writeInt(attribute.getTime());
      }, buf -> new RandomVaultModifierAttribute(buf.readIdentifier(), buf.readInt(), buf.readInt()), (buf, attribute) -> {
         NetcodeUtils.writeIdentifier(buf, attribute.getModifier());
         buf.writeInt(attribute.getCount());
         buf.writeInt(attribute.getTime());
      }, buf -> {
         ResourceLocation modifier = NetcodeUtils.readIdentifier(buf);
         int count = buf.readInt();
         int time = buf.readInt();
         return new RandomVaultModifierAttribute(modifier, count, time);
      }, VaultGearAttributeType.GSON::toJsonTree, nbt -> {
         CompoundTag tag = (CompoundTag)nbt;
         ResourceLocation modifier = ResourceLocation.tryParse(tag.getString("modifier"));
         int count = tag.getInt("count");
         int time = tag.getInt("time");
         return new RandomVaultModifierAttribute(modifier, count, time);
      }, attribute -> {
         CompoundTag tag = new CompoundTag();
         tag.putString("modifier", attribute.getModifier().toString());
         tag.putInt("count", attribute.getCount());
         tag.putInt("time", attribute.getTime());
         return tag;
      });
   }

   public static ConfigurableAttributeGenerator<RandomVaultModifierAttribute, RandomVaultModifierAttribute.Config> generator() {
      return new ConfigurableAttributeGenerator<RandomVaultModifierAttribute, RandomVaultModifierAttribute.Config>() {
         @Override
         public Class<RandomVaultModifierAttribute.Config> getConfigurationObjectClass() {
            return RandomVaultModifierAttribute.Config.class;
         }

         public MutableComponent getConfigRangeDisplay(
            VaultGearModifierReader<RandomVaultModifierAttribute> reader, RandomVaultModifierAttribute.Config min, RandomVaultModifierAttribute.Config max
         ) {
            return new TextComponent("%s-%s".formatted(min.getTime().min / 20, max.getTime().max / 20));
         }

         public RandomVaultModifierAttribute generateRandomValue(RandomVaultModifierAttribute.Config object, Random random) {
            return new RandomVaultModifierAttribute(object.getModifier(), object.getCount(), object.getTime().generateNumber(random));
         }

         @Override
         public Optional<RandomVaultModifierAttribute> getMinimumValue(List<RandomVaultModifierAttribute.Config> configurations) {
            return configurations.stream()
               .min(Comparator.comparingInt(RandomVaultModifierAttribute.Config::getCount))
               .map(config -> new RandomVaultModifierAttribute(config.getModifier(), config.getCount(), config.getTime().min));
         }

         @Override
         public Optional<RandomVaultModifierAttribute> getMaximumValue(List<RandomVaultModifierAttribute.Config> configurations) {
            return configurations.stream()
               .max(Comparator.comparingInt(RandomVaultModifierAttribute.Config::getCount))
               .map(config -> new RandomVaultModifierAttribute(config.getModifier(), config.getCount(), config.getTime().generateMaximumNumber()));
         }
      };
   }

   public static VaultGearModifierReader<RandomVaultModifierAttribute> reader() {
      return new VaultGearModifierReader<RandomVaultModifierAttribute>("", 15638784) {
         @Nullable
         @Override
         public MutableComponent getDisplay(VaultGearAttributeInstance<RandomVaultModifierAttribute> instance, VaultGearModifier.AffixType type) {
            RandomVaultModifierAttribute attribute = instance.getValue();
            MutableComponent valueDisplay = this.getValueDisplay(attribute);
            VaultModifier<?> modifier = VaultModifierRegistry.<VaultModifier<?>>getOpt(attribute.getModifier()).orElse(null);
            return modifier == null
               ? null
               : new TextComponent("")
                  .append(type.getAffixPrefixComponent(attribute.getCount() >= 0).withStyle(this.getColoredTextStyle()))
                  .append(valueDisplay.withStyle(this.getColoredTextStyle()))
                  .append(" of ")
                  .append(attribute.getCount() + "x ")
                  .append(modifier.getNameComponentFormatted(attribute.getCount()))
                  .withStyle(this.getColoredTextStyle());
         }

         public MutableComponent getValueDisplay(RandomVaultModifierAttribute value) {
            return new TextComponent(value.getTime() / 20 + " seconds");
         }

         @Override
         protected void serializeTextElements(
            JsonArray out, VaultGearAttributeInstance<RandomVaultModifierAttribute> instance, VaultGearModifier.AffixType type
         ) {
            RandomVaultModifierAttribute attribute = instance.getValue();
            MutableComponent valueDisplay = this.getValueDisplay(attribute);
            VaultModifier<?> modifier = VaultModifierRegistry.<VaultModifier<?>>getOpt(attribute.getModifier()).orElse(null);
            if (modifier != null) {
               out.add(type.getAffixPrefix(attribute.getCount() >= 0));
               out.add(valueDisplay.getString());
               out.add(" of ");
               out.add(attribute.getCount() + "x ");
               out.add(modifier.getNameComponentFormatted(attribute.getCount()).getString());
            }
         }
      };
   }

   public static VaultGearAttributeComparator<RandomVaultModifierAttribute> comparator() {
      return new VaultGearAttributeComparator<RandomVaultModifierAttribute>() {
         public Optional<RandomVaultModifierAttribute> merge(RandomVaultModifierAttribute thisValue, RandomVaultModifierAttribute thatValue) {
            return thisValue.getModifier().equals(thatValue.getModifier()) && thisValue.getCount() == thatValue.getCount()
               ? Optional.of(new RandomVaultModifierAttribute(thisValue.getModifier(), thisValue.getCount(), thisValue.getTime() + thatValue.getTime()))
               : Optional.empty();
         }

         public Optional<RandomVaultModifierAttribute> difference(RandomVaultModifierAttribute thisValue, RandomVaultModifierAttribute thatValue) {
            return Optional.empty();
         }

         @NotNull
         @Override
         public Comparator<RandomVaultModifierAttribute> getComparator() {
            return Comparator.comparing(RandomVaultModifierAttribute::getTime);
         }
      };
   }

   public static class Config {
      @Expose
      private ResourceLocation modifier;
      @Expose
      private int count;
      @Expose
      private IntegerAttributeGenerator.Range time;

      public Config(ResourceLocation modifier, int count, IntegerAttributeGenerator.Range time) {
         this.modifier = modifier;
         this.count = count;
         this.time = time;
      }

      public ResourceLocation getModifier() {
         return this.modifier;
      }

      public int getCount() {
         return this.count;
      }

      public IntegerAttributeGenerator.Range getTime() {
         return this.time;
      }
   }
}
