package iskallia.vault.item;

import iskallia.vault.entity.entity.DrillArrowEntity;
import iskallia.vault.util.MiscUtils;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.ArrowItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemDrillArrow extends ArrowItem {
   public ItemDrillArrow(CreativeModeTab group, ResourceLocation id) {
      super(new Properties().tab(group).stacksTo(63));
      this.setRegistryName(id);
   }

   public void fillItemCategory(CreativeModeTab group, NonNullList<ItemStack> items) {
      if (this.allowdedIn(group)) {
         for (ItemDrillArrow.ArrowTier tier : ItemDrillArrow.ArrowTier.values()) {
            ItemStack stack = new ItemStack(this);
            setArrowTier(stack, tier);
            items.add(stack);
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
      tooltip.add(new TranslatableComponent(getArrowTier(stack).getName()).withStyle(ChatFormatting.GOLD));
   }

   public AbstractArrow createArrow(Level world, ItemStack stack, LivingEntity shooter) {
      return new DrillArrowEntity(world, shooter).setMaxBreakCount(getArrowTier(stack).getBreakCount());
   }

   public boolean isInfinite(ItemStack stack, ItemStack bow, Player player) {
      return false;
   }

   @Nonnull
   public static ItemDrillArrow.ArrowTier getArrowTier(ItemStack stack) {
      if (!(stack.getItem() instanceof ItemDrillArrow)) {
         return ItemDrillArrow.ArrowTier.NORMAL;
      } else {
         int tierOrd = stack.getOrCreateTag().getInt("tier");
         return MiscUtils.getEnumEntry(ItemDrillArrow.ArrowTier.class, tierOrd);
      }
   }

   public static void setArrowTier(ItemStack stack, @Nonnull ItemDrillArrow.ArrowTier tier) {
      if (stack.getItem() instanceof ItemDrillArrow) {
         stack.getOrCreateTag().putInt("tier", tier.ordinal());
      }
   }

   public static enum ArrowTier {
      NORMAL(400),
      RARE(700),
      EPIC(1000);

      private final int breakCount;

      private ArrowTier(int breakCount) {
         this.breakCount = breakCount;
      }

      public int getBreakCount() {
         return this.breakCount;
      }

      public String getName() {
         return "item.the_vault.drill_arrow." + this.name().toLowerCase();
      }
   }
}
