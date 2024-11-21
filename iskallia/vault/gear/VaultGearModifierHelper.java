package iskallia.vault.gear;

import iskallia.vault.VaultMod;
import iskallia.vault.config.gear.VaultGearTagConfig;
import iskallia.vault.config.gear.VaultGearTierConfig;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.core.random.RandomSource;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.gear.attribute.VaultGearAttribute;
import iskallia.vault.gear.attribute.VaultGearAttributeInstance;
import iskallia.vault.gear.attribute.VaultGearAttributeRegistry;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.attribute.config.ConfigurableAttributeGenerator;
import iskallia.vault.gear.comparator.VaultGearAttributeComparator;
import iskallia.vault.gear.data.AttributeGearData;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.gear.modification.GearModification;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModItems;
import iskallia.vault.util.MiscUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;

public class VaultGearModifierHelper {
   public static GearModification.Result reForgeAllModifiers(ItemStack stack, Random random) {
      GearModification.Result result = removeAllModifiers(stack);
      return !result.success() ? result : generateModifiers(stack, random);
   }

   public static GearModification.Result reForgeAllImplicits(ItemStack stack, Random random) {
      GearModification.Result result = removeAllModifiersOfType(stack, VaultGearModifier.AffixType.IMPLICIT);
      if (!result.success()) {
         return result;
      } else {
         GearModification.Result genResult = generateImplicits(stack, random);
         if (genResult.success()) {
            VaultGearData data = VaultGearData.read(stack);
            if (!data.has(ModGearAttributes.DURABILITY, VaultGearData.Type.ATTRIBUTES)) {
               generateBaseAttributes(stack, random);
            }
         }

         return genResult;
      }
   }

   public static GearModification.Result reForgeAllWithTag(VaultGearTagConfig.ModTagGroup modGroupTag, ItemStack stack, Random random) {
      ItemStack emptyCopy = stack.copy();
      GearModification.Result result = removeAllModifiers(emptyCopy);
      if (!result.success()) {
         return result;
      } else if (getAvailableModGroupOutcomes(modGroupTag, emptyCopy).isEmpty()) {
         return GearModification.Result.makeActionError("no_modifiers", modGroupTag.getDisplayComponent());
      } else {
         result = removeAllModifiers(stack);
         if (!result.success()) {
            return result;
         } else {
            result = addNewModifierOfGroup(modGroupTag, stack, random);
            return !result.success() ? result : generateModifiers(stack, random);
         }
      }
   }

   public static GearModification.Result reForgeAllModifiersTagged(ItemStack stack, Random random, String tag) {
      VaultGearData data = VaultGearData.read(stack);
      if (!data.isModifiable()) {
         return GearModification.Result.errorUnmodifiable();
      } else {
         VaultGearTierConfig config = VaultGearTierConfig.getConfig(stack).orElse(null);
         if (config == null) {
            VaultMod.LOGGER.error("Unknown VaultGear: " + stack);
            return GearModification.Result.errorUnmodifiable();
         } else {
            RandomSource src = JavaRandom.ofInternal(random.nextLong());
            List<Tuple<VaultGearTierConfig.ModifierAffixTagGroup, VaultGearTierConfig.ModifierTierGroup>> modifierTagGroups = config.getAnyGroupsFulfilling(
               tier -> tier.getTags().contains(tag)
            );
            modifierTagGroups.stream()
               .<VaultGearTierConfig.ModifierTierGroup>map(Tuple::getB)
               .forEach(
                  groupx -> {
                     VaultGearAttribute<?> attr = VaultGearAttributeRegistry.getAttribute(groupx.getAttribute());
                     if (attr != null) {
                        data.getModifiers(attr, VaultGearData.Type.ALL)
                           .stream()
                           .filter(VaultGearAttributeInstance::canBeModified)
                           .filter(inst -> inst instanceof VaultGearModifier)
                           .map(inst -> (VaultGearModifier)inst)
                           .filter(mod -> groupx.getIdentifier().equals(mod.getModifierIdentifier()))
                           .forEach(data::removeModifier);
                     }
                  }
               );
            ItemStack stackCopy = stack.copy();
            data.write(stackCopy);
            Set<String> allGroups = data.getExistingModifierGroups(VaultGearData.Type.ALL);
            modifierTagGroups.removeIf(tpl -> allGroups.contains(((VaultGearTierConfig.ModifierTierGroup)tpl.getB()).getModifierGroup()));
            Set<String> modGroups = new HashSet<>();
            modifierTagGroups.forEach(tpl -> modGroups.add(((VaultGearTierConfig.ModifierTierGroup)tpl.getB()).getModifierGroup()));
            boolean hasValidOutcome = false;

            for (String group : modGroups) {
               boolean applied = getAvailableModGroupOutcomes(grp -> group.equalsIgnoreCase(grp.getModifierGroup()), stackCopy, true)
                  .getRandom(src)
                  .map(outcome -> outcome.type().addModifier(data, outcome.tier().makeModifier(outcome.tierGroup(), random)))
                  .orElse(false);
               if (applied) {
                  hasValidOutcome = true;
               }
            }

            if (!hasValidOutcome) {
               return GearModification.Result.makeActionError("no_modifiers");
            } else {
               data.write(stack);
               return GearModification.Result.makeSuccess();
            }
         }
      }
   }

