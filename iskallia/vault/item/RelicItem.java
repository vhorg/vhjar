package iskallia.vault.item;

import iskallia.vault.init.ModItems;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.Properties;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

public class RelicItem extends Item {
   public RelicItem(ItemGroup group, ResourceLocation id) {
      super(new Properties().func_200916_a(group).func_200917_a(64));
      this.setRegistryName(id);
   }

   public static ItemStack withCustomModelData(int customModelData) {
      ItemStack itemStack = new ItemStack(ModItems.VAULT_RELIC);
      CompoundNBT nbt = new CompoundNBT();
      nbt.func_74768_a("CustomModelData", customModelData);
      itemStack.func_77982_d(nbt);
      return itemStack;
   }
}
