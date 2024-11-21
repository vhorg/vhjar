package iskallia.vault.gear;

import iskallia.vault.config.gear.VaultGearTierConfig;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.attribute.config.ConfigurableAttributeGenerator;
import iskallia.vault.gear.data.AttributeGearData;
import iskallia.vault.gear.data.GearDataCache;
import iskallia.vault.gear.data.VaultGearData;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Map.Entry;
import net.minecraft.world.item.ItemStack;

public class GearScoreHelper {
   public static Optional<ItemStack> pickHighestWeight(Collection<ItemStack> stacks) {
      Map<ItemStack, Integer> weights = new HashMap<>();
      stacks.forEach(stack -> weights.put(stack, getWeight(stack)));
      return weights.entrySet().stream().max(Comparator.comparingInt(Entry::getValue)).map(Entry::getKey);
   }

   public static int getWeight(ItemStack stack) {
      if (AttributeGearData.read(stack) instanceof VaultGearData gearData) {
         GearDataCache cache = GearDataCache.of(stack);
         int weight = 0;
         if (cache.hasModifierOfCategory(VaultGearModifier.AffixCategory.LEGENDARY)) {
            weight += 1000000;
         }

         VaultGearRarity rarity = gearData.getRarity();
         weight += rarity.ordinal() * 10000;
         VaultGearTierConfig cfg = VaultGearTierConfig.getConfig(stack).orElse(null);
         if (cfg == null) {
            return weight;
         } else {
            int level = gearData.getItemLevel();
            List<VaultGearModifier<?>> modifiers = new ArrayList<>();
            modifiers.addAll(gearData.getModifiers(VaultGearModifier.AffixType.PREFIX));
            modifiers.addAll(gearData.getModifiers(VaultGearModifier.AffixType.SUFFIX));
            List<Float> rangePercentage = new ArrayList<>();
            modifiers.forEach(mod -> {
               VaultGearTierConfig.ModifierConfigRange range = cfg.getTierConfigRange((VaultGearModifier<?>)mod, level);
               ConfigurableAttributeGenerator generator = mod.getAttribute().getGenerator();
               generator.getRollPercentage(mod.getValue(), range.allTierConfigs()).ifPresent(value -> {
                  if (value instanceof Float f) {
                     rangePercentage.add(f);
                  }
               });
            });
            if (rangePercentage.size() > 0) {
               float avg = (float)rangePercentage.stream().mapToDouble(Float::doubleValue).average().orElse(1.0);
               weight += (int)(avg * 1000.0F);
            }

            return weight;
         }
      } else {
         return 0;
      }
   }
}
