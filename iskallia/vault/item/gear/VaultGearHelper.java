package iskallia.vault.item.gear;

import iskallia.vault.attribute.EffectTalentAttribute;
import iskallia.vault.attribute.FloatAttribute;
import iskallia.vault.attribute.IntegerAttribute;
import iskallia.vault.attribute.VAttribute;
import iskallia.vault.config.VaultGearConfig;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.skill.talent.type.EffectTalent;
import iskallia.vault.util.data.WeightedList;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.entity.LivingEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.Color;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;

public class VaultGearHelper {
   private static final Random rand = new Random();

   public static boolean removeRandomModifier(ItemStack stack) {
      return removeRandomModifiers(stack, 1) == 1;
   }

   public static int removeRandomModifiers(ItemStack stack, int toRemove) {
      if (!hasModifier(stack)) {
         return 0;
      } else {
         List<VAttribute<?, ?>> modifiers = getRollableGearModifiers();
         Collections.shuffle(modifiers);
         int removed = 0;

         for (VAttribute<?, ?> attribute : modifiers) {
            if (removed >= toRemove) {
               break;
            }

            if (attribute.exists(stack) && attribute == ModAttributes.EXTRA_EFFECTS) {
               Optional<List<EffectTalent>> possibleEffects = ModAttributes.EXTRA_EFFECTS.getValue(stack).filter(effectsx -> !effectsx.isEmpty());
               if (possibleEffects.isPresent()) {
                  List<EffectTalent> effects = possibleEffects.get();
                  Collections.shuffle(effects);
                  EffectTalent removedEffect = effects.remove(0);
                  EffectTalentAttribute created = ModAttributes.EXTRA_EFFECTS.create(stack, effects);
                  if (!created.getValue(stack).contains(removedEffect)) {
                     removed++;
                     continue;
                  }
               }
            }

            if (attribute.exists(stack) && attribute != ModAttributes.EXTRA_EFFECTS) {
               ListNBT existingAttributes = stack.func_196082_o().func_74775_l("Vault").func_150295_c("Attributes", 10);
               if (removeAttribute(attribute, existingAttributes)) {
                  removed++;
               }
            }
         }

         return removed;
      }
   }

   public static void removeAllAttributes(ItemStack stack) {
      List<VAttribute<?, ?>> removeable = new ArrayList<>(ModAttributes.REGISTRY.values());
      List<VAttribute<?, ?>> baseAttributes = getBaseAttributes();

      for (VAttribute<?, ?> attribute : removeable) {
         if (!baseAttributes.contains(attribute)) {
            removeAttribute(stack, attribute);
         }
      }
   }

   public static boolean removeAttribute(ItemStack stack, VAttribute<?, ?> attribute) {
      if (attribute.exists(stack) && attribute == ModAttributes.EXTRA_EFFECTS) {
         Optional<List<EffectTalent>> possibleEffects = ModAttributes.EXTRA_EFFECTS.getValue(stack).filter(effectsx -> !effectsx.isEmpty());
         if (possibleEffects.isPresent()) {
            List<EffectTalent> effects = possibleEffects.get();
            effects.clear();
            ModAttributes.EXTRA_EFFECTS.create(stack, effects);
            return true;
         }
      }

      if (attribute.exists(stack) && attribute != ModAttributes.EXTRA_EFFECTS) {
         ListNBT existingAttributes = stack.func_196082_o().func_74775_l("Vault").func_150295_c("Attributes", 10);
         return removeAttribute(attribute, existingAttributes);
      } else {
         return false;
      }
   }

   private static boolean removeAttribute(VAttribute<?, ?> attribute, ListNBT attributes) {
      return attributes.stream()
         .map(nbt -> (CompoundNBT)nbt)
         .filter(compoundNBT -> attribute.getId().equals(new ResourceLocation(compoundNBT.func_74779_i("Id"))))
         .findFirst()
         .map(attributes::remove)
         .orElse(false);
   }

   @Nullable
   public static VAttribute<?, ?> getRandomModifier(ItemStack stack, Random rand) {
      List<VAttribute<?, ?>> modifiers = getRollableGearModifiers();
      Collections.shuffle(modifiers, rand);

      for (VAttribute<?, ?> attribute : modifiers) {
         if (attribute.exists(stack)
            && attribute == ModAttributes.EXTRA_EFFECTS
            && ModAttributes.EXTRA_EFFECTS.getValue(stack).map(effects -> !effects.isEmpty()).orElse(false)) {
            return attribute;
         }

         if (attribute.exists(stack) && attribute != ModAttributes.EXTRA_EFFECTS) {
            return attribute;
         }
      }

      return null;
   }

