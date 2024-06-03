package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.core.random.JavaRandom;
import iskallia.vault.dynamodel.DynamicModel;
import iskallia.vault.dynamodel.model.armor.ArmorModel;
import iskallia.vault.dynamodel.model.armor.ArmorPieceModel;
import iskallia.vault.dynamodel.model.item.HandHeldModel;
import iskallia.vault.dynamodel.registry.DynamicModelRegistry;
import iskallia.vault.gear.VaultGearRarity;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModDynamicModels;
import iskallia.vault.item.gear.FocusItem;
import iskallia.vault.item.gear.VaultArmorItem;
import iskallia.vault.item.gear.VaultAxeItem;
import iskallia.vault.item.gear.VaultShieldItem;
import iskallia.vault.item.gear.VaultSwordItem;
import iskallia.vault.item.gear.WandItem;
import iskallia.vault.util.MiscUtils;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class GearModelRollRaritiesConfig extends Config {
   @Expose
   Map<String, List<String>> ARMOR_MODEL_ROLLS;
   @Expose
   Map<String, List<String>> SWORD_MODEL_ROLLS;
   @Expose
   Map<String, List<String>> AXE_MODEL_ROLLS;
   @Expose
   Map<String, List<String>> SHIELD_MODEL_ROLLS;
   @Expose
   Map<String, List<String>> WAND_MODEL_ROLLS;
   @Expose
   Map<String, List<String>> FOCUS_MODEL_ROLLS;

   public Map<String, List<String>> getRolls(ItemStack stack) {
      if (stack.getItem() instanceof VaultArmorItem) {
         return this.ARMOR_MODEL_ROLLS;
      } else if (stack.getItem() instanceof VaultSwordItem) {
         return this.SWORD_MODEL_ROLLS;
      } else if (stack.getItem() instanceof VaultAxeItem) {
         return this.AXE_MODEL_ROLLS;
      } else if (stack.getItem() instanceof VaultShieldItem) {
         return this.SHIELD_MODEL_ROLLS;
      } else if (stack.getItem() instanceof WandItem) {
         return this.WAND_MODEL_ROLLS;
      } else {
         return stack.getItem() instanceof FocusItem ? this.FOCUS_MODEL_ROLLS : Collections.emptyMap();
      }
   }

   private String transformWithEquipmentSlot(String modelId, EquipmentSlot slot) {
      String slotName = ArmorModel.slotName(slot);
      return slotName == null ? modelId : modelId + "/" + slotName;
   }

   public ResourceLocation getRandomRoll(ItemStack stack, VaultGearData data, EquipmentSlot slot, Random random) {
      return data.getRarity() == VaultGearRarity.UNIQUE
         ? ModConfigs.UNIQUE_GEAR.getRandomEntry(stack, data.getItemLevel(), JavaRandom.ofNanoTime()).orElseThrow().getModel()
         : MiscUtils.getRandomEntry(this.getPossibleRolls(stack, data, slot), random);
   }

   private <G extends Item & VaultGearItem> Set<ResourceLocation> getPossibleRolls(ItemStack stack, VaultGearData data, EquipmentSlot slot) {
      Set<ResourceLocation> rolls = this.getUnfilteredRolls(stack, data, slot);
      rolls.removeIf(modelId -> this.getForcedTierRarity(stack, modelId) != null);
      return rolls;
   }

   private <G extends Item & VaultGearItem> Set<ResourceLocation> getUnfilteredRolls(ItemStack stack, VaultGearData data, EquipmentSlot slot) {
      List<String> modelIds = this.getRolls(stack).get(data.getRarity().name());
      return modelIds == null
         ? ModDynamicModels.REGISTRIES.getAssociatedRegistry(stack.getItem()).map(DynamicModelRegistry::getIds).orElseGet(Collections::emptySet)
         : modelIds.stream()
            .map(modelId -> this.transformWithEquipmentSlot(modelId, slot))
            .<ResourceLocation>map(ResourceLocation::new)
            .filter(modelId -> ModDynamicModels.REGISTRIES.getModel(stack.getItem(), modelId).isPresent())
            .collect(Collectors.toSet());
   }

   public VaultGearRarity getRarityOf(ItemStack stack, ResourceLocation modelId) {
      Map<String, List<String>> rolls = this.getRolls(stack);
      if (rolls == null) {
         return VaultGearRarity.SCRAPPY;
      } else {
         VaultGearRarity predefined = this.getForcedTierRarity(stack, modelId);
         if (predefined != null) {
            return predefined;
         } else {
            if (stack.getItem() instanceof VaultArmorItem) {
               modelId = ModDynamicModels.Armor.PIECE_REGISTRY.get(modelId).map(ArmorPieceModel::getArmorModel).map(DynamicModel::getId).orElse(modelId);
            }

            for (int i = VaultGearRarity.values().length - 1; i >= 0; i--) {
               VaultGearRarity rarity = VaultGearRarity.values()[i];
               List<String> modelIds = rolls.get(rarity.name());
               if (modelIds != null && modelIds.contains(modelId.toString())) {
                  return rarity;
               }
            }

            return VaultGearRarity.SCRAPPY;
         }
      }
   }

   public boolean canAppearNormally(ItemStack stack, ResourceLocation modelId) {
      return this.getForcedTierRarity(stack, modelId) == null;
   }

   @Nullable
   private VaultGearRarity getForcedTierRarity(ItemStack stack, ResourceLocation modelId) {
      if (stack.getItem() instanceof VaultArmorItem) {
         Optional<ArmorModel> modelOpt = ModDynamicModels.Armor.MODEL_REGISTRY.get(modelId);
         if (modelOpt.isEmpty()) {
            modelOpt = ModDynamicModels.Armor.PIECE_REGISTRY.get(modelId).map(ArmorPieceModel::getArmorModel);
         }

         if (modelOpt.isPresent()) {
            ArmorModel armorModel = modelOpt.get();
            if (armorModel.equals(ModDynamicModels.Armor.GOBLIN) || armorModel.equals(ModDynamicModels.Armor.CHAMPION)) {
               return VaultGearRarity.UNIQUE;
            }
         }
      }

      if (stack.getItem() instanceof VaultSwordItem) {
         Optional<HandHeldModel> modelOptx = ModDynamicModels.Swords.REGISTRY.get(modelId);
         if (modelOptx.isPresent()) {
            HandHeldModel handHeldModel = modelOptx.get();
            if (handHeldModel.equals(ModDynamicModels.Swords.GODSWORD)) {
               return VaultGearRarity.UNIQUE;
            }
         }
      }

      if (stack.getItem() instanceof VaultAxeItem) {
         Optional<HandHeldModel> modelOptx = ModDynamicModels.Axes.REGISTRY.get(modelId);
         if (modelOptx.isPresent()) {
            HandHeldModel handHeldModel = modelOptx.get();
            if (handHeldModel.equals(ModDynamicModels.Axes.GODAXE)) {
               return VaultGearRarity.UNIQUE;
            }
         }
      }

      return null;
   }

   @Override
   public String getName() {
      return "gear_model_roll_rarities";
   }

   @Override
   protected void reset() {
      this.ARMOR_MODEL_ROLLS = new HashMap<>();
      this.ARMOR_MODEL_ROLLS
         .put(
            VaultGearRarity.SCRAPPY.name(),
            ModDynamicModels.Armor.MODEL_REGISTRY.getIds().stream().<String>map(ResourceLocation::toString).collect(Collectors.toList())
         );
      this.SWORD_MODEL_ROLLS = new HashMap<>();
      this.SWORD_MODEL_ROLLS
         .put(
            VaultGearRarity.SCRAPPY.name(),
            ModDynamicModels.Swords.REGISTRY.getIds().stream().<String>map(ResourceLocation::toString).collect(Collectors.toList())
         );
      this.AXE_MODEL_ROLLS = new HashMap<>();
      this.AXE_MODEL_ROLLS
         .put(
            VaultGearRarity.SCRAPPY.name(),
            ModDynamicModels.Axes.REGISTRY.getIds().stream().<String>map(ResourceLocation::toString).collect(Collectors.toList())
         );
      this.SHIELD_MODEL_ROLLS = new HashMap<>();
      this.SHIELD_MODEL_ROLLS
         .put(
            VaultGearRarity.SCRAPPY.name(),
            ModDynamicModels.Shields.REGISTRY.getIds().stream().<String>map(ResourceLocation::toString).collect(Collectors.toList())
         );
      this.WAND_MODEL_ROLLS = new HashMap<>();
      this.WAND_MODEL_ROLLS
         .put(
            VaultGearRarity.SCRAPPY.name(),
            ModDynamicModels.Wands.REGISTRY.getIds().stream().<String>map(ResourceLocation::toString).collect(Collectors.toList())
         );
      this.FOCUS_MODEL_ROLLS = new HashMap<>();
      this.FOCUS_MODEL_ROLLS
         .put(
            VaultGearRarity.SCRAPPY.name(),
            ModDynamicModels.Focus.REGISTRY.getIds().stream().<String>map(ResourceLocation::toString).collect(Collectors.toList())
         );
   }
}
