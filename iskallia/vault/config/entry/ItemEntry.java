package iskallia.vault.config.entry;

import com.google.gson.annotations.Expose;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class ItemEntry extends SingleItemEntry {
   @Expose
   public int AMOUNT;

   public ItemEntry(String item, int amount, String nbt) {
      super(item, nbt);
      this.AMOUNT = amount;
   }

   public ItemEntry(ResourceLocation key, int amount, CompoundTag nbt) {
      this(key.toString(), amount, nbt.toString());
   }

   public ItemEntry(ItemLike item, int amount) {
      this(item.asItem().getRegistryName(), amount, new CompoundTag());
   }

   public ItemEntry(ItemStack itemStack) {
      this(itemStack.getItem().getRegistryName(), itemStack.getCount(), itemStack.getOrCreateTag());
   }

   @Override
   public ItemStack createItemStack() {
      ItemStack stack = super.createItemStack();
      stack.setCount(this.AMOUNT);
      return stack;
   }
}
