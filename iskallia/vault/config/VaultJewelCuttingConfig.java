package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.entry.ChanceItemStackEntry;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.gear.DataInitializationItem;
import iskallia.vault.item.gear.DataTransferItem;
import java.util.Random;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;

public class VaultJewelCuttingConfig extends Config {
   @Expose
   private VaultJewelCuttingConfig.JewelCuttingInput jewelCuttingInput;
   @Expose
   private VaultJewelCuttingConfig.JewelCuttingOutput jewelCuttingOutput;
   @Expose
   private VaultJewelCuttingConfig.JewelCuttingRange jewelCuttingRange;
   @Expose
   private float jewelCuttingModifierRemovalChance;

   @Override
   public String getName() {
      return "vault_jewel_cutting";
   }

   public VaultJewelCuttingConfig.JewelCuttingOutput getJewelCuttingOutput() {
      return this.jewelCuttingOutput;
   }

   public VaultJewelCuttingConfig.JewelCuttingInput getJewelCuttingInput() {
      return this.jewelCuttingInput;
   }

   public VaultJewelCuttingConfig.JewelCuttingRange getJewelCuttingRange() {
      return this.jewelCuttingRange;
   }

   public float getJewelCuttingModifierRemovalChance() {
      return this.jewelCuttingModifierRemovalChance;
   }

   @Override
   protected void reset() {
      this.jewelCuttingModifierRemovalChance = 0.35F;
      this.jewelCuttingRange = new VaultJewelCuttingConfig.JewelCuttingRange(1, 10);
      this.jewelCuttingInput = new VaultJewelCuttingConfig.JewelCuttingInput(new ItemStack(ModItems.SILVER_SCRAP, 5), new ItemStack(ModBlocks.VAULT_BRONZE, 16));
      this.jewelCuttingOutput = new VaultJewelCuttingConfig.JewelCuttingOutput(
         new ChanceItemStackEntry(new ItemStack(ModItems.SILVER_SCRAP), 1, 4, 1.0F),
         new ChanceItemStackEntry(new ItemStack(ModItems.WUTODIE_GEM), 1, 2, 0.75F),
         new ChanceItemStackEntry(ItemStack.EMPTY, 1, 1, 0.0F)
      );
   }

   public static class JewelCuttingInput {
      @Expose
      private final ItemStack mainInput;
      @Expose
      private final ItemStack secondInput;

      public JewelCuttingInput(ItemStack mainInput, ItemStack secondInput) {
         this.mainInput = mainInput;
         this.secondInput = secondInput;
      }

      public ItemStack getMainInput() {
         return this.mainInput;
      }

      public ItemStack getSecondInput() {
         return this.secondInput;
      }
   }

   public static class JewelCuttingOutput {
      @Expose
      private final ChanceItemStackEntry mainOutput;
      @Expose
      private final ChanceItemStackEntry extraOutput1;
      @Expose
      private final ChanceItemStackEntry extraOutput2;

      public JewelCuttingOutput(ChanceItemStackEntry mainOutput, ChanceItemStackEntry extraOutput1, ChanceItemStackEntry extraOutput2) {
         this.mainOutput = mainOutput;
         this.extraOutput1 = extraOutput1;
         this.extraOutput2 = extraOutput2;
      }

      public ItemStack generateMainOutput() {
         ItemStack out = DataTransferItem.doConvertStack(this.mainOutput.createItemStack(Config.rand));
         DataInitializationItem.doInitialize(out);
         return out;
      }

      public ChanceItemStackEntry getMainOutput() {
         return this.mainOutput;
      }

      public ChanceItemStackEntry getExtraOutput1() {
         return this.extraOutput1;
      }

      public ChanceItemStackEntry getExtraOutput2() {
         return this.extraOutput2;
      }

      public ItemStack getMainOutputMatching() {
         return this.mainOutput.getMatchingStack();
      }

      public ItemStack generateExtraOutput1() {
         ItemStack out = DataTransferItem.doConvertStack(this.extraOutput1.createItemStack(Config.rand));
         DataInitializationItem.doInitialize(out);
         return out;
      }

      public ItemStack getExtraOutput1Matching() {
         return this.extraOutput1.getMatchingStack();
      }

      public ItemStack generateExtraOutput2() {
         ItemStack out = DataTransferItem.doConvertStack(this.extraOutput2.createItemStack(Config.rand));
         DataInitializationItem.doInitialize(out);
         return out;
      }

      public ItemStack getExtraOutput2Matching() {
         return this.extraOutput2.getMatchingStack();
      }
   }

   public static class JewelCuttingRange {
      @Expose
      private final int min;
      @Expose
      private final int max;

      public JewelCuttingRange(int min, int max) {
         this.min = min;
         this.max = max;
      }

      public int getMin() {
         return this.min;
      }

      public int getMax() {
         return this.max;
      }

      public int getRandom() {
         return Mth.nextInt(new Random(), this.min, this.max);
      }
   }
}