   public static GearModification.Result addNewModifierOfGroup(VaultGearTagConfig.ModTagGroup modGroupTag, ItemStack stack, Random random) {
      VaultGearData data = VaultGearData.read(stack);
      if (!data.isModifiable()) {
         return GearModification.Result.errorUnmodifiable();
      } else {
         WeightedList<VaultGearModifierHelper.TierGroupOutcome> outcomes = getAvailableModGroupOutcomes(modGroupTag, stack);
         Map<VaultGearTierConfig.ModifierTierGroup, Integer> highestTiers = new HashMap<>();

         for (VaultGearModifierHelper.TierGroupOutcome outcome : outcomes.keySet()) {
            int current = highestTiers.getOrDefault(outcome.tierGroup, -1);
            highestTiers.put(outcome.tierGroup, Math.max(outcome.tier.getModifierTier(), current));
         }

         outcomes.entrySet().removeIf(entry -> entry.getKey().tier.getModifierTier() != highestTiers.get(entry.getKey().tierGroup));
         VaultGearModifierHelper.TierGroupOutcome outcome = outcomes.getRandom(random).orElse(null);
         if (outcome == null) {
            return GearModification.Result.makeActionError("no_modifiers", modGroupTag.getDisplayComponent());
         } else if (!outcome.type().addModifier(data, outcome.tier().makeModifier(outcome.tierGroup(), random))) {
            return GearModification.Result.errorUnmodifiable();
         } else {
            data.write(stack);
            return GearModification.Result.makeSuccess();
         }
      }
   }

   public static GearModification.Result reForgeOutcomeOfRandomModifier(ItemStack stack, long worldGameTime, Random random) {
      VaultGearData data = VaultGearData.read(stack);
      if (!data.isModifiable()) {
         return GearModification.Result.errorUnmodifiable();
      } else {
         List<Tuple<VaultGearModifier<?>, WeightedList<VaultGearTierConfig.ModifierOutcome<?>>>> modifierReplacements = getAvailableModifierConfigurationOutcomes(
            data, stack, true
         );
         modifierReplacements.removeIf(tpl -> ((WeightedList)tpl.getB()).size() <= 1);
         if (modifierReplacements.isEmpty()) {
            return GearModification.Result.makeActionError("no_modifiers");
         } else {
            Tuple<VaultGearModifier<?>, WeightedList<VaultGearTierConfig.ModifierOutcome<?>>> potentialReplacements = MiscUtils.getRandomEntry(
               modifierReplacements
            );
            if (potentialReplacements == null) {
               return GearModification.Result.errorInternal();
            } else {
               VaultGearTierConfig.ModifierOutcome<?> replacement = (VaultGearTierConfig.ModifierOutcome<?>)((WeightedList)potentialReplacements.getB())
                  .getRandom(random)
                  .orElse(null);
               if (replacement == null) {
                  return GearModification.Result.errorInternal();
               } else {
                  VaultGearModifier existing = (VaultGearModifier)potentialReplacements.getA();
                  VaultGearModifier newModifier = replacement.makeModifier(random);
                  VaultGearAttributeComparator comparator = existing.getAttribute().getAttributeComparator();
                  if (comparator != null && comparator.compare(existing.getValue(), newModifier.getValue()) == 0) {
                     return reForgeOutcomeOfRandomModifier(stack, worldGameTime, random);
                  } else {
                     data.getAllModifierAffixes().forEach(VaultGearModifier::resetGameTimeAdded);
                     existing.setValue(newModifier.getValue());
                     existing.setRolledTier(newModifier.getRolledTier());
                     existing.setGameTimeAdded(worldGameTime);
                     existing.clearCategories();
                     data.write(stack);
                     return GearModification.Result.makeSuccess();
                  }
               }
            }
         }
      }
   }

