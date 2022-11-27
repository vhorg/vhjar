package iskallia.vault.gear.item;

import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.crafting.Ingredient;

public class VaultGearArmorMaterial implements ArmorMaterial {
   public static final VaultGearArmorMaterial INSTANCE = new VaultGearArmorMaterial();

   private VaultGearArmorMaterial() {
   }

   public int getDurabilityForSlot(EquipmentSlot pSlot) {
      return 0;
   }

   public int getDefenseForSlot(EquipmentSlot pSlot) {
      return 0;
   }

   public int getEnchantmentValue() {
      return ArmorMaterials.DIAMOND.getEnchantmentValue();
   }

   public SoundEvent getEquipSound() {
      return ArmorMaterials.DIAMOND.getEquipSound();
   }

   public Ingredient getRepairIngredient() {
      return Ingredient.EMPTY;
   }

   public String getName() {
      return "vault_dummy";
   }

   public float getToughness() {
      return 0.0F;
   }

   public float getKnockbackResistance() {
      return 0.0F;
   }
}
