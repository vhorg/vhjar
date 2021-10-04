package iskallia.vault.container.inventory;

import iskallia.vault.attribute.EnumAttribute;
import iskallia.vault.attribute.IntegerAttribute;
import iskallia.vault.container.base.RecipeInventory;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.gear.VaultArmorItem;
import iskallia.vault.item.gear.VaultGear;
import net.minecraft.entity.MobEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public class TransmogTableInventory extends RecipeInventory {
   public static final int ARMOR_SLOT = 0;
   public static final int APPEARANCE_SLOT = 1;
   public static final int BRONZE_SLOT = 2;

   public TransmogTableInventory() {
      super(3);
   }

   public int requiredVaultBronze() {
      ItemStack armorItemStack = this.func_70301_a(0);
      if (armorItemStack.func_190926_b()) {
         return -1;
      } else {
         IntegerAttribute levelAttr = ModAttributes.MIN_VAULT_LEVEL.getOrDefault(armorItemStack, 1);
         int gearLevel = levelAttr.getValue(armorItemStack);
         return MathHelper.func_76125_a(gearLevel, 1, 64);
      }
   }

   @Override
   public boolean recipeFulfilled() {
      ItemStack armorStack = this.func_70301_a(0);
      ItemStack appearanceStack = this.func_70301_a(1);
      ItemStack bronzeStack = this.func_70301_a(2);
      EnumAttribute<VaultGear.Rarity> rarityAttr = ModAttributes.GEAR_RARITY.getOrDefault(appearanceStack, VaultGear.Rarity.SCRAPPY);
      VaultGear.Rarity armorRarity = rarityAttr.getValue(armorStack);
      VaultGear.Rarity appearanceRarity = rarityAttr.getValue(appearanceStack);
      return armorStack.func_77973_b() instanceof VaultArmorItem
         && appearanceStack.func_77973_b() instanceof VaultArmorItem
         && armorRarity != VaultGear.Rarity.SCRAPPY
         && appearanceRarity != VaultGear.Rarity.SCRAPPY
         && MobEntity.func_184640_d(appearanceStack) == MobEntity.func_184640_d(armorStack)
         && bronzeStack.func_77973_b() == ModItems.VAULT_BRONZE
         && bronzeStack.func_190916_E() >= this.requiredVaultBronze();
   }

   @Override
   public ItemStack resultingItemStack() {
      ItemStack armorStack = this.func_70301_a(0);
      ItemStack appearanceStack = this.func_70301_a(1);
      IntegerAttribute modelAttr = ModAttributes.GEAR_MODEL.getOrDefault(appearanceStack, 0);
      int modelId = modelAttr.getValue(appearanceStack);
      ItemStack resultingStack = armorStack.func_77946_l();
      ModAttributes.GEAR_MODEL.create(resultingStack, modelId);
      return resultingStack;
   }

   @Override
   public void consumeIngredients() {
      this.func_70298_a(2, this.requiredVaultBronze());
      this.func_70298_a(0, 1);
   }
}
