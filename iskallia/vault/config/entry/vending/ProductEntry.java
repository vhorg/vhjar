package iskallia.vault.config.entry.vending;

import com.google.gson.annotations.Expose;
import iskallia.vault.util.MathUtilities;
import iskallia.vault.vending.Product;
import java.util.Objects;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.TagParser;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.tags.ITagManager;

public class ProductEntry {
   private static final Random RANDOM = new Random();
   @Expose
   protected String id;
   @Expose
   protected String tag;
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
      this(stack.getItem(), stack.getCount(), stack.getTag());
   }

   public ProductEntry(Item item, int amount, @Nullable CompoundTag nbt) {
      this(item, amount, amount, nbt);
   }

   public ProductEntry(Item item, int amountMin, int amountMax, @Nullable CompoundTag nbt) {
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

   public CompoundTag getNBT() {
      if (this.nbt == null) {
         return null;
      } else {
         try {
            return TagParser.parseTag(this.nbt);
         } catch (Exception var2) {
            return null;
         }
      }
   }

   public Product toProduct() {
      return new Product(this.getItem(), this.generateAmount(), this.getNBT());
   }

   public ItemStack generateItemStack() {
      Item item = null;
      if (this.tag != null && !this.tag.isEmpty()) {
         ResourceLocation resourceLocation = ResourceLocation.tryParse(this.tag);
         if (resourceLocation != null) {
            TagKey<Item> tagKey = TagKey.create(ForgeRegistries.ITEMS.getRegistryKey(), resourceLocation);
            ITagManager<Item> itemTags = ForgeRegistries.ITEMS.tags();
            System.out.println(itemTags.getTagNames().collect(Collectors.toList()));
            if (itemTags != null && itemTags.isKnownTagName(tagKey)) {
               Optional<Item> optionalItem = itemTags.getTag(tagKey).getRandomElement(RANDOM);
               if (optionalItem.isPresent()) {
                  item = optionalItem.get();
               }
            }
         }
      } else {
         item = this.getItem();
      }

      ItemStack itemStack = new ItemStack(item, this.generateAmount());
      CompoundTag tag = this.getNBT();
      if (tag != null && !tag.isEmpty()) {
         itemStack.setTag(tag);
      }

      return itemStack;
   }
}
