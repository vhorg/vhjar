package iskallia.vault.event;

import iskallia.vault.VaultMod;
import iskallia.vault.config.entry.EnchantedBookEntry;
import iskallia.vault.core.world.generator.layout.DIYRoomEntry;
import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.ItemVaultCrystalSeal;
import iskallia.vault.item.MagnetItem;
import iskallia.vault.item.PaxelJewelItem;
import iskallia.vault.item.VaultCatalystInfusedItem;
import iskallia.vault.item.VaultRuneItem;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.item.crystal.VaultCrystalItem;
import iskallia.vault.item.crystal.layout.CrystalLayout;
import iskallia.vault.item.crystal.layout.DIYCrystalLayout;
import iskallia.vault.item.crystal.theme.PoolCrystalTheme;
import iskallia.vault.item.paxel.PaxelItem;
import iskallia.vault.util.EnchantmentUtil;
import iskallia.vault.util.MathUtilities;
import iskallia.vault.util.OverlevelEnchantHelper;
import iskallia.vault.world.data.ServerVaults;
import iskallia.vault.world.vault.modifier.VaultModifierStack;
import iskallia.vault.world.vault.modifier.registry.VaultModifierRegistry;
import iskallia.vault.world.vault.modifier.spi.VaultModifier;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public class AnvilEvents {
   @SubscribeEvent(
      priority = EventPriority.HIGH
   )
   public static void onVaultAnvil(AnvilUpdateEvent event) {
      Level world = event.getPlayer().getCommandSenderWorld();
      Item repairItem = event.getRight().getItem();
      if (repairItem != ModItems.REPAIR_CORE) {
         if (!(repairItem instanceof PaxelJewelItem)) {
            if (ServerVaults.isVaultWorld(world)) {
               event.setCanceled(true);
            }
         }
      }
   }

   @SubscribeEvent
   public static void repairVaultGear(AnvilUpdateEvent event) {
      ItemStack equipment = event.getLeft();
      ItemStack core = event.getRight();
      if (!equipment.isEmpty() && equipment.getItem() instanceof VaultGearItem) {
         if (core.is(ModItems.REPAIR_CORE)) {
            VaultGearData data = VaultGearData.read(equipment);
            if (data.getUsedRepairSlots() < data.getRepairSlots()) {
               ItemStack repairedGear = equipment.copy();
               repairedGear.setDamageValue(0);
               data.setUsedRepairSlots(data.getUsedRepairSlots() + 1);
               data.write(repairedGear);
               event.setOutput(repairedGear);
               event.setMaterialCost(1);
               event.setCost(1);
            }
         }
      }
   }

   @SubscribeEvent
   public static void onPreventEnchantmentApply(AnvilUpdateEvent event) {
      Map<Enchantment, Integer> enchantmentsToApply = EnchantmentHelper.getEnchantments(event.getRight());

      for (Enchantment ench : enchantmentsToApply.keySet()) {
         if (EnchantmentUtil.isEnchantmentBlocked(ench, event.getLeft())) {
            event.setCanceled(true);
            return;
         }
      }
   }

   @SubscribeEvent
   public static void onAnvilUpdate(AnvilUpdateEvent event) {
      ItemStack equipment = event.getLeft();
      ItemStack enchantedBook = event.getRight();
      if (equipment.getItem() != Items.ENCHANTED_BOOK) {
         if (enchantedBook.getItem() == Items.ENCHANTED_BOOK) {
            ItemStack upgradedEquipment = equipment.copy();
            Map<Enchantment, Integer> equipmentEnchantments = OverlevelEnchantHelper.getEnchantments(equipment);
            Map<Enchantment, Integer> bookEnchantments = OverlevelEnchantHelper.getEnchantments(enchantedBook);
            int overlevels = OverlevelEnchantHelper.getOverlevels(enchantedBook);
            if (overlevels != -1) {
               Map<Enchantment, Integer> enchantmentsToApply = new HashMap<>(equipmentEnchantments);

               for (Enchantment bookEnchantment : bookEnchantments.keySet()) {
                  if (equipmentEnchantments.containsKey(bookEnchantment)) {
                     int currentLevel = equipmentEnchantments.getOrDefault(bookEnchantment, 0);
                     int bookLevel = bookEnchantments.get(bookEnchantment);
                     int nextLevel = currentLevel == bookLevel ? currentLevel + 1 : Math.max(currentLevel, bookLevel);
                     enchantmentsToApply.put(bookEnchantment, nextLevel);
                  }
               }

               EnchantmentHelper.setEnchantments(enchantmentsToApply, upgradedEquipment);
               if (upgradedEquipment.equals(equipment, true)) {
                  event.setCanceled(true);
               } else {
                  EnchantedBookEntry bookTier = ModConfigs.OVERLEVEL_ENCHANT.getTier(overlevels);
                  event.setOutput(upgradedEquipment);
                  event.setCost(bookTier == null ? 1 : bookTier.getLevelNeeded());
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onApplySeal(AnvilUpdateEvent event) {
      ItemStack input = event.getLeft();
      ItemStack seal = event.getRight();
      if (seal.getItem() instanceof ItemVaultCrystalSeal) {
         ItemStack copy = input.getItem() == ModItems.VAULT_CRYSTAL ? input.copy() : new ItemStack(ModItems.VAULT_CRYSTAL);
         CrystalData crystal = new CrystalData(copy);
         if (crystal.getModifiers().isEmpty()) {
            if (ModConfigs.VAULT_CRYSTAL.applySeal(seal.getItem(), input.getItem(), crystal)) {
               VaultCrystalItem.setRandomSeed(copy);
               event.setOutput(copy);
               event.setMaterialCost(1);
               event.setCost(8);
            }
         }
      }
   }

   @SubscribeEvent
   public static void onApplyCatalyst(AnvilUpdateEvent event) {
      if (event.getLeft().getItem() instanceof VaultCrystalItem && event.getRight().getItem() instanceof VaultCatalystInfusedItem) {
         ItemStack output = event.getLeft().copy();
         CrystalData data = VaultCrystalItem.getData(output);
         List<VaultModifierStack> modifierStackList = VaultCatalystInfusedItem.getModifiers(event.getRight())
            .stream()
            .map(VaultModifierRegistry::getOpt)
            .flatMap(Optional::stream)
            .map(VaultModifierStack::of)
            .toList();
         if (data.addModifiersByCrafting(modifierStackList, CrystalData.Simulate.FALSE)) {
            VaultCrystalItem.scheduleTask(new VaultCrystalItem.AddCursesTask(VaultMod.id("catalyst_curse"), true), output);
            VaultCrystalItem.scheduleTask(VaultCrystalItem.ExhaustTask.INSTANCE, output);
            event.setOutput(output);
            event.setCost(modifierStackList.size() * 4);
            event.setMaterialCost(1);
         }
      }
   }

   @SubscribeEvent
   public static void onApplyWitherSkull(AnvilUpdateEvent event) {
      if (event.getLeft().getItem() instanceof VaultCrystalItem && event.getRight().getItem() == Items.WITHER_SKELETON_SKULL) {
         ItemStack output = event.getLeft().copy();
         VaultCrystalItem.scheduleTask(new VaultCrystalItem.AddCursesTask(VaultMod.id("wither_skull_curse"), false), output);
         event.setOutput(output);
         event.setCost(1);
         event.setMaterialCost(1);
      }
   }

   @SubscribeEvent
   public static void onApplyMote(AnvilUpdateEvent event) {
      if (event.getLeft().getItem() instanceof VaultCrystalItem) {
         Item item = event.getRight().getItem();
         ItemStack output = event.getLeft().copy();
         CrystalData data = VaultCrystalItem.getData(output);
         if (item == ModItems.MOTE_CLARITY && !data.hasClarity()) {
            VaultCrystalItem.scheduleTask(VaultCrystalItem.AddClarityTask.INSTANCE, output);
            event.setOutput(output);
            event.setCost(ModConfigs.VAULT_CRYSTAL.MOTES.clarityLevelCost);
            event.setMaterialCost(1);
         } else if (item == ModItems.MOTE_PURITY && data.isCursed()) {
            VaultCrystalItem.scheduleTask(VaultCrystalItem.RemoveRandomCurseTask.INSTANCE, output);
            event.setOutput(output);
            event.setCost(ModConfigs.VAULT_CRYSTAL.MOTES.purityLevelCost);
            event.setMaterialCost(1);
         } else if (item == ModItems.MOTE_SANCTITY && data.isCursed()) {
            VaultCrystalItem.getData(output).removeAllCurses();
            event.setOutput(output);
            event.setCost(ModConfigs.VAULT_CRYSTAL.MOTES.sanctityLevelCost);
            event.setMaterialCost(1);
         }
      }
   }

   @SubscribeEvent
   public static void onApplyRune(AnvilUpdateEvent event) {
      if (event.getLeft().getItem() instanceof VaultCrystalItem && event.getRight().getItem() instanceof VaultRuneItem) {
         ItemStack output = event.getLeft().copy();
         CrystalData data = VaultCrystalItem.getData(output);
         if (!data.canModifyWithCrafting()) {
            return;
         }

         data.setTheme(new PoolCrystalTheme(VaultMod.id("diy")));
         CrystalLayout layout = data.getLayout();
         List<DIYRoomEntry> entries = new ArrayList<>();

         for (int i = 0; i < event.getRight().getCount(); i++) {
            entries.addAll(VaultRuneItem.getEntries(event.getRight()));
         }

         if (!(layout instanceof DIYCrystalLayout)) {
            layout = new DIYCrystalLayout(1, new ArrayList<>());
         }

         entries.forEach(((DIYCrystalLayout)layout)::add);
         data.setLayout(layout);
         int amount = event.getRight().getCount();
         event.setOutput(output);
         event.setCost(amount * 4);
         event.setMaterialCost(amount);
      }
   }

   @SubscribeEvent
   public static void onApplyBanishedSoul(AnvilUpdateEvent event) {
      if (event.getRight().getItem() == ModItems.BANISHED_SOUL) {
         Item input = event.getLeft().getItem();
         if (input == ModItems.VAULT_CRYSTAL) {
            ItemStack output = event.getLeft().copy();
            CrystalData data = new CrystalData(output);
            if (data.isCursed() || !data.canBeModified() || data.getLevel() <= 0) {
               return;
            }

            int newLevel = Math.max(0, data.getLevel() - event.getRight().getCount());
            int price = data.getLevel() - newLevel;
            data.setLevel(newLevel);
            event.setMaterialCost(price);
            event.setCost(price);
            event.setOutput(output);
         } else if (input instanceof VaultGearItem) {
            ItemStack output = event.getLeft().copy();
            VaultGearData data = VaultGearData.read(output);
            if (data.getState() != VaultGearState.UNIDENTIFIED || data.getItemLevel() <= 0) {
               return;
            }

            int newLevel = Math.max(0, data.getItemLevel() - event.getRight().getCount());
            int price = data.getItemLevel() - newLevel;
            data.setItemLevel(newLevel);
            data.write(output);
            event.setMaterialCost(price);
            event.setCost(price);
            event.setOutput(output);
         }
      }
   }

   @SubscribeEvent
   public static void onApplyEchoGemToCrystal(AnvilUpdateEvent event) {
      if (event.getLeft().getItem() instanceof VaultCrystalItem && event.getRight().getItem() == ModItems.ECHO_GEM) {
         ItemStack output = event.getLeft().copy();
         CrystalData data = VaultCrystalItem.getData(output);
         if (data.getEchoData().getEchoCount() == 0) {
            data.addEchoGems(1);
            data.setModifiable(false);
            event.setMaterialCost(1);
         } else {
            int count = event.getRight().getCount();
            VaultCrystalItem.scheduleTask(new VaultCrystalItem.EchoTask(count), output);
            event.setMaterialCost(count);
         }

         event.setCost(1);
         event.setOutput(output);
      }
   }

   @SubscribeEvent
   public static void onApplyEchoCrystal(AnvilUpdateEvent event) {
      if (event.getLeft().getItem() instanceof VaultCrystalItem) {
         if (event.getRight().getItem() instanceof VaultCrystalItem) {
            ItemStack output = event.getLeft().copy();
            if (!output.getOrCreateTag().getBoolean("Cloned")) {
               CrystalData crystalData = VaultCrystalItem.getData(output);
               if (crystalData.getEchoData().getEchoCount() == 0) {
                  if (crystalData.canBeModified()) {
                     ItemStack echoCrystal = event.getRight().copy();
                     CrystalData echoCrystalData = VaultCrystalItem.getData(echoCrystal);
                     if (echoCrystalData.getEchoData().getEchoCount() > 0) {
                        boolean success = MathUtilities.randomFloat(0.0F, 1.0F) < echoCrystalData.getEchoData().getCloneSuccessRate();
                        VaultCrystalItem.scheduleTask(new VaultCrystalItem.CloneTask(success), output);
                        event.setCost(1);
                        event.setMaterialCost(1);
                        event.setOutput(output);
                     }
                  }
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onRepairDeny(AnvilUpdateEvent event) {
      if (event.getLeft().getItem() instanceof MagnetItem && event.getRight().getItem() instanceof MagnetItem) {
         event.setCanceled(true);
      } else if (event.getLeft().getItem() instanceof PaxelItem && event.getRight().getItem() instanceof PaxelItem) {
         event.setCanceled(true);
      }

      if (event.getLeft().getItem() instanceof VaultGearItem && event.getRight().getItem() instanceof VaultGearItem) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public static void onApplySoulFlame(AnvilUpdateEvent event) {
      if (event.getLeft().getItem() instanceof VaultCrystalItem && event.getRight().getItem() == ModItems.SOUL_FLAME) {
         ItemStack output = event.getLeft().copy();
         CrystalData data = VaultCrystalItem.getData(output);
         if (!data.getModifiers().isEmpty()) {
            return;
         }

         VaultModifierRegistry.getOpt(VaultMod.id("afterlife")).ifPresent(vaultModifier -> {
            VaultModifierStack modifierStack = VaultModifierStack.of((VaultModifier<?>)vaultModifier);
            if (data.addModifierByCrafting(modifierStack, false, CrystalData.Simulate.TRUE)) {
               data.addModifierByCrafting(modifierStack, false, CrystalData.Simulate.FALSE);
               VaultCrystalItem.scheduleTask(new VaultCrystalItem.AddCursesTask(VaultMod.id("soul_flame_curse"), false), output);
               data.setModifiable(false);
               event.setOutput(output);
               event.setMaterialCost(1);
               event.setCost(10);
            }
         });
      }
   }

   @SubscribeEvent
   public static void onApplyPhoenix(AnvilUpdateEvent event) {
      if (event.getLeft().getItem() instanceof VaultCrystalItem && event.getRight().getItem() == ModItems.PHOENIX_FEATHER) {
         ItemStack output = event.getLeft().copy();
         CrystalData data = VaultCrystalItem.getData(output);
         VaultModifierRegistry.getOpt(VaultMod.id("phoenix")).ifPresent(modifier -> {
            VaultModifierStack modifierStack = VaultModifierStack.of((VaultModifier<?>)modifier);
            if (data.addModifierByCrafting(modifierStack, false, CrystalData.Simulate.TRUE)) {
               data.addModifierByCrafting(modifierStack, false, CrystalData.Simulate.FALSE);
               data.setModifiable(false);
               event.setOutput(output);
               event.setMaterialCost(1);
               event.setCost(10);
            }
         });
      }
   }

   @SubscribeEvent
   public static void onApplyLootersDream(AnvilUpdateEvent event) {
      if (event.getLeft().getItem() instanceof VaultCrystalItem && event.getRight().getItem() == ModItems.EYE_OF_AVARICE) {
         ItemStack output = event.getLeft().copy();
         CrystalData data = VaultCrystalItem.getData(output);
         VaultModifierRegistry.getOpt(VaultMod.id("looters_dream")).ifPresent(modifier -> {
            VaultModifierStack modifierStack = VaultModifierStack.of((VaultModifier<?>)modifier);
            if (data.addModifierByCrafting(modifierStack, false, CrystalData.Simulate.TRUE)) {
               data.addModifierByCrafting(modifierStack, false, CrystalData.Simulate.FALSE);
               data.setModifiable(false);
               event.setOutput(output);
               event.setMaterialCost(1);
               event.setCost(10);
            }
         });
      }
   }

   @SubscribeEvent
   public static void onApplyPog(AnvilUpdateEvent event) {
      if (event.getRight().getItem() == ModItems.OMEGA_POG) {
         ResourceLocation name = event.getLeft().getItem().getRegistryName();
         if (name.equals(ModBlocks.VAULT_ARTIFACT.getRegistryName())) {
            event.setOutput(new ItemStack(ModItems.UNIDENTIFIED_ARTIFACT));
            event.setMaterialCost(1);
            event.setCost(1);
         }
      }
   }

   @SubscribeEvent
   public static void onApplyMending(AnvilUpdateEvent event) {
      ItemStack out = event.getOutput();
      if (out.getItem() instanceof ShieldItem) {
         if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.MENDING, out) > 0) {
            event.setCanceled(true);
         }

         if (EnchantmentHelper.getItemEnchantmentLevel(Enchantments.THORNS, out) > 0) {
            event.setCanceled(true);
         }
      }
   }

   @SubscribeEvent
   public static void onAddPerkToPaxel(AnvilUpdateEvent event) {
      ItemStack paxel = event.getLeft();
      ItemStack jewel = event.getRight();
      if (paxel.getItem() instanceof PaxelItem && jewel.getItem() instanceof PaxelJewelItem ji) {
         int sockets = PaxelItem.getSockets(paxel);
         if (sockets != 0) {
            PaxelItem.Perk perk = ji.getPerk();
            if (!PaxelItem.getPerks(paxel).contains(perk)) {
               event.setMaterialCost(1);
               event.setCost(1);
               ItemStack output = paxel.copy();
               PaxelItem.setSockets(output, sockets - 1);
               PaxelItem.addPerk(output, perk);
               event.setOutput(output);
            }
         }
      }
   }

   @SubscribeEvent
   public static void onRepairMagnetOrPaxel(AnvilUpdateEvent event) {
      ItemStack magnet = event.getLeft();
      ItemStack magnetite = event.getRight();
      if (magnetite.is(ModItems.REPAIR_CORE)) {
         ItemStack output = magnet.copy();
         int used;
         int max;
         if (magnet.getItem() instanceof MagnetItem) {
            used = MagnetItem.getUsedRepairSlots(magnet);
            max = MagnetItem.getMaxRepairSlots(magnet);
         } else {
            if (!(magnet.getItem() instanceof PaxelItem)) {
               return;
            }

            used = PaxelItem.getUsedRepairSlots(magnet);
            max = PaxelItem.getMaxRepairSlots(magnet);
         }

         int left = max - used;
         if (event.getLeft().getDamageValue() != 0 && left != 0) {
            event.setMaterialCost(1);
            event.setCost(1);
            MagnetItem.useRepairSlot(output);
            output.setDamageValue(0);
            event.setOutput(output);
         } else {
            event.setCanceled(true);
         }
      }
   }
}
