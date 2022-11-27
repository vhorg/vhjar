package iskallia.vault.gear;

import iskallia.vault.VaultMod;
import iskallia.vault.config.gear.VaultGearTagConfig;
import iskallia.vault.config.gear.VaultGearTierConfig;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.util.MiscUtils;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;
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
      removeAllImplicits(stack);
      generateImplicits(stack, random);
   }

   public static boolean reForgeAllWithTag(VaultGearTagConfig.ModGroupTag modGroupTag, ItemStack stack, Random random) {
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

   public static boolean addNewModifierOfGroup(VaultGearTagConfig.ModGroupTag modGroupTag, ItemStack stack, Random random) {
      WeightedList<VaultGearModifierHelper.TierGroupOutcome> outcomes = getAvailableModGroupOutcomes(modGroupTag, stack);
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
      VaultGearTagConfig.ModGroupTag modGroupTag, ItemStack stack
   ) {
      WeightedList<VaultGearModifierHelper.TierGroupOutcome> groupOutcomes = new WeightedList<>();
      VaultGearTierConfig cfg = VaultGearTierConfig.getConfig(stack.getItem()).orElse(null);
      if (cfg == null) {
         return groupOutcomes;
      } else {
         VaultGearData data = VaultGearData.read(stack);
         int itemLevel = data.getItemLevel();
         Set<String> existingGroups = data.getExistingModifierGroups();
         boolean generatePrefixes = data.getFirstValue(ModGearAttributes.PREFIXES).orElse(0) > data.getModifiers(VaultGearModifier.AffixType.PREFIX).size();
         boolean generateSuffixes = data.getFirstValue(ModGearAttributes.SUFFIXES).orElse(0) > data.getModifiers(VaultGearModifier.AffixType.SUFFIX).size();
         if (!generatePrefixes && !generateSuffixes) {
            return groupOutcomes;
         } else {
            modGroupTag.getGroups()
               .forEach(
                  group -> {
                     if (!existingGroups.contains(group)) {
                        cfg.getModifierGroupConfigurations(group)
                           .forEach(
                              tpl -> {
                                 VaultGearModifier.AffixType type = (VaultGearModifier.AffixType)tpl.getA();
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
                           );
                     }
                  }
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
            VaultGearModifier<?> randomNew = cfg.generateModifier(modifier.getModifierIdentifier(), itemLevel, random);
            if (randomNew != null && modifier.getModifierIdentifier().equals(randomNew.getModifierIdentifier())) {
               modifierReplacements.add(new Tuple(modifier, randomNew));
            }
         });
         data.getModifiers(VaultGearModifier.AffixType.SUFFIX).forEach(modifier -> {
            VaultGearModifier<?> randomNew = cfg.generateModifier(modifier.getModifierIdentifier(), itemLevel, random);
            if (randomNew != null && modifier.getModifierIdentifier().equals(randomNew.getModifierIdentifier())) {
               modifierReplacements.add(new Tuple(modifier, randomNew));
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
            existing.setLegendary(false);
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
            cfg.getRandomModifier(type, itemLevel, random, data.getExistingModifierGroups()).ifPresent(modifier -> {
               data.getAllModifierAffixes().forEach(VaultGearModifier::resetGameTimeAdded);
               modifier.setGameTimeAdded(worldGameTime);
               data.addModifier(type, (VaultGearModifier<?>)modifier);
            });
            data.write(stack);
            return true;
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
         VaultGearModifier<?> randomMod = MiscUtils.getRandomEntry(data.getModifiers(type), random);
         data.removeModifier(randomMod);
         data.write(stack);
         return true;
      }
   }

   public static void removeAllModifiers(ItemStack stack) {
      VaultGearData data = VaultGearData.read(stack);
      new ArrayList<>(data.getModifiers(VaultGearModifier.AffixType.PREFIX)).forEach(data::removeModifier);
      new ArrayList<>(data.getModifiers(VaultGearModifier.AffixType.SUFFIX)).forEach(data::removeModifier);
      data.write(stack);
   }

   public static void removeAllImplicits(ItemStack stack) {
      VaultGearData data = VaultGearData.read(stack);
      new ArrayList<>(data.getModifiers(VaultGearModifier.AffixType.IMPLICIT)).forEach(data::removeModifier);
      data.write(stack);
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
               newMod.setLegendary(true);
               if (data.removeModifier((VaultGearModifier<?>)randomMod.getB())) {
                  data.addModifier((VaultGearModifier.AffixType)randomMod.getA(), newMod, Deque::addFirst);
               }

               data.write(stack);
               return true;
            }
         }
      }
   }

   public static void generateAffixSlots(ItemStack stack, Random random) {
      VaultGearData data = VaultGearData.read(stack);
      VaultGearItem item = VaultGearItem.of(stack);
      VaultGearRarity rarity = data.getRarity();
      VaultGearClassification classification = item.getClassification(stack);
      int modifierCount = classification.getModifierCount(rarity);
      int prefixes = modifierCount / 2;
      int suffixes = modifierCount / 2;
      if (rarity.getArmorModifierCount() - prefixes - suffixes > 0) {
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
               VaultGearModifier<?> modifier = cfg.getRandomModifier(type, itemLevel, random, data.getExistingModifierGroups()).orElse(null);
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

   private record TierGroupOutcome(VaultGearModifier.AffixType type, VaultGearTierConfig.ModifierTierGroup tierGroup, VaultGearTierConfig.ModifierTier<?> tier) {
   }
}
