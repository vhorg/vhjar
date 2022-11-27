package iskallia.vault.item;

import iskallia.vault.init.ModItems;
import javax.annotation.Nonnull;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;

public abstract class UsableItem extends Item {
   public UsableItem(ResourceLocation id) {
      super(new Properties().tab(ModItems.VAULT_MOD_GROUP).stacksTo(64));
      this.setRegistryName(id);
   }

   protected abstract SoundEvent getSuccessSound();

   protected abstract void doUse(ServerLevel var1, ServerPlayer var2);

   @Nonnull
   public InteractionResultHolder<ItemStack> use(Level world, Player player, @Nonnull InteractionHand hand) {
      ItemStack heldItemStack = player.getItemInHand(hand);
      world.playSound(
         null, player.getX(), player.getY(), player.getZ(), this.getSuccessSound(), SoundSource.PLAYERS, 0.5F, 0.4F / (world.random.nextFloat() * 0.4F + 0.8F)
      );
      if (!world.isClientSide) {
         this.doUse((ServerLevel)world, (ServerPlayer)player);
      }

      player.awardStat(Stats.ITEM_USED.get(this));
      if (!player.getAbilities().instabuild) {
         heldItemStack.shrink(1);
      }

      return InteractionResultHolder.sidedSuccess(heldItemStack, world.isClientSide());
   }

   @Nonnull
   public Component getName(@Nonnull ItemStack stack) {
      return ((MutableComponent)super.getName(stack)).setStyle(Style.EMPTY.withColor(this.getNameColor()));
   }

   protected TextColor getNameColor() {
      return null;
   }
}
