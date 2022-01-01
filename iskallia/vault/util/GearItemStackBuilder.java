package iskallia.vault.util;

import iskallia.vault.init.ModAttributes;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.item.gear.VaultGear;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public class GearItemStackBuilder {
   int modelId = -1;
   int specialModelId = -1;
   int color = -1;
   Item item = null;
   VaultGear.Rarity gearRarity = VaultGear.Rarity.UNIQUE;

   public GearItemStackBuilder(Item item) {
      if (!(item instanceof VaultGear)) {
         throw new IllegalArgumentException("Expected a vault gear item");
      } else {
         this.item = item;
      }
   }

   public GearItemStackBuilder setColor(int color) {
      this.color = color;
      return this;
   }

   public GearItemStackBuilder setModelId(int modelId) {
      this.modelId = modelId;
      return this;
   }

   public GearItemStackBuilder setSpecialModelId(int specialModelId) {
      this.specialModelId = specialModelId;
      return this;
   }

   public GearItemStackBuilder setGearRarity(VaultGear.Rarity gearRarity) {
      this.gearRarity = gearRarity;
      return this;
   }

   public ItemStack build() {
      ItemStack itemStack = new ItemStack(this.item);
      ModAttributes.GEAR_STATE.create(itemStack, VaultGear.State.IDENTIFIED);
      ModAttributes.GEAR_RARITY.create(itemStack, this.gearRarity);
      itemStack.func_196082_o().func_82580_o("RollTicks");
      itemStack.func_196082_o().func_82580_o("LastModelHit");
      ModAttributes.GEAR_ROLL_TYPE.create(itemStack, ModConfigs.VAULT_GEAR.DEFAULT_ROLL);
      ModAttributes.GEAR_COLOR.create(itemStack, this.color);
      ModAttributes.GEAR_MODEL.create(itemStack, this.modelId);
      ModAttributes.GEAR_SPECIAL_MODEL.create(itemStack, this.specialModelId);
      return itemStack;
   }
}
