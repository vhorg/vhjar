package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.dynamodel.DynamicModel;
import iskallia.vault.dynamodel.model.armor.ArmorModel;
import iskallia.vault.dynamodel.model.armor.ArmorPieceModel;
import iskallia.vault.dynamodel.registry.DynamicModelRegistry;
import iskallia.vault.gear.VaultGearRarity;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModDynamicModels;
import iskallia.vault.item.gear.VaultArmorItem;
import iskallia.vault.item.gear.VaultAxeItem;
import iskallia.vault.item.gear.VaultShieldItem;
import iskallia.vault.item.gear.VaultSwordItem;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.Item;

public class GearModelRollRaritiesConfig extends Config {
   @Expose
   Map<VaultGearRarity, List<String>> ARMOR_MODEL_ROLLS;
   @Expose
   Map<VaultGearRarity, List<String>> SWORD_MODEL_ROLLS;
   @Expose
   Map<VaultGearRarity, List<String>> AXE_MODEL_ROLLS;
   @Expose
   Map<VaultGearRarity, List<String>> SHIELD_MODEL_ROLLS;

   public Map<VaultGearRarity, List<String>> getRolls(VaultGearItem gear) {
      if (gear instanceof VaultArmorItem) {
         return this.ARMOR_MODEL_ROLLS;
      } else if (gear instanceof VaultSwordItem) {
         return this.SWORD_MODEL_ROLLS;
      } else if (gear instanceof VaultAxeItem) {
         return this.AXE_MODEL_ROLLS;
      } else {
         return gear instanceof VaultShieldItem ? this.SHIELD_MODEL_ROLLS : Collections.emptyMap();
      }
   }

   private String transformWithEquipmentSlot(String modelId, EquipmentSlot slot) {
      String slotName = ArmorModel.slotName(slot);
      return slotName == null ? modelId : modelId + "/" + slotName;
   }

   public <G extends Item & VaultGearItem> Set<ResourceLocation> getPossibleRolls(G gearItem, VaultGearRarity rarity, EquipmentSlot slot) {
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
   }
}
