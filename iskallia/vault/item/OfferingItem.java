package iskallia.vault.item;

import iskallia.vault.init.ModItems;
import iskallia.vault.util.StringUtils;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.Item.Properties;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class OfferingItem extends BasicItem {
   public OfferingItem(ResourceLocation id) {
      super(id, new Properties().tab(ModItems.VAULT_MOD_GROUP).stacksTo(1));
   }

   public static String getModifier(ItemStack stack) {
      if (stack.hasTag()) {
         CompoundTag tag = stack.getTag();
         if (tag != null) {
            return tag.getString("Modifier");
         }
      }

      return "";
   }

   public static Collection<ItemStack> getItems(ItemStack original) {
      ListTag itemsNbt = original.getOrCreateTag().getList("Items", 10);
      List<ItemStack> items = new ArrayList<>();

      for (Tag itemNbt : itemsNbt) {
         items.add(ItemStack.of((CompoundTag)itemNbt));
      }

      return items;
   }

   @Override
   public void appendHoverText(ItemStack stack, @Nullable Level worldIn, List<Component> tooltip, TooltipFlag flagIn) {
      super.appendHoverText(stack, worldIn, tooltip, flagIn);
      if (stack.hasTag()) {
         CompoundTag tag = stack.getTag();
         if (tag != null) {
            if (tag.contains("Items")) {
               for (Tag itemTag : tag.getList("Items", 10)) {
                  ItemStack itemStack = ItemStack.of((CompoundTag)itemTag);
                  tooltip.add(
                     new TextComponent("Guaranteed ")
                        .append(new TextComponent(itemStack.getHoverName().getString()).withStyle(ChatFormatting.GREEN))
                        .withStyle(ChatFormatting.GRAY)
                  );
               }
            }

            if (tag.contains("Modifier")) {
               tooltip.add(
                  new TextComponent("Adds \"")
                     .append(new TextComponent(StringUtils.convertToTitleCase(tag.getString("Modifier"))).withStyle(ChatFormatting.RED))
                     .append("\" to boss")
                     .withStyle(ChatFormatting.GRAY)
               );
            }
         }
      }
   }

   public static void setModifier(ItemStack stack, String randomModifier) {
      stack.getOrCreateTag().putString("Modifier", randomModifier);
   }

   public static void setItems(ItemStack stack, List<ItemStack> randomLootItems) {
      ListTag items = new ListTag();
      randomLootItems.forEach(itemStack -> items.add(itemStack.save(new CompoundTag())));
      stack.getOrCreateTag().put("Items", items);
   }
}
