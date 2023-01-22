package iskallia.vault.integration.jei;

import com.google.common.collect.Lists;
import iskallia.vault.VaultMod;
import iskallia.vault.core.world.generator.layout.DIYRoomEntry;
import iskallia.vault.gear.VaultGearModifierHelper;
import iskallia.vault.gear.VaultGearRarity;
import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.crafting.VaultGearCraftingHelper;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.MagnetItem;
import iskallia.vault.item.VaultCatalystInfusedItem;
import iskallia.vault.item.VaultRuneItem;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.item.crystal.VaultCrystalItem;
import iskallia.vault.item.crystal.layout.CrystalLayout;
import iskallia.vault.item.crystal.layout.DIYCrystalLayout;
import iskallia.vault.item.crystal.theme.PoolCrystalTheme;
import iskallia.vault.world.vault.modifier.VaultModifierStack;
import iskallia.vault.world.vault.modifier.registry.VaultModifierRegistry;
import iskallia.vault.world.vault.modifier.spi.VaultModifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import mezz.jei.api.recipe.vanilla.IJeiAnvilRecipe;
import mezz.jei.api.recipe.vanilla.IVanillaRecipeFactory;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;

public class AnvilRecipesJEI {
   private static final Random random = new Random(0L);

   public static List<IJeiAnvilRecipe> getAnvilRecipes(IVanillaRecipeFactory vanillaRecipeFactory) {
      List<IJeiAnvilRecipe> recipeList = Lists.newArrayList();
      List<List<ItemStack>> vaultArmorList = Lists.newArrayList();
      List<ItemStack> vaultArmorListUnidentified = Lists.newArrayList();
      vaultArmorList.add(generateRandomArmorList(ModItems.HELMET));
      vaultArmorList.add(generateRandomArmorList(ModItems.CHESTPLATE));
      vaultArmorList.add(generateRandomArmorList(ModItems.LEGGINGS));
      vaultArmorList.add(generateRandomArmorList(ModItems.BOOTS));
      vaultArmorListUnidentified.add(new ItemStack(ModItems.HELMET));
      vaultArmorListUnidentified.add(new ItemStack(ModItems.CHESTPLATE));
      vaultArmorListUnidentified.add(new ItemStack(ModItems.LEGGINGS));
      vaultArmorListUnidentified.add(new ItemStack(ModItems.BOOTS));
      List<ItemStack> vaultWeapons = List.of(new ItemStack(ModItems.SWORD), new ItemStack(ModItems.AXE));
      List<ItemStack> vaultSeals = List.of(
         new ItemStack(ModItems.CRYSTAL_SEAL_CAKE), new ItemStack(ModItems.CRYSTAL_SEAL_HUNTER), new ItemStack(ModItems.CRYSTAL_SEAL_EXECUTIONER)
      );
      List<ItemStack> vaultPicks = List.of(
         new ItemStack(ModItems.VAULTERITE_PICKAXE),
         new ItemStack(ModItems.VAULT_PICKAXE),
         new ItemStack(ModItems.BLACK_CHROMATIC_PICKAXE),
         new ItemStack(ModItems.ECHOING_PICKAXE),
         new ItemStack(ModItems.PRISMATIC_PICKAXE)
      );
      List<ItemStack> omegaPog = List.of(new ItemStack(ModItems.OMEGA_POG));
      List<ItemStack> repairCore = List.of(new ItemStack(ModItems.REPAIR_CORE));

      for (List<ItemStack> armorItemList : vaultArmorList) {
         List<ItemStack> inputList = new ArrayList<>(armorItemList.size());
         List<ItemStack> resultList = new ArrayList<>(armorItemList.size());

         for (ItemStack armorItem : armorItemList) {
            ItemStack input = armorItem.copy();
            ItemStack result = armorItem.copy();
            input.setDamageValue(input.getMaxDamage() - 5);
            VaultGearData data = VaultGearData.read(result);
            int repairs = data.getUsedRepairSlots() + 1;
            data.setUsedRepairSlots(repairs);
            data.write(result);
            inputList.add(input);
            resultList.add(result);
         }

         recipeList.add(vanillaRecipeFactory.createAnvilRecipe(inputList, repairCore, resultList));
      }

      for (ItemStack weapon : vaultWeapons) {
         List<ItemStack> list = generateRandomArmorList(weapon.getItem());
         vaultArmorList.add(generateRandomArmorList(weapon.getItem()));
         List<ItemStack> damaged = new ArrayList<>(list);
         list.get(0).setDamageValue(list.get(0).getMaxDamage() - 5);
         ItemStack result = list.get(0).copy();
         VaultGearData data = VaultGearData.read(result);
         int repairs = data.getUsedRepairSlots() + 1;
         data.setRepairSlots(repairs);
         data.write(result);
         recipeList.add(vanillaRecipeFactory.createAnvilRecipe(damaged, repairCore, List.of(result)));
      }

      for (ItemStack seal : vaultSeals) {
         ItemStack input = new ItemStack(ModItems.VAULT_CRYSTAL);
         ItemStack copy = input.getItem() == ModItems.VAULT_CRYSTAL ? input.copy() : new ItemStack(ModItems.VAULT_CRYSTAL);
         CrystalData crystal = new CrystalData(copy);
         ModConfigs.VAULT_CRYSTAL.applySeal(seal.getItem(), input.getItem(), crystal);
         VaultCrystalItem.setRandomSeed(copy);
         recipeList.add(vanillaRecipeFactory.createAnvilRecipe(List.of(new ItemStack(ModItems.VAULT_CRYSTAL)), List.of(seal), List.of(copy)));
      }

      ItemStack left = new ItemStack(ModItems.VAULT_CRYSTAL);
      ItemStack right = new ItemStack(ModItems.VAULT_CATALYST_INFUSED);
      ItemStack output = new ItemStack(ModItems.VAULT_CRYSTAL);
      VaultCatalystInfusedItem.setJeiModifiers(right, List.of(VaultMod.id("living"), VaultMod.id("wild")));
      CrystalData data = VaultCrystalItem.getData(output);
      List<VaultModifierStack> modifierStackList = VaultCatalystInfusedItem.getModifiers(right)
         .stream()
         .map(VaultModifierRegistry::getOpt)
         .flatMap(Optional::stream)
         .map(VaultModifierStack::of)
         .toList();
      if (data.addModifiersByCrafting(modifierStackList, CrystalData.Simulate.FALSE)) {
         VaultCrystalItem.scheduleTask(new VaultCrystalItem.AddCursesTask(VaultMod.id("catalyst_curse"), true), output);
         VaultCrystalItem.scheduleTask(VaultCrystalItem.ExhaustTask.INSTANCE, output);
      }

      recipeList.add(vanillaRecipeFactory.createAnvilRecipe(List.of(left), List.of(right), List.of(output)));
      left = new ItemStack(ModItems.VAULT_CRYSTAL);
      right = new ItemStack(Items.WITHER_SKELETON_SKULL);
      output = new ItemStack(ModItems.VAULT_CRYSTAL);
      data = VaultCrystalItem.getData(output);
      addModifier(data, VaultMod.id("hunger"));
      recipeList.add(vanillaRecipeFactory.createAnvilRecipe(List.of(left), List.of(right), List.of(output)));
      left = new ItemStack(ModItems.VAULT_CRYSTAL);
      right = new ItemStack(ModItems.MOTE_CLARITY);
      output = new ItemStack(ModItems.VAULT_CRYSTAL);
      data = VaultCrystalItem.getData(left);
      CrystalData dataOutput = VaultCrystalItem.getData(output);
      data.setLevel(5);
      dataOutput.setLevel(5);
      addModifier(dataOutput, VaultMod.id("hunger"));
      addModifier(dataOutput, VaultMod.id("tired"));
      addModifier(data, VaultMod.id("hunger"));
      addModifier(data, VaultMod.id("tired"));
      dataOutput.setClarity(true);
      recipeList.add(vanillaRecipeFactory.createAnvilRecipe(List.of(left), List.of(right), List.of(output)));
      left = new ItemStack(ModItems.VAULT_CRYSTAL);
      right = new ItemStack(ModItems.MOTE_PURITY);
      output = new ItemStack(ModItems.VAULT_CRYSTAL);
      data = VaultCrystalItem.getData(left);
      CrystalData dataOutputx = VaultCrystalItem.getData(output);
      data.setLevel(5);
      dataOutputx.setLevel(5);
      addModifier(dataOutputx, VaultMod.id("hunger"));
      addModifier(dataOutputx, VaultMod.id("tired"));
      addModifier(data, VaultMod.id("hunger"));
      addModifier(data, VaultMod.id("tired"));
      dataOutputx.removeRandomCurse(random);
      recipeList.add(vanillaRecipeFactory.createAnvilRecipe(List.of(left), List.of(right), List.of(output)));
      left = new ItemStack(ModItems.VAULT_CRYSTAL);
      right = new ItemStack(ModItems.MOTE_SANCTITY);
      output = new ItemStack(ModItems.VAULT_CRYSTAL);
      data = VaultCrystalItem.getData(left);
      CrystalData dataOutputxx = VaultCrystalItem.getData(output);
      data.setLevel(5);
      dataOutputxx.setLevel(5);
      addModifier(dataOutputxx, VaultMod.id("hunger"));
      addModifier(dataOutputxx, VaultMod.id("tired"));
      addModifier(data, VaultMod.id("hunger"));
      addModifier(data, VaultMod.id("tired"));
      VaultCrystalItem.getData(output).removeAllCurses();
      recipeList.add(vanillaRecipeFactory.createAnvilRecipe(List.of(left), List.of(right), List.of(output)));
      left = new ItemStack(ModItems.VAULT_CRYSTAL);
      right = new ItemStack(ModItems.RUNE);
      output = new ItemStack(ModItems.VAULT_CRYSTAL);
      ListTag list = new ListTag();
      list.add(DIYRoomEntry.ofType(DIYRoomEntry.Type.COMMON, 1).serializeNBT());
      right.getOrCreateTag().put("entries", list);
      CrystalData dataLeft = VaultCrystalItem.getData(left);
      CrystalData dataOutputxxx = VaultCrystalItem.getData(output);
      dataLeft.setLevel(5);
      dataOutputxxx.setLevel(5);
      CrystalData datax = VaultCrystalItem.getData(output);
      datax.setTheme(new PoolCrystalTheme(VaultMod.id("diy")));
      CrystalLayout layout = datax.getLayout();
      List<DIYRoomEntry> entries = new ArrayList<>();

      for (int i = 0; i < right.getCount(); i++) {
         entries.addAll(VaultRuneItem.getEntries(right));
      }

      if (!(layout instanceof DIYCrystalLayout)) {
         layout = new DIYCrystalLayout(1, new ArrayList<>());
      }

      entries.forEach(((DIYCrystalLayout)layout)::add);
      datax.setLayout(layout);
      VaultCrystalItem.getData(output).removeAllCurses();
      recipeList.add(vanillaRecipeFactory.createAnvilRecipe(List.of(left), List.of(right), List.of(output)));

      for (ItemStack armorItem : vaultArmorListUnidentified) {
         output = new ItemStack(ModItems.BANISHED_SOUL);
         ItemStack input = armorItem.copy();
         ItemStack result = armorItem.copy();
         VaultGearData dataInput = VaultGearData.read(input);
         VaultGearData dataOutputxxxx = VaultGearData.read(result);
         dataInput.setItemLevel(5);
         dataOutputxxxx.setItemLevel(4);
         dataInput.write(input);
         dataOutputxxxx.write(result);
         recipeList.add(vanillaRecipeFactory.createAnvilRecipe(List.of(input), List.of(output), List.of(result)));
      }

      left = new ItemStack(ModItems.VAULT_CRYSTAL);
      right = new ItemStack(ModItems.ECHO_GEM);
      output = new ItemStack(ModItems.VAULT_CRYSTAL);
      data = VaultCrystalItem.getData(left);
      CrystalData dataOutputxxxx = VaultCrystalItem.getData(output);
      dataOutputxxxx.addEchoGems(1);
      dataOutputxxxx.setModifiable(false);
      recipeList.add(vanillaRecipeFactory.createAnvilRecipe(List.of(left), List.of(right), List.of(output)));
      left = new ItemStack(ModItems.VAULT_CRYSTAL);
      right = new ItemStack(Items.DIAMOND);
      output = new ItemStack(ModItems.VAULT_CRYSTAL);
      data = VaultCrystalItem.getData(left);
      CrystalData dataOutputxxxxx = VaultCrystalItem.getData(output);
      data.setLevel(5);
      dataOutputxxxxx.setLevel(4);
      recipeList.add(vanillaRecipeFactory.createAnvilRecipe(List.of(left), List.of(right), List.of(output)));
      left = new ItemStack(ModItems.VAULT_CRYSTAL);
      right = new ItemStack(ModItems.SOUL_FLAME);
      output = new ItemStack(ModItems.VAULT_CRYSTAL);
      data = VaultCrystalItem.getData(left);
      CrystalData dataOutputxxxxxx = VaultCrystalItem.getData(output);
      data.setLevel(10);
      dataOutputxxxxxx.setLevel(10);
      VaultModifierRegistry.getOpt(VaultMod.id("afterlife")).ifPresent(vaultModifier -> {
         VaultModifierStack modifierStack = VaultModifierStack.of((VaultModifier<?>)vaultModifier);
         if (dataOutput.addModifierByCrafting(modifierStack, false, CrystalData.Simulate.TRUE)) {
            dataOutput.addModifierByCrafting(modifierStack, false, CrystalData.Simulate.FALSE);
            addModifier(dataOutput, VaultMod.id("hunger"));
            addModifier(dataOutput, VaultMod.id("tired"));
            dataOutput.setModifiable(false);
         }
      });
      recipeList.add(vanillaRecipeFactory.createAnvilRecipe(List.of(left), List.of(right), List.of(output)));
      left = new ItemStack(ModItems.VAULT_CRYSTAL);
      right = new ItemStack(ModItems.PHOENIX_FEATHER);
      output = new ItemStack(ModItems.VAULT_CRYSTAL);
      data = VaultCrystalItem.getData(left);
      CrystalData dataOutputxxxxxxx = VaultCrystalItem.getData(output);
      data.setLevel(10);
      dataOutputxxxxxxx.setLevel(10);
      VaultModifierRegistry.getOpt(VaultMod.id("phoenix")).ifPresent(modifier -> {
         VaultModifierStack modifierStack = VaultModifierStack.of((VaultModifier<?>)modifier);
         if (dataOutput.addModifierByCrafting(modifierStack, false, CrystalData.Simulate.TRUE)) {
            dataOutput.addModifierByCrafting(modifierStack, false, CrystalData.Simulate.FALSE);
            dataOutput.setModifiable(false);
         }
      });
      recipeList.add(vanillaRecipeFactory.createAnvilRecipe(List.of(left), List.of(right), List.of(output)));
      left = new ItemStack(ModItems.VAULT_CRYSTAL);
      right = new ItemStack(ModItems.EYE_OF_AVARICE);
      output = new ItemStack(ModItems.VAULT_CRYSTAL);
      data = VaultCrystalItem.getData(left);
      CrystalData dataOutputxxxxxxxx = VaultCrystalItem.getData(output);
      data.setLevel(10);
      dataOutputxxxxxxxx.setLevel(10);
      VaultModifierRegistry.getOpt(VaultMod.id("looters_dream")).ifPresent(modifier -> {
         VaultModifierStack modifierStack = VaultModifierStack.of((VaultModifier<?>)modifier);
         if (dataOutput.addModifierByCrafting(modifierStack, false, CrystalData.Simulate.TRUE)) {
            dataOutput.addModifierByCrafting(modifierStack, false, CrystalData.Simulate.FALSE);
            dataOutput.setModifiable(false);
         }
      });
      recipeList.add(vanillaRecipeFactory.createAnvilRecipe(List.of(left), List.of(right), List.of(output)));
      left = new ItemStack(ModItems.VAULT_CRYSTAL);
      right = new ItemStack(ModItems.VAULT_CRYSTAL);
      CrystalData dataLeftx = VaultCrystalItem.getData(left);
      data = VaultCrystalItem.getData(right);
      data.addEchoGems(1);
      data.setModifiable(false);
      addModifier(dataLeftx, VaultMod.id("gilded"));
      addModifier(dataLeftx, VaultMod.id("lucky"));
      ItemStack outputx = left.copy();
      recipeList.add(vanillaRecipeFactory.createAnvilRecipe(List.of(left), List.of(right), List.of(outputx)));
      left = new ItemStack(ModItems.MAGNET_ITEM);
      right = new ItemStack(ModItems.REPAIR_CORE);
      output = new ItemStack(ModItems.MAGNET_ITEM);
      MagnetItem.useRepairSlot(output);
      left.setDamageValue(left.getMaxDamage() - 5);
      recipeList.add(vanillaRecipeFactory.createAnvilRecipe(List.of(left), List.of(right), List.of(output)));

      for (ItemStack leftx : vaultPicks) {
         output = new ItemStack(ModItems.REPAIR_CORE);
         ItemStack outputxx = leftx.copy();
         MagnetItem.useRepairSlot(outputxx);
         leftx.setDamageValue(leftx.getMaxDamage() - 5);
         recipeList.add(vanillaRecipeFactory.createAnvilRecipe(List.of(leftx), List.of(output), List.of(outputxx)));
      }

      recipeList.add(
         vanillaRecipeFactory.createAnvilRecipe(
            List.of(new ItemStack(ModItems.BLANK_KEY)), List.of(new ItemStack(ModItems.BOMIGNITE_CLUSTER)), List.of(new ItemStack(ModItems.BOMIGNITE_KEY))
         )
      );
      recipeList.add(
         vanillaRecipeFactory.createAnvilRecipe(
            List.of(new ItemStack(ModItems.BLANK_KEY)), List.of(new ItemStack(ModItems.ASHIUM_CLUSTER)), List.of(new ItemStack(ModItems.ASHIUM_KEY))
         )
      );
      recipeList.add(
         vanillaRecipeFactory.createAnvilRecipe(
            List.of(new ItemStack(ModItems.BLANK_KEY)), List.of(new ItemStack(ModItems.ISKALLIUM_CLUSTER)), List.of(new ItemStack(ModItems.ISKALLIUM_KEY))
         )
      );
      recipeList.add(
         vanillaRecipeFactory.createAnvilRecipe(
            List.of(new ItemStack(ModItems.BLANK_KEY)), List.of(new ItemStack(ModItems.GORGINITE_CLUSTER)), List.of(new ItemStack(ModItems.GORGINITE_KEY))
         )
      );
      recipeList.add(
         vanillaRecipeFactory.createAnvilRecipe(
            List.of(new ItemStack(ModItems.BLANK_KEY)), List.of(new ItemStack(ModItems.PETZANITE_CLUSTER)), List.of(new ItemStack(ModItems.PETZANITE_KEY))
         )
      );
      recipeList.add(
         vanillaRecipeFactory.createAnvilRecipe(
            List.of(new ItemStack(ModItems.BLANK_KEY)), List.of(new ItemStack(ModItems.TUBIUM_CLUSTER)), List.of(new ItemStack(ModItems.TUBIUM_KEY))
         )
      );
      recipeList.add(
         vanillaRecipeFactory.createAnvilRecipe(
            List.of(new ItemStack(ModItems.BLANK_KEY)), List.of(new ItemStack(ModItems.SPARKLETINE_CLUSTER)), List.of(new ItemStack(ModItems.SPARKLETINE_KEY))
         )
      );
      recipeList.add(
         vanillaRecipeFactory.createAnvilRecipe(
            List.of(new ItemStack(ModItems.BLANK_KEY)), List.of(new ItemStack(ModItems.UPALINE_CLUSTER)), List.of(new ItemStack(ModItems.UPALINE_KEY))
         )
      );
      recipeList.add(
         vanillaRecipeFactory.createAnvilRecipe(
            List.of(new ItemStack(ModItems.BLANK_KEY)), List.of(new ItemStack(ModItems.XENIUM_CLUSTER)), List.of(new ItemStack(ModItems.XENIUM_KEY))
         )
      );
      left = Lists.newArrayList(new ItemStack[]{new ItemStack(ModBlocks.VAULT_ARTIFACT)});
      List<ItemStack> unidentifiedArtifact = List.of(new ItemStack(ModItems.UNIDENTIFIED_ARTIFACT));
      recipeList.add(vanillaRecipeFactory.createAnvilRecipe(left, omegaPog, unidentifiedArtifact));
      return recipeList;
   }

