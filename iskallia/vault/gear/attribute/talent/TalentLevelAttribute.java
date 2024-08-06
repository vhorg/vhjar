package iskallia.vault.gear.attribute.talent;

import com.google.gson.JsonArray;
import com.google.gson.annotations.Expose;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.attribute.ability.special.base.SpecialAbilityModification;
import iskallia.vault.gear.attribute.config.ConfigurableAttributeGenerator;
import iskallia.vault.gear.attribute.type.VaultGearAttributeType;
import iskallia.vault.gear.comparator.VaultGearAttributeComparator;
import iskallia.vault.gear.reader.VaultGearModifierReader;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.util.NetcodeUtils;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import org.jetbrains.annotations.NotNull;

public class TalentLevelAttribute {
   public static final String ALL_TALENTS = "all_talents";
   protected final String talent;
   protected final int levelChange;

   public TalentLevelAttribute(String talent, int levelChange) {
      this.talent = talent;
      this.levelChange = levelChange;
   }

   public String getTalent() {
      return this.talent;
   }

   public int getLevelChange() {
      return this.levelChange;
   }

   public static VaultGearAttributeType<TalentLevelAttribute> type() {
      return VaultGearAttributeType.of((buf, attribute) -> {
         buf.writeString(attribute.getTalent());
         buf.writeInt(attribute.getLevelChange());
      }, buf -> new TalentLevelAttribute(buf.readString(), buf.readInt()), (buf, attribute) -> {
         NetcodeUtils.writeString(buf, attribute.getTalent());
         buf.writeInt(attribute.getLevelChange());
      }, buf -> {
         String talent = NetcodeUtils.readString(buf);
         int levelChange = buf.readInt();
         return new TalentLevelAttribute(talent, levelChange);
      }, VaultGearAttributeType.GSON::toJsonTree, nbt -> {
         CompoundTag tag = (CompoundTag)nbt;
         String talent = tag.getString("talent");
         int levelChange = tag.getInt("levelChange");
         return new TalentLevelAttribute(talent, levelChange);
      }, attribute -> {
         CompoundTag tag = new CompoundTag();
         tag.putString("talent", attribute.getTalent());
         tag.putInt("levelChange", attribute.getLevelChange());
         return tag;
      });
   }

   public static ConfigurableAttributeGenerator<TalentLevelAttribute, TalentLevelAttribute.Config> generator() {
      return new ConfigurableAttributeGenerator<TalentLevelAttribute, TalentLevelAttribute.Config>() {
         @Override
         public Class<TalentLevelAttribute.Config> getConfigurationObjectClass() {
            return TalentLevelAttribute.Config.class;
         }

         public MutableComponent getConfigRangeDisplay(
            VaultGearModifierReader<TalentLevelAttribute> reader, TalentLevelAttribute.Config min, TalentLevelAttribute.Config max
         ) {
            return new TextComponent("%s-%s".formatted(min.getLevelChange(), max.getLevelChange()));
         }

         public TalentLevelAttribute generateRandomValue(TalentLevelAttribute.Config object, Random random) {
            return new TalentLevelAttribute(object.getTalent(), object.getLevelChange());
         }

         @Override
         public Optional<TalentLevelAttribute> getMinimumValue(List<TalentLevelAttribute.Config> configurations) {
            return configurations.stream()
               .min(Comparator.comparingInt(TalentLevelAttribute.Config::getLevelChange))
               .map(config -> new TalentLevelAttribute(config.getTalent(), config.getLevelChange()));
         }

         @Override
         public Optional<TalentLevelAttribute> getMaximumValue(List<TalentLevelAttribute.Config> configurations) {
            return configurations.stream()
               .max(Comparator.comparingInt(TalentLevelAttribute.Config::getLevelChange))
               .map(config -> new TalentLevelAttribute(config.getTalent(), config.getLevelChange()));
         }
      };
   }

