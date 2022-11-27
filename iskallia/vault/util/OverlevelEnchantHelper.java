package iskallia.vault.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.registries.ForgeRegistries;

public class OverlevelEnchantHelper {
   public static int getOverlevels(ItemStack enchantedBookStack) {
      Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(enchantedBookStack);

      for (Enchantment enchantment : enchantments.keySet()) {
         int level = enchantments.get(enchantment);
         if (level > enchantment.getMaxLevel()) {
            return level - enchantment.getMaxLevel();
         }
      }

      return -1;
   }

   public static Map<Enchantment, Integer> getEnchantments(ItemStack stack) {
      CompoundTag nbt = Optional.ofNullable(stack.getTag()).orElseGet(CompoundTag::new);
      ListTag enchantmentsNBT = nbt.getList(stack.getItem() == Items.ENCHANTED_BOOK ? "StoredEnchantments" : "Enchantments", 10);
      HashMap<Enchantment, Integer> enchantments = new HashMap<>();

      for (int i = 0; i < enchantmentsNBT.size(); i++) {
         CompoundTag enchantmentNBT = enchantmentsNBT.getCompound(i);
         ResourceLocation id = new ResourceLocation(enchantmentNBT.getString("id"));
         int level = enchantmentNBT.getInt("lvl");
         Enchantment enchantment = (Enchantment)ForgeRegistries.ENCHANTMENTS.getValue(id);
         if (enchantment != null) {
            enchantments.put(enchantment, level);
         }
      }

      return enchantments;
   }

   public static ItemStack increaseFortuneBy(ItemStack itemStack, int amount) {
      Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(itemStack);
      int level = enchantments.getOrDefault(Enchantments.BLOCK_FORTUNE, 0);
      enchantments.put(Enchantments.BLOCK_FORTUNE, level + amount);
      EnchantmentHelper.setEnchantments(enchantments, itemStack);
      return itemStack;
   }

   public static ItemStack increaseUnbreakingBy(ItemStack itemStack, int amount) {
      Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(itemStack);
      int level = enchantments.getOrDefault(Enchantments.UNBREAKING, 0);
      enchantments.put(Enchantments.UNBREAKING, level + amount);
      EnchantmentHelper.setEnchantments(enchantments, itemStack);
      return itemStack;
   }
}