   public static boolean applyGearModifier(ItemStack stack, VaultGear.Rarity rarity, int tier, VAttribute<?, ?> attribute) {
      if (!stack.func_190926_b() && stack.func_77973_b() instanceof VaultGear) {
         VaultGearConfig.Tier gearTier = VaultGearConfig.get(rarity).TIERS.get(tier);
         return gearTier == null ? false : applyGearModifier(stack, gearTier, attribute);
      } else {
         return false;
      }
   }

   public static boolean applyGearModifier(ItemStack stack, VaultGearConfig.Tier tierConfig, VAttribute<?, ?> attribute) {
      if (hasModifier(stack, attribute)) {
         return false;
      } else {
         VaultGearConfig.BaseModifiers modifiers = tierConfig.BASE_MODIFIERS.get(stack.func_77973_b().getRegistryName().toString());
         if (modifiers == null) {
            return false;
         } else {
            WeightedList.Entry<? extends VAttribute.Instance.Generator<?>> generatorEntry = modifiers.getGenerator(attribute);
            if (generatorEntry == null) {
               return false;
            } else {
               VAttribute.Instance.Generator generator = generatorEntry.value;
               attribute.create(stack, rand, generator);
               return true;
            }
         }
      }
   }

   public static boolean hasUsedLevels(ItemStack stack) {
      if (!stack.func_190926_b() && stack.func_77973_b() instanceof VaultGear) {
         float currentLevel = ModAttributes.GEAR_LEVEL.get(stack).map(level -> level.getValue(stack)).orElse(0.0F);
         return currentLevel > 1.0E-4;
      } else {
         return false;
      }
   }

   public static boolean hasModifier(ItemStack stack) {
      if (!stack.func_190926_b() && stack.func_77973_b() instanceof VaultGear) {
         List<VAttribute<?, ?>> modifiers = getRollableGearModifiers();
         Optional<VAttribute<?, ?>> extraEffects = modifiers.stream().filter(vAttribute -> vAttribute == ModAttributes.EXTRA_EFFECTS).findFirst();
         return extraEffects.isPresent() && ModAttributes.EXTRA_EFFECTS.getValue(stack).map(effects -> !effects.isEmpty()).orElse(false)
            ? true
            : modifiers.stream().anyMatch(vAttribute -> vAttribute != ModAttributes.EXTRA_EFFECTS && vAttribute.exists(stack));
      } else {
         return false;
      }
   }

   public static boolean hasModifier(ItemStack stack, VAttribute<?, ?> attribute) {
      if (stack.func_190926_b() || !(stack.func_77973_b() instanceof VaultGear)) {
         return false;
      } else {
         return attribute.exists(stack)
            ? attribute != ModAttributes.EXTRA_EFFECTS || !ModAttributes.EXTRA_EFFECTS.getValue(stack).map(List::isEmpty).orElse(false)
            : false;
      }
   }

   public static List<VAttribute<?, ?>> getModifiers(ItemStack stack) {
      return !stack.func_190926_b() && stack.func_77973_b() instanceof VaultGear
         ? getRollableGearModifiers().stream().filter(modifier -> hasModifier(stack, (VAttribute<?, ?>)modifier)).collect(Collectors.toList())
         : Collections.emptyList();
   }

   public static boolean canRollModifier(ItemStack stack, VaultGear.Rarity rarity, int tier, VAttribute<?, ?> attribute) {
      if (!stack.func_190926_b() && stack.func_77973_b() instanceof VaultGear) {
         VaultGearConfig.Tier tierConfig = VaultGearConfig.get(rarity).TIERS.get(tier);
         return tierConfig.getModifiers(stack).map(modifiers -> modifiers.getGenerator(attribute) != null).orElse(false);
      } else {
         return false;
      }
   }

   public static int getAttributeValueOnGearSumInt(LivingEntity le, VAttribute<Integer, IntegerAttribute>... attributes) {
      int sum = 0;

      for (EquipmentSlotType slotType : EquipmentSlotType.values()) {
         ItemStack stack = le.func_184582_a(slotType);
         if (!stack.func_190926_b() && stack.func_77973_b() instanceof VaultGear) {
            VaultGear<?> gear = (VaultGear<?>)stack.func_77973_b();
            if (gear.isIntendedForSlot(slotType)) {
               for (VAttribute<Integer, IntegerAttribute> attribute : attributes) {
                  sum += attribute.getBase(stack).orElse(0);
               }
            }
         }
      }

      return sum;
   }

