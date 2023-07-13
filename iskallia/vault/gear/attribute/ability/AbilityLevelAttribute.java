package iskallia.vault.gear.attribute.ability;

import com.google.gson.JsonArray;
import com.google.gson.annotations.Expose;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.attribute.ability.special.base.SpecialAbilityModification;
import iskallia.vault.gear.attribute.config.ConfigurableAttributeGenerator;
import iskallia.vault.gear.attribute.type.VaultGearAttributeType;
import iskallia.vault.gear.reader.VaultGearModifierReader;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.skill.base.Skill;
import iskallia.vault.util.NetcodeUtils;
import java.util.Random;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;

public class AbilityLevelAttribute {
   public static final String ALL_ABILITIES = "all_abilities";
   protected final String ability;
   protected final int levelChange;

   public AbilityLevelAttribute(String ability, int levelChange) {
      this.ability = ability;
      this.levelChange = levelChange;
   }

   public String getAbility() {
      return this.ability;
   }

   public int getLevelChange() {
      return this.levelChange;
   }

   public static VaultGearAttributeType<AbilityLevelAttribute> type() {
      return VaultGearAttributeType.of((buf, attribute) -> {
         buf.writeString(attribute.getAbility());
         buf.writeInt(attribute.getLevelChange());
      }, buf -> new AbilityLevelAttribute(buf.readString(), buf.readInt()), (buf, attribute) -> {
         NetcodeUtils.writeString(buf, attribute.getAbility());
         buf.writeInt(attribute.getLevelChange());
      }, buf -> {
         String ability = NetcodeUtils.readString(buf);
         int levelChange = buf.readInt();
         return new AbilityLevelAttribute(ability, levelChange);
      }, VaultGearAttributeType.GSON::toJsonTree, nbt -> {
         CompoundTag tag = (CompoundTag)nbt;
         String ability = tag.getString("ability");
         int levelChange = tag.getInt("levelChange");
         return new AbilityLevelAttribute(ability, levelChange);
      }, attribute -> {
         CompoundTag tag = new CompoundTag();
         tag.putString("ability", attribute.getAbility());
         tag.putInt("levelChange", attribute.getLevelChange());
         return tag;
      });
   }

   public static ConfigurableAttributeGenerator<AbilityLevelAttribute, AbilityLevelAttribute.Config> generator() {
      return new ConfigurableAttributeGenerator<AbilityLevelAttribute, AbilityLevelAttribute.Config>() {
         @Override
         public Class<AbilityLevelAttribute.Config> getConfigurationObjectClass() {
            return AbilityLevelAttribute.Config.class;
         }

         public MutableComponent getConfigRangeDisplay(
            VaultGearModifierReader<AbilityLevelAttribute> reader, AbilityLevelAttribute.Config min, AbilityLevelAttribute.Config max
         ) {
            return new TextComponent("%s-%s".formatted(min.getLevelChange(), max.getLevelChange()));
         }

         public AbilityLevelAttribute generateRandomValue(AbilityLevelAttribute.Config object, Random random) {
            return new AbilityLevelAttribute(object.getAbilityKey(), object.getLevelChange());
         }
      };
   }

   public static VaultGearModifierReader<AbilityLevelAttribute> reader() {
      return new VaultGearModifierReader<AbilityLevelAttribute>("", 15638784) {
         @Nullable
         @Override
         public MutableComponent getDisplay(VaultGearAttributeInstance<AbilityLevelAttribute> instance, VaultGearModifier.AffixType type) {
            AbilityLevelAttribute attribute = instance.getValue();
            MutableComponent valueDisplay = this.getValueDisplay(attribute);
            if (attribute.getAbility().equals("all_abilities")) {
               return new TextComponent("")
                  .append(type.getAffixPrefixComponent(attribute.getLevelChange() >= 0).withStyle(this.getColoredTextStyle()))
                  .append(valueDisplay.withStyle(this.getColoredTextStyle()))
                  .append(" to level of ")
                  .append(new TextComponent("all Abilities").withStyle(SpecialAbilityModification.getAbilityStyle()))
                  .withStyle(this.getColoredTextStyle());
            } else {
               Skill ability = ModConfigs.ABILITIES.getAbilityById(attribute.getAbility()).orElse(null);
               return ability == null
                  ? null
                  : new TextComponent("")
                     .append(type.getAffixPrefixComponent(attribute.getLevelChange() >= 0).withStyle(this.getColoredTextStyle()))
                     .append(valueDisplay.withStyle(this.getColoredTextStyle()))
                     .append(" to level of ")
                     .append(new TextComponent(ability.getName()).withStyle(SpecialAbilityModification.getAbilityStyle()))
                     .withStyle(this.getColoredTextStyle());
            }
         }

         public MutableComponent getValueDisplay(AbilityLevelAttribute value) {
            return new TextComponent(String.valueOf(value.getLevelChange()));
         }

         @Override
         protected void serializeTextElements(JsonArray out, VaultGearAttributeInstance<AbilityLevelAttribute> instance, VaultGearModifier.AffixType type) {
            AbilityLevelAttribute attribute = instance.getValue();
            MutableComponent valueDisplay = this.getValueDisplay(attribute);
            if (attribute.getAbility().equals("all_abilities")) {
               out.add(type.getAffixPrefix(attribute.getLevelChange() >= 0));
               out.add(valueDisplay.getString());
               out.add(" to level of all Abilities");
            } else {
               Skill ability = ModConfigs.ABILITIES.getAbilityById(attribute.getAbility()).orElse(null);
               if (ability != null) {
                  out.add(type.getAffixPrefix(attribute.getLevelChange() >= 0));
                  out.add(valueDisplay.getString());
                  out.add(" to level of ");
                  out.add(ability.getName());
               }
            }
         }
      };
   }

   public static class Config {
      @Expose
      private String abilityKey;
      @Expose
      private int levelChange;

      public Config(String abilityKey, int levelChange) {
         this.abilityKey = abilityKey;
         this.levelChange = levelChange;
      }

      public String getAbilityKey() {
         return this.abilityKey;
      }

      public int getLevelChange() {
         return this.levelChange;
      }
   }
}
