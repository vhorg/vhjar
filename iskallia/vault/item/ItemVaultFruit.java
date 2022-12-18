package iskallia.vault.item;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.util.MathUtilities;
import iskallia.vault.world.data.ServerVaults;
import java.util.List;
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
import net.minecraft.world.item.CreativeModeTab;
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
   public static final UUID MAX_HEALTH_REDUCTION_ATTRIBUTE_MODIFIER_UUID = UUID.fromString("94574f3d-49dc-4fc5-8ca5-74707eb1c34d");
   public static FoodProperties VAULT_FRUIT_FOOD = new Builder().saturationMod(0.0F).nutrition(0).fast().alwaysEat().build();
   protected int extraVaultTicks;

   public ItemVaultFruit(CreativeModeTab group, ResourceLocation id, int extraVaultTicks) {
      super(new Properties().tab(group).food(VAULT_FRUIT_FOOD).stacksTo(64));
      this.setRegistryName(id);
      this.extraVaultTicks = extraVaultTicks;
   }

   public boolean onEaten(Level level, Player player) {
      CommonEvents.FRUIT_EATEN.invoke(this, player, this.extraVaultTicks);
      return true;
   }

   public int getExtraVaultTicks() {
      return this.extraVaultTicks;
   }

   @ParametersAreNonnullByDefault
   @Nonnull
   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      ItemStack itemStack = player.getItemInHand(hand);
      if (!ServerVaults.isVaultWorld(level)) {
         return InteractionResultHolder.fail(itemStack);
      } else if (!this.isPlayerMaxHealthGreaterThan(player, 2)) {
         if (player.level.isClientSide) {
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
      tooltip.add(new TextComponent(""));
      TextComponent textComponent = new TextComponent("[!] Only edible inside a Vault");
      textComponent.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(16711680)).withItalic(true));
      tooltip.add(textComponent);
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

         reductionAmount -= Math.max((double)minimumReduction, attributeInstance.getBaseValue() * percentageReduction);
         attributeInstance.addPermanentModifier(
            new AttributeModifier(MAX_HEALTH_REDUCTION_ATTRIBUTE_MODIFIER_UUID, "VaultFruitMaxHealthReduction", reductionAmount, Operation.ADDITION)
         );
      }
   }

   @SubscribeEvent
   public static void on(PlayerTickEvent event) {
      if (event.side != LogicalSide.CLIENT && event.player.getLevel().getGameTime() % 10L == 0L && !ServerVaults.isInVault(event.player)) {
         synchronized (event.player) {
            AttributeInstance attributeInstance = event.player.getAttribute(Attributes.MAX_HEALTH);
            if (attributeInstance != null) {
               attributeInstance.removeModifier(MAX_HEALTH_REDUCTION_ATTRIBUTE_MODIFIER_UUID);
            }
         }
      }
   }

   public static class BitterLemon extends ItemVaultFruit {
      public BitterLemon(CreativeModeTab group, ResourceLocation id, int extraVaultTicks) {
         super(group, id, extraVaultTicks);
      }

      @Nonnull
      @ParametersAreNonnullByDefault
      public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entityLiving) {
         if (!level.isClientSide && entityLiving instanceof ServerPlayer player) {
            if (!this.onEaten(level, player)) {
               return stack;
            }

            this.reducePlayerMaxHealth(player);
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.CONDUIT_ACTIVATE, SoundSource.MASTER, 1.0F, 1.0F);
         }

         return super.finishUsingItem(stack, level, entityLiving);
      }

      @ParametersAreNonnullByDefault
      @OnlyIn(Dist.CLIENT)
      @Override
      public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> tooltip, TooltipFlag tooltipFlag) {
         tooltip.add(new TextComponent(""));
         TextComponent comp = new TextComponent("A magical lemon with a bitter taste");
         comp.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(12512238)).withItalic(true));
         tooltip.add(comp);
         comp = new TextComponent("It is grown on the gorgeous trees of Iskallia.");
         comp.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(12512238)).withItalic(true));
         tooltip.add(comp);
         tooltip.add(new TextComponent(""));
         comp = new TextComponent("- Removes 10% max health");
         comp.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(16711680)));
         tooltip.add(comp);
         comp = new TextComponent("- Adds 30 seconds to the Vault Timer");
         comp.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(65280)));
         tooltip.add(comp);
         super.appendHoverText(itemStack, level, tooltip, tooltipFlag);
      }
   }

   public static class MysticPear extends ItemVaultFruit {
      public MysticPear(CreativeModeTab group, ResourceLocation id, int extraVaultTicks) {
         super(group, id, extraVaultTicks);
      }

      @Nonnull
      @ParametersAreNonnullByDefault
      public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity entityLiving) {
         if (!worldIn.isClientSide && entityLiving instanceof ServerPlayer player) {
            if (!this.onEaten(worldIn, player)) {
               return stack;
            }

            this.reducePlayerMaxHealth(player);
            if (MathUtilities.randomFloat(0.0F, 100.0F) <= 50.0F) {
               player.addEffect(new MobEffectInstance(MobEffects.POISON, 600));
            } else {
               player.addEffect(new MobEffectInstance(MobEffects.WITHER, 600));
            }

            worldIn.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.CONDUIT_ACTIVATE, SoundSource.MASTER, 1.0F, 1.0F);
         }

         return super.finishUsingItem(stack, worldIn, entityLiving);
      }

      @ParametersAreNonnullByDefault
      @OnlyIn(Dist.CLIENT)
      @Override
      public void appendHoverText(ItemStack itemStack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag tooltipFlag) {
         tooltip.add(new TextComponent(""));
         TextComponent comp = new TextComponent("A magical pear with a strange taste");
         comp.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(12512238)).withItalic(true));
         tooltip.add(comp);
         comp = new TextComponent("It is grown on the gorgeous trees of Iskallia.");
         comp.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(12512238)).withItalic(true));
         tooltip.add(comp);
         tooltip.add(new TextComponent(""));
         comp = new TextComponent("- Removes 10% max health");
         comp.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(16711680)));
         tooltip.add(comp);
         comp = new TextComponent("- Inflicts with either Wither or Poison effect");
         comp.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(16711680)));
         tooltip.add(comp);
         comp = new TextComponent("- Adds 5 minutes to the Vault Timer");
         comp.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(65280)));
         tooltip.add(comp);
         super.appendHoverText(itemStack, worldIn, tooltip, tooltipFlag);
      }
   }

   public static class SourOrange extends ItemVaultFruit {
      public SourOrange(CreativeModeTab group, ResourceLocation id, int extraVaultTicks) {
         super(group, id, extraVaultTicks);
      }

      @Nonnull
      @ParametersAreNonnullByDefault
      public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entityLiving) {
         if (!level.isClientSide && entityLiving instanceof ServerPlayer player) {
            if (!this.onEaten(level, player)) {
               return stack;
            }

            this.reducePlayerMaxHealth(player);
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.CONDUIT_ACTIVATE, SoundSource.MASTER, 1.0F, 1.0F);
         }

         return super.finishUsingItem(stack, level, entityLiving);
      }

      @ParametersAreNonnullByDefault
      @OnlyIn(Dist.CLIENT)
      @Override
      public void appendHoverText(ItemStack itemStack, @Nullable Level level, List<Component> tooltip, TooltipFlag tooltipFlag) {
         tooltip.add(new TextComponent(""));
         TextComponent comp = new TextComponent("A magical orange with a sour taste");
         comp.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(12512238)).withItalic(true));
         tooltip.add(comp);
         comp = new TextComponent("It is grown on the gorgeous trees of Iskallia.");
         comp.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(12512238)).withItalic(true));
         tooltip.add(comp);
         tooltip.add(new TextComponent(""));
         comp = new TextComponent("- Removes 10% max health");
         comp.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(16711680)));
         tooltip.add(comp);
         comp = new TextComponent("- Adds 60 seconds to the Vault Timer");
         comp.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(65280)));
         tooltip.add(comp);
         super.appendHoverText(itemStack, level, tooltip, tooltipFlag);
      }
   }

   public static class SweetKiwi extends ItemVaultFruit {
      public SweetKiwi(CreativeModeTab group, ResourceLocation id, int extraVaultTicks) {
         super(group, id, extraVaultTicks);
      }

      @Nonnull
      @ParametersAreNonnullByDefault
      public ItemStack finishUsingItem(ItemStack stack, Level level, LivingEntity entityLiving) {
         if (!level.isClientSide && entityLiving instanceof ServerPlayer player) {
            if (!this.onEaten(level, player)) {
               return stack;
            }

            this.reducePlayerMaxHealth(player);
            level.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.CONDUIT_ACTIVATE, SoundSource.MASTER, 1.0F, 1.0F);
         }

         return super.finishUsingItem(stack, level, entityLiving);
      }

      @ParametersAreNonnullByDefault
      @OnlyIn(Dist.CLIENT)
      @Override
      public void appendHoverText(ItemStack itemStack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag tooltipFlag) {
         tooltip.add(new TextComponent(""));
         TextComponent comp = new TextComponent("- Removes 10% max health");
         comp.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(16711680)));
         tooltip.add(comp);
         comp = new TextComponent("- Adds 10 seconds to the Vault Timer");
         comp.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(65280)));
         tooltip.add(comp);
         super.appendHoverText(itemStack, worldIn, tooltip, tooltipFlag);
      }
   }
}