   public static float getAttributeValueOnGearSumFloat(LivingEntity le, VAttribute<Float, FloatAttribute>... attributes) {
      float sum = 0.0F;

      for (EquipmentSlotType slotType : EquipmentSlotType.values()) {
         ItemStack stack = le.func_184582_a(slotType);
         if (!stack.func_190926_b() && stack.func_77973_b() instanceof VaultGear) {
            VaultGear<?> gear = (VaultGear<?>)stack.func_77973_b();
            if (gear.isIntendedForSlot(slotType)) {
               for (VAttribute<Float, FloatAttribute> attribute : attributes) {
                  sum += attribute.getBase(stack).orElse(0.0F);
               }
            }
         }
      }

      return sum;
   }

   public static List<VAttribute<?, ?>> getAliasAttributes(ResourceLocation key) {
      List<VAttribute<?, ?>> attributes = new ArrayList<>();
      String var2 = key.func_110623_a();
      switch (var2) {
         case "add_armor":
         case "add_armor_2":
            attributes.add(ModAttributes.ADD_ARMOR);
            attributes.add(ModAttributes.ADD_ARMOR_2);
            break;
         case "add_armor_toughness":
         case "add_armor_toughness_2":
            attributes.add(ModAttributes.ADD_ARMOR_TOUGHNESS);
            attributes.add(ModAttributes.ADD_ARMOR_TOUGHNESS_2);
            break;
         case "add_knockback_resistance":
         case "add_knockback_resistance_2":
            attributes.add(ModAttributes.ADD_KNOCKBACK_RESISTANCE);
            attributes.add(ModAttributes.ADD_KNOCKBACK_RESISTANCE_2);
            break;
         case "add_attack_damage":
         case "add_attack_damage_2":
            attributes.add(ModAttributes.ADD_ATTACK_DAMAGE);
            attributes.add(ModAttributes.ADD_ATTACK_DAMAGE_2);
            break;
         case "add_attack_speed":
         case "add_attack_speed_2":
            attributes.add(ModAttributes.ADD_ATTACK_SPEED);
            attributes.add(ModAttributes.ADD_ATTACK_SPEED_2);
            break;
         case "add_durability":
         case "add_durability_2":
            attributes.add(ModAttributes.ADD_DURABILITY);
            attributes.add(ModAttributes.ADD_DURABILITY_2);
            break;
         case "add_reach":
         case "add_reach_2":
            attributes.add(ModAttributes.ADD_REACH);
            attributes.add(ModAttributes.ADD_REACH_2);
            break;
         case "add_cooldown_reduction":
         case "add_cooldown_reduction_2":
            attributes.add(ModAttributes.ADD_COOLDOWN_REDUCTION);
            attributes.add(ModAttributes.ADD_COOLDOWN_REDUCTION_2);
            break;
         case "add_extra_leech_ratio":
         case "extra_leech_ratio":
            attributes.add(ModAttributes.ADD_EXTRA_LEECH_RATIO);
            attributes.add(ModAttributes.EXTRA_LEECH_RATIO);
            break;
         case "add_extra_resistance":
         case "extra_resistance":
            attributes.add(ModAttributes.ADD_EXTRA_RESISTANCE);
            attributes.add(ModAttributes.EXTRA_RESISTANCE);
            break;
         case "add_extra_parry_chance":
         case "extra_parry_chance":
            attributes.add(ModAttributes.ADD_EXTRA_PARRY_CHANCE);
            attributes.add(ModAttributes.EXTRA_PARRY_CHANCE);
            break;
         case "add_extra_health":
         case "extra_health":
            attributes.add(ModAttributes.ADD_EXTRA_HEALTH);
            attributes.add(ModAttributes.EXTRA_HEALTH);
            break;
         case "damage_increase":
         case "damage_increase_2":
            attributes.add(ModAttributes.DAMAGE_INCREASE);
            attributes.add(ModAttributes.DAMAGE_INCREASE_2);
            break;
         default:
            VAttribute<?, ?> attribute = ModAttributes.REGISTRY.get(key);
            if (attribute != null) {
               attributes.add(attribute);
            }
      }

      return attributes;
   }

   public static List<VAttribute<?, ?>> getBaseAttributes() {
      return Arrays.asList(
         ModAttributes.GEAR_CRAFTED_BY,
         ModAttributes.GEAR_SPECIAL_MODEL,
         ModAttributes.GEAR_COLOR,
         ModAttributes.GEAR_NAME,
         ModAttributes.IDOL_TYPE,
         ModAttributes.GEAR_LEVEL_CHANCE,
         ModAttributes.GEAR_TIER,
         ModAttributes.GEAR_ROLL_POOL,
         ModAttributes.GEAR_ROLL_TYPE
      );
   }

