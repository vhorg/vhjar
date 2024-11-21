package iskallia.vault.gear;

import iskallia.vault.VaultMod;
import iskallia.vault.config.gear.VaultGearTierConfig;
import iskallia.vault.core.util.WeightedList;
import iskallia.vault.gear.attribute.VaultGearAttribute;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.modification.GearModification;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.util.MiscUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.world.item.ItemStack;

public class VaultGearLegendaryHelper {
   public static boolean generateImprovedModifier(ItemStack stack, int tierIncrease, Random random, Collection<VaultGearModifier.AffixCategory> categoriesToAdd) {
      VaultGearData data = VaultGearData.read(stack);
      VaultGearTierConfig cfg = VaultGearTierConfig.getConfig(stack).orElse(null);
      if (cfg == null) {
         VaultMod.LOGGER.error("Unknown VaultGear: " + stack);
         return false;
      } else if (!data.isModifiable()) {
         return false;
      } else {
         return improveExistingModifier(stack, tierIncrease, random, cfg, data, categoriesToAdd)
            ? true
            : useOpenSlotToAddImprovedModifier(stack, tierIncrease, random, categoriesToAdd, cfg, data);
      }
   }

   private static boolean useOpenSlotToAddImprovedModifier(
      ItemStack stack,
      int tierIncrease,
      Random random,
      Collection<VaultGearModifier.AffixCategory> categoriesToAdd,
      VaultGearTierConfig cfg,
      VaultGearData data
   ) {
      List<VaultGearModifier.AffixType> types = new ArrayList<>();
      types.add(VaultGearModifier.AffixType.PREFIX);
      types.add(VaultGearModifier.AffixType.SUFFIX);
      Collections.shuffle(types, random);
      Set<String> existingGroups = data.getExistingModifierGroups(VaultGearData.Type.EXPLICIT_MODIFIERS);

      for (VaultGearModifier.AffixType type : types) {
         VaultGearAttribute<Integer> affixTypeAttribute = type == VaultGearModifier.AffixType.PREFIX ? ModGearAttributes.PREFIXES : ModGearAttributes.SUFFIXES;
         VaultGearTierConfig.ModifierAffixTagGroup affixTagGroup = type == VaultGearModifier.AffixType.PREFIX
            ? VaultGearTierConfig.ModifierAffixTagGroup.PREFIX
            : VaultGearTierConfig.ModifierAffixTagGroup.SUFFIX;
         int affixSlots = data.getFirstValue(affixTypeAttribute).orElse(0);
         List<VaultGearModifier<?>> affixes = data.getModifiers(type).stream().filter(VaultGearModifier::canBeModified).toList();
         List<ResourceLocation> affixIds = affixes.stream().map(VaultGearModifier::getModifierIdentifier).filter(Objects::nonNull).toList();
         if (affixes.size() < affixSlots) {
            List<Tuple<VaultGearTierConfig.ModifierAffixTagGroup, VaultGearTierConfig.ModifierTierGroup>> availableGroups = cfg.getGenericGroupsFulfilling(
               group -> !existingGroups.contains(group.getModifierGroup())
                  && !group.getTags().contains("noLegendary")
                  && !affixIds.contains(group.getIdentifier())
            );
            WeightedList<VaultGearTierConfig.ModifierOutcome<?>> outcomes = new WeightedList<>();
            availableGroups.forEach(group -> {
               if (group.getA() == affixTagGroup) {
                  VaultGearTierConfig.ModifierTier<?> tier = ((VaultGearTierConfig.ModifierTierGroup)group.getB()).getHighestForLevel(data.getItemLevel());
                  if (tier != null) {
                     outcomes.add(new VaultGearTierConfig.ModifierOutcome<>(tier, (VaultGearTierConfig.ModifierTierGroup)group.getB()), tier.getWeight());
                  }
               }
            });
            VaultGearTierConfig.ModifierOutcome<?> outcome = outcomes.getRandom(random).orElse(null);
            if (outcome != null) {
               VaultGearModifier<?> generatedModifier = outcome.makeModifier(random);
               VaultGearModifier<?> legendary = cfg.maxAndIncreaseTier(type, generatedModifier, data.getItemLevel(), tierIncrease, random);
               if (legendary != null) {
                  categoriesToAdd.forEach(legendary::addCategory);
                  data.addModifierFirst(type, legendary);
                  data.write(stack);
                  return true;
               }
            }
         }
      }

      return false;
   }

   public static GearModification.Result improveExistingModifier(
      ItemStack stack, int amount, Random random, Collection<VaultGearModifier.AffixCategory> categoriesToAdd
   ) {
      VaultGearData data = VaultGearData.read(stack);
      VaultGearTierConfig cfg = VaultGearTierConfig.getConfig(stack).orElse(null);
      if (cfg == null) {
         VaultMod.LOGGER.error("Unknown VaultGear: " + stack);
         return GearModification.Result.errorUnmodifiable();
      } else if (!data.isModifiable()) {
         return GearModification.Result.errorUnmodifiable();
      } else {
         return !improveExistingModifier(stack, amount, random, cfg, data, categoriesToAdd)
            ? GearModification.Result.errorInternal()
            : GearModification.Result.makeSuccess();
      }
   }

   private static boolean improveExistingModifier(
      ItemStack stack, int amount, Random random, VaultGearTierConfig cfg, VaultGearData data, Collection<VaultGearModifier.AffixCategory> categoriesToAdd
   ) {
      List<Tuple<VaultGearModifier.AffixType, VaultGearModifier<?>>> modifiers = new ArrayList<>();
      data.getModifiers(VaultGearModifier.AffixType.PREFIX).forEach(modifier -> modifiers.add(new Tuple(VaultGearModifier.AffixType.PREFIX, modifier)));
      data.getModifiers(VaultGearModifier.AffixType.SUFFIX).forEach(modifier -> modifiers.add(new Tuple(VaultGearModifier.AffixType.SUFFIX, modifier)));
      modifiers.removeIf(tpl -> {
         VaultGearTierConfig.ModifierTierGroup group = cfg.getTierGroup(((VaultGearModifier)tpl.getB()).getModifierIdentifier());
         return group == null ? false : group.getTags().contains("noLegendary");
      });
      modifiers.removeIf(tpl -> !((VaultGearModifier)tpl.getB()).hasNoCategoryMatching(VaultGearModifier.AffixCategory::cannotBeModifiedByArtisanFoci));
      Tuple<VaultGearModifier.AffixType, VaultGearModifier<?>> randomMod = MiscUtils.getRandomEntry(modifiers, random);
      if (randomMod == null) {
         return false;
      } else {
         VaultGearModifier<?> newMod = cfg.maxAndIncreaseTier(
            (VaultGearModifier.AffixType)randomMod.getA(), (VaultGearModifier<?>)randomMod.getB(), data.getItemLevel(), amount, random
         );
         if (newMod == null) {
            return false;
         } else {
            categoriesToAdd.forEach(newMod::addCategory);
            if (data.removeModifier((VaultGearModifier<?>)randomMod.getB())) {
               data.addModifierFirst((VaultGearModifier.AffixType)randomMod.getA(), newMod);
            }

            data.write(stack);
            return true;
         }
      }
   }
}
