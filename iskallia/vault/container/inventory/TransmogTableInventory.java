package iskallia.vault.container.inventory;

import iskallia.vault.attribute.IntegerAttribute;
import iskallia.vault.container.base.RecipeInventory;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.init.ModItems;
import iskallia.vault.init.ModModels;
import iskallia.vault.item.gear.VaultArmorItem;
import iskallia.vault.item.gear.VaultGear;
import iskallia.vault.item.gear.VaultSwordItem;
import net.minecraft.entity.MobEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public class TransmogTableInventory extends RecipeInventory {
   public static final int GEAR_SLOT = 0;
   public static final int APPEARANCE_SLOT = 1;
   public static final int BRONZE_SLOT = 2;

   public TransmogTableInventory() {
      super(3);
   }

   public int requiredVaultBronze() {
      ItemStack gearStack = this.func_70301_a(0);
      if (gearStack.func_190926_b()) {
         return -1;
      } else {
         IntegerAttribute levelAttr = ModAttributes.MIN_VAULT_LEVEL.getOrDefault(gearStack, 1);
         int gearLevel = levelAttr.getValue(gearStack);
         return MathHelper.func_76125_a(gearLevel, 1, 64);
      }
   }

   @Override
   public boolean recipeFulfilled() {
      ItemStack gearStack = this.func_70301_a(0);
      ItemStack appearanceStack = this.func_70301_a(1);
      ItemStack bronzeStack = this.func_70301_a(2);
      if (gearStack.func_77973_b() instanceof VaultArmorItem && appearanceStack.func_77973_b() instanceof VaultArmorItem) {
         return this.armorRecipeFulfilled(gearStack, appearanceStack, bronzeStack);
      } else {
         return gearStack.func_77973_b() instanceof VaultSwordItem && appearanceStack.func_77973_b() instanceof VaultSwordItem
            ? this.swordRecipeFulfilled(gearStack, appearanceStack, bronzeStack)
            : false;
      }
   }

   private boolean armorRecipeFulfilled(ItemStack armorStack, ItemStack appearanceStack, ItemStack bronzeStack) {
      VaultGear.Rarity armorRarity = ModAttributes.GEAR_RARITY.getBase(armorStack).orElse(VaultGear.Rarity.SCRAPPY);
      VaultGear.Rarity appearanceRarity = ModAttributes.GEAR_RARITY.getBase(appearanceStack).orElse(VaultGear.Rarity.SCRAPPY);
      if (armorRarity == VaultGear.Rarity.SCRAPPY) {
         return false;
      } else if (appearanceRarity == VaultGear.Rarity.SCRAPPY) {
         return false;
      } else if (armorRarity == VaultGear.Rarity.UNIQUE) {
         return false;
      } else {
         EquipmentSlotType armorSlot = MobEntity.func_184640_d(appearanceStack);
         EquipmentSlotType appearanceSlot = MobEntity.func_184640_d(armorStack);
         if (armorSlot != appearanceSlot) {
            return false;
         } else {
            int appearanceSpecialModel = ModAttributes.GEAR_SPECIAL_MODEL.getBase(appearanceStack).orElse(-1);
            if (appearanceSpecialModel != -1) {
               ModModels.SpecialGearModel specialGearModel = ModModels.SpecialGearModel.getModel(appearanceSlot, appearanceSpecialModel);
               if (specialGearModel != null && !specialGearModel.getModelProperties().doesAllowTransmogrification()) {
                  return false;
               }
            }

            return bronzeStack.func_77973_b() == ModItems.VAULT_BRONZE && bronzeStack.func_190916_E() >= this.requiredVaultBronze();
         }
      }
   }

   private boolean swordRecipeFulfilled(ItemStack swordStack, ItemStack appearanceStack, ItemStack bronzeStack) {
      VaultGear.Rarity swordRarity = ModAttributes.GEAR_RARITY.getBase(swordStack).orElse(VaultGear.Rarity.SCRAPPY);
      VaultGear.Rarity appearanceRarity = ModAttributes.GEAR_RARITY.getBase(appearanceStack).orElse(VaultGear.Rarity.SCRAPPY);
      if (swordRarity == VaultGear.Rarity.SCRAPPY) {
         return false;
      } else if (appearanceRarity == VaultGear.Rarity.SCRAPPY) {
         return false;
      } else if (swordRarity == VaultGear.Rarity.UNIQUE) {
         return false;
      } else {
         int appearanceSpecialModel = ModAttributes.GEAR_SPECIAL_MODEL.getBase(appearanceStack).orElse(-1);
         if (appearanceSpecialModel != -1) {
            ModModels.SpecialSwordModel specialSwordModel = ModModels.SpecialSwordModel.getModel(appearanceSpecialModel);
            if (specialSwordModel != null && !specialSwordModel.getModelProperties().doesAllowTransmogrification()) {
               return false;
            }
         }

         return bronzeStack.func_77973_b() == ModItems.VAULT_BRONZE && bronzeStack.func_190916_E() >= this.requiredVaultBronze();
      }
   }

   @Override
   public ItemStack resultingItemStack() {
      ItemStack gearStack = this.func_70301_a(0);
      ItemStack appearanceStack = this.func_70301_a(1);
      int gearModel = ModAttributes.GEAR_MODEL.getBase(gearStack).orElse(-1);
      int gearSpecialModel = ModAttributes.GEAR_SPECIAL_MODEL.getBase(gearStack).orElse(-1);
      int appearanceModel = ModAttributes.GEAR_MODEL.getBase(appearanceStack).orElse(-1);
      int appearanceSpecialModel = ModAttributes.GEAR_SPECIAL_MODEL.getBase(appearanceStack).orElse(-1);
      ItemStack resultingStack = gearStack.func_77946_l();
      if (appearanceSpecialModel != -1) {
         ModAttributes.GEAR_SPECIAL_MODEL.create(resultingStack, appearanceSpecialModel);
      } else {
         ModAttributes.GEAR_MODEL.create(resultingStack, appearanceModel);
         ModAttributes.GEAR_SPECIAL_MODEL.create(resultingStack, -1);
      }

      return resultingStack;
   }

   @Override
   public void consumeIngredients() {
      this.func_70298_a(2, this.requiredVaultBronze());
      this.func_70298_a(0, 1);
   }
}