   public static List<VAttribute<?, ?>> getRollableGearModifiers() {
      return Arrays.asList(
         ModAttributes.ADD_ARMOR,
         ModAttributes.ADD_ARMOR_2,
         ModAttributes.ADD_ARMOR_TOUGHNESS,
         ModAttributes.ADD_ARMOR_TOUGHNESS_2,
         ModAttributes.ADD_KNOCKBACK_RESISTANCE,
         ModAttributes.ADD_KNOCKBACK_RESISTANCE_2,
         ModAttributes.ADD_ATTACK_DAMAGE,
         ModAttributes.ADD_ATTACK_DAMAGE_2,
         ModAttributes.ADD_ATTACK_SPEED,
         ModAttributes.ADD_ATTACK_SPEED_2,
         ModAttributes.ADD_DURABILITY,
         ModAttributes.ADD_DURABILITY_2,
         ModAttributes.ADD_REACH,
         ModAttributes.ADD_REACH_2,
         ModAttributes.ADD_COOLDOWN_REDUCTION,
         ModAttributes.ADD_COOLDOWN_REDUCTION_2,
         ModAttributes.ADD_MIN_VAULT_LEVEL,
         ModAttributes.ADD_REGEN_CLOUD,
         ModAttributes.ADD_WEAKENING_CLOUD,
         ModAttributes.ADD_WITHER_CLOUD,
         ModAttributes.ADD_POISON_IMMUNITY,
         ModAttributes.ADD_WITHER_IMMUNITY,
         ModAttributes.ADD_HUNGER_IMMUNITY,
         ModAttributes.ADD_MINING_FATIGUE_IMMUNITY,
         ModAttributes.ADD_SLOWNESS_IMMUNITY,
         ModAttributes.ADD_WEAKNESS_IMMUNITY,
         ModAttributes.ADD_FEATHER_FEET,
         ModAttributes.ADD_SOULBOUND,
         ModAttributes.ADD_EXTRA_LEECH_RATIO,
         ModAttributes.ADD_EXTRA_RESISTANCE,
         ModAttributes.ADD_EXTRA_PARRY_CHANCE,
         ModAttributes.ADD_EXTRA_HEALTH,
         ModAttributes.FATAL_STRIKE_CHANCE,
         ModAttributes.FATAL_STRIKE_DAMAGE,
         ModAttributes.THORNS_CHANCE,
         ModAttributes.THORNS_DAMAGE,
         ModAttributes.EXTRA_LEECH_RATIO,
         ModAttributes.EXTRA_RESISTANCE,
         ModAttributes.EXTRA_PARRY_CHANCE,
         ModAttributes.EXTRA_HEALTH,
         ModAttributes.EXTRA_EFFECTS,
         ModAttributes.CHEST_RARITY,
         ModAttributes.DAMAGE_INCREASE,
         ModAttributes.DAMAGE_INCREASE_2,
         ModAttributes.DAMAGE_ILLAGERS,
         ModAttributes.DAMAGE_SPIDERS,
         ModAttributes.DAMAGE_UNDEAD,
         ModAttributes.ON_HIT_CHAIN,
         ModAttributes.ON_HIT_AOE,
         ModAttributes.ON_HIT_STUN
      );
   }

