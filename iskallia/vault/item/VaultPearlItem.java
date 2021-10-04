package iskallia.vault.item;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.entity.VaultPearlEntity;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.EnderPearlItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.Properties;
import net.minecraft.stats.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvents;
import net.minecraft.world.World;

public class VaultPearlItem extends EnderPearlItem {
   public VaultPearlItem(ResourceLocation id) {
      super(new Properties().func_200917_a(1).func_200916_a(ModItems.VAULT_MOD_GROUP).func_200918_c(0));
      this.setRegistryName(id);
   }

   public boolean showDurabilityBar(ItemStack stack) {
      return stack.func_77952_i() > 0;
   }

   public void setDamage(ItemStack stack, int damage) {
      int currentDamage = this.getDamage(stack);
      if (damage > currentDamage) {
         super.setDamage(stack, damage);
      }
   }

   public double getDurabilityForDisplay(ItemStack stack) {
      return (double)stack.func_77952_i() / this.getMaxDamage(stack);
   }

   public int getMaxDamage(ItemStack stack) {
      return ModConfigs.VAULT_UTILITIES != null ? ModConfigs.VAULT_UTILITIES.getVaultPearlMaxUses() : 0;
   }

   public boolean func_77645_m() {
      return true;
   }

   public ActionResult<ItemStack> func_77659_a(World world, PlayerEntity player, Hand hand) {
      if (hand != Hand.MAIN_HAND) {
         return ActionResult.func_226250_c_(player.func_184586_b(hand));
      } else {
         ItemStack stack = player.func_184586_b(hand);
         world.func_184148_a(
            null,
            player.func_226277_ct_(),
            player.func_226278_cu_(),
            player.func_226281_cx_(),
            SoundEvents.field_187595_bc,
            SoundCategory.NEUTRAL,
            0.5F,
            0.4F / (field_77697_d.nextFloat() * 0.4F + 0.8F)
         );
         player.func_184811_cZ().func_185145_a(this, 20);
         if (!world.field_72995_K) {
            VaultPearlEntity pearl = new VaultPearlEntity(world, player);
            pearl.func_213884_b(stack);
            pearl.func_234612_a_(player, player.field_70125_A, player.field_70177_z, 0.0F, 1.5F, 1.0F);
            world.func_217376_c(pearl);
            stack.func_222118_a(1, player, e -> e.func_213334_d(hand));
         }

         player.func_71029_a(Stats.field_75929_E.func_199076_b(this));
         return ActionResult.func_233538_a_(stack, world.func_201670_d());
      }
   }

   public boolean func_77616_k(ItemStack stack) {
      return false;
   }

   public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
      return false;
   }

   public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
      return false;
   }
}