   public static GearModification.Result improveRandomModifier(ItemStack stack, long worldGameTime, Random random) {
      VaultGearData data = VaultGearData.read(stack);
      if (!data.isModifiable()) {
         return GearModification.Result.errorUnmodifiable();
      } else {
         List<Tuple<VaultGearModifier<?>, WeightedList<VaultGearTierConfig.ModifierOutcome<?>>>> modifierReplacements = getAvailableModifierConfigurationOutcomes(
            data, stack, true
         );
         modifierReplacements.removeIf(
            tpl -> {
               VaultGearModifier<?> existingx = (VaultGearModifier<?>)tpl.getA();
               VaultGearAttributeComparator comparatorx = existingx.getAttribute().getAttributeComparator();
               if (comparatorx == null) {
                  return true;
               } else {
                  ConfigurableAttributeGenerator generator = existingx.getAttribute().getGenerator();
                  ((WeightedList)tpl.getB()).entrySet().removeIf(weightedOutcome -> {
                     VaultGearTierConfig.ModifierOutcome<?> outcome = (VaultGearTierConfig.ModifierOutcome<?>)weightedOutcome.getKey();
                     Object tierConfig = outcome.tier().getModifierConfiguration();
                     Object maxValue = generator.getMaximumValue(List.of(tierConfig)).orElse(null);
                     return maxValue == null ? true : comparatorx.compare(maxValue, existingx.getValue()) <= 0;
                  });
                  return ((WeightedList)tpl.getB()).isEmpty()
                     ? true
                     : ((WeightedList)tpl.getB())
                        .entrySet()
                        .stream()
                        .allMatch(
                           weightedOutcome -> {
                              VaultGearTierConfig.ModifierOutcome<?> outcome = (VaultGearTierConfig.ModifierOutcome<?>)weightedOutcome.getKey();
                              Object tierConfig = outcome.tier().getModifierConfiguration();
                              Object minValue = generator.getMinimumValue(List.of(tierConfig)).orElse(null);
                              Object maxValue = generator.getMaximumValue(List.of(tierConfig)).orElse(null);
                              return minValue != null && maxValue != null
                                 ? comparatorx.compare(minValue, existingx.getValue()) == 0 && comparatorx.compare(maxValue, existingx.getValue()) == 0
                                 : true;
                           }
                        );
               }
            }
         );
         if (modifierReplacements.isEmpty()) {
            return GearModification.Result.makeActionError("all_max");
         } else {
            Tuple<VaultGearModifier<?>, WeightedList<VaultGearTierConfig.ModifierOutcome<?>>> potentialReplacements = MiscUtils.getRandomEntry(
               modifierReplacements
            );
            if (potentialReplacements == null) {
               return GearModification.Result.errorInternal();
            } else {
               VaultGearTierConfig.ModifierOutcome<?> replacement = (VaultGearTierConfig.ModifierOutcome<?>)((WeightedList)potentialReplacements.getB())
                  .getRandom(random)
                  .orElse(null);
               if (replacement == null) {
                  return GearModification.Result.errorInternal();
               } else {
                  VaultGearModifier existing = (VaultGearModifier)potentialReplacements.getA();
                  VaultGearAttributeComparator comparator = existing.getAttribute().getAttributeComparator();
                  if (comparator == null) {
                     return GearModification.Result.errorInternal();
                  } else {
                     VaultGearModifier newModifier;
                     do {
                        newModifier = replacement.makeModifier(random);
                     } while (comparator.compare(existing.getValue(), newModifier.getValue()) >= 0);

                     data.getAllModifierAffixes().forEach(VaultGearModifier::resetGameTimeAdded);
                     existing.setValue(newModifier.getValue());
                     existing.setRolledTier(newModifier.getRolledTier());
                     existing.setGameTimeAdded(worldGameTime);
                     existing.clearCategories();
                     data.write(stack);
                     return GearModification.Result.makeSuccess();
                  }
               }
            }
         }
      }
   }

   public static GearModification.Result addNewModifier(ItemStack stack, long worldGameTime, Random random) {
      VaultGearData data = VaultGearData.read(stack);
      VaultGearTierConfig cfg = VaultGearTierConfig.getConfig(stack).orElse(null);
      if (cfg == null) {
         VaultMod.LOGGER.error("Unknown VaultGear: " + stack);
         return GearModification.Result.errorUnmodifiable();
      } else if (!data.isModifiable()) {
         return GearModification.Result.errorUnmodifiable();
      } else {
         int itemLevel = data.getItemLevel();
         int prefixes = data.getFirstValue(ModGearAttributes.PREFIXES).orElse(0);
         int suffixes = data.getFirstValue(ModGearAttributes.SUFFIXES).orElse(0);
         prefixes -= data.getModifiers(VaultGearModifier.AffixType.PREFIX).size();
         suffixes -= data.getModifiers(VaultGearModifier.AffixType.SUFFIX).size();
         if (prefixes <= 0 && suffixes <= 0) {
            return GearModification.Result.makeActionError("full");
         } else {
            List<VaultGearModifier.AffixType> types = new ArrayList<>();
            if (prefixes > 0) {
               types.add(VaultGearModifier.AffixType.PREFIX);
            }

            if (suffixes > 0) {
               types.add(VaultGearModifier.AffixType.SUFFIX);
            }

            VaultGearModifier.AffixType type = MiscUtils.getRandomEntry(types, random);
            return cfg.getRandomModifier(type, itemLevel, random, data.getExistingModifierGroups(VaultGearData.Type.EXPLICIT_MODIFIERS)).map(modifier -> {
               data.getAllModifierAffixes().forEach(VaultGearModifier::resetGameTimeAdded);
               modifier.setGameTimeAdded(worldGameTime);
               data.addModifier(type, (VaultGearModifier<?>)modifier);
               data.write(stack);
               return GearModification.Result.makeSuccess();
            }).orElse(GearModification.Result.makeActionError("no_modifiers"));
         }
      }
   }

