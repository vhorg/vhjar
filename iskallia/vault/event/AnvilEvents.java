package iskallia.vault.event;

import iskallia.vault.config.entry.EnchantedBookEntry;
import iskallia.vault.gear.item.VaultGearItem;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.LegacyMagnetItem;
import iskallia.vault.item.tool.PaxelItem;
import iskallia.vault.util.EnchantmentUtil;
import iskallia.vault.util.OverlevelEnchantHelper;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.ShieldItem;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public class AnvilEvents {
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
}
