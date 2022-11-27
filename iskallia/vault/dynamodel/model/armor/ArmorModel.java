package iskallia.vault.dynamodel.model.armor;

import iskallia.vault.dynamodel.DynamicModel;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import org.apache.commons.lang3.StringUtils;

public class ArmorModel extends DynamicModel<ArmorModel> {
   private final Map<EquipmentSlot, ArmorPieceModel> pieces = new HashMap<>();
   private ArmorLayers layers;

   public ArmorModel(ResourceLocation id, String displayName) {
      super(id, displayName);
   }

   public ArmorModel usingLayers(ArmorLayers rootModel) {
      this.layers = rootModel;
      return this;
   }

   public ArmorModel addSlot(EquipmentSlot equipmentSlot) {
      return this.addSlot(equipmentSlot, this.displayName + " " + StringUtils.capitalize(slotName(equipmentSlot)));
   }

   public ArmorModel addSlot(EquipmentSlot equipmentSlot, String displayName) {
      ResourceLocation id = new ResourceLocation(this.id.getNamespace(), this.id.getPath() + "/" + slotName(equipmentSlot));
      ArmorPieceModel definition = new ArmorPieceModel(id, displayName);
      definition.armorModel = this;
      definition.equipmentSlot = equipmentSlot;
      definition.layers = this.layers;
      this.pieces.put(equipmentSlot, definition);
      return this;
   }

   public Map<EquipmentSlot, ArmorPieceModel> getPieces() {
      return Collections.unmodifiableMap(this.pieces);
   }

   public Optional<ArmorPieceModel> getPiece(EquipmentSlot slot) {
      return Optional.ofNullable(this.pieces.get(slot));
   }

   public static String slotName(EquipmentSlot equipmentSlot) {
      return switch (equipmentSlot) {
         case HEAD -> "helmet";
         case CHEST -> "chestplate";
         case LEGS -> "leggings";
         case FEET -> "boots";
         default -> null;
      };
   }
}
