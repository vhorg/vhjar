package iskallia.vault.config.entry;

import com.google.gson.annotations.Expose;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.IItemProvider;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.Registry;

public class SingleItemEntry {
   @Expose
   public final String ITEM;
   @Expose
   public final String NBT;

   public SingleItemEntry(String item, String nbt) {
      this.ITEM = item;
      this.NBT = nbt;
   }

   public SingleItemEntry(ResourceLocation key, CompoundNBT nbt) {
      this(key.toString(), nbt.toString());
   }

   public SingleItemEntry(IItemProvider item) {
      this(item.func_199767_j().getRegistryName(), new CompoundNBT());
   }

   public SingleItemEntry(ItemStack itemStack) {
      this(itemStack.func_77973_b().getRegistryName(), itemStack.func_196082_o());
   }

   public ItemStack createItemStack() {
      return Registry.field_212630_s.func_241873_b(new ResourceLocation(this.ITEM)).map(item -> {
         ItemStack stack = new ItemStack(item);

         try {
            if (this.NBT != null) {
               CompoundNBT tag = JsonToNBT.func_180713_a(this.NBT);
               if (!tag.isEmpty()) {
                  stack.func_77982_d(tag);
               }
            }
         } catch (CommandSyntaxException var4) {
         }

         return stack;
      }).orElse(ItemStack.field_190927_a);
   }
}
