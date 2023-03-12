package iskallia.vault.gear;

import iskallia.vault.VaultMod;
import iskallia.vault.config.gear.VaultGearTagConfig;
import iskallia.vault.config.gear.VaultGearTierConfig;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModItems;
import iskallia.vault.util.MiscUtils;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;

public class VaultGearModifierHelper {
   public static void reForgeAllModifiers(ItemStack stack, Random random) {
      removeAllModifiers(stack);
      generateModifiers(stack, random);
   }

   public static void reForgeAllImplicits(ItemStack stack, Random random) {
      removeAllModifiersOfType(stack, VaultGearModifier.AffixType.IMPLICIT);
      generateImplicits(stack, random);
   }

   public static boolean reForgeAllWithTag(VaultGearTagConfig.ModTagGroup modGroupTag, ItemStack stack, Random random) {
      ItemStack emptyCopy = stack.copy();
      removeAllModifiers(emptyCopy);
      if (getAvailableModGroupOutcomes(modGroupTag, emptyCopy).isEmpty()) {
         return false;
      } else {
         removeAllModifiers(stack);
         if (!addNewModifierOfGroup(modGroupTag, stack, random)) {
            return false;
         } else {
            generateModifiers(stack, random);
            return true;
         }
      }
   }

   public static boolean addNewModifierOfGroup(VaultGearTagConfig.ModTagGroup modGroupTag, ItemStack stack, Random random) {
      WeightedList<VaultGearModifierHelper.TierGroupOutcome> outcomes = getAvailableModGroupOutcomes(modGroupTag, stack);
      Map<VaultGearTierConfig.ModifierTierGroup, Integer> highestTiers = new WeightedList<>();

      for (VaultGearModifierHelper.TierGroupOutcome outcome : outcomes.keySet()) {
         int current = highestTiers.getOrDefault(outcome.tierGroup, -1);
         highestTiers.put(outcome.tierGroup, Math.max(outcome.tier.getModifierTier(), current));
      }

      outcomes.entrySet().removeIf(entry -> entry.getKey().tier.getModifierTier() != highestTiers.get(entry.getKey().tierGroup));
      VaultGearModifierHelper.TierGroupOutcome outcome = outcomes.getRandom(random).orElse(null);
      if (outcome == null) {
         return false;
      } else {
         VaultGearData data = VaultGearData.read(stack);
         data.addModifier(outcome.type(), outcome.tier().makeModifier(outcome.tierGroup(), random));
         data.write(stack);
         return true;
      }
   }

   private static WeightedList<VaultGearModifierHelper.TierGroupOutcome> getAvailableModGroupOutcomes(
      VaultGearTagConfig.ModTagGroup modGroupTag, ItemStack stack
   ) {
      WeightedList<VaultGearModifierHelper.TierGroupOutcome> groupOutcomes = new WeightedList<>();
      VaultGearTierConfig cfg = VaultGearTierConfig.getConfig(stack.getItem()).orElse(null);
      if (cfg == null) {
         return groupOutcomes;
      } else {
         VaultGearData data = VaultGearData.read(stack);
         int itemLevel = data.getItemLevel();
         Set<String> existingGroups = data.getExistingModifierGroups(VaultGearData.Type.EXPLICIT_MODIFIERS);
         boolean generatePrefixes = data.getFirstValue(ModGearAttributes.PREFIXES).orElse(0) > data.getModifiers(VaultGearModifier.AffixType.PREFIX).size();
         boolean generateSuffixes = data.getFirstValue(ModGearAttributes.SUFFIXES).orElse(0) > data.getModifiers(VaultGearModifier.AffixType.SUFFIX).size();
         if (!generatePrefixes && !generateSuffixes) {
            return groupOutcomes;
         } else {
            modGroupTag.getTags()
               .forEach(
                  tag -> cfg.getModifierConfigurationsByTag(tag)
                     .forEach(
                        tpl -> {
                           if (((VaultGearTierConfig.ModifierAffixTagGroup)tpl.getA()).isGenericGroup()) {
                              if (!existingGroups.contains(((VaultGearTierConfig.ModifierTierGroup)tpl.getB()).getModifierGroup())) {
                                 VaultGearModifier.AffixType type = ((VaultGearTierConfig.ModifierAffixTagGroup)tpl.getA()).getTargetAffixType();
                                 if (type != VaultGearModifier.AffixType.PREFIX || generatePrefixes) {
                                    if (type != VaultGearModifier.AffixType.SUFFIX || generateSuffixes) {
                                       ((VaultGearTierConfig.ModifierTierGroup)tpl.getB())
                                          .getModifiersForLevel(itemLevel)
                                          .forEach(
                                             tier -> groupOutcomes.add(
                                                new VaultGearModifierHelper.TierGroupOutcome(
                                                   type, (VaultGearTierConfig.ModifierTierGroup)tpl.getB(), (VaultGearTierConfig.ModifierTier<?>)tier
                                                ),
                                                tier.getWeight()
                                             )
                                          );
                                    }
                                 }
                              }
                           }
                        }
                     )
               );
            return groupOutcomes;
         }
      }
   }

