package iskallia.vault.dynamodel.registry;

import iskallia.vault.dynamodel.model.armor.ArmorModel;
import iskallia.vault.dynamodel.model.armor.ArmorPieceModel;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.world.entity.EquipmentSlot;

public class ArmorPieceModelRegistry extends DynamicModelRegistry<ArmorPieceModel> {
   private final DynamicModelRegistry<ArmorModel> ARMOR_MODELS = new DynamicModelRegistry<>();
   private final Map<EquipmentSlot, DynamicModelRegistry<ArmorPieceModel>> PIECES_BY_SLOT = new HashMap<>();

   public DynamicModelRegistry<ArmorPieceModel> getPiecesOf(EquipmentSlot equipmentSlot) {
      return this.PIECES_BY_SLOT.computeIfAbsent(equipmentSlot, slot -> new DynamicModelRegistry<>());
   }

   public DynamicModelRegistry<ArmorModel> getArmorModels() {
      return this.ARMOR_MODELS;
   }

   public ArmorPieceModel register(ArmorPieceModel modelPiece) {
      ArmorModel model = modelPiece.getArmorModel();
      if (!this.ARMOR_MODELS.containsId(model.getId())) {
         this.ARMOR_MODELS.register(model);
      }

      this.getPiecesOf(modelPiece.getEquipmentSlot()).register(modelPiece);
      return super.register(modelPiece);
   }

   public ArmorModel registerAll(ArmorModel armorModel) {
      armorModel.getPieces().forEach((equipmentSlot, armorModelPiece) -> this.register(armorModelPiece));
      return armorModel;
   }
}
