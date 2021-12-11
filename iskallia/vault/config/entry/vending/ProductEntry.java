package iskallia.vault.config.entry.vending;

import com.google.gson.annotations.Expose;
import iskallia.vault.util.MathUtilities;
import iskallia.vault.vending.Product;
import java.util.Objects;
import javax.annotation.Nullable;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class ProductEntry {
   @Expose
   protected String id;
   @Expose
   protected String nbt;
   @Expose
   protected int amountMin;
   @Expose
   protected int amountMax;

   public ProductEntry() {
   }

   public ProductEntry(Item item) {
      this(item, 1, null);
   }

   public ProductEntry(ItemStack stack) {
      this(stack.func_77973_b(), stack.func_190916_E(), stack.func_77978_p());
   }

   public ProductEntry(Item item, int amount, @Nullable CompoundNBT nbt) {
      this(item, amount, amount, nbt);
   }

   public ProductEntry(Item item, int amountMin, int amountMax, @Nullable CompoundNBT nbt) {
      this.id = Objects.requireNonNull(item.getRegistryName()).toString();
      this.nbt = nbt == null ? null : nbt.toString();
      this.amountMin = amountMin;
      this.amountMax = amountMax;
   }

   public Item getItem() {
      return (Item)ForgeRegistries.ITEMS.getValue(new ResourceLocation(this.id));
   }

   public int generateAmount() {
      return MathUtilities.getRandomInt(this.amountMin, this.amountMax);
   }

   public CompoundNBT getNBT() {
      if (this.nbt == null) {
         return null;
      } else {
         try {
            return JsonToNBT.func_180713_a(this.nbt);
         } catch (Exception var2) {
            return null;
         }
      }
   }

   public Product toProduct() {
      return new Product(this.getItem(), this.generateAmount(), this.getNBT());
   }

   public ItemStack generateItemStack() {
      ItemStack itemStack = new ItemStack(this.getItem(), this.generateAmount());
      itemStack.func_77982_d(this.getNBT());
      return itemStack;
   }
}
