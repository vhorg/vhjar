package iskallia.vault.vending;

import com.google.gson.annotations.Expose;
import iskallia.vault.util.nbt.INBTSerializable;
import iskallia.vault.util.nbt.NBTSerialize;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistries;

public class Product implements INBTSerializable {
   protected Item itemCache;
   protected CompoundNBT nbtCache;
   @Expose
   @NBTSerialize
   protected String id;
   @Expose
   @NBTSerialize
   protected String nbt;
   @Expose
   @NBTSerialize
   protected int amount;

   public Product() {
   }

   public Product(Item item, int amount, CompoundNBT nbt) {
      this.itemCache = item;
      if (this.itemCache != null) {
         this.id = item.getRegistryName().toString();
      }

      this.nbtCache = nbt;
      if (this.nbtCache != null) {
         this.nbt = this.nbtCache.toString();
      }

      this.amount = amount;
   }

   @Override
   public boolean equals(Object obj) {
      if (obj == null) {
         return false;
      } else if (obj == this) {
         return true;
      } else if (this.getClass() != obj.getClass()) {
         return false;
      } else {
         Product product = (Product)obj;
         boolean similarNBT;
         if (this.getNBT() != null && product.getNBT() != null) {
            similarNBT = this.getNBT().equals(product.getNBT());
         } else {
            similarNBT = true;
         }

         return product.getItem() == this.getItem() && similarNBT;
      }
   }

   public int getAmount() {
      return this.amount;
   }

   public Item getItem() {
      if (this.itemCache != null) {
         return this.itemCache;
      } else {
         this.itemCache = (Item)ForgeRegistries.ITEMS.getValue(new ResourceLocation(this.id));
         if (this.itemCache == null) {
            System.out.println("Unknown item " + this.id + ".");
         }

         return this.itemCache;
      }
   }

   public String getId() {
      return this.id;
   }

   public CompoundNBT getNBT() {
      if (this.nbt == null) {
         return null;
      } else {
         try {
            if (this.nbtCache == null) {
               this.nbtCache = JsonToNBT.func_180713_a(this.nbt);
            }
         } catch (Exception var2) {
            this.nbtCache = null;
            System.out.println("Unknown NBT for item " + this.id + ".");
         }

         return this.nbtCache;
      }
   }

   public boolean isValid() {
      if (this.getAmount() <= 0) {
         return false;
      } else if (this.getItem() == null) {
         return false;
      } else if (this.getItem() == Items.field_190931_a) {
         return false;
      } else {
         return this.getAmount() > this.getItem().func_77639_j() ? false : this.nbt == null || this.getNBT() != null;
      }
   }

   public ItemStack toStack() {
      ItemStack stack = new ItemStack(this.getItem(), this.getAmount());
      stack.func_77982_d(this.getNBT());
      return stack;
   }

   @Override
   public String toString() {
      return "{ id='" + this.id + '\'' + ", nbt='" + this.nbt + '\'' + ", amount=" + this.amount + '}';
   }
}