   public static boolean reForgeTierOfRandomModifier(ItemStack stack, long worldGameTime, Random random) {
      VaultGearData data = VaultGearData.read(stack);
      VaultGearTierConfig cfg = VaultGearTierConfig.getConfig(stack.getItem()).orElse(null);
      if (cfg == null) {
         VaultMod.LOGGER.error("Unknown VaultGear: " + stack);
         return false;
      } else {
         int itemLevel = data.getItemLevel();
         List<Tuple<VaultGearModifier<?>, VaultGearModifier<?>>> modifierReplacements = new ArrayList<>();
         data.getModifiers(VaultGearModifier.AffixType.PREFIX).forEach(modifier -> {
            if (modifier.getCategory().isModifiableByArtisanFoci()) {
               VaultGearModifier<?> randomNew = cfg.generateModifier(modifier.getModifierIdentifier(), itemLevel, random);
               if (randomNew != null && modifier.getModifierIdentifier().equals(randomNew.getModifierIdentifier())) {
                  modifierReplacements.add(new Tuple(modifier, randomNew));
               }
            }
         });
         data.getModifiers(VaultGearModifier.AffixType.SUFFIX).forEach(modifier -> {
            if (modifier.getCategory().isModifiableByArtisanFoci()) {
               VaultGearModifier<?> randomNew = cfg.generateModifier(modifier.getModifierIdentifier(), itemLevel, random);
               if (randomNew != null && modifier.getModifierIdentifier().equals(randomNew.getModifierIdentifier())) {
                  modifierReplacements.add(new Tuple(modifier, randomNew));
               }
            }
         });
         Tuple<VaultGearModifier<?>, VaultGearModifier<?>> replacement = MiscUtils.getRandomEntry(modifierReplacements, random);
         if (replacement == null) {
            return false;
         } else {
            VaultGearModifier existing = (VaultGearModifier)replacement.getA();
            VaultGearModifier newModifier = (VaultGearModifier)replacement.getB();
            existing.setValue(newModifier.getValue());
            existing.setRolledTier(newModifier.getRolledTier());
            existing.setGameTimeAdded(worldGameTime);
            existing.setCategory(VaultGearModifier.AffixCategory.NONE);
            data.write(stack);
            return true;
         }
      }
   }

