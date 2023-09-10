package iskallia.vault.item;

import iskallia.vault.client.gui.overlay.VaultBarOverlay;
import iskallia.vault.util.MathUtilities;
import iskallia.vault.world.data.PlayerVaultStatsData;
import java.util.List;
import java.util.function.Supplier;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.FoodProperties.Builder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public abstract class VaultXPFoodItem extends Item {
   public static FoodProperties FOOD = new Builder().saturationMod(0.0F).nutrition(0).fast().alwaysEat().build();
   private final int levelLimit;

   public VaultXPFoodItem(ResourceLocation id, Properties properties) {
      this(id, properties, -1);
   }

   public VaultXPFoodItem(ResourceLocation id, Properties properties, int levelLimit) {
      super(properties.food(FOOD));
      this.setRegistryName(id);
      this.levelLimit = levelLimit;
   }

   public UseAnim getUseAnimation(ItemStack stack) {
      return super.getUseAnimation(stack);
   }

   public InteractionResultHolder<ItemStack> use(Level world, Player player, InteractionHand hand) {
      if (this.levelLimit > 0) {
         int vaultLevel;
         if (player instanceof ServerPlayer) {
            vaultLevel = PlayerVaultStatsData.get(((ServerPlayer)player).getLevel()).getVaultStats(player).getVaultLevel();
         } else {
            vaultLevel = this.getVaultLevel();
         }

         if (vaultLevel >= this.levelLimit) {
            return InteractionResultHolder.pass(player.getItemInHand(hand));
         }
      }

      return super.use(world, player, hand);
   }

   @OnlyIn(Dist.CLIENT)
   private int getVaultLevel() {
      return VaultBarOverlay.vaultLevel;
   }

   public ItemStack finishUsingItem(ItemStack stack, Level world, LivingEntity entity) {
      if (!world.isClientSide && entity instanceof ServerPlayer player) {
         this.grantExp(player, entity.isShiftKeyDown() ? stack.getCount() : 1);
      }

      if (stack.getItem().isEdible() && entity.isShiftKeyDown() && !(entity instanceof Player player && player.getAbilities().instabuild)) {
         stack.setCount(1);
      }

      return super.finishUsingItem(stack, world, entity);
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
      if (this.levelLimit > 0) {
         tooltip.add(TextComponent.EMPTY);
         tooltip.add(
            new TextComponent("Can't be consumed after Level: ")
               .withStyle(ChatFormatting.GRAY)
               .append(new TextComponent(String.valueOf(this.levelLimit)).withStyle(ChatFormatting.AQUA))
         );
      }
   }

   public abstract void grantExp(ServerPlayer var1, int var2);

   public static class Flat extends VaultXPFoodItem {
      private final Supplier<Integer> min;
      private final Supplier<Integer> max;

      public Flat(ResourceLocation id, Supplier<Integer> min, Supplier<Integer> max, Properties properties) {
         this(id, min, max, properties, -1);
      }

      public Flat(ResourceLocation id, Supplier<Integer> min, Supplier<Integer> max, Properties properties, int levelRequirement) {
         super(id, properties, levelRequirement);
         this.min = min;
         this.max = max;
      }

      @Override
      public void grantExp(ServerPlayer sPlayer, int count) {
         PlayerVaultStatsData statsData = PlayerVaultStatsData.get(sPlayer.getLevel());
         int exp = 0;

         for (int i = 0; i < count; i++) {
            exp += MathUtilities.getRandomInt(this.min.get(), this.max.get());
         }

         statsData.addVaultExp(sPlayer, exp);
      }
   }
}
