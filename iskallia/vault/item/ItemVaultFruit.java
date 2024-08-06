package iskallia.vault.item;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.gear.attribute.type.VaultGearAttributeTypeMerger;
import iskallia.vault.init.ModGearAttributes;
import iskallia.vault.init.ModItems;
import iskallia.vault.snapshot.AttributeSnapshot;
import iskallia.vault.snapshot.AttributeSnapshotHelper;
import iskallia.vault.world.data.ServerVaults;
import java.util.List;
import java.util.Random;
import java.util.UUID;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.attributes.AttributeModifier.Operation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.FoodProperties.Builder;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@EventBusSubscriber(
   bus = Bus.FORGE
)
public class ItemVaultFruit extends Item {
   private static final Random rand = new Random();
   public static final UUID MAX_HEALTH_REDUCTION_ATTRIBUTE_MODIFIER_UUID = UUID.fromString("94574f3d-49dc-4fc5-8ca5-74707eb1c34d");
   public static FoodProperties VAULT_FRUIT_FOOD = new Builder().saturationMod(0.0F).nutrition(0).fast().alwaysEat().build();
   protected int extraVaultTicks;

   public ItemVaultFruit(ResourceLocation id, int extraVaultTicks) {
      super(new Properties().tab(ModItems.VAULT_MOD_GROUP).food(VAULT_FRUIT_FOOD).stacksTo(64));
      this.setRegistryName(id);
      this.extraVaultTicks = extraVaultTicks;
   }

   public boolean onEaten(Level level, Player player) {
      AttributeSnapshot snapshot = AttributeSnapshotHelper.getInstance().getSnapshot(player);
      float effectiveness = snapshot.getAttributeValue(ModGearAttributes.FRUIT_EFFECTIVENESS, VaultGearAttributeTypeMerger.floatSum());
      int time = (int)(this.extraVaultTicks * (1.0F + effectiveness));
      CommonEvents.FRUIT_EATEN.invoke(this, player, time);
      return true;
   }

   @ParametersAreNonnullByDefault
   @Nonnull
   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      ItemStack itemStack = player.getItemInHand(hand);
      if (!level.isClientSide() && ServerVaults.get(level).isEmpty()) {
         return InteractionResultHolder.fail(itemStack);
      } else if (!this.isPlayerMaxHealthGreaterThan(player, 2)) {
         if (player.level.isClientSide()) {
            player.displayClientMessage(new TextComponent("Your max health is too low to eat this!").withStyle(ChatFormatting.RED), true);
         }

         return InteractionResultHolder.fail(itemStack);
      } else {
         return super.use(level, player, hand);
      }
   }

   @ParametersAreNonnullByDefault
   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack itemStack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag tooltipFlag) {
      int seconds = Mth.floor(this.extraVaultTicks / 20.0F);
      String timeText = String.format("%d seconds", seconds);
      if (seconds > 90) {
         int minutes = seconds / 60;
         timeText = String.format("%d minutes", minutes);
      }

      MutableComponent cmp = new TextComponent("Adds ")
         .withStyle(ChatFormatting.GRAY)
         .append(new TextComponent(timeText).withStyle(ChatFormatting.GREEN))
         .append(" to the Vault timer");
      tooltip.add(TextComponent.EMPTY);
      tooltip.add(new TextComponent("Removes").withStyle(ChatFormatting.GRAY).append(new TextComponent(" 10% max health").withStyle(ChatFormatting.RED)));
      tooltip.add(cmp);
      tooltip.add(TextComponent.EMPTY);
      tooltip.add(new TextComponent("Only edible inside a Vault").withStyle(ChatFormatting.RED));
      super.appendHoverText(itemStack, worldIn, tooltip, tooltipFlag);
   }

   @Nonnull
   public Component getName(@Nonnull ItemStack itemStack) {
      MutableComponent displayName = (MutableComponent)super.getName(itemStack);
      return displayName.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(16563456)));
   }

   protected boolean isPlayerMaxHealthGreaterThan(Player player, int threshold) {
      AttributeInstance attributeInstance = player.getAttribute(Attributes.MAX_HEALTH);
      return attributeInstance == null ? false : attributeInstance.getValue() > threshold;
   }

   protected void reducePlayerMaxHealth(ServerPlayer serverPlayer) {
      this.reducePlayerMaxHealth(serverPlayer, 0.1F, 2);
   }

   protected void reducePlayerMaxHealth(ServerPlayer serverPlayer, float percentageReduction, int minimumReduction) {
      AttributeInstance attributeInstance = serverPlayer.getAttribute(Attributes.MAX_HEALTH);
      if (attributeInstance != null) {
         AttributeModifier existingModifier = attributeInstance.getModifier(MAX_HEALTH_REDUCTION_ATTRIBUTE_MODIFIER_UUID);
         double reductionAmount = 0.0;
         if (existingModifier != null) {
            reductionAmount += existingModifier.getAmount();
            attributeInstance.removeModifier(MAX_HEALTH_REDUCTION_ATTRIBUTE_MODIFIER_UUID);
         }

         reductionAmount -= Math.max((double)minimumReduction, attributeInstance.getValue() * percentageReduction);
         attributeInstance.addPermanentModifier(
            new AttributeModifier(MAX_HEALTH_REDUCTION_ATTRIBUTE_MODIFIER_UUID, "VaultFruitMaxHealthReduction", reductionAmount, Operation.ADDITION)
         );
      }
   }

   @SubscribeEvent
   public static void on(PlayerTickEvent event) {
      if (event.side != LogicalSide.CLIENT && event.player.getLevel().getGameTime() % 10L == 0L && !ServerVaults.get(event.player.level).isPresent()) {
         synchronized (event.player) {
            AttributeInstance attributeInstance = event.player.getAttribute(Attributes.MAX_HEALTH);
            if (attributeInstance != null) {
               attributeInstance.removeModifier(MAX_HEALTH_REDUCTION_ATTRIBUTE_MODIFIER_UUID);
            }
         }
      }
   }

   public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entityLiving) {
      if (!level.isClientSide() && entityLiving instanceof ServerPlayer player) {
         if (!this.onEaten(level, player)) {
            return stack;
         }

         this.reducePlayerMaxHealth(player);
         this.successEaten(level, player);
         level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.CONDUIT_ACTIVATE, SoundSource.MASTER, 1.0F, 1.0F);
      }

      return entityLiving.eat(level, stack);
   }

   protected void successEaten(Level level, ServerPlayer sPlayer) {
   }

   public static class MysticPear extends ItemVaultFruit {
      public MysticPear(ResourceLocation id, int extraVaultTicks) {
         super(id, extraVaultTicks);
      }

      @Override
      protected void successEaten(Level level, ServerPlayer sPlayer) {
         sPlayer.addEffect(new MobEffectInstance(MobEffects.WITHER, 600));
      }

      @Override
      public void appendHoverText(ItemStack itemStack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag tooltipFlag) {
         tooltip.add(TextComponent.EMPTY);
         tooltip.add(new TextComponent("Inflicts Wither or Poison").withStyle(ChatFormatting.RED));
         super.appendHoverText(itemStack, worldIn, tooltip, tooltipFlag);
      }
   }
}
