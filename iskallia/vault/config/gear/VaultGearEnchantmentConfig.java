package iskallia.vault.config.gear;

import com.google.gson.annotations.Expose;
import iskallia.vault.config.Config;
import iskallia.vault.util.EnchantmentCost;
import iskallia.vault.util.EnchantmentEntry;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.registries.ForgeRegistries;

public class VaultGearEnchantmentConfig extends Config {
   @Expose
   private Map<Enchantment, EnchantmentCost> costs = new HashMap<>();

   @Override
   public String getName() {
      return "gear_enchantment";
   }

   public EnchantmentCost getCost(EnchantmentEntry entry) {
      return entry == null ? EnchantmentCost.EMPTY : this.costs.getOrDefault(entry.getEnchantment(), EnchantmentCost.EMPTY);
   }

   @Override
   protected void reset() {
      this.costs = new LinkedHashMap<>();

      for (Enchantment enchantment : ForgeRegistries.ENCHANTMENTS) {
         EnchantmentCost cost = new EnchantmentCost(Arrays.asList(new ItemStack(Blocks.STONE), new ItemStack(Items.STICK)), 1);
         this.costs.put(enchantment, cost);
      }
   }
}