   public static VaultGearModifierReader<TalentLevelAttribute> reader() {
      return new VaultGearModifierReader<TalentLevelAttribute>("", 15638784) {
         @Nullable
         @Override
         public MutableComponent getDisplay(VaultGearAttributeInstance<TalentLevelAttribute> instance, VaultGearModifier.AffixType type) {
            TalentLevelAttribute attribute = instance.getValue();
            MutableComponent valueDisplay = this.getValueDisplay(attribute);
            if (attribute.getTalent().equals("all_talents")) {
               return new TextComponent("")
                  .append(type.getAffixPrefixComponent(attribute.getLevelChange() >= 0).withStyle(this.getColoredTextStyle()))
                  .append(valueDisplay.withStyle(this.getColoredTextStyle()))
                  .append(" to level of ")
                  .append(new TextComponent("all Talents").withStyle(SpecialAbilityModification.getAbilityStyle()))
                  .withStyle(this.getColoredTextStyle());
            } else {
               Skill talent = ModConfigs.TALENTS.getTalentById(attribute.getTalent()).orElse(null);
               return talent == null
                  ? null
                  : new TextComponent("")
                     .append(type.getAffixPrefixComponent(attribute.getLevelChange() >= 0).withStyle(this.getColoredTextStyle()))
                     .append(valueDisplay.withStyle(this.getColoredTextStyle()))
                     .append(" to level of ")
                     .append(new TextComponent(talent.getName()).withStyle(SpecialAbilityModification.getAbilityStyle()))
                     .withStyle(this.getColoredTextStyle());
            }
         }

         public MutableComponent getValueDisplay(TalentLevelAttribute value) {
            return new TextComponent(String.valueOf(value.getLevelChange()));
         }

         @Override
         protected void serializeTextElements(JsonArray out, VaultGearAttributeInstance<TalentLevelAttribute> instance, VaultGearModifier.AffixType type) {
            TalentLevelAttribute attribute = instance.getValue();
            MutableComponent valueDisplay = this.getValueDisplay(attribute);
            if (attribute.getTalent().equals("all_talents")) {
               out.add(type.getAffixPrefix(attribute.getLevelChange() >= 0));
               out.add(valueDisplay.getString());
               out.add(" to level of all Talents");
            } else {
               Skill talent = ModConfigs.TALENTS.getTalentById(attribute.getTalent()).orElse(null);
               if (talent != null) {
                  out.add(type.getAffixPrefix(attribute.getLevelChange() >= 0));
                  out.add(valueDisplay.getString());
                  out.add(" to level of ");
                  out.add(talent.getName());
               }
            }
         }
      };
   }

   public static VaultGearAttributeComparator<TalentLevelAttribute> comparator() {
      return new VaultGearAttributeComparator<TalentLevelAttribute>() {
         public Optional<TalentLevelAttribute> merge(TalentLevelAttribute thisValue, TalentLevelAttribute thatValue) {
            return !thisValue.getTalent().equals(thatValue.getTalent())
               ? Optional.empty()
               : Optional.of(new TalentLevelAttribute(thisValue.getTalent(), thisValue.getLevelChange() + thatValue.getLevelChange()));
         }

         public Optional<TalentLevelAttribute> difference(TalentLevelAttribute thisValue, TalentLevelAttribute thatValue) {
            return Optional.empty();
         }

         @NotNull
         @Override
         public Comparator<TalentLevelAttribute> getComparator() {
            return Comparator.comparing(TalentLevelAttribute::getLevelChange);
         }
      };
   }

   public static class Config {
      @Expose
      private String talentKey;
      @Expose
      private int levelChange;

      public Config(String talentKey, int levelChange) {
         this.talentKey = talentKey;
         this.levelChange = levelChange;
      }

      public String getTalent() {
         return this.talentKey;
      }

      public int getLevelChange() {
         return this.levelChange;
      }
   }
}
