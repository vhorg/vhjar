package iskallia.vault.config.entry;

import com.google.gson.annotations.Expose;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;

public class ItemEntry extends SingleItemEntry {
   @Expose
   public int AMOUNT;

   public ItemEntry(String item, int amount, String nbt) {
      super(item, nbt);
      this.AMOUNT = amount;
   }

   public ItemEntry(ResourceLocation key, int amount, CompoundNBT nbt) {
      this(key.toString(), amount, nbt.toString());
   }

   public ItemEntry(IItemProvider item, int amount) {
      this(item.func_199767_j().getRegistryName(), amount, new CompoundNBT());
   }

   public ItemEntry(ItemStack itemStack) {
      this(itemStack.func_77973_b().getRegistryName(), itemStack.func_190916_E(), itemStack.func_196082_o());
   }

   @Override
   public ItemStack createItemStack() {
      ItemStack stack = super.createItemStack();
      stack.func_190920_e(this.AMOUNT);
      return stack;
   }
}
