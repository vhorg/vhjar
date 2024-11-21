package iskallia.vault.gear;

import javax.annotation.Nullable;
import net.minecraft.world.entity.EquipmentSlot;

public enum VaultGearType {
   SWORD(EquipmentSlot.MAINHAND),
   AXE(EquipmentSlot.MAINHAND),
   HELMET(EquipmentSlot.HEAD),
   CHESTPLATE(EquipmentSlot.CHEST),
   LEGGINGS(EquipmentSlot.LEGS),
   BOOTS(EquipmentSlot.FEET),
   FOCUS(EquipmentSlot.OFFHAND),
   WAND(EquipmentSlot.OFFHAND),
   SHIELD(EquipmentSlot.OFFHAND),
   IDOL(EquipmentSlot.OFFHAND),
   JEWEL(null),
   MAGNET(null),
   TOOL(EquipmentSlot.MAINHAND);

   @Nullable
   private final EquipmentSlot slot;

   private VaultGearType(@Nullable EquipmentSlot slot) {
      this.slot = slot;
   }

   @Nullable
   public EquipmentSlot getEquipmentSlot() {
      return this.slot;
   }
}
