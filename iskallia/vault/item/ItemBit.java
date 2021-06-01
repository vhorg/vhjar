package iskallia.vault.item;

import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.Item.Properties;
import net.minecraft.util.ResourceLocation;

public class ItemBit extends Item {
   protected int value;

   public ItemBit(ItemGroup group, ResourceLocation id, int value) {
      super(new Properties().func_200916_a(group).func_200917_a(64));
      this.setRegistryName(id);
      this.value = value;
   }

   public int getValue() {
      return this.value;
   }
}