   public static ITextComponent getDisplayName(VAttribute<?, ?> attribute) {
      if (attribute == ModAttributes.ADD_ARMOR || attribute == ModAttributes.ADD_ARMOR_2) {
         return text("Armor", 4766456);
      } else if (attribute == ModAttributes.ADD_ARMOR_TOUGHNESS || attribute == ModAttributes.ADD_ARMOR_TOUGHNESS_2) {
         return text("Armor Toughness", 13302672);
      } else if (attribute == ModAttributes.THORNS_CHANCE) {
         return text("Thorns Chance", 7195648);
      } else if (attribute == ModAttributes.THORNS_DAMAGE) {
         return text("Thorns Damage", 3646976);
      } else if (attribute == ModAttributes.ADD_KNOCKBACK_RESISTANCE || attribute == ModAttributes.ADD_KNOCKBACK_RESISTANCE_2) {
         return text("Knockback Resistance", 16756751);
      } else if (attribute == ModAttributes.ADD_ATTACK_DAMAGE || attribute == ModAttributes.ADD_ATTACK_DAMAGE_2) {
         return text("Attack Damage", 13116966);
      } else if (attribute == ModAttributes.ADD_ATTACK_SPEED || attribute == ModAttributes.ADD_ATTACK_SPEED_2) {
         return text("Attack Speed", 16767592);
      } else if (attribute == ModAttributes.ADD_DURABILITY || attribute == ModAttributes.ADD_DURABILITY_2) {
         return text("Durability", 14668030);
      } else if (attribute == ModAttributes.ADD_REACH || attribute == ModAttributes.ADD_REACH_2) {
         return text("Reach", 8706047);
      } else if (attribute == ModAttributes.ADD_FEATHER_FEET) {
         return text("Feather Feet", 13499899);
      } else if (attribute == ModAttributes.ADD_COOLDOWN_REDUCTION || attribute == ModAttributes.ADD_COOLDOWN_REDUCTION_2) {
         return text("Cooldown Reduction", 63668);
      } else if (attribute == ModAttributes.EXTRA_LEECH_RATIO || attribute == ModAttributes.ADD_EXTRA_LEECH_RATIO) {
         return text("Leech", 16716820);
      } else if (attribute == ModAttributes.FATAL_STRIKE_CHANCE) {
         return text("Fatal Strike Chance", 16523264);
      } else if (attribute == ModAttributes.FATAL_STRIKE_DAMAGE) {
         return text("Fatal Strike Damage", 12520704);
      } else if (attribute == ModAttributes.EXTRA_HEALTH || attribute == ModAttributes.ADD_EXTRA_HEALTH) {
         return text("Health", 2293541);
      } else if (attribute == ModAttributes.EXTRA_PARRY_CHANCE || attribute == ModAttributes.ADD_EXTRA_PARRY_CHANCE) {
         return text("Parry", 11534098);
      } else if (attribute == ModAttributes.EXTRA_RESISTANCE || attribute == ModAttributes.ADD_EXTRA_RESISTANCE) {
         return text("Resistance", 16702720);
      } else if (attribute == ModAttributes.ADD_HUNGER_IMMUNITY) {
         return text("Hunger Immunity", 10801083);
      } else if (attribute == ModAttributes.ADD_MINING_FATIGUE_IMMUNITY) {
         return text("Mining Fatigue Immunity", 10801083);
      } else if (attribute == ModAttributes.ADD_POISON_IMMUNITY) {
         return text("Poison Immunity", 10801083);
      } else if (attribute == ModAttributes.ADD_SLOWNESS_IMMUNITY) {
         return text("Slowness Immunity", 10801083);
      } else if (attribute == ModAttributes.ADD_WEAKNESS_IMMUNITY) {
         return text("Weakness Immunity", 10801083);
      } else if (attribute == ModAttributes.ADD_WITHER_IMMUNITY) {
         return text("Wither Immunity", 10801083);
      } else if (attribute == ModAttributes.ADD_REGEN_CLOUD) {
         return text("Rejuvenate Cloud", 15007916);
      } else if (attribute == ModAttributes.ADD_WEAKENING_CLOUD) {
         return text("Weakening Cloud", 15007916);
      } else if (attribute == ModAttributes.ADD_WITHER_CLOUD) {
         return text("Withering Cloud", 15007916);
      } else if (attribute == ModAttributes.EXTRA_EFFECTS) {
         return text("Potion Effect", 14111487);
      } else if (attribute == ModAttributes.ADD_SOULBOUND) {
         return text("Soulbound", 9856253);
      } else if (attribute == ModAttributes.CHEST_RARITY) {
         return text("Chest Rarity", 11073085);
      } else if (attribute == ModAttributes.DAMAGE_INCREASE || attribute == ModAttributes.DAMAGE_INCREASE_2) {
         return text("Increased Damage", 16739072);
      } else if (attribute == ModAttributes.DAMAGE_ILLAGERS) {
         return text("Spiteful", 40882);
      } else if (attribute == ModAttributes.DAMAGE_SPIDERS) {
         return text("Baneful", 8281694);
      } else if (attribute == ModAttributes.DAMAGE_UNDEAD) {
         return text("Holy", 16382128);
      } else if (attribute == ModAttributes.ON_HIT_CHAIN) {
         return text("Chaining Attacks", 6119096);
      } else if (attribute == ModAttributes.ON_HIT_AOE) {
         return text("Attack AoE", 12085504);
      } else {
         return (ITextComponent)(attribute == ModAttributes.ON_HIT_STUN
            ? text("Stun Attack Chance", 1681124)
            : new StringTextComponent(attribute.getId().toString()).func_240699_a_(TextFormatting.GRAY));
      }
   }

   private static ITextComponent text(String txt, int color) {
      return new StringTextComponent(txt).func_230530_a_(Style.field_240709_b_.func_240718_a_(Color.func_240743_a_(color)));
   }
}
