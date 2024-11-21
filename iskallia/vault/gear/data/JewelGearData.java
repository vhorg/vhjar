package iskallia.vault.gear.data;

import iskallia.vault.core.net.BitBuffer;
import iskallia.vault.gear.VaultGearRarity;
import iskallia.vault.gear.attribute.VaultGearModifier;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModGearAttributes;

public class JewelGearData extends VaultGearData {
   public JewelGearData() {
   }

   public JewelGearData(BitBuffer buf) {
      this.read(buf);
   }

   @Override
   protected void read(BitBuffer buf) {
      super.read(buf);
      if (GearDataVersion.V0_7.isLaterThan(this.version)) {
         if (!this.hasAttribute(ModGearAttributes.JEWEL_SIZE)) {
            return;
         }

         if (this.getRarity() == VaultGearRarity.UNIQUE) {
            return;
         }

         this.getAllAttributes()
            .filter(inst -> inst.getAttribute() == ModGearAttributes.JEWEL_SIZE)
            .filter(inst -> inst instanceof VaultGearModifier)
            .map(inst -> (VaultGearModifier)inst)
            .toList()
            .forEach(this::removeModifier);
         ModConfigs.JEWEL_SIZE
            .getSize(this.getRarity())
            .ifPresent(
               size -> this.addModifier(
                  VaultGearModifier.AffixType.IMPLICIT, new VaultGearModifier<>(ModGearAttributes.JEWEL_SIZE, size.getRandom(VaultGearItem.random))
               )
            );
      }
   }
}
