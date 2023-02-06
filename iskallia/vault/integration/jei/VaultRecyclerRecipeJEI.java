package iskallia.vault.integration.jei;

import com.google.common.collect.Lists;
import iskallia.vault.config.VaultRecyclerConfig;
import iskallia.vault.config.entry.ChanceItemStackEntry;
import iskallia.vault.gear.GearRollHelper;
import iskallia.vault.gear.VaultGearRarity;
import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.gear.trinket.TrinketEffectRegistry;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.gear.DataTransferItem;
import iskallia.vault.item.gear.TrinketItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class VaultRecyclerRecipeJEI {
   private static final Random random = new Random(0L);
   private ItemStack INPUT = new ItemStack(ModItems.HELMET);
   private ItemStack OUTPUT1 = new ItemStack(ModItems.VAULT_SCRAP);
   private ItemStack OUTPUT2 = new ItemStack(ModItems.WILD_FOCUS);
   private ItemStack OUTPUT3 = new ItemStack(ModItems.FACETED_FOCUS);

   public VaultRecyclerRecipeJEI(ItemStack input, ItemStack output1, ItemStack output2, ItemStack output3) {
      this.INPUT = input;
      this.OUTPUT1 = output1;
      this.OUTPUT2 = output2;
      this.OUTPUT3 = output3;
   }

   public ItemStack getInput() {
      return this.INPUT;
   }

   public ItemStack getOutput1() {
      return this.OUTPUT1;
   }

   public ItemStack getOutput2() {
      return this.OUTPUT2;
   }

   public ItemStack getOutput3() {
      return this.OUTPUT3;
   }

   public static List<VaultRecyclerRecipeJEI> getRecipeList() {
      List<VaultRecyclerRecipeJEI> recipeList = Lists.newArrayList();
      List<ItemStack> vaultArmorList = Lists.newArrayList();
      ItemStack sword = generateRandomArmor(ModItems.SWORD, VaultGearRarity.COMMON);
      vaultArmorList.add(sword);
      ItemStack axe = generateRandomArmor(ModItems.AXE, VaultGearRarity.COMMON);
      axe.setDamageValue(axe.getMaxDamage() / 6);
      vaultArmorList.add(axe);
      ItemStack helmet = generateRandomArmor(ModItems.HELMET, VaultGearRarity.COMMON);
      helmet.setDamageValue(helmet.getMaxDamage() / 6 * 2);
      vaultArmorList.add(helmet);
      ItemStack chestplate = generateRandomArmor(ModItems.CHESTPLATE, VaultGearRarity.COMMON);
      chestplate.setDamageValue(chestplate.getMaxDamage() / 6 * 3);
      vaultArmorList.add(chestplate);
      ItemStack leggings = generateRandomArmor(ModItems.LEGGINGS, VaultGearRarity.COMMON);
      leggings.setDamageValue(leggings.getMaxDamage() / 6 * 4);
      vaultArmorList.add(leggings);
      ItemStack boots = generateRandomArmor(ModItems.BOOTS, VaultGearRarity.COMMON);
      boots.setDamageValue(boots.getMaxDamage() / 6 * 5 - 1);
      vaultArmorList.add(boots);

      for (ItemStack armor : vaultArmorList) {
         helmet = getUseRelatedOutput(armor, DataTransferItem.doConvertStack(ModConfigs.VAULT_RECYCLER.getGearRecyclingOutput().getMainOutputMatching()));
         chestplate = getUseRelatedOutput(armor, DataTransferItem.doConvertStack(ModConfigs.VAULT_RECYCLER.getGearRecyclingOutput().getExtraOutput1Matching()));
         leggings = getUseRelatedOutput(armor, DataTransferItem.doConvertStack(ModConfigs.VAULT_RECYCLER.getGearRecyclingOutput().getExtraOutput2Matching()));
         recipeList.add(new VaultRecyclerRecipeJEI(armor, helmet, chestplate, leggings));
      }

      sword = TrinketItem.createRandomTrinket(TrinketEffectRegistry.getOrderedEntries().get(random.nextInt(TrinketEffectRegistry.getOrderedEntries().size())));
      axe = getUseRelatedOutput(sword, DataTransferItem.doConvertStack(ModConfigs.VAULT_RECYCLER.getTrinketRecyclingOutput().getMainOutputMatching()));
      helmet = getUseRelatedOutput(sword, DataTransferItem.doConvertStack(ModConfigs.VAULT_RECYCLER.getTrinketRecyclingOutput().getExtraOutput1Matching()));
      chestplate = getUseRelatedOutput(sword, DataTransferItem.doConvertStack(ModConfigs.VAULT_RECYCLER.getTrinketRecyclingOutput().getExtraOutput2Matching()));
      recipeList.add(new VaultRecyclerRecipeJEI(sword, axe, helmet, chestplate));
      sword = TrinketItem.createRandomTrinket(
         TrinketEffectRegistry.getOrderedEntries().get(Math.max(0, random.nextInt(TrinketEffectRegistry.getOrderedEntries().size() - 1)))
      );

      for (int i = 0; i < Math.max(1, TrinketItem.getUses(sword) / 5); i++) {
         TrinketItem.addUsedVault(sword, new UUID(i, i));
      }

      axe = getUseRelatedOutput(sword, DataTransferItem.doConvertStack(ModConfigs.VAULT_RECYCLER.getTrinketRecyclingOutput().getMainOutputMatching()));
      helmet = getUseRelatedOutput(sword, DataTransferItem.doConvertStack(ModConfigs.VAULT_RECYCLER.getTrinketRecyclingOutput().getExtraOutput1Matching()));
      chestplate = getUseRelatedOutput(sword, DataTransferItem.doConvertStack(ModConfigs.VAULT_RECYCLER.getTrinketRecyclingOutput().getExtraOutput2Matching()));
      recipeList.add(new VaultRecyclerRecipeJEI(sword, axe, helmet, chestplate));
      sword = TrinketItem.createRandomTrinket(
         TrinketEffectRegistry.getOrderedEntries().get(Math.max(0, random.nextInt(TrinketEffectRegistry.getOrderedEntries().size() - 1)))
      );

      for (int i = 0; i < Math.max(1, TrinketItem.getUses(sword) / 5 * 2); i++) {
         TrinketItem.addUsedVault(sword, new UUID(i, i));
      }

      axe = getUseRelatedOutput(sword, DataTransferItem.doConvertStack(ModConfigs.VAULT_RECYCLER.getTrinketRecyclingOutput().getMainOutputMatching()));
      helmet = getUseRelatedOutput(sword, DataTransferItem.doConvertStack(ModConfigs.VAULT_RECYCLER.getTrinketRecyclingOutput().getExtraOutput1Matching()));
      chestplate = getUseRelatedOutput(sword, DataTransferItem.doConvertStack(ModConfigs.VAULT_RECYCLER.getTrinketRecyclingOutput().getExtraOutput2Matching()));
      recipeList.add(new VaultRecyclerRecipeJEI(sword, axe, helmet, chestplate));
      sword = TrinketItem.createRandomTrinket(
         TrinketEffectRegistry.getOrderedEntries().get(Math.max(0, random.nextInt(TrinketEffectRegistry.getOrderedEntries().size() - 1)))
      );

      for (int i = 0; i < Math.max(1, TrinketItem.getUses(sword) / 5 * 3); i++) {
         TrinketItem.addUsedVault(sword, new UUID(i, i));
      }

      axe = getUseRelatedOutput(sword, DataTransferItem.doConvertStack(ModConfigs.VAULT_RECYCLER.getTrinketRecyclingOutput().getMainOutputMatching()));
      helmet = getUseRelatedOutput(sword, DataTransferItem.doConvertStack(ModConfigs.VAULT_RECYCLER.getTrinketRecyclingOutput().getExtraOutput1Matching()));
      chestplate = getUseRelatedOutput(sword, DataTransferItem.doConvertStack(ModConfigs.VAULT_RECYCLER.getTrinketRecyclingOutput().getExtraOutput2Matching()));
      recipeList.add(new VaultRecyclerRecipeJEI(sword, axe, helmet, chestplate));
      sword = TrinketItem.createRandomTrinket(
         TrinketEffectRegistry.getOrderedEntries().get(Math.max(0, random.nextInt(TrinketEffectRegistry.getOrderedEntries().size() - 1)))
      );

      for (int i = 0; i < Math.max(1, TrinketItem.getUses(sword) / 5 * 4); i++) {
         TrinketItem.addUsedVault(sword, new UUID(i, i));
      }

      axe = getUseRelatedOutput(sword, DataTransferItem.doConvertStack(ModConfigs.VAULT_RECYCLER.getTrinketRecyclingOutput().getMainOutputMatching()));
      helmet = getUseRelatedOutput(sword, DataTransferItem.doConvertStack(ModConfigs.VAULT_RECYCLER.getTrinketRecyclingOutput().getExtraOutput1Matching()));
      chestplate = getUseRelatedOutput(sword, DataTransferItem.doConvertStack(ModConfigs.VAULT_RECYCLER.getTrinketRecyclingOutput().getExtraOutput2Matching()));
      recipeList.add(new VaultRecyclerRecipeJEI(sword, axe, helmet, chestplate));
      sword = TrinketItem.createRandomTrinket(
         TrinketEffectRegistry.getOrderedEntries().get(Math.max(0, random.nextInt(TrinketEffectRegistry.getOrderedEntries().size() - 2)))
      );

      for (int i = 0; i < Math.max(1, TrinketItem.getUses(sword) - 1); i++) {
         TrinketItem.addUsedVault(sword, new UUID(i, i));
      }

      axe = getUseRelatedOutput(sword, DataTransferItem.doConvertStack(ModConfigs.VAULT_RECYCLER.getTrinketRecyclingOutput().getMainOutputMatching()));
      helmet = getUseRelatedOutput(sword, DataTransferItem.doConvertStack(ModConfigs.VAULT_RECYCLER.getTrinketRecyclingOutput().getExtraOutput1Matching()));
      chestplate = getUseRelatedOutput(sword, DataTransferItem.doConvertStack(ModConfigs.VAULT_RECYCLER.getTrinketRecyclingOutput().getExtraOutput2Matching()));
      recipeList.add(new VaultRecyclerRecipeJEI(sword, axe, helmet, chestplate));
      sword = TrinketItem.createRandomTrinket(
         TrinketEffectRegistry.getOrderedEntries().get(Math.max(0, random.nextInt(TrinketEffectRegistry.getOrderedEntries().size() - 3)))
      );

      for (int i = 0; i < TrinketItem.getUses(sword); i++) {
         TrinketItem.addUsedVault(sword, new UUID(i, i));
      }

      axe = getUseRelatedOutput(sword, DataTransferItem.doConvertStack(ModConfigs.VAULT_RECYCLER.getTrinketRecyclingOutput().getMainOutputMatching()));
      helmet = getUseRelatedOutput(sword, DataTransferItem.doConvertStack(ModConfigs.VAULT_RECYCLER.getTrinketRecyclingOutput().getExtraOutput1Matching()));
      chestplate = getUseRelatedOutput(sword, DataTransferItem.doConvertStack(ModConfigs.VAULT_RECYCLER.getTrinketRecyclingOutput().getExtraOutput2Matching()));
      recipeList.add(new VaultRecyclerRecipeJEI(sword, axe, helmet, chestplate));
      return recipeList;
   }

   public static List<Component> getRelatedTooltip(ItemStack input, VaultRecyclerConfig.RecyclerOutput recyclerOutput, int i) {
      ChanceItemStackEntry output;
      if (i == 0) {
         output = recyclerOutput.getMainOutput();
      } else if (i == 1) {
         output = recyclerOutput.getExtraOutput1();
      } else {
         output = recyclerOutput.getExtraOutput2();
      }

      float chance = getResultPercentage(input);
      float out = output.getMatchingStack().getCount() * chance;
      List<Component> list = new ArrayList<>();
      list.add(new TextComponent("Base Count: " + output.getMinCount() + " - " + output.getMaxCount()));
      int resultCount = Mth.floor(out);
      if (resultCount < 1 && out > 0.0F) {
         resultCount++;
      }

      if (output.getChance() != 1.0F) {
         list.add(new TextComponent(String.format("%.1f", output.getChance() * 100.0F) + "% Chance"));
      }

      list.add(new TextComponent("Quantity and chance scale off input item quality"));
      ItemStack copyOut = output.getMatchingStack().copy();
      copyOut.setCount(resultCount);
      return list;
   }

   private static ItemStack getUseRelatedOutput(ItemStack input, ItemStack output) {
      float chance = getResultPercentage(input);
      float out = output.getCount() * chance;
      int resultCount = Mth.floor(out);
      if (resultCount < 1 && out > 0.0F) {
         resultCount++;
      }

      ItemStack copyOut = output.copy();
      copyOut.setCount(resultCount);
      return copyOut;
   }

   public static float getResultPercentage(ItemStack input) {
      if (input.isEmpty()) {
         return 0.0F;
      } else if (input.getItem() instanceof VaultGearItem) {
         return VaultGearData.read(input).getState() != VaultGearState.IDENTIFIED ? 1.0F : 1.0F - (float)input.getDamageValue() / input.getMaxDamage();
      } else if (input.getItem() instanceof TrinketItem) {
         return !TrinketItem.isIdentified(input) ? 1.0F : 1.0F - (float)TrinketItem.getUsedVaults(input).size() / TrinketItem.getUses(input);
      } else {
         return 0.0F;
      }
   }

   private static ItemStack generateRandomArmor(Item gear, VaultGearRarity rarity) {
      ItemStack itemStack = new ItemStack(gear);
      VaultGearData data = VaultGearData.read(itemStack);
      VaultGearItem item = VaultGearItem.of(itemStack);
      data.setRarity(rarity);
      data.write(itemStack);
      ResourceLocation modelKey = item.getRandomModel(itemStack, random);
      if (modelKey != null) {
         data.updateAttribute(ModGearAttributes.GEAR_MODEL, modelKey);
      }

      data.write(itemStack);
      GearRollHelper.initializeGear(itemStack);
      return itemStack;
   }
}
