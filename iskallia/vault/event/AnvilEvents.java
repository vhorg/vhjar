package iskallia.vault.event;

import iskallia.vault.config.entry.EnchantedBookEntry;
import iskallia.vault.init.ModBlocks;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.ItemVaultCrystalSeal;
import iskallia.vault.item.ItemVaultRaffleSeal;
import iskallia.vault.item.VaultCatalystItem;
import iskallia.vault.item.VaultMagnetItem;
import iskallia.vault.item.crystal.CrystalData;
import iskallia.vault.item.crystal.VaultCrystalItem;
import iskallia.vault.item.paxel.VaultPaxelItem;
import iskallia.vault.item.paxel.enhancement.PaxelEnhancements;
import iskallia.vault.util.OverlevelEnchantHelper;
import iskallia.vault.world.vault.logic.objective.VaultObjective;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.item.ShieldItem;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public class AnvilEvents {
   @SubscribeEvent
   public static void onAnvilUpdate(AnvilUpdateEvent event) {
      ItemStack equipment = event.getLeft();
      ItemStack enchantedBook = event.getRight();
      if (equipment.func_77973_b() != Items.field_151134_bR) {
         if (enchantedBook.func_77973_b() == Items.field_151134_bR) {
            ItemStack upgradedEquipment = equipment.func_77946_l();
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

               EnchantmentHelper.func_82782_a(enchantmentsToApply, upgradedEquipment);
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
   public static void onApplyPaxelCharm(AnvilUpdateEvent event) {
      ItemStack paxelStack = event.getLeft();
      ItemStack charmStack = event.getRight();
      if (paxelStack.func_77973_b() == ModItems.VAULT_PAXEL) {
         if (charmStack.func_77973_b() == ModItems.PAXEL_CHARM) {
            if (PaxelEnhancements.getEnhancement(paxelStack) == null) {
               ItemStack enhancedStack = paxelStack.func_77946_l();
               PaxelEnhancements.markShouldEnhance(enhancedStack);
               event.setCost(5);
               event.setOutput(enhancedStack);
            }
         }
      }
   }

   @SubscribeEvent
   public static void onApplySeal(AnvilUpdateEvent event) {
      if (event.getLeft().func_77973_b() instanceof VaultCrystalItem && event.getRight().func_77973_b() instanceof ItemVaultCrystalSeal) {
         ItemStack output = event.getLeft().func_77946_l();
         CrystalData data = VaultCrystalItem.getData(output);
         if (!data.getModifiers().isEmpty() || data.getSelectedObjective() != null) {
            return;
         }

         if (event.getRight().func_77973_b() == ModItems.CRYSTAL_SEAL_ANCIENTS && data.getType() == CrystalData.Type.COOP) {
            return;
         }

         ResourceLocation objectiveKey = ((ItemVaultCrystalSeal)event.getRight().func_77973_b()).getObjectiveId();
         VaultObjective objective = VaultObjective.getObjective(objectiveKey);
         if (objective != null) {
            data.setSelectedObjective(objectiveKey);
            VaultCrystalItem.setRandomSeed(output);
            event.setOutput(output);
            event.setMaterialCost(1);
            event.setCost(8);
         }
      }
   }

   @SubscribeEvent
   public static void onApplyRaffleSeal(AnvilUpdateEvent event) {
      if (event.getLeft().func_77973_b() instanceof VaultCrystalItem && event.getRight().func_77973_b() instanceof ItemVaultRaffleSeal) {
         ItemStack output = event.getLeft().func_77946_l();
         CrystalData data = VaultCrystalItem.getData(output);
         if (!data.getModifiers().isEmpty() || data.getSelectedObjective() != null) {
            return;
         }

         String playerName = ItemVaultRaffleSeal.getPlayerName(event.getRight());
         if (playerName.isEmpty()) {
            return;
         }

         data.setPlayerBossName(playerName);
         event.setOutput(output);
         event.setMaterialCost(1);
         event.setCost(8);
      }
   }

   @SubscribeEvent
   public static void onApplyCatalyst(AnvilUpdateEvent event) {
      if (event.getLeft().func_77973_b() instanceof VaultCrystalItem && event.getRight().func_77973_b() instanceof VaultCatalystItem) {
         ItemStack output = event.getLeft().func_77946_l();
         CrystalData data = VaultCrystalItem.getData(output);
         if (!data.canCraftCatalysts()) {
            return;
         }

         List<String> modifiers = VaultCatalystItem.getCrystalCombinationModifiers(event.getRight(), event.getLeft());
         if (modifiers == null || modifiers.isEmpty()) {
            return;
         }

         modifiers.forEach(modifier -> data.addCatalystModifier(modifier, true, CrystalData.Modifier.Operation.ADD));
         event.setOutput(output);
         event.setCost(modifiers.size() * 8);
         event.setMaterialCost(1);
      }
   }

   @SubscribeEvent
   public static void onRepairDeny(AnvilUpdateEvent event) {
      if (event.getLeft().func_77973_b() instanceof VaultPaxelItem && event.getRight().func_77973_b() instanceof VaultPaxelItem) {
         event.setCanceled(true);
      }

      if (event.getLeft().func_77973_b() instanceof VaultMagnetItem && event.getRight().func_77973_b() instanceof VaultMagnetItem) {
         event.setCanceled(true);
      }
   }

   @SubscribeEvent
   public static void onApplySoulFlame(AnvilUpdateEvent event) {
      if (event.getLeft().func_77973_b() instanceof VaultCrystalItem && event.getRight().func_77973_b() == ModItems.SOUL_FLAME) {
         ItemStack output = event.getLeft().func_77946_l();
         CrystalData data = VaultCrystalItem.getData(output);
         if (!data.getModifiers().isEmpty()) {
            return;
         }

         if (!data.canAddModifier("Afterlife", CrystalData.Modifier.Operation.ADD)) {
            return;
         }

         if (data.addCatalystModifier("Afterlife", false, CrystalData.Modifier.Operation.ADD)) {
            event.setOutput(output);
            event.setMaterialCost(1);
            event.setCost(10);
         }
      }
   }

   @SubscribeEvent
   public static void onApplyPog(AnvilUpdateEvent event) {
      if (event.getRight().func_77973_b() == ModItems.OMEGA_POG) {
         ResourceLocation name = event.getLeft().func_77973_b().getRegistryName();
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
      if (out.func_77973_b() instanceof ShieldItem) {
         if (EnchantmentHelper.func_77506_a(Enchantments.field_185296_A, out) > 0) {
            event.setCanceled(true);
         }

         if (EnchantmentHelper.func_77506_a(Enchantments.field_92091_k, out) > 0) {
            event.setCanceled(true);
         }
      }
   }

   @SubscribeEvent
   public static void onRepairMagnet(AnvilUpdateEvent event) {
      if (event.getLeft().func_77973_b() instanceof VaultMagnetItem) {
         if (event.getRight().func_77973_b() == ModItems.MAGNETITE) {
            if (event.getLeft().func_77952_i() != 0 && event.getLeft().func_196082_o().func_74762_e("TotalRepairs") < 30) {
               ItemStack magnet = event.getLeft();
               ItemStack magnetite = event.getRight();
               ItemStack output = magnet.func_77946_l();
               CompoundNBT nbt = output.func_196082_o();
               if (!nbt.func_74764_b("TotalRepairs")) {
                  nbt.func_74768_a("TotalRepairs", 0);
                  output.func_77982_d(nbt);
               }

               int damage = magnet.func_77952_i();
               int repairAmount = (int)Math.ceil(magnet.func_77958_k() * 0.1);
               int newDamage = Math.max(0, damage - magnetite.func_190916_E() * repairAmount);
               int materialCost = (int)Math.ceil((double)(damage - newDamage) / repairAmount);
               event.setMaterialCost(materialCost);
               event.setCost(materialCost);
               nbt.func_74768_a("TotalRepairs", (int)Math.ceil(materialCost + nbt.func_74762_e("TotalRepairs")));
               output.func_77982_d(nbt);
               output.func_196085_b(newDamage);
               event.setOutput(output);
            } else {
               event.setCanceled(true);
            }
         }
      }
   }
}
