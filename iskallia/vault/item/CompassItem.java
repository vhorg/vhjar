package iskallia.vault.item;

import iskallia.vault.VaultMod;
import iskallia.vault.core.vault.Vault;
import iskallia.vault.core.vault.VaultUtils;
import iskallia.vault.core.vault.player.Listener;
import iskallia.vault.core.vault.player.Listeners;
import iskallia.vault.init.ModItems;
import iskallia.vault.world.data.ServerVaults;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ClipContext.Fluid;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult.Type;

public class CompassItem extends BasicItem {
   private static final int TARGET_SET_DURATION = 40;

   public CompassItem() {
      super(VaultMod.id("vault_compass"), new Properties().tab(ModItems.VAULT_MOD_GROUP).stacksTo(1));
   }

   public UseAnim getUseAnimation(ItemStack stack) {
      return UseAnim.BOW;
   }

   public int getUseDuration(ItemStack stack) {
      return 40;
   }

   public InteractionResult useOn(UseOnContext context) {
      if (VaultUtils.isVaultLevel(context.getLevel()) && context.getPlayer() != null && context.getPlayer().isShiftKeyDown()) {
         context.getPlayer().startUsingItem(context.getHand());
         return super.useOn(context);
      } else {
         return InteractionResult.PASS;
      }
   }

   public void onUsingTick(ItemStack stack, LivingEntity player, int count) {
      if (player.getLevel().getGameTime() % 5L == 0L) {
         player.playSound(SoundEvents.NOTE_BLOCK_BELL, 1.0F, player.getTicksUsingItem() / 40.0F);
      }

      super.onUsingTick(stack, player, count);
   }

   public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity livingEntity) {
      if (livingEntity instanceof Player player) {
         if (level.isClientSide()) {
            return stack;
         } else {
            BlockHitResult blockHitResult = getPlayerPOVHitResult(level, player, Fluid.ANY);
            if (blockHitResult.getType() == Type.BLOCK) {
               this.setTarget(player, level, blockHitResult.getBlockPos());
            }

            player.getCooldowns().addCooldown(stack.getItem(), 10);
            player.displayClientMessage(new TextComponent("New compass target set"), true);
            return super.finishUsingItem(stack, level, livingEntity);
         }
      } else {
         return super.finishUsingItem(stack, level, livingEntity);
      }
   }

   private void setTarget(Player player, Level level, BlockPos pos) {
      ServerVaults.get(level).ifPresent(vault -> {
         Listeners listeners = vault.get(Vault.LISTENERS);
         if (listeners.contains(player.getUUID())) {
            listeners.get(player.getUUID()).set(Listener.COMPASS_TARGET, pos);
         }
      });
   }
}