   public static GearModification.Result removeRandomModifier(ItemStack stack, Random random) {
      VaultGearData data = VaultGearData.read(stack);
      if (!data.isModifiable()) {
         return GearModification.Result.errorUnmodifiable();
      } else {
         List<VaultGearModifier<?>> affixes = new ArrayList<>();
         affixes.addAll(data.getModifiers(VaultGearModifier.AffixType.PREFIX));
         affixes.addAll(data.getModifiers(VaultGearModifier.AffixType.SUFFIX));
         affixes.removeIf(modifier -> !modifier.hasNoCategoryMatching(VaultGearModifier.AffixCategory::cannotBeModifiedByArtisanFoci));
         if (affixes.isEmpty()) {
            return GearModification.Result.makeActionError("no_modifiers");
         } else {
            VaultGearModifier<?> randomMod = MiscUtils.getRandomEntry(affixes, random);
            data.removeModifier(randomMod);
            data.write(stack);
            return GearModification.Result.makeSuccess();
         }
      }
   }

   public static int getAffixCount(ItemStack stack) {
      AttributeGearData data = AttributeGearData.read(stack);
      int prefixes = data.getFirstValue(ModGearAttributes.PREFIXES).orElse(0);
      int suffixes = data.getFirstValue(ModGearAttributes.SUFFIXES).orElse(0);
      return prefixes + suffixes;
   }

   public static boolean hasAnyOpenAffix(ItemStack stack) {
      VaultGearData data = VaultGearData.read(stack);
      int prefixes = data.getFirstValue(ModGearAttributes.PREFIXES).orElse(0);
      int suffixes = data.getFirstValue(ModGearAttributes.SUFFIXES).orElse(0);
      prefixes -= data.getModifiers(VaultGearModifier.AffixType.PREFIX).size();
      suffixes -= data.getModifiers(VaultGearModifier.AffixType.SUFFIX).size();
      return prefixes > 0 || suffixes > 0;
   }

   public static boolean hasOpenPrefix(ItemStack stack) {
      VaultGearData data = VaultGearData.read(stack);
      int prefixes = data.getFirstValue(ModGearAttributes.PREFIXES).orElse(0) - data.getModifiers(VaultGearModifier.AffixType.PREFIX).size();
      return prefixes > 0;
   }

   public static boolean hasOpenSuffix(ItemStack stack) {
      VaultGearData data = VaultGearData.read(stack);
      int suffixes = data.getFirstValue(ModGearAttributes.SUFFIXES).orElse(0) - data.getModifiers(VaultGearModifier.AffixType.SUFFIX).size();
      return suffixes > 0;
   }

   public static GearModification.Result removeAllModifiers(ItemStack stack) {
      GearModification.Result result = removeAllModifiersOfType(stack, VaultGearModifier.AffixType.PREFIX);
      return !result.success() ? result : removeAllModifiersOfType(stack, VaultGearModifier.AffixType.SUFFIX);
   }

   public static GearModification.Result removeAllModifiersOfType(ItemStack stack, VaultGearModifier.AffixType type) {
      VaultGearData data = VaultGearData.read(stack);
      if (!data.isModifiable()) {
         return GearModification.Result.errorUnmodifiable();
      } else {
         for (VaultGearModifier<?> modifier : new ArrayList<>(data.getModifiers(type))) {
            if (modifier.hasNoCategoryMatching(VaultGearModifier.AffixCategory::cannotBeModifiedByArtisanFoci)) {
               data.removeModifier(modifier);
            }
         }

         data.write(stack);
         return GearModification.Result.makeSuccess();
      }
   }

   public static boolean createOrReplaceAbilityEnhancementModifier(ItemStack stack, Random random) {
      VaultGearData data = VaultGearData.read(stack);
      VaultGearTierConfig cfg = VaultGearTierConfig.getConfig(stack).orElse(null);
      if (cfg == null) {
         VaultMod.LOGGER.error("Unknown VaultGear: " + stack);
         return false;
      } else if (!data.isModifiable()) {
         return false;
      } else {
         Optional<VaultGearModifier<?>> modifierOpt = cfg.getRandomModifier(
            VaultGearTierConfig.ModifierAffixTagGroup.ABILITY_ENHANCEMENT, data.getItemLevel(), random, Collections.emptySet()
         );
         if (modifierOpt.isEmpty()) {
            return false;
         } else {
            VaultGearModifier<?> newModifier = modifierOpt.get();
            newModifier.addCategory(VaultGearModifier.AffixCategory.ABILITY_ENHANCEMENT);
            VaultGearModifier.AffixType targetType = VaultGearTierConfig.ModifierAffixTagGroup.ABILITY_ENHANCEMENT.getTargetAffixType();
            if (targetType == null) {
               throw new IllegalStateException("Ability enhancement affix has no valid target type.");
            } else {
               List<VaultGearModifier<?>> implicits = data.getModifiers(targetType);

               for (VaultGearModifier<?> modifier : new ArrayList<>(implicits)) {
                  if (modifier.hasCategory(VaultGearModifier.AffixCategory.ABILITY_ENHANCEMENT)) {
                     data.removeModifier(modifier);
                  }
               }

               if (!data.addModifierFirst(VaultGearModifier.AffixType.IMPLICIT, newModifier)) {
                  return false;
               } else {
                  data.write(stack);
                  return true;
               }
            }
         }
      }
   }

