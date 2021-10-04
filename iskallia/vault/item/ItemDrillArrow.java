package iskallia.vault.item;

import iskallia.vault.entity.DrillArrowEntity;
import iskallia.vault.util.MiscUtils;
import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.AbstractArrowEntity;
import net.minecraft.item.ArrowItem;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.Properties;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

public class ItemDrillArrow extends ArrowItem {
   public ItemDrillArrow(ItemGroup group, ResourceLocation id) {
      super(new Properties().func_200916_a(group).func_200917_a(63));
      this.setRegistryName(id);
   }

   public void func_150895_a(ItemGroup group, NonNullList<ItemStack> items) {
      if (this.func_194125_a(group)) {
         for (ItemDrillArrow.ArrowTier tier : ItemDrillArrow.ArrowTier.values()) {
            ItemStack stack = new ItemStack(this);
            setArrowTier(stack, tier);
            items.add(stack);
         }
      }
   }

   @OnlyIn(Dist.CLIENT)
   public void func_77624_a(ItemStack stack, @Nullable World worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
      tooltip.add(new TranslationTextComponent(getArrowTier(stack).getName()).func_240699_a_(TextFormatting.GOLD));
   }

   public AbstractArrowEntity func_200887_a(World world, ItemStack stack, LivingEntity shooter) {
      return new DrillArrowEntity(world, shooter).setMaxBreakCount(getArrowTier(stack).getBreakCount());
   }

   public boolean isInfinite(ItemStack stack, ItemStack bow, PlayerEntity player) {
      return false;
   }

   @Nonnull
   public static ItemDrillArrow.ArrowTier getArrowTier(ItemStack stack) {
      if (!(stack.func_77973_b() instanceof ItemDrillArrow)) {
         return ItemDrillArrow.ArrowTier.NORMAL;
      } else {
         int tierOrd = stack.func_196082_o().func_74762_e("tier");
         return MiscUtils.getEnumEntry(ItemDrillArrow.ArrowTier.class, tierOrd);
      }
   }

   public static void setArrowTier(ItemStack stack, @Nonnull ItemDrillArrow.ArrowTier tier) {
      if (stack.func_77973_b() instanceof ItemDrillArrow) {
         stack.func_196082_o().func_74768_a("tier", tier.ordinal());
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
