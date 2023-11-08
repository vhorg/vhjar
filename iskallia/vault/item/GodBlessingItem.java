package iskallia.vault.item;

import iskallia.vault.core.vault.influence.VaultGod;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class GodBlessingItem extends BasicItem {
   public GodBlessingItem(ResourceLocation id) {
      super(id);
   }

   public void fillItemCategory(CreativeModeTab tab, NonNullList<ItemStack> items) {
      if (tab == CreativeModeTab.TAB_SEARCH) {
         for (VaultGod god : VaultGod.values()) {
            ItemStack stack = new ItemStack(this);
            stack.getOrCreateTag().putString("type", god.getName().toLowerCase());
            items.add(stack);
         }
      }
   }

   @Override
   public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
      super.appendHoverText(stack, worldIn, tooltip, flagIn);
      if (stack.hasTag() && stack.getOrCreateTag().contains("rotten")) {
         tooltip.add(tooltip.size(), new TextComponent("Rotten items can not be used in the vault").withStyle(Style.EMPTY.withColor(ChatFormatting.GRAY)));
      }
   }

   @NotNull
   public Component getName(@NotNull ItemStack stack) {
      CompoundTag nbt = stack.getTag();
      if (nbt == null) {
         return super.getName(stack);
      } else {
         String type = nbt.getString("type");
         MutableComponent name = new TranslatableComponent(stack.getItem().getDescriptionId() + "_" + type);
         return stack.hasTag() && stack.getOrCreateTag().contains("rotten")
            ? new TextComponent("")
               .append(new TextComponent("Rotten ").withStyle(Style.EMPTY.withColor(TextColor.parseColor("#00680A"))))
               .append(name.withStyle(ChatFormatting.WHITE))
            : name;
      }
   }

   public static VaultGod getGod(ItemStack stack) {
      CompoundTag nbt = stack.getTag();
      if (nbt == null) {
         return null;
      } else {
         String type = nbt.getString("type");
         return Enum.valueOf(VaultGod.class, type.toUpperCase());
      }
   }
}