   public static GearModification.Result improveGearRarity(ItemStack stack, Random random) {
      VaultGearData data = VaultGearData.read(stack);
      if (!data.isModifiable()) {
         return GearModification.Result.errorUnmodifiable();
      } else {
         VaultGearRarity currentRarity = data.getRarity();
         VaultGearRarity nextRarity = currentRarity.getNextTier();
         if (nextRarity == null) {
            return GearModification.Result.makeActionError("max_rarity");
         } else {
            data.setRarity(nextRarity);
            data.write(stack);
            ItemStack copyStack = stack.copy();
            generateAffixSlots(copyStack, random);
            VaultGearData copyData = VaultGearData.read(copyStack);
            int copyAffixes = getAffixSlotCount(copyData);
            int affixes = getAffixSlotCount(data);
            if (copyAffixes > affixes) {
               data.createOrReplaceAttributeValue(
                  ModGearAttributes.PREFIXES,
                  copyData.getFirstValue(ModGearAttributes.PREFIXES).orElse(data.getFirstValue(ModGearAttributes.PREFIXES).orElse(0))
               );
               data.createOrReplaceAttributeValue(
                  ModGearAttributes.SUFFIXES,
                  copyData.getFirstValue(ModGearAttributes.SUFFIXES).orElse(data.getFirstValue(ModGearAttributes.SUFFIXES).orElse(0))
               );
               data.write(stack);
            }

            return GearModification.Result.makeSuccess();
         }
      }
   }

   public static void generateAffixSlots(ItemStack stack, Random random) {
      VaultGearData data = VaultGearData.read(stack);
      VaultGearRarity rarity = data.getRarity();
      if (stack.getItem() instanceof VaultGearItem item) {
         VaultGearClassification classification = item.getClassification(stack);
         int modifierCount = classification.getModifierCount(rarity);
         int prefixes = modifierCount / 2;
         int suffixes = modifierCount / 2;
         if (modifierCount - prefixes - suffixes > 0) {
            if (random.nextBoolean()) {
               prefixes++;
            } else {
               suffixes++;
            }
         }

         if (classification.hasOnlySuffixes()) {
            prefixes = 0;
            suffixes = classification.getModifierCount(rarity);
         }

         data.createOrReplaceAttributeValue(ModGearAttributes.PREFIXES, Integer.valueOf(prefixes));
         data.createOrReplaceAttributeValue(ModGearAttributes.SUFFIXES, Integer.valueOf(suffixes));
      }

      data.write(stack);
   }

   public static GearModification.Result generateBaseAttributes(ItemStack stack, Random random) {
      VaultGearData data = VaultGearData.read(stack);
      VaultGearTierConfig cfg = VaultGearTierConfig.getConfig(stack).orElse(null);
      if (cfg == null) {
         VaultMod.LOGGER.error("Unknown VaultGear: " + stack);
         return GearModification.Result.errorUnmodifiable();
      } else if (!data.isModifiable()) {
         return GearModification.Result.errorUnmodifiable();
      } else {
         int itemLevel = data.getItemLevel();
         cfg.generateBaseAttributes(itemLevel, random).stream().map(VaultGearModifier::cast).forEach(data::addAttribute);
         data.write(stack);
         return GearModification.Result.makeSuccess();
      }
   }

   public static GearModification.Result generateImplicits(ItemStack stack, Random random) {
      VaultGearData data = VaultGearData.read(stack);
      VaultGearTierConfig cfg = VaultGearTierConfig.getConfig(stack).orElse(null);
      if (cfg == null) {
         VaultMod.LOGGER.error("Unknown VaultGear: " + stack);
         return GearModification.Result.errorUnmodifiable();
      } else if (!data.isModifiable()) {
         return GearModification.Result.errorUnmodifiable();
      } else {
         int itemLevel = data.getItemLevel();
         cfg.generateImplicits(itemLevel, random).forEach(modifier -> {
            if (!data.hasModifier(modifier.getModifierIdentifier())) {
               data.addModifier(VaultGearModifier.AffixType.IMPLICIT, (VaultGearModifier<?>)modifier);
            }
         });
         data.write(stack);
         return GearModification.Result.makeSuccess();
      }
   }

