package iskallia.vault.event;

import iskallia.vault.VaultMod;
import iskallia.vault.config.entry.EnchantedBookEntry;
import iskallia.vault.gear.VaultGearState;
import iskallia.vault.gear.data.VaultGearData;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.AugmentItem;
import iskallia.vault.item.ItemVaultCrystalSeal;
import iskallia.vault.item.LegacyMagnetItem;
import iskallia.vault.item.PaxelJewelItem;
import iskallia.vault.item.VaultCatalystInfusedItem;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.item.crystal.VaultCrystalItem;
import iskallia.vault.item.crystal.theme.ValueCrystalTheme;
import iskallia.vault.item.data.InscriptionData;
import iskallia.vault.item.tool.PaxelItem;
import iskallia.vault.item.tool.ToolItem;
import iskallia.vault.util.EnchantmentUtil;
import iskallia.vault.util.OverlevelEnchantHelper;
import iskallia.vault.world.data.ServerVaults;
import iskallia.vault.world.vault.modifier.VaultModifierStack;
import iskallia.vault.world.vault.modifier.registry.VaultModifierRegistry;
import iskallia.vault.world.vault.modifier.spi.VaultModifier;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
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
      if (!equipment.isEmpty() && (equipment.getItem() instanceof VaultGearItem || equipment.getItem() == ModItems.TOOL)) {
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
         if (seal.getItem() != ModItems.CRYSTAL_SEAL_SPEEDRUN) {
            ItemStack copy = input.getItem() == ModItems.VAULT_CRYSTAL ? input.copy() : new ItemStack(ModItems.VAULT_CRYSTAL);
            CrystalData crystal = CrystalData.read(copy);
            if (crystal.getModifiers().isEmpty()) {
               if (ModConfigs.VAULT_CRYSTAL.applySeal(input, seal, copy, crystal)) {
                  event.setOutput(copy);
                  event.setMaterialCost(1);
                  event.setCost(8);
               }
            }
         }
      }
   }

   @SubscribeEvent
   public static void onApplyCatalyst(AnvilUpdateEvent event) {
      if (event.getLeft().getItem() instanceof VaultCrystalItem && event.getRight().getItem() instanceof VaultCatalystInfusedItem) {
         ItemStack output = event.getLeft().copy();
         CrystalData data = CrystalData.read(output);
         List<VaultModifierStack> modifierStackList = VaultCatalystInfusedItem.getModifiers(event.getRight())
            .stream()
            .map(VaultModifierRegistry::getOpt)
            .flatMap(Optional::stream)
            .map(VaultModifierStack::of)
            .toList();
         float instability = data.getInstability();
         if (data.getModifiers().addByCrafting(data, modifierStackList, CrystalData.Simulate.FALSE)) {
            Random random = new Random();
            if (random.nextFloat() < instability) {
               if (random.nextFloat() < ModConfigs.VAULT_CRYSTAL.MODIFIER_STABILITY.exhaustProbability) {
                  VaultCrystalItem.scheduleTask(VaultCrystalItem.ExhaustTask.INSTANCE, output);
               } else {
                  VaultCrystalItem.scheduleTask(new VaultCrystalItem.AddModifiersTask(VaultMod.id("catalyst_curse")), output);
               }
            }

            data.write(output);
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
         VaultCrystalItem.scheduleTask(new VaultCrystalItem.AddModifiersTask(VaultMod.id("wither_skull_curse")), output);
         event.setOutput(output);
         event.setCost(1);
         event.setMaterialCost(1);
      }
   }

   @SubscribeEvent
   public static void onApplyChaosCatalyst(AnvilUpdateEvent event) {
      if (event.getLeft().getItem() instanceof VaultCrystalItem && event.getRight().getItem() == ModItems.VAULT_CATALYST_CHAOS) {
         ItemStack output = event.getLeft().copy();
         VaultCrystalItem.scheduleTask(new VaultCrystalItem.AddModifiersTask(VaultMod.id("vault_catalyst_chaos")), output);
         CrystalData data = CrystalData.read(output);
         data.setUnmodifiable(true);
         data.write(output);
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
         CrystalData data = CrystalData.read(output);
         if (item == ModItems.MOTE_CLARITY && !data.getModifiers().hasClarity()) {
            VaultCrystalItem.scheduleTask(VaultCrystalItem.AddClarityTask.INSTANCE, output);
            event.setOutput(output);
            event.setCost(ModConfigs.VAULT_CRYSTAL.MOTES.clarityLevelCost);
            event.setMaterialCost(1);
         } else if (item == ModItems.MOTE_PURITY && data.getModifiers().hasCurse()) {
            VaultCrystalItem.scheduleTask(VaultCrystalItem.RemoveRandomCurseTask.INSTANCE, output);
            event.setOutput(output);
            event.setCost(ModConfigs.VAULT_CRYSTAL.MOTES.purityLevelCost);
            event.setMaterialCost(1);
         } else if (item == ModItems.MOTE_SANCTITY && data.getModifiers().hasCurse()) {
            data.getModifiers().removeAllCurses();
            data.write(output);
            event.setOutput(output);
            event.setCost(ModConfigs.VAULT_CRYSTAL.MOTES.sanctityLevelCost);
            event.setMaterialCost(1);
         }
      }
   }

   @SubscribeEvent
   public static void onApplyBanishedSoul(AnvilUpdateEvent event) {
      if (event.getRight().getItem() == ModItems.BANISHED_SOUL) {
         Item input = event.getLeft().getItem();
         if (input == ModItems.VAULT_CRYSTAL) {
            ItemStack output = event.getLeft().copy();
            CrystalData data = CrystalData.read(output);
            if (data.getModifiers().hasCurse() || data.isUnmodifiable() || data.getLevel() <= 0) {
               return;
            }

            int newLevel = Math.max(0, data.getLevel() - event.getRight().getCount());
            int price = data.getLevel() - newLevel;
            data.setLevel(newLevel);
            data.write(output);
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
   public static void onRepairDeny(AnvilUpdateEvent event) {
      if (event.getLeft().getItem() instanceof LegacyMagnetItem && event.getRight().getItem() instanceof LegacyMagnetItem) {
         event.setCanceled(true);
      } else if (event.getLeft().getItem() instanceof PaxelItem && event.getRight().getItem() instanceof PaxelItem) {
         event.setCanceled(true);
      }

      if (event.getLeft().getItem() instanceof VaultGearItem && event.getRight().getItem() instanceof VaultGearItem && !event.getRight().is(ModItems.JEWEL)) {
         event.setOutput(ItemStack.EMPTY);
      }
   }

   @SubscribeEvent
   public static void onApplySoulFlame(AnvilUpdateEvent event) {
      if (event.getLeft().getItem() instanceof VaultCrystalItem && event.getRight().getItem() == ModItems.SOUL_FLAME) {
         ItemStack output = event.getLeft().copy();
         CrystalData data = CrystalData.read(output);
         if (data.getModifiers().isEmpty()) {
            VaultModifierRegistry.getOpt(VaultMod.id("afterlife")).ifPresent(vaultModifier -> {
               VaultModifierStack modifierStack = VaultModifierStack.of((VaultModifier<?>)vaultModifier);
               if (data.addModifierByCrafting(modifierStack, false, CrystalData.Simulate.TRUE)) {
                  data.addModifierByCrafting(modifierStack, false, CrystalData.Simulate.FALSE);
                  VaultCrystalItem.scheduleTask(new VaultCrystalItem.AddModifiersTask(VaultMod.id("soul_flame_curse")), output);
                  data.setUnmodifiable(true);
                  data.write(output);
                  event.setOutput(output);
                  event.setMaterialCost(1);
                  event.setCost(10);
               }
            });
         }
      }
   }

   @SubscribeEvent
   public static void onApplyPhoenix(AnvilUpdateEvent event) {
      if (event.getLeft().getItem() instanceof VaultCrystalItem && event.getRight().getItem() == ModItems.PHOENIX_FEATHER) {
         ItemStack output = event.getLeft().copy();
         CrystalData data = CrystalData.read(output);
         VaultModifierRegistry.getOpt(VaultMod.id("phoenix")).ifPresent(modifier -> {
            VaultModifierStack modifierStack = VaultModifierStack.of((VaultModifier<?>)modifier);
            boolean hasPhoenix = false;

            for (VaultModifierStack stack : data.getModifiers()) {
               if (stack.getModifier() == modifier) {
                  hasPhoenix = true;
                  break;
               }
            }

            if (hasPhoenix) {
               data.setUnmodifiable(false);
            }

            if (data.addModifierByCrafting(modifierStack, false, CrystalData.Simulate.TRUE)) {
               data.addModifierByCrafting(modifierStack, false, CrystalData.Simulate.FALSE);
               data.setUnmodifiable(true);
               data.write(output);
               event.setOutput(output);
               event.setMaterialCost(1);
               event.setCost(10);
            } else if (hasPhoenix) {
               data.setUnmodifiable(true);
            }
         });
      }
   }

   @SubscribeEvent
   public static void onApplyLootersDream(AnvilUpdateEvent event) {
      if (event.getLeft().getItem() instanceof VaultCrystalItem && event.getRight().getItem() == ModItems.EYE_OF_AVARICE) {
         ItemStack output = event.getLeft().copy();
         CrystalData data = CrystalData.read(output);
         VaultModifierRegistry.getOpt(VaultMod.id("looters_dream")).ifPresent(modifier -> {
            VaultModifierStack modifierStack = VaultModifierStack.of((VaultModifier<?>)modifier);
            if (data.addModifierByCrafting(modifierStack, false, CrystalData.Simulate.TRUE)) {
               data.addModifierByCrafting(modifierStack, false, CrystalData.Simulate.FALSE);
               data.setUnmodifiable(true);
               data.write(output);
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
   public static void onRepairMagnetOrPaxel(AnvilUpdateEvent event) {
      ItemStack magnet = event.getLeft();
      ItemStack magnetite = event.getRight();
      if (magnetite.is(ModItems.REPAIR_CORE)) {
         ItemStack output = magnet.copy();
         int used;
         int max;
         if (magnet.getItem() instanceof LegacyMagnetItem) {
            used = LegacyMagnetItem.getUsedRepairSlots(magnet);
            max = LegacyMagnetItem.getMaxRepairSlots(magnet);
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
            LegacyMagnetItem.useRepairSlot(output);
            output.setDamageValue(0);
            event.setOutput(output);
         } else {
            event.setCanceled(true);
         }
      }
   }

   @SubscribeEvent
   public static void onApplyJewel(AnvilUpdateEvent event) {
      if (event.getLeft().getItem() == ModItems.TOOL) {
         if (event.getRight().getItem() == ModItems.JEWEL) {
            ItemStack result = event.getLeft().copy();
            if (ToolItem.applyJewel(result, event.getRight())) {
               event.setMaterialCost(1);
               event.setCost(1);
               event.setOutput(result);
            }
         }
      }
   }

   @SubscribeEvent
   public static void onApplyInscription(AnvilUpdateEvent event) {
      if (event.getLeft().getItem() == ModItems.VAULT_CRYSTAL) {
         if (event.getRight().getItem() == ModItems.INSCRIPTION) {
            ItemStack output = event.getLeft().copy();
            CrystalData data = CrystalData.read(output);
            if (InscriptionData.from(event.getRight()).apply(output, data)) {
               event.setMaterialCost(1);
               event.setCost(1);
               event.setOutput(output);
            }
         }
      }
   }

   @SubscribeEvent
   public static void onApplyAugment(AnvilUpdateEvent event) {
      if (event.getLeft().getItem() == ModItems.VAULT_CRYSTAL) {
         if (event.getRight().getItem() == ModItems.AUGMENT) {
            AugmentItem.getTheme(event.getRight()).ifPresent(key -> {
               ItemStack output = event.getLeft().copy();
               CrystalData data = CrystalData.read(output);
               data.setTheme(new ValueCrystalTheme(key.getId()));
               data.write(output);
               event.setMaterialCost(1);
               event.setCost(1);
               event.setOutput(output);
            });
         }
      }
   }
}