   public static boolean addNewModifier(ItemStack stack, long worldGameTime, Random random) {
      VaultGearData data = VaultGearData.read(stack);
      VaultGearTierConfig cfg = VaultGearTierConfig.getConfig(stack.getItem()).orElse(null);
      if (cfg == null) {
         VaultMod.LOGGER.error("Unknown VaultGear: " + stack);
         return false;
      } else {
         int itemLevel = data.getItemLevel();
         int prefixes = data.getFirstValue(ModGearAttributes.PREFIXES).orElse(0);
         int suffixes = data.getFirstValue(ModGearAttributes.SUFFIXES).orElse(0);
         prefixes -= data.getModifiers(VaultGearModifier.AffixType.PREFIX).size();
         suffixes -= data.getModifiers(VaultGearModifier.AffixType.SUFFIX).size();
         if (prefixes <= 0 && suffixes <= 0) {
            return false;
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
               return true;
            }).orElse(false);
         }
      }
   }

   public static boolean removeRandomModifier(ItemStack stack, Random random) {
      VaultGearData data = VaultGearData.read(stack);
      int prefixes = data.getModifiers(VaultGearModifier.AffixType.PREFIX).size();
      int suffixes = data.getModifiers(VaultGearModifier.AffixType.SUFFIX).size();
      if (prefixes <= 0 && suffixes <= 0) {
         return false;
      } else {
         List<VaultGearModifier.AffixType> types = new ArrayList<>();
         if (prefixes > 0) {
            types.add(VaultGearModifier.AffixType.PREFIX);
         }

         if (suffixes > 0) {
            types.add(VaultGearModifier.AffixType.SUFFIX);
         }

         VaultGearModifier.AffixType type = MiscUtils.getRandomEntry(types, random);
         if (type == null) {
            return false;
         } else {
            List<VaultGearModifier<?>> modifiers = new ArrayList<>(data.getModifiers(type));
            modifiers.removeIf(modifier -> !modifier.getCategory().isModifiableByArtisanFoci());
            if (modifiers.isEmpty()) {
               return false;
            } else {
               VaultGearModifier<?> randomMod = MiscUtils.getRandomEntry(modifiers, random);
               data.removeModifier(randomMod);
               data.write(stack);
               return true;
            }
         }
      }
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

   public static void removeAllModifiers(ItemStack stack) {
      removeAllModifiersOfType(stack, VaultGearModifier.AffixType.PREFIX);
      removeAllModifiersOfType(stack, VaultGearModifier.AffixType.SUFFIX);
   }

   public static void removeAllModifiersOfType(ItemStack stack, VaultGearModifier.AffixType type) {
      VaultGearData data = VaultGearData.read(stack);

      for (VaultGearModifier<?> modifier : new ArrayList<>(data.getModifiers(type))) {
         if (modifier.getCategory().isModifiableByArtisanFoci()) {
            data.removeModifier(modifier);
         }
      }

      data.write(stack);
   }

   public static boolean createOrReplaceAbilityEnhancementModifier(ItemStack stack, Random random) {
      VaultGearData data = VaultGearData.read(stack);
      VaultGearTierConfig cfg = VaultGearTierConfig.getConfig(stack.getItem()).orElse(null);
      if (cfg == null) {
         VaultMod.LOGGER.error("Unknown VaultGear: " + stack);
         return false;
      } else {
         Optional<VaultGearModifier<?>> modifierOpt = cfg.getRandomModifier(
            VaultGearTierConfig.ModifierAffixTagGroup.ABILITY_ENHANCEMENT, data.getItemLevel(), random, Collections.emptySet()
         );
         if (modifierOpt.isEmpty()) {
            return false;
         } else {
            VaultGearModifier<?> newModifier = modifierOpt.get();
            newModifier.setCategory(VaultGearModifier.AffixCategory.ABILITY_ENHANCEMENT);
            List<VaultGearModifier<?>> implicits = data.getModifiers(VaultGearTierConfig.ModifierAffixTagGroup.ABILITY_ENHANCEMENT.getTargetAffixType());

            for (VaultGearModifier<?> modifier : new ArrayList<>(implicits)) {
               if (modifier.getCategory() == VaultGearModifier.AffixCategory.ABILITY_ENHANCEMENT) {
                  data.removeModifier(modifier);
               }
            }

            data.addModifierFirst(VaultGearModifier.AffixType.IMPLICIT, newModifier);
            data.write(stack);
            return true;
         }
      }
   }

   public static boolean generateLegendaryModifier(ItemStack stack, Random random) {
      VaultGearData data = VaultGearData.read(stack);
      VaultGearTierConfig cfg = VaultGearTierConfig.getConfig(stack.getItem()).orElse(null);
      if (cfg == null) {
         VaultMod.LOGGER.error("Unknown VaultGear: " + stack);
         return false;
      } else {
         List<Tuple<VaultGearModifier.AffixType, VaultGearModifier<?>>> modifiers = new ArrayList<>();
         data.getModifiers(VaultGearModifier.AffixType.PREFIX).forEach(modifier -> modifiers.add(new Tuple(VaultGearModifier.AffixType.PREFIX, modifier)));
         data.getModifiers(VaultGearModifier.AffixType.SUFFIX).forEach(modifier -> modifiers.add(new Tuple(VaultGearModifier.AffixType.SUFFIX, modifier)));
         modifiers.removeIf(tpl -> cfg.getAllTiers(((VaultGearModifier)tpl.getB()).getModifierIdentifier()).size() <= 1);
         modifiers.removeIf(tpl -> !((VaultGearModifier)tpl.getB()).getCategory().isModifiableByArtisanFoci());
         Tuple<VaultGearModifier.AffixType, VaultGearModifier<?>> randomMod = MiscUtils.getRandomEntry(modifiers, random);
         if (randomMod == null) {
            return false;
         } else {
            VaultGearModifier<?> newMod = cfg.maxAndIncreaseTier(
               (VaultGearModifier.AffixType)randomMod.getA(), (VaultGearModifier<?>)randomMod.getB(), data.getItemLevel(), 2, random
            );
            if (newMod == null) {
               return false;
            } else {
               newMod.setCategory(VaultGearModifier.AffixCategory.LEGENDARY);
               if (data.removeModifier((VaultGearModifier<?>)randomMod.getB())) {
                  data.addModifierFirst((VaultGearModifier.AffixType)randomMod.getA(), newMod);
               }

               data.write(stack);
               return true;
            }
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

         if (classification == VaultGearClassification.IDOL) {
            prefixes = 0;
            suffixes = classification.getModifierCount(rarity);
         }

         data.updateAttribute(ModGearAttributes.PREFIXES, Integer.valueOf(prefixes));
         data.updateAttribute(ModGearAttributes.SUFFIXES, Integer.valueOf(suffixes));
      }

      data.write(stack);
   }

   public static void generateImplicits(ItemStack stack, Random random) {
      VaultGearData data = VaultGearData.read(stack);
      VaultGearTierConfig cfg = VaultGearTierConfig.getConfig(stack.getItem()).orElse(null);
      if (cfg == null) {
         VaultMod.LOGGER.error("Unknown VaultGear: " + stack);
      } else {
         int itemLevel = data.getItemLevel();
         cfg.generateImplicits(itemLevel, random).forEach(modifier -> data.addModifier(VaultGearModifier.AffixType.IMPLICIT, (VaultGearModifier<?>)modifier));
         data.write(stack);
      }
   }

   public static void reRollRepairSlots(ItemStack stack, Random random) {
      VaultGearData data = VaultGearData.read(stack);
      int repairs = 2 + random.nextInt(4);
      data.setRepairSlots(repairs);
      data.write(stack);
   }

   public static boolean generateModifiersOfAffix(ItemStack stack, VaultGearModifier.AffixType affixType, Random random) {
      VaultGearData data = VaultGearData.read(stack);
      VaultGearTierConfig cfg = VaultGearTierConfig.getConfig(stack.getItem()).orElse(null);
      if (cfg == null) {
         VaultMod.LOGGER.error("Unknown VaultGear: " + stack);
         return false;
      } else {
         int itemLevel = data.getItemLevel();
         int maxPrefixes = data.getFirstValue(ModGearAttributes.PREFIXES).orElse(0);
         int maxSuffixes = data.getFirstValue(ModGearAttributes.SUFFIXES).orElse(0);
         if (affixType == VaultGearModifier.AffixType.PREFIX && maxPrefixes <= 0) {
            return false;
         } else if (affixType == VaultGearModifier.AffixType.SUFFIX && maxSuffixes <= 0) {
            return false;
         } else {
            int prefixCount = data.getModifiers(VaultGearModifier.AffixType.PREFIX).size();
            int suffixCount = data.getModifiers(VaultGearModifier.AffixType.SUFFIX).size();
            int possibleGenerated = getGeneratedModifierCount(maxPrefixes + maxSuffixes, random);
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
               if (modifier != null) {
                  data.addModifier(affixType, modifier);
                  if (affixType == VaultGearModifier.AffixType.PREFIX) {
                     prefixCount++;
                  } else {
                     suffixCount++;
                  }
               }
            }

            data.write(stack);
            return true;
         }
      }
   }

   public static void generateModifiers(ItemStack stack, Random random) {
      VaultGearData data = VaultGearData.read(stack);
      VaultGearTierConfig cfg = VaultGearTierConfig.getConfig(stack.getItem()).orElse(null);
      if (cfg == null) {
         VaultMod.LOGGER.error("Unknown VaultGear: " + stack);
      } else {
         int itemLevel = data.getItemLevel();
         int maxPrefixes = data.getFirstValue(ModGearAttributes.PREFIXES).orElse(0) - data.getModifiers(VaultGearModifier.AffixType.PREFIX).size();
         int maxSuffixes = data.getFirstValue(ModGearAttributes.SUFFIXES).orElse(0) - data.getModifiers(VaultGearModifier.AffixType.SUFFIX).size();
         int generatedModifiers = getGeneratedModifierCount(maxPrefixes + maxSuffixes, random);
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
               if (modifier != null) {
                  data.addModifier(type, modifier);
                  if (type == VaultGearModifier.AffixType.PREFIX) {
                     rolledPrefixes++;
                  } else {
                     rolledSuffixes++;
                  }
               }
            }
         }

         data.write(stack);
      }
   }

   private static int getGeneratedModifierCount(int modifierCount, Random random) {
      return Math.round(modifierCount * 0.6F + modifierCount * 0.4F * random.nextFloat());
   }

   public record TierGroupOutcome(VaultGearModifier.AffixType type, VaultGearTierConfig.ModifierTierGroup tierGroup, VaultGearTierConfig.ModifierTier<?> tier) {
   }
}
