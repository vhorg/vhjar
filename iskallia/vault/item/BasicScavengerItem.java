package iskallia.vault.item;

import iskallia.vault.VaultMod;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.init.ModItems;
import java.awt.Color;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.util.Mth;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class BasicScavengerItem extends BasicItem {
   private static final HashMap<Item, Integer> NAME_COLOR_CACHE = new HashMap<>();
   private static final Component SCAVENGER_ITEM_HINT = new TranslatableComponent("tooltip.the_vault.scavenger_item").withStyle(ChatFormatting.GOLD);

   public BasicScavengerItem(String id) {
      super(VaultMod.id("scavenger_" + id), new Properties().tab(ModItems.SCAVENGER_GROUP));
      this.withTooltip(Collections.singletonList(SCAVENGER_ITEM_HINT));
   }

   @Override
   public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
      super.appendHoverText(stack, worldIn, tooltip, flagIn);
      if (stack.hasTag() && stack.getOrCreateTag().contains("rotten")) {
         tooltip.add(
            Mth.clamp(tooltip.size(), 0, tooltip.size() - 2),
            new TextComponent("Rotten items can not be used in the vault").withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY))
         );
      }
   }

   @NotNull
   public Component getName(@NotNull ItemStack stack) {
      if (ModConfigs.SCAVENGER == null) {
         return super.getName(stack);
      } else {
         MutableComponent name = super.getName(stack).copy();
         if (NAME_COLOR_CACHE.isEmpty()) {
            NAME_COLOR_CACHE.putAll(ModConfigs.SCAVENGER.getNameColors());
         }

         int color = NAME_COLOR_CACHE.computeIfAbsent(stack.getItem(), item -> Color.DARK_GRAY.getRGB());
         return stack.hasTag() && stack.getOrCreateTag().contains("rotten")
            ? new TextComponent("Rotten ")
               .withStyle(Style.EMPTY.withColor(TextColor.parseColor("#00680A")))
               .append(name.withStyle(Style.EMPTY.withColor(color)))
            : name.withStyle(Style.EMPTY.withColor(color));
      }
   }

   public static void setVaultIdentifier(ItemStack stack, UUID identifier) {
      if (stack.getItem() instanceof BasicScavengerItem) {
         stack.getOrCreateTag().putUUID("vault_id", identifier);
      }
   }

   @javax.annotation.Nullable
   public static UUID getVaultIdentifier(ItemStack stack) {
      if (!(stack.getItem() instanceof BasicScavengerItem)) {
         return null;
      } else {
         CompoundTag tag = stack.getOrCreateTag();
         return !tag.hasUUID("vault_id") ? null : stack.getOrCreateTag().getUUID("vault_id");
      }
   }
}
