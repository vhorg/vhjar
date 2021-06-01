package iskallia.vault.event;

import iskallia.vault.config.entry.EnchantedBookEntry;
import iskallia.vault.init.ModAttributes;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.CrystalData;
import iskallia.vault.item.ItemVaultCrystal;
import iskallia.vault.item.gear.VaultArmorItem;
import iskallia.vault.item.gear.VaultGear;
import iskallia.vault.util.OverlevelEnchantHelper;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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
   public static void onUnlockCrystal(AnvilUpdateEvent event) {
      if (event.getLeft().func_77973_b() instanceof ItemVaultCrystal && event.getRight().func_77973_b() == ModItems.VOID_CORE) {
         ItemStack output = event.getLeft().func_77946_l();
         if (ItemVaultCrystal.getData(output).addModifier("Locked", CrystalData.Modifier.Operation.REMOVE, 1.0F)) {
            event.setOutput(output);
            event.setCost(1);
         }
      }
   }

   @SubscribeEvent
   public static void onApplyEtching(AnvilUpdateEvent event) {
      if (event.getLeft().func_77973_b() instanceof VaultArmorItem && event.getRight().func_77973_b() == ModItems.ETCHING) {
         ItemStack output = event.getLeft().func_77946_l();
         VaultGear.Set set = ModAttributes.GEAR_SET.getOrDefault(event.getRight(), VaultGear.Set.NONE).getValue(event.getRight());
         ModAttributes.GEAR_SET.create(output, set);
         event.setOutput(output);
         event.setCost(1);
      }
   }

   @SubscribeEvent
   public static void onApplyPog(AnvilUpdateEvent event) {
      if (event.getRight().func_77973_b() == ModItems.OMEGA_POG) {
         ResourceLocation name = event.getLeft().func_77973_b().getRegistryName();
         if (event.getLeft().func_77973_b() instanceof VaultGear) {
            ItemStack output = event.getLeft().func_77946_l();
            int maxRepairs = ModAttributes.MAX_REPAIRS.getOrDefault(output, -1).getValue(output);
            int curRepairs = ModAttributes.CURRENT_REPAIRS.getOrDefault(output, 0).getValue(output);
            if (maxRepairs != -1 && curRepairs >= maxRepairs) {
               return;
            }

            ModAttributes.CURRENT_REPAIRS.create(output, curRepairs + 1);
            output.func_196085_b(0);
            event.setOutput(output);
            event.setMaterialCost(1);
            event.setCost(1);
         } else if (name.func_110624_b().equals("the_vault") && name.func_110623_a().startsWith("artifact")) {
            event.setOutput(new ItemStack(ModItems.UNIDENTIFIED_ARTIFACT));
            event.setMaterialCost(1);
            event.setCost(1);
         }
      }
   }
}
