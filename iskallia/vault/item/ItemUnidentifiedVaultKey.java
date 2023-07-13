package iskallia.vault.item;

import java.util.function.Supplier;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;

public class ItemUnidentifiedVaultKey extends LootableItem {
   public ItemUnidentifiedVaultKey(ResourceLocation id, Properties properties, Supplier<ItemStack> supplier) {
      super(id, properties, supplier);
   }

   public Component getName(ItemStack stack) {
      return super.getName(stack).copy().withStyle(Style.EMPTY.withColor(TextColor.fromRgb(15496448)));
   }
}
