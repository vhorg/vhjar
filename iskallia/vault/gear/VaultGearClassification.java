package iskallia.vault.gear;

import java.util.function.Function;

public enum VaultGearClassification {
   ARMOR(VaultGearRarity::getArmorModifierCount),
   AXE(VaultGearRarity::getWeaponModifierCount),
   SWORD(VaultGearRarity::getWeaponModifierCount),
   SHIELD(VaultGearRarity::getShieldModifierCount),
   IDOL(VaultGearRarity::getIdolModifierCount);

   private final Function<VaultGearRarity, Integer> modifierCountFn;

   private VaultGearClassification(Function<VaultGearRarity, Integer> modifierCountFn) {
      this.modifierCountFn = modifierCountFn;
   }

   public int getModifierCount(VaultGearRarity rarity) {
      return this.modifierCountFn.apply(rarity);
   }
}