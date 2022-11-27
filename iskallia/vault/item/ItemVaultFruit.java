package iskallia.vault.item;

import iskallia.vault.core.event.CommonEvents;
import iskallia.vault.util.EntityHelper;
import iskallia.vault.util.MathUtilities;
import iskallia.vault.world.data.ServerVaults;
import java.util.List;
import javax.annotation.Nullable;
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
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
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

public class ItemVaultFruit extends Item {
   public static FoodProperties VAULT_FRUIT_FOOD = new Builder().saturationMod(0.0F).nutrition(0).fast().alwaysEat().build();
   protected int extraVaultTicks;

   public ItemVaultFruit(CreativeModeTab group, ResourceLocation id, int extraVaultTicks) {
      super(new Properties().tab(group).food(VAULT_FRUIT_FOOD).stacksTo(64));
      this.setRegistryName(id);
      this.extraVaultTicks = extraVaultTicks;
   }

   public boolean onEaten(Level world, Player player) {
      CommonEvents.FRUIT_EATEN.invoke(this, player, this.extraVaultTicks);
      return true;
   }

   public int getExtraVaultTicks() {
      return this.extraVaultTicks;
   }

   public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand hand) {
      ItemStack itemStack = player.getItemInHand(hand);
      return !ServerVaults.isVaultWorld(level) ? InteractionResultHolder.fail(itemStack) : super.use(level, player, hand);
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
      tooltip.add(new TextComponent(""));
      TextComponent comp = new TextComponent("[!] Only edible inside a Vault");
      comp.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(16711680)).withItalic(true));
      tooltip.add(comp);
      super.appendHoverText(stack, worldIn, tooltip, flagIn);
   }

   public Component getName(ItemStack stack) {
      MutableComponent displayName = (MutableComponent)super.getName(stack);
      return displayName.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(16563456)));
   }

   public static class BitterLemon extends ItemVaultFruit {
      protected DamageSource damageSource = new DamageSource("bitter_lemon").bypassArmor();

      public BitterLemon(CreativeModeTab group, ResourceLocation id, int extraVaultTicks) {
         super(group, id, extraVaultTicks);
      }

      public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity entityLiving) {
         if (!worldIn.isClientSide && entityLiving instanceof ServerPlayer player) {
            if (!this.onEaten(worldIn, player)) {
               return stack;
            }

            EntityHelper.changeHealth(player, -6);
            worldIn.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.CONDUIT_ACTIVATE, SoundSource.MASTER, 1.0F, 1.0F);
         }

         return super.finishUsingItem(stack, worldIn, entityLiving);
      }

      @OnlyIn(Dist.CLIENT)
      @Override
      public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
         tooltip.add(new TextComponent(""));
         TextComponent comp = new TextComponent("A magical lemon with a bitter taste");
         comp.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(12512238)).withItalic(true));
         tooltip.add(comp);
         comp = new TextComponent("It is grown on the gorgeous trees of Iskallia.");
         comp.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(12512238)).withItalic(true));
         tooltip.add(comp);
         tooltip.add(new TextComponent(""));
         comp = new TextComponent("- Wipes away 3 hearts");
         comp.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(16711680)));
         tooltip.add(comp);
         comp = new TextComponent("- Adds 30 seconds to the Vault Timer");
         comp.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(65280)));
         tooltip.add(comp);
         super.appendHoverText(stack, worldIn, tooltip, flagIn);
      }
   }

   public static class MysticPear extends ItemVaultFruit {
      protected DamageSource damageSource = new DamageSource("mystic_pear").bypassArmor();

      public MysticPear(CreativeModeTab group, ResourceLocation id, int extraVaultTicks) {
         super(group, id, extraVaultTicks);
      }

      public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity entityLiving) {
         if (!worldIn.isClientSide && entityLiving instanceof ServerPlayer player) {
            if (!this.onEaten(worldIn, player)) {
               return stack;
            }

            EntityHelper.changeHealth(player, -MathUtilities.getRandomInt(10, 20));
            if (MathUtilities.randomFloat(0.0F, 100.0F) <= 50.0F) {
               player.addEffect(new MobEffectInstance(MobEffects.POISON, 600));
            } else {
               player.addEffect(new MobEffectInstance(MobEffects.WITHER, 600));
            }

            worldIn.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.CONDUIT_ACTIVATE, SoundSource.MASTER, 1.0F, 1.0F);
         }

         return super.finishUsingItem(stack, worldIn, entityLiving);
      }

      @OnlyIn(Dist.CLIENT)
      @Override
      public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
         tooltip.add(new TextComponent(""));
         TextComponent comp = new TextComponent("A magical pear with a strange taste");
         comp.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(12512238)).withItalic(true));
         tooltip.add(comp);
         comp = new TextComponent("It is grown on the gorgeous trees of Iskallia.");
         comp.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(12512238)).withItalic(true));
         tooltip.add(comp);
         tooltip.add(new TextComponent(""));
         comp = new TextComponent("- Wipes away 5 to 10 hearts");
         comp.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(16711680)));
         tooltip.add(comp);
         comp = new TextComponent("- Inflicts with either Wither or Poison effect");
         comp.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(16711680)));
         tooltip.add(comp);
         comp = new TextComponent("- Adds 5 minutes to the Vault Timer");
         comp.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(65280)));
         tooltip.add(comp);
         super.appendHoverText(stack, worldIn, tooltip, flagIn);
      }
   }

   public static class SourOrange extends ItemVaultFruit {
      protected DamageSource damageSource = new DamageSource("sour_orange").bypassArmor();

      public SourOrange(CreativeModeTab group, ResourceLocation id, int extraVaultTicks) {
         super(group, id, extraVaultTicks);
      }

      public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity entityLiving) {
         if (!worldIn.isClientSide && entityLiving instanceof ServerPlayer player) {
            if (!this.onEaten(worldIn, player)) {
               return stack;
            }

            EntityHelper.changeHealth(player, -10);
            worldIn.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.CONDUIT_ACTIVATE, SoundSource.MASTER, 1.0F, 1.0F);
         }

         return super.finishUsingItem(stack, worldIn, entityLiving);
      }

      @OnlyIn(Dist.CLIENT)
      @Override
      public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
         tooltip.add(new TextComponent(""));
         TextComponent comp = new TextComponent("A magical orange with a sour taste");
         comp.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(12512238)).withItalic(true));
         tooltip.add(comp);
         comp = new TextComponent("It is grown on the gorgeous trees of Iskallia.");
         comp.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(12512238)).withItalic(true));
         tooltip.add(comp);
         tooltip.add(new TextComponent(""));
         comp = new TextComponent("- Wipes away 5 hearts");
         comp.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(16711680)));
         tooltip.add(comp);
         comp = new TextComponent("- Adds 60 seconds to the Vault Timer");
         comp.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(65280)));
         tooltip.add(comp);
         super.appendHoverText(stack, worldIn, tooltip, flagIn);
      }
   }

   public static class SweetKiwi extends ItemVaultFruit {
      public SweetKiwi(CreativeModeTab group, ResourceLocation id, int extraVaultTicks) {
         super(group, id, extraVaultTicks);
      }

      public ItemStack finishUsingItem(ItemStack stack, Level worldIn, LivingEntity entityLiving) {
         if (!worldIn.isClientSide && entityLiving instanceof ServerPlayer player) {
            if (!this.onEaten(worldIn, player)) {
               return stack;
            }

            worldIn.playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.CONDUIT_ACTIVATE, SoundSource.MASTER, 1.0F, 1.0F);
         }

         return super.finishUsingItem(stack, worldIn, entityLiving);
      }

      @OnlyIn(Dist.CLIENT)
      @Override
      public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
         tooltip.add(new TextComponent(""));
         TextComponent comp = new TextComponent("- Adds 5 seconds to the Vault Timer");
         comp.setStyle(Style.EMPTY.withColor(TextColor.fromRgb(65280)));
         tooltip.add(comp);
         super.appendHoverText(stack, worldIn, tooltip, flagIn);
      }
   }
}
