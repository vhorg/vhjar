package iskallia.vault.config;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.entry.ChanceItemStackEntry;
import iskallia.vault.gear.VaultGearRarity;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.gear.DataInitializationItem;
import iskallia.vault.item.gear.DataTransferItem;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.BiFunction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class VaultRecyclerConfig extends Config {
   @Expose
   private int processingTickTime;
   @Expose
   private final Map<VaultGearRarity, Float> additionalOutputRarityChances = new LinkedHashMap<>();
   @Expose
   private VaultRecyclerConfig.RecyclerOutput gearRecyclingOutput;
   @Expose
   private VaultRecyclerConfig.RecyclerOutput trinketRecyclingOutput;
   @Expose
   private VaultRecyclerConfig.RecyclerOutput jewelRecyclingOutput;
   @Expose
   private VaultRecyclerConfig.RecyclerOutput inscriptionRecyclingOutput;
   @Expose
   private VaultRecyclerConfig.RecyclerOutput magnetRecyclingOutput;
   @Expose
   private VaultRecyclerConfig.RecyclerOutput charmRecyclingOutput;

   @Override
   public String getName() {
      return "vault_recycler";
   }

   public int getProcessingTickTime() {
      return this.processingTickTime;
   }

   public VaultRecyclerConfig.RecyclerOutput getGearRecyclingOutput() {
      return this.gearRecyclingOutput;
   }

   public VaultRecyclerConfig.RecyclerOutput getTrinketRecyclingOutput() {
      return this.trinketRecyclingOutput;
   }

   public VaultRecyclerConfig.RecyclerOutput getJewelRecyclingOutput() {
      return this.jewelRecyclingOutput;
   }

   public VaultRecyclerConfig.RecyclerOutput getInscriptionRecyclingOutput() {
      return this.inscriptionRecyclingOutput;
   }

   public VaultRecyclerConfig.RecyclerOutput getMagnetRecyclingOutput() {
      return this.magnetRecyclingOutput;
   }

   public VaultRecyclerConfig.RecyclerOutput getCharmRecyclingOutput() {
      return this.charmRecyclingOutput;
   }

   public float getAdditionalOutputRarityChance(VaultGearRarity rarity) {
      return this.additionalOutputRarityChances.getOrDefault(rarity, 0.0F);
   }

   @Override
   protected void reset() {
      this.processingTickTime = 40;
      this.additionalOutputRarityChances.clear();
      this.additionalOutputRarityChances.put(VaultGearRarity.SCRAPPY, 0.0F);
      this.additionalOutputRarityChances.put(VaultGearRarity.COMMON, 0.05F);
      this.additionalOutputRarityChances.put(VaultGearRarity.RARE, 0.1F);
      this.additionalOutputRarityChances.put(VaultGearRarity.EPIC, 0.15F);
      this.additionalOutputRarityChances.put(VaultGearRarity.OMEGA, 0.2F);
      this.gearRecyclingOutput = new VaultRecyclerConfig.RecyclerOutput(
         new ChanceItemStackEntry(new ItemStack(ModItems.VAULT_SCRAP), 4, 8, 1.0F),
         new ChanceItemStackEntry(new ItemStack(ModItems.PAINITE_GEM), 1, 1, 0.05F),
         new ChanceItemStackEntry(new ItemStack(ModItems.FACETED_FOCUS), 1, 1, 0.0F)
      );
      this.trinketRecyclingOutput = new VaultRecyclerConfig.RecyclerOutput(
         new ChanceItemStackEntry(new ItemStack(Items.APPLE), 1, 1, 1.0F),
         new ChanceItemStackEntry(ItemStack.EMPTY, 1, 1, 0.0F),
         new ChanceItemStackEntry(ItemStack.EMPTY, 1, 1, 0.0F)
      );
      this.jewelRecyclingOutput = new VaultRecyclerConfig.RecyclerOutput(
         new ChanceItemStackEntry(new ItemStack(ModItems.GEMSTONE), 1, 1, 1.0F),
         new ChanceItemStackEntry(ItemStack.EMPTY, 1, 1, 0.0F),
         new ChanceItemStackEntry(ItemStack.EMPTY, 1, 1, 0.0F)
      );
      this.inscriptionRecyclingOutput = new VaultRecyclerConfig.RecyclerOutput(
         new ChanceItemStackEntry(new ItemStack(ModItems.INSCRIPTION_PIECE), 1, 1, 1.0F),
         new ChanceItemStackEntry(ItemStack.EMPTY, 1, 1, 0.0F),
         new ChanceItemStackEntry(ItemStack.EMPTY, 1, 1, 0.0F)
      );
      this.magnetRecyclingOutput = new VaultRecyclerConfig.RecyclerOutput(
         new ChanceItemStackEntry(new ItemStack(ModItems.MAGNETITE), 1, 1, 1.0F),
         new ChanceItemStackEntry(ItemStack.EMPTY, 1, 1, 0.0F),
         new ChanceItemStackEntry(ItemStack.EMPTY, 1, 1, 0.0F)
      );
      this.charmRecyclingOutput = new VaultRecyclerConfig.RecyclerOutput(
         new ChanceItemStackEntry(new ItemStack(ModItems.ALEXANDRITE_GEM), 1, 1, 1.0F),
         new ChanceItemStackEntry(ItemStack.EMPTY, 1, 1, 0.0F),
         new ChanceItemStackEntry(ItemStack.EMPTY, 1, 1, 0.0F)
      );
   }

   public static class RecyclerOutput {
      @Expose
      private final ChanceItemStackEntry mainOutput;
      @Expose
      private final ChanceItemStackEntry extraOutput1;
      @Expose
      private final ChanceItemStackEntry extraOutput2;

      public RecyclerOutput(ChanceItemStackEntry mainOutput, ChanceItemStackEntry extraOutput1, ChanceItemStackEntry extraOutput2) {
         this.mainOutput = mainOutput;
         this.extraOutput1 = extraOutput1;
         this.extraOutput2 = extraOutput2;
      }

      public ItemStack generateMainOutput(float additionalChance) {
         ItemStack out = DataTransferItem.doConvertStack(this.mainOutput.adjustChance(additionalChance).createItemStack(Config.rand));
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

      public ItemStack generateExtraOutput1(float additionalChance) {
         ItemStack out = DataTransferItem.doConvertStack(this.extraOutput1.adjustChance(additionalChance).createItemStack(Config.rand));
         DataInitializationItem.doInitialize(out);
         return out;
      }

      public ItemStack getExtraOutput1Matching() {
         return this.extraOutput1.getMatchingStack();
      }

      public ItemStack generateExtraOutput2(float additionalChance) {
         ItemStack out = DataTransferItem.doConvertStack(this.extraOutput2.adjustChance(additionalChance).createItemStack(Config.rand));
         DataInitializationItem.doInitialize(out);
         return out;
      }

      public ItemStack getExtraOutput2Matching() {
         return this.extraOutput2.getMatchingStack();
      }

      public VaultRecyclerConfig.RecyclerOutput process(BiFunction<Integer, ItemStack, ItemStack> processor) {
         return new VaultRecyclerConfig.RecyclerOutput(
            new ChanceItemStackEntry(
               processor.apply(0, this.mainOutput.getMatchingStack()),
               this.mainOutput.getMinCount(),
               this.mainOutput.getMaxCount(),
               this.mainOutput.getChance()
            ),
            new ChanceItemStackEntry(
               processor.apply(1, this.extraOutput1.getMatchingStack()),
               this.extraOutput1.getMinCount(),
               this.extraOutput1.getMaxCount(),
               this.extraOutput1.getChance()
            ),
            new ChanceItemStackEntry(
               processor.apply(2, this.extraOutput2.getMatchingStack()),
               this.extraOutput2.getMinCount(),
               this.extraOutput2.getMaxCount(),
               this.extraOutput2.getChance()
            )
         );
      }
   }
}