   private static void addModifier(CrystalData data, ResourceLocation id) {
      VaultModifier<?> modifier = VaultModifierRegistry.getOrDefault(id, null);
      if (modifier != null) {
         data.addModifier(VaultModifierStack.of(modifier));
      }
   }

   private static List<ItemStack> generateRandomArmorList(Item gear) {
      List<ItemStack> returnList = new ArrayList<>();
      ItemStack itemStack = new ItemStack(gear);
      VaultGearData data = VaultGearData.read(itemStack);
      VaultGearItem item = VaultGearItem.of(itemStack);
      data.setRarity(VaultGearRarity.EPIC);
      data.write(itemStack);
      ResourceLocation modelKey = item.getRandomModel(itemStack, random);
      if (modelKey != null) {
         data.updateAttribute(ModGearAttributes.GEAR_MODEL, modelKey);
      }

      data.write(itemStack);
      data.setState(VaultGearState.IDENTIFIED);
      data.write(itemStack);
      VaultGearModifierHelper.reRollRepairSlots(itemStack, random);
      VaultGearCraftingHelper.reRollCraftingPotential(itemStack);
      VaultGearModifierHelper.generateAffixSlots(itemStack, random);
      VaultGearModifierHelper.generateImplicits(itemStack, random);
      VaultGearModifierHelper.generateModifiers(itemStack, random);
      returnList.add(itemStack);
      return returnList;
   }
}