   public static GearModification.Result generateCorruptedImplicit(ItemStack stack, Random random) {
      VaultGearData data = VaultGearData.read(stack);
      VaultGearTierConfig cfg = VaultGearTierConfig.getConfig(stack).orElse(null);
      if (cfg == null) {
         VaultMod.LOGGER.error("Unknown VaultGear: " + stack);
         return GearModification.Result.errorUnmodifiable();
      } else if (!data.isModifiable()) {
         return GearModification.Result.errorUnmodifiable();
      } else {
         int itemLevel = data.getItemLevel();
         Set<String> groups = data.getExistingModifierGroups(VaultGearData.Type.IMPLICIT_MODIFIERS);
         return cfg.getRandomModifier(VaultGearTierConfig.ModifierAffixTagGroup.CORRUPTED_IMPLICIT, itemLevel, random, groups).map(modifier -> {
            modifier.addCategory(VaultGearModifier.AffixCategory.CORRUPTED);
            data.addModifierFirst(VaultGearModifier.AffixType.IMPLICIT, (VaultGearModifier<?>)modifier);
            data.write(stack);
            return GearModification.Result.makeSuccess();
         }).orElse(GearModification.Result.makeActionError("no_modifiers"));
      }
   }

   public static GearModification.Result generateRepairSlots(ItemStack stack, Random random) {
      VaultGearData data = VaultGearData.read(stack);
      if (!data.isModifiable()) {
         return GearModification.Result.errorUnmodifiable();
      } else {
         int repairs = 1 + random.nextInt(8);
         data.setRepairSlots(repairs);
         data.write(stack);
         return GearModification.Result.makeSuccess();
      }
   }

   public static GearModification.Result setGearCorrupted(ItemStack stack) {
      VaultGearData data = VaultGearData.read(stack);
      if (!data.isModifiable()) {
         return GearModification.Result.errorUnmodifiable();
      } else {
         data.createOrReplaceAttributeValue(ModGearAttributes.IS_CORRUPTED, Boolean.valueOf(true));
         data.write(stack);
         return GearModification.Result.makeSuccess();
      }
   }

   public static GearModification.Result lockRandomAffix(ItemStack stack, Random random) {
      VaultGearData data = VaultGearData.read(stack);
      if (!data.isModifiable()) {
         return GearModification.Result.errorUnmodifiable();
      } else {
         List<VaultGearModifier<?>> affixes = new ArrayList<>();
         affixes.addAll(data.getModifiers(VaultGearModifier.AffixType.PREFIX));
         affixes.addAll(data.getModifiers(VaultGearModifier.AffixType.SUFFIX));
         if (affixes.stream().anyMatch(modifier -> modifier.hasCategory(VaultGearModifier.AffixCategory.FROZEN))) {
            return GearModification.Result.makeActionError("frozen");
         } else {
            VaultGearModifier<?> randomModifier = MiscUtils.getRandomEntry(affixes, random);
            if (randomModifier == null) {
               return GearModification.Result.errorInternal();
            } else {
               randomModifier.addCategory(VaultGearModifier.AffixCategory.FROZEN);
               data.write(stack);
               return GearModification.Result.makeSuccess();
            }
         }
      }
   }

   public static GearModification.Result generateModifiersOfAffix(ItemStack stack, VaultGearModifier.AffixType affixType, Random random) {
      VaultGearData data = VaultGearData.read(stack);
      VaultGearTierConfig cfg = VaultGearTierConfig.getConfig(stack).orElse(null);
      if (cfg == null) {
         VaultMod.LOGGER.error("Unknown VaultGear: " + stack);
         return GearModification.Result.errorUnmodifiable();
      } else if (!data.isModifiable()) {
         return GearModification.Result.errorUnmodifiable();
      } else {
         int itemLevel = data.getItemLevel();
         int maxPrefixes = data.getFirstValue(ModGearAttributes.PREFIXES).orElse(0);
         int maxSuffixes = data.getFirstValue(ModGearAttributes.SUFFIXES).orElse(0);
         if (affixType == VaultGearModifier.AffixType.PREFIX && maxPrefixes <= 0) {
            return GearModification.Result.makeActionError("no_prefixes");
         } else if (affixType == VaultGearModifier.AffixType.SUFFIX && maxSuffixes <= 0) {
            return GearModification.Result.makeActionError("no_suffixes");
         } else {
            int prefixCount = data.getModifiers(VaultGearModifier.AffixType.PREFIX).size();
            int suffixCount = data.getModifiers(VaultGearModifier.AffixType.SUFFIX).size();
            int possibleGenerated = getGeneratedModifierCount(maxPrefixes + maxSuffixes, itemLevel, random);
            int toGenerate = possibleGenerated - Math.min(prefixCount, maxPrefixes) - Math.min(suffixCount, maxSuffixes);
            if (affixType == VaultGearModifier.AffixType.PREFIX) {
               toGenerate = Math.min(toGenerate, maxPrefixes - prefixCount);
            } else {
               toGenerate = Math.min(toGenerate, maxSuffixes - suffixCount);
            }

            for (int i = 0; i < toGenerate; i++) {
               VaultGearModifier<?> modifier = cfg.getRandomModifier(
                     affixType, itemLevel, random, data.getExistingModifierGroups(VaultGearData.Type.EXPLICIT_MODIFIERS)
                  )
                  .orElse(null);
               if (modifier != null && data.addModifier(affixType, modifier)) {
                  if (affixType == VaultGearModifier.AffixType.PREFIX) {
                     prefixCount++;
                  } else {
                     suffixCount++;
                  }
               }
            }

            data.write(stack);
            return GearModification.Result.makeSuccess();
         }
      }
   }

