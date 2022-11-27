package iskallia.vault.item;

import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import iskallia.vault.item.entity.VaultPearlEntity;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.EnderpearlItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;

public class VaultPearlItem extends EnderpearlItem {
   public VaultPearlItem(ResourceLocation id) {
      super(new Properties().stacksTo(1).tab(ModItems.VAULT_MOD_GROUP).durability(0));
      this.setRegistryName(id);
   }

   public boolean isBarVisible(ItemStack pStack) {
      return pStack.getDamageValue() > 0;
   }

   public void setDamage(ItemStack stack, int damage) {
      int currentDamage = this.getDamage(stack);
      if (damage > currentDamage) {
         super.setDamage(stack, damage);
      }
   }

   public int getBarWidth(ItemStack pStack) {
      return (int)((double)pStack.getDamageValue() / this.getMaxDamage(pStack));
   }

   public int getMaxDamage(ItemStack stack) {
      return ModConfigs.VAULT_UTILITIES != null ? ModConfigs.VAULT_UTILITIES.getVaultPearlMaxUses() : 0;
   }

   public boolean canBeDepleted() {
      return true;
   }

   public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
      if (hand != InteractionHand.MAIN_HAND) {
         return InteractionResultHolder.pass(player.getItemInHand(hand));
      } else {
         ItemStack stack = player.getItemInHand(hand);
         world.playSound(
            null,
            player.getX(),
            player.getY(),
            player.getZ(),
            SoundEvents.ENDER_PEARL_THROW,
            SoundSource.NEUTRAL,
            0.5F,
            0.4F / (world.random.nextFloat() * 0.4F + 0.8F)
         );
         player.getCooldowns().addCooldown(this, 20);
         if (!world.isClientSide) {
            VaultPearlEntity pearl = new VaultPearlEntity(world, player);
            pearl.setItem(stack);
            pearl.shootFromRotation(player, player.getXRot(), player.getYRot(), 0.0F, 1.5F, 1.0F);
            world.addFreshEntity(pearl);
            stack.hurtAndBreak(1, player, e -> e.broadcastBreakEvent(hand));
         }

         player.awardStat(Stats.ITEM_USED.get(this));
         return InteractionResultHolder.sidedSuccess(stack, world.isClientSide());
      }
   }

   public boolean isEnchantable(ItemStack stack) {
      return false;
   }

   public boolean canApplyAtEnchantingTable(ItemStack stack, Enchantment enchantment) {
      return false;
   }

   public boolean isBookEnchantable(ItemStack stack, ItemStack book) {
      return false;
   }
}
