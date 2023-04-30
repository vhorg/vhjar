package iskallia.vault.util;

import io.netty.buffer.ByteBuf;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.item.crystal.data.serializable.IByteSerializable;
import java.util.Map;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class EnchantmentEntry implements IByteSerializable {
   private Enchantment enchantment;
   private int level;

   public EnchantmentEntry(Enchantment enchantment, int level) {
      this.enchantment = enchantment;
      this.level = level;
   }

   public EnchantmentEntry(ByteBuf buffer) {
      this.readBytes(buffer);
   }

   public Enchantment getEnchantment() {
      return this.enchantment;
   }

   public int getLevel() {
      return this.level;
   }

   public boolean isValid() {
      return this.enchantment != null && this.level > 0 && this.level <= this.enchantment.getMaxLevel();
   }

   public void apply(ItemStack stack) {
      if (this.enchantment.canEnchant(stack)) {
         Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
         enchantments.keySet().removeIf(other -> !this.enchantment.isCompatibleWith(other));
         enchantments.put(this.enchantment, this.level);
         EnchantmentHelper.setEnchantments(enchantments, stack);
      }
   }

   public EnchantmentCost getCost() {
      return ModConfigs.VAULT_GEAR_ENCHANTMENT_CONFIG.getCost(this);
   }

   @Override
   public void writeBytes(ByteBuf buffer) {
      Adapters.ENCHANTMENT.writeBytes((IForgeRegistryEntry)this.enchantment, buffer);
      Adapters.INT_SEGMENTED_7.writeBytes(Integer.valueOf(this.level), buffer);
   }

   @Override
   public void readBytes(ByteBuf buffer) {
      this.enchantment = (Enchantment)Adapters.ENCHANTMENT.readBytes(buffer).orElse(null);
      this.level = Adapters.INT_SEGMENTED_7.readBytes(buffer).orElseThrow();
   }
}