   public static GearModification.Result generateModifiers(ItemStack stack, Random random) {
      VaultGearData data = VaultGearData.read(stack);
      VaultGearTierConfig cfg = VaultGearTierConfig.getConfig(stack).orElse(null);
      if (cfg == null) {
         VaultMod.LOGGER.error("Unknown VaultGear: " + stack);
         return GearModification.Result.errorUnmodifiable();
      } else if (!data.isModifiable()) {
         return GearModification.Result.errorUnmodifiable();
      } else {
         int itemLevel = data.getItemLevel();
         int maxPrefixes = data.getFirstValue(ModGearAttributes.PREFIXES).orElse(0) - data.getModifiers(VaultGearModifier.AffixType.PREFIX).size();
         int maxSuffixes = data.getFirstValue(ModGearAttributes.SUFFIXES).orElse(0) - data.getModifiers(VaultGearModifier.AffixType.SUFFIX).size();
         int generatedModifiers = getGeneratedModifierCount(maxPrefixes + maxSuffixes, itemLevel, random);
         int rolledPrefixes = 0;
         int rolledSuffixes = 0;
         if (stack.getItem() == ModItems.JEWEL) {
            generatedModifiers = maxPrefixes + maxSuffixes;
         }

         for (int i = 0; i < generatedModifiers; i++) {
            List<VaultGearModifier.AffixType> types = new ArrayList<>();
            if (rolledPrefixes < maxPrefixes) {
               types.add(VaultGearModifier.AffixType.PREFIX);
            }

            if (rolledSuffixes < maxSuffixes) {
               types.add(VaultGearModifier.AffixType.SUFFIX);
            }

            VaultGearModifier.AffixType type = MiscUtils.getRandomEntry(types, random);
            if (type != null) {
               VaultGearModifier<?> modifier = cfg.getRandomModifier(
                     type, itemLevel, random, data.getExistingModifierGroups(VaultGearData.Type.EXPLICIT_MODIFIERS)
                  )
                  .orElse(null);
               if (modifier != null && data.addModifier(type, modifier)) {
                  if (type == VaultGearModifier.AffixType.PREFIX) {
                     rolledPrefixes++;
                  } else {
                     rolledSuffixes++;
                  }
               }
            }
         }

         data.write(stack);
         return GearModification.Result.makeSuccess();
      }
   }

   private static int getAffixSlotCount(VaultGearData data) {
      return data.getFirstValue(ModGearAttributes.PREFIXES).orElse(0) + data.getFirstValue(ModGearAttributes.SUFFIXES).orElse(0);
   }

   private static List<Tuple<VaultGearModifier<?>, WeightedList<VaultGearTierConfig.ModifierOutcome<?>>>> getAvailableModifierConfigurationOutcomes(
      VaultGearData data, ItemStack stack, boolean includeOnlyModifiableModifiers
   ) {
      List<Tuple<VaultGearModifier<?>, WeightedList<VaultGearTierConfig.ModifierOutcome<?>>>> modifierReplacements = new ArrayList<>();
      if (!data.isModifiable()) {
         return modifierReplacements;
      } else {
         VaultGearTierConfig cfg = VaultGearTierConfig.getConfig(stack).orElse(null);
         if (cfg == null) {
            VaultMod.LOGGER.error("Unknown VaultGear: " + stack);
            return modifierReplacements;
         } else {
            int itemLevel = data.getItemLevel();
            data.getModifiers(VaultGearModifier.AffixType.PREFIX)
               .forEach(
                  modifier -> {
                     if (includeOnlyModifiableModifiers) {
                        if (!modifier.hasNoCategoryMatching(VaultGearModifier.AffixCategory::cannotBeModifiedByArtisanFoci)) {
                           return;
                        }

                        if (!modifier.hasNoCategoryMatching(VaultGearModifier.AffixCategory::cannotBeRolledByArtisanFoci)) {
                           return;
                        }
                     }

                     VaultGearTierConfig.ModifierTierGroup group = cfg.getTierGroup(modifier.getModifierIdentifier());
                     if (group != null) {
                        WeightedList<VaultGearTierConfig.ModifierOutcome<?>> replacementTiers = new WeightedList<>();
                        group.getModifiersForLevel(itemLevel)
                           .forEach(
                              tier -> replacementTiers.add(
                                 new VaultGearTierConfig.ModifierOutcome<>((VaultGearTierConfig.ModifierTier<?>)tier, group), tier.getWeight()
                              )
                           );
                        modifierReplacements.add(new Tuple(modifier, replacementTiers));
                     }
                  }
               );
            data.getModifiers(VaultGearModifier.AffixType.SUFFIX)
               .forEach(
                  modifier -> {
                     if (includeOnlyModifiableModifiers) {
                        if (!modifier.hasNoCategoryMatching(VaultGearModifier.AffixCategory::cannotBeModifiedByArtisanFoci)) {
                           return;
                        }

                        if (!modifier.hasNoCategoryMatching(VaultGearModifier.AffixCategory::cannotBeRolledByArtisanFoci)) {
                           return;
                        }
                     }

                     VaultGearTierConfig.ModifierTierGroup group = cfg.getTierGroup(modifier.getModifierIdentifier());
                     if (group != null) {
                        WeightedList<VaultGearTierConfig.ModifierOutcome<?>> replacementTiers = new WeightedList<>();
                        group.getModifiersForLevel(itemLevel)
                           .forEach(
                              tier -> replacementTiers.add(
                                 new VaultGearTierConfig.ModifierOutcome<>((VaultGearTierConfig.ModifierTier<?>)tier, group), tier.getWeight()
                              )
                           );
                        modifierReplacements.add(new Tuple(modifier, replacementTiers));
                     }
                  }
               );
            return modifierReplacements;
         }
      }
   }

