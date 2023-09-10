package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.dynamodel.DynamicModel;
import iskallia.vault.dynamodel.model.armor.ArmorModel;
import iskallia.vault.dynamodel.model.armor.ArmorPieceModel;
import iskallia.vault.dynamodel.model.item.HandHeldModel;
import iskallia.vault.dynamodel.registry.DynamicModelRegistry;
import iskallia.vault.gear.VaultGearRarity;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModDynamicModels;
import iskallia.vault.item.gear.VaultArmorItem;
import iskallia.vault.item.gear.VaultAxeItem;
import iskallia.vault.item.gear.VaultShieldItem;
import iskallia.vault.item.gear.VaultSwordItem;
import iskallia.vault.item.gear.WandItem;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.extensions.IForgeItem;

public class GearModelRollRaritiesConfig extends Config {
   @Expose
   Map<VaultGearRarity, List<String>> ARMOR_MODEL_ROLLS;
   @Expose
   Map<VaultGearRarity, List<String>> SWORD_MODEL_ROLLS;
   @Expose
   Map<VaultGearRarity, List<String>> AXE_MODEL_ROLLS;
   @Expose
   Map<VaultGearRarity, List<String>> SHIELD_MODEL_ROLLS;
   @Expose
   Map<VaultGearRarity, List<String>> WAND_MODEL_ROLLS;

   public Map<VaultGearRarity, List<String>> getRolls(VaultGearItem gear) {
      if (gear instanceof VaultArmorItem) {
         return this.ARMOR_MODEL_ROLLS;
      } else if (gear instanceof VaultSwordItem) {
         return this.SWORD_MODEL_ROLLS;
      } else if (gear instanceof VaultAxeItem) {
         return this.AXE_MODEL_ROLLS;
      } else if (gear instanceof VaultShieldItem) {
         return this.SHIELD_MODEL_ROLLS;
      } else {
         return gear instanceof WandItem ? this.WAND_MODEL_ROLLS : Collections.emptyMap();
      }
   }

   private String transformWithEquipmentSlot(String modelId, EquipmentSlot slot) {
      String slotName = ArmorModel.slotName(slot);
      return slotName == null ? modelId : modelId + "/" + slotName;
   }

   public <G extends Item & VaultGearItem> Set<ResourceLocation> getPossibleRolls(G gearItem, VaultGearRarity rarity, EquipmentSlot slot) {
      Set<ResourceLocation> rolls = this.getUnfilteredRolls(gearItem, rarity, slot);
      rolls.removeIf(modelId -> this.getForcedTierRarity(gearItem, modelId) != null);
      return rolls;
   }

   private <G extends Item & VaultGearItem> Set<ResourceLocation> getUnfilteredRolls(G gearItem, VaultGearRarity rarity, EquipmentSlot slot) {
      List<String> modelIds = this.getRolls(gearItem).get(rarity);
      return modelIds == null
         ? ModDynamicModels.REGISTRIES.getAssociatedRegistry(gearItem).map(DynamicModelRegistry::getIds).orElseGet(Collections::emptySet)
         : modelIds.stream()
            .map(modelId -> this.transformWithEquipmentSlot(modelId, slot))
            .<ResourceLocation>map(ResourceLocation::new)
            .filter(modelId -> ModDynamicModels.REGISTRIES.getModel(gearItem, modelId).isPresent())
            .collect(Collectors.toSet());
   }

   public VaultGearRarity getRarityOf(VaultGearItem gearItem, ResourceLocation modelId) {
      Map<VaultGearRarity, List<String>> rolls = this.getRolls(gearItem);
      if (rolls == null) {
         return VaultGearRarity.SCRAPPY;
      } else {
         VaultGearRarity predefined = this.getForcedTierRarity(gearItem, modelId);
         if (predefined != null) {
            return predefined;
         } else {
            if (gearItem instanceof VaultArmorItem) {
               modelId = ModDynamicModels.Armor.PIECE_REGISTRY.get(modelId).map(ArmorPieceModel::getArmorModel).map(DynamicModel::getId).orElse(modelId);
            }

            for (int i = VaultGearRarity.values().length - 1; i >= 0; i--) {
               VaultGearRarity rarity = VaultGearRarity.values()[i];
               List<String> modelIds = rolls.get(rarity);
               if (modelIds != null && modelIds.contains(modelId.toString())) {
                  return rarity;
               }
            }

            return VaultGearRarity.SCRAPPY;
         }
      }
   }

   public boolean canAppearNormally(IForgeItem item, ResourceLocation modelId) {
      return this.getForcedTierRarity(item, modelId) == null;
   }

   @Nullable
   private VaultGearRarity getForcedTierRarity(IForgeItem item, ResourceLocation modelId) {
      if (item instanceof VaultArmorItem) {
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

      if (item instanceof VaultSwordItem) {
         Optional<HandHeldModel> modelOptx = ModDynamicModels.Swords.REGISTRY.get(modelId);
         if (modelOptx.isPresent()) {
            HandHeldModel handHeldModel = modelOptx.get();
            if (handHeldModel.equals(ModDynamicModels.Swords.GODSWORD)) {
               return VaultGearRarity.UNIQUE;
            }
         }
      }

      if (item instanceof VaultAxeItem) {
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
            VaultGearRarity.SCRAPPY,
            ModDynamicModels.Armor.MODEL_REGISTRY.getIds().stream().<String>map(ResourceLocation::toString).collect(Collectors.toList())
         );
      this.SWORD_MODEL_ROLLS = new HashMap<>();
      this.SWORD_MODEL_ROLLS
         .put(VaultGearRarity.SCRAPPY, ModDynamicModels.Swords.REGISTRY.getIds().stream().<String>map(ResourceLocation::toString).collect(Collectors.toList()));
      this.AXE_MODEL_ROLLS = new HashMap<>();
      this.AXE_MODEL_ROLLS
         .put(VaultGearRarity.SCRAPPY, ModDynamicModels.Axes.REGISTRY.getIds().stream().<String>map(ResourceLocation::toString).collect(Collectors.toList()));
      this.SHIELD_MODEL_ROLLS = new HashMap<>();
      this.SHIELD_MODEL_ROLLS
         .put(VaultGearRarity.SCRAPPY, ModDynamicModels.Shields.REGISTRY.getIds().stream().<String>map(ResourceLocation::toString).collect(Collectors.toList()));
      this.WAND_MODEL_ROLLS = new HashMap<>();
      this.SHIELD_MODEL_ROLLS
         .put(VaultGearRarity.SCRAPPY, ModDynamicModels.Wands.REGISTRY.getIds().stream().<String>map(ResourceLocation::toString).collect(Collectors.toList()));
   }
}
