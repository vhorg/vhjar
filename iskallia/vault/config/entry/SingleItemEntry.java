package iskallia.vault.config.entry;

import com.google.gson.annotations.Expose;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.core.Registry;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class SingleItemEntry {
   public static final SingleItemEntry EMPTY = new SingleItemEntry("", null);
   @Expose
   public final String ITEM;
   @Expose
   public final String NBT;

   public SingleItemEntry(String item, String nbt) {
      this.ITEM = item;
      this.NBT = nbt;
   }

   public SingleItemEntry(ResourceLocation key, CompoundTag nbt) {
      this(key.toString(), nbt.toString());
   }

   public SingleItemEntry(ItemLike item) {
      this(item.asItem().getRegistryName(), new CompoundTag());
   }

   public SingleItemEntry(ItemStack itemStack) {
      this(itemStack.getItem().getRegistryName(), itemStack.getOrCreateTag());
   }

   public ItemStack createItemStack() {
      return Registry.ITEM.getOptional(new ResourceLocation(this.ITEM)).map(item -> {
         ItemStack stack = new ItemStack(item);

         try {
            if (this.NBT != null) {
               CompoundTag tag = TagParser.parseTag(this.NBT);
               if (!tag.isEmpty()) {
                  stack.setTag(tag);
               }
            }
         } catch (CommandSyntaxException var4) {
         }

         return stack;
      }).orElse(ItemStack.EMPTY);
   }
}