   private static WeightedList<VaultGearModifierHelper.TierGroupOutcome> getAvailableModGroupOutcomes(
      VaultGearTagConfig.ModTagGroup modGroupTag, ItemStack stack
   ) {
      return getAvailableModGroupOutcomes(modGroupTag.getTags(), stack, false);
   }

   private static WeightedList<VaultGearModifierHelper.TierGroupOutcome> getAvailableModGroupOutcomes(List<String> tags, ItemStack stack, boolean anyGroup) {
      return getAvailableModGroupOutcomes(group -> !MiscUtils.intersection(group.getTags(), tags).isEmpty(), stack, anyGroup);
   }

   private static WeightedList<VaultGearModifierHelper.TierGroupOutcome> getAvailableModGroupOutcomes(
      Predicate<VaultGearTierConfig.ModifierTierGroup> filter, ItemStack stack, boolean anyGroup
   ) {
      WeightedList<VaultGearModifierHelper.TierGroupOutcome> groupOutcomes = new WeightedList<>();
      VaultGearTierConfig cfg = VaultGearTierConfig.getConfig(stack).orElse(null);
      if (cfg == null) {
         return groupOutcomes;
      } else {
         VaultGearData data = VaultGearData.read(stack);
         int itemLevel = data.getItemLevel();
         Set<String> existingGroups = data.getExistingModifierGroups(VaultGearData.Type.ALL);
         boolean generatePrefixes = data.getFirstValue(ModGearAttributes.PREFIXES).orElse(0) > data.getModifiers(VaultGearModifier.AffixType.PREFIX).size();
         boolean generateSuffixes = data.getFirstValue(ModGearAttributes.SUFFIXES).orElse(0) > data.getModifiers(VaultGearModifier.AffixType.SUFFIX).size();
         cfg.getAnyGroupsFulfilling(filter)
            .forEach(
               tpl -> {
                  if (anyGroup || ((VaultGearTierConfig.ModifierAffixTagGroup)tpl.getA()).isGenericGroup()) {
                     if (!existingGroups.contains(((VaultGearTierConfig.ModifierTierGroup)tpl.getB()).getModifierGroup())) {
                        VaultGearModifier.AffixType type = ((VaultGearTierConfig.ModifierAffixTagGroup)tpl.getA()).getTargetAffixType();
                        if (type != null) {
                           if (type == VaultGearModifier.AffixType.PREFIX && !generatePrefixes) {
                              return;
                           }

                           if (type == VaultGearModifier.AffixType.SUFFIX && !generateSuffixes) {
                              return;
                           }
                        }

                        ((VaultGearTierConfig.ModifierTierGroup)tpl.getB())
                           .getModifiersForLevel(itemLevel)
                           .forEach(
                              tier -> groupOutcomes.add(
                                 new VaultGearModifierHelper.TierGroupOutcome(
                                    (VaultGearTierConfig.ModifierAffixTagGroup)tpl.getA(),
                                    (VaultGearTierConfig.ModifierTierGroup)tpl.getB(),
                                    (VaultGearTierConfig.ModifierTier<?>)tier
                                 ),
                                 tier.getWeight()
                              )
                           );
                     }
                  }
               }
            );
         return groupOutcomes;
      }
   }

   private static int getGeneratedModifierCount(int modifierCount, int itemLevel, Random random) {
      return itemLevel <= 20 ? modifierCount : Math.round(modifierCount * 0.6F + modifierCount * 0.4F * random.nextFloat());
   }

   public record TierGroupOutcome(
      VaultGearTierConfig.ModifierAffixTagGroup type, VaultGearTierConfig.ModifierTierGroup tierGroup, VaultGearTierConfig.ModifierTier<?> tier
   ) {
   }
}
