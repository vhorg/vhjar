package iskallia.vault.util;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.ListNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class OverlevelEnchantHelper {
   public static int getOverlevels(ItemStack enchantedBookStack) {
      Map<Enchantment, Integer> enchantments = EnchantmentHelper.func_82781_a(enchantedBookStack);

      for (Enchantment enchantment : enchantments.keySet()) {
         int level = enchantments.get(enchantment);
         if (level > enchantment.func_77325_b()) {
            return level - enchantment.func_77325_b();
         }
      }

      return -1;
   }

   public static Map<Enchantment, Integer> getEnchantments(ItemStack stack) {
      CompoundNBT nbt = Optional.ofNullable(stack.func_77978_p()).orElseGet(CompoundNBT::new);
      ListNBT enchantmentsNBT = nbt.func_150295_c(stack.func_77973_b() == Items.field_151134_bR ? "StoredEnchantments" : "Enchantments", 10);
      HashMap<Enchantment, Integer> enchantments = new HashMap<>();

      for (int i = 0; i < enchantmentsNBT.size(); i++) {
         CompoundNBT enchantmentNBT = enchantmentsNBT.func_150305_b(i);
         ResourceLocation id = new ResourceLocation(enchantmentNBT.func_74779_i("id"));
         int level = enchantmentNBT.func_74762_e("lvl");
         Enchantment enchantment = (Enchantment)ForgeRegistries.ENCHANTMENTS.getValue(id);
         if (enchantment != null) {
            enchantments.put(enchantment, level);
         }
      }

      return enchantments;
   }

   public static ItemStack increaseFortuneBy(ItemStack itemStack, int amount) {
      Map<Enchantment, Integer> enchantments = EnchantmentHelper.func_82781_a(itemStack);
      int level = enchantments.getOrDefault(Enchantments.field_185308_t, 0);
      enchantments.put(Enchantments.field_185308_t, level + amount);
      EnchantmentHelper.func_82782_a(enchantments, itemStack);
      return itemStack;
   }
}
