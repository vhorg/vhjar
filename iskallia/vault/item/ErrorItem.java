package iskallia.vault.item;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Item.Properties;

public class ErrorItem extends Item {
   public ErrorItem(ResourceLocation id) {
      super(new Properties());
      this.setRegistryName(id);
   }

   public Component getName(ItemStack stack) {
      ResourceLocation id = getId(stack);
      return (Component)(id != null ? new TextComponent(id.toString()) : super.getName(stack));
   }

   public static ResourceLocation getId(ItemStack stack) {
      return stack.hasTag() ? ResourceLocation.tryParse(stack.getTag().getString("__id__")) : null;
   }
}
