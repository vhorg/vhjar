package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.gear.VaultGearRarity;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.tool.ToolMaterial;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class SpiritConfig extends Config {
   @Expose
   public float perRecoveryMultiplierIncrease;
   @Expose
   public float perCompletionMultiplierDecrease;
   @Expose
   public float heroDiscountMin;
   @Expose
   public float heroDiscountMax;
   @Expose
   public float rescuedBonusMin;
   @Expose
   public float rescuedBonusMax;
   @Expose
   public Set<SpiritConfig.LevelCost> levelCosts;

   @Override
   public String getName() {
      return "spirit";
   }

   @Override
   protected void reset() {
      this.perRecoveryMultiplierIncrease = 0.3F;
      this.perCompletionMultiplierDecrease = 0.05F;
      this.heroDiscountMin = 0.2F;
      this.heroDiscountMax = 0.8F;
      this.rescuedBonusMin = 0.2F;
      this.rescuedBonusMax = 0.8F;
      this.levelCosts = new HashSet<>();
      Map<VaultGearRarity, Integer> gearRarityCost = new LinkedHashMap<>();
      gearRarityCost.put(VaultGearRarity.SCRAPPY, 1);
      gearRarityCost.put(VaultGearRarity.COMMON, 2);
      gearRarityCost.put(VaultGearRarity.RARE, 3);
      gearRarityCost.put(VaultGearRarity.EPIC, 4);
      gearRarityCost.put(VaultGearRarity.OMEGA, 5);
      gearRarityCost.put(VaultGearRarity.UNIQUE, 6);
      Map<ToolMaterial, Integer> toolMaterialCost = new LinkedHashMap<>();
      toolMaterialCost.put(ToolMaterial.CHROMATIC_IRON_INGOT, 1);
      toolMaterialCost.put(ToolMaterial.CHROMATIC_STEEL_INGOT, 2);
      toolMaterialCost.put(ToolMaterial.VAULTERITE_INGOT, 3);
      toolMaterialCost.put(ToolMaterial.VAULT_ALLOY, 4);
      toolMaterialCost.put(ToolMaterial.BLACK_CHROMATIC_STEEL_INGOT, 5);
      toolMaterialCost.put(ToolMaterial.ECHOING_INGOT, 6);
      toolMaterialCost.put(ToolMaterial.OMEGA_POG, 7);
      Map<ItemStack, Integer> itemCost = new LinkedHashMap<>();
      itemCost.put(new ItemStack(ModItems.VAULT_PICKAXE), 1);
      itemCost.put(new ItemStack(ModItems.MAGNET), 1);
      this.levelCosts.add(new SpiritConfig.LevelCost(0, ModBlocks.VAULT_BRONZE, 2.5F, gearRarityCost, toolMaterialCost, 3, itemCost));
      this.levelCosts.add(new SpiritConfig.LevelCost(5, ModBlocks.VAULT_SILVER, 3.0F, gearRarityCost, toolMaterialCost, 3, itemCost));
   }

   public float getCompletionMultiplierDecrease() {
      return (this.perCompletionMultiplierDecrease < 0.5F ? 1 : 0) - this.perCompletionMultiplierDecrease;
   }

   public float getHeroDiscount(Random random) {
      return this.heroDiscountMax > 0.0F ? random.nextFloat(this.heroDiscountMin, this.heroDiscountMax) : 0.0F;
   }

   public float getRescuedBonus(Random random) {
      return random.nextFloat(this.rescuedBonusMin, this.rescuedBonusMax);
   }

   public static class LevelCost {
      @Expose
      public int minLevel;
      @Expose
      public Item item;
      @Expose
      public float count;
      @Expose
      public Map<VaultGearRarity, Integer> gearRarityCost;
      @Expose
      public Map<ToolMaterial, Integer> toolMaterialCost;
      @Expose
      public Map<ItemStack, Integer> itemCost;
      @Expose
      public int trinketCost;

      public LevelCost(
         int minLevel,
         Item item,
         float count,
         Map<VaultGearRarity, Integer> gearRarityCost,
         Map<ToolMaterial, Integer> toolMaterialCost,
         int trinketCost,
         Map<ItemStack, Integer> itemCost
      ) {
         this.minLevel = minLevel;
         this.item = item;
         this.count = count;
         this.gearRarityCost = gearRarityCost;
         this.toolMaterialCost = toolMaterialCost;
         this.trinketCost = trinketCost;
         this.itemCost = itemCost;
      }

      public int getStackCost(ItemStack stack) {
         for (Entry<ItemStack, Integer> entry : this.itemCost.entrySet()) {
            ItemStack costStack = entry.getKey();
            if (stack.getItem() == costStack.getItem() && this.nbtMatches(stack, costStack)) {
               return entry.getValue();
            }
         }

         return 0;
      }

      private boolean nbtMatches(ItemStack stack, ItemStack costStack) {
         if (!costStack.hasTag()) {
            return true;
         } else if (!stack.hasTag()) {
            return false;
         } else {
            CompoundTag costTag = costStack.getTag();
            CompoundTag stackTag = stack.getTag();

            for (String nbtKey : costTag.getAllKeys()) {
               if (!stackTag.contains(nbtKey) || !stackTag.get(nbtKey).equals(costTag.get(nbtKey))) {
                  return false;
               }
            }

            return true;
         }
      }
   }
}
