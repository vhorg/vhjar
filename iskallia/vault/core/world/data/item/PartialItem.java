package iskallia.vault.core.world.data.item;

import com.google.gson.JsonElement;
import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.core.data.adapter.Adapters;
import iskallia.vault.core.world.data.PartialCompoundNbt;
import iskallia.vault.item.crystal.data.adapter.ISimpleAdapter;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.ResourceLocationException;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.ForgeRegistries;

public class PartialItem implements ItemPlacement<PartialItem> {
   protected ResourceLocation id;

   protected PartialItem(ResourceLocation id) {
      this.id = id;
   }

   public static PartialItem empty() {
      return new PartialItem(null);
   }

   public static PartialItem of(ResourceLocation id) {
      return new PartialItem(id);
   }

   public static PartialItem of(Item item) {
      return new PartialItem(item.getRegistryName());
   }

   public static PartialItem of(ItemStack stack) {
      return new PartialItem(stack.getItem().getRegistryName());
   }

   public boolean isSubsetOf(PartialItem other) {
      return this.id == null || this.id.equals(other.id);
   }

   @Override
   public boolean isSubsetOf(ItemStack stack) {
      return this.isSubsetOf(of(stack));
   }

   public void fillInto(PartialItem other) {
      if (this.id != null) {
         other.id = this.id;
      }
   }

   @Override
   public Optional<ItemStack> generate(int count) {
      return this.asWhole().map(item -> new ItemStack(item, count));
   }

   @Override
   public boolean test(PartialItem item, PartialCompoundNbt nbt) {
      return this.isSubsetOf(item);
   }

   public Optional<Item> asWhole() {
      return !ForgeRegistries.ITEMS.containsKey(this.id) ? Optional.empty() : Optional.ofNullable((Item)ForgeRegistries.ITEMS.getValue(this.id));
   }

   public PartialItem copy() {
      return new PartialItem(this.id);
   }

   @Override
   public String toString() {
      return this.id == null ? "" : this.id.toString();
   }

   public static Optional<PartialItem> parse(String string, boolean logErrors) {
      try {
         return Optional.of(parse(new StringReader(string)));
      } catch (IllegalArgumentException | CommandSyntaxException var3) {
         if (logErrors) {
            var3.printStackTrace();
         }

         return Optional.empty();
      }
   }

   public static PartialItem parse(String string) throws CommandSyntaxException {
      return parse(new StringReader(string));
   }

   public static PartialItem parse(StringReader reader) throws CommandSyntaxException {
      if (reader.canRead() && isCharValid(reader.peek())) {
         int cursor = reader.getCursor();

         while (reader.canRead() && isCharValid(reader.peek())) {
            reader.skip();
         }

         String string = reader.getString().substring(cursor, reader.getCursor());

         try {
            return of(new ResourceLocation(string));
         } catch (ResourceLocationException var4) {
            reader.setCursor(cursor);
            throw new IllegalArgumentException("Invalid item identifier '" + string + "' in stack '" + reader.getString() + "'");
         }
      } else {
         return empty();
      }
   }

   protected static boolean isCharValid(char c) {
      return c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c == '_' || c == ':' || c == '/' || c == '.' || c == '-';
   }

   public static class Adapter implements ISimpleAdapter<PartialItem, Tag, JsonElement> {
      public Optional<Tag> writeNbt(@Nullable PartialItem value) {
         return value == null ? Optional.empty() : Adapters.IDENTIFIER.writeNbt(value.id);
      }

      @Override
      public Optional<PartialItem> readNbt(@Nullable Tag nbt) {
         return nbt == null ? Optional.empty() : Adapters.IDENTIFIER.readNbt(nbt).map(PartialItem::of);
      }
   }
}
