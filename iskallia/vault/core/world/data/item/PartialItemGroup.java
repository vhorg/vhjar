package iskallia.vault.core.world.data.item;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.core.world.data.PartialCompoundNbt;
import iskallia.vault.init.ModConfigs;
import java.util.Optional;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class PartialItemGroup implements ItemPlacement<PartialItemGroup> {
   private ResourceLocation id;
   private PartialCompoundNbt nbt;

   public PartialItemGroup(ResourceLocation id, PartialCompoundNbt nbt) {
      this.id = id;
      this.nbt = nbt;
   }

   public static PartialItemGroup of(ResourceLocation id, PartialCompoundNbt entity) {
      return new PartialItemGroup(id, entity);
   }

   public boolean isSubsetOf(PartialItemGroup other) {
      return (this.id == null || this.id.equals(other.id)) && this.nbt.isSubsetOf(other.nbt);
   }

   @Override
   public boolean isSubsetOf(ItemStack stack) {
      throw new UnsupportedOperationException();
   }

   public void fillInto(PartialItemGroup other) {
      if (this.id != null) {
         other.id = this.id;
      }

      this.nbt.fillInto(other.nbt);
   }

   @Override
   public Optional<ItemStack> generate(int count) {
      return this.nbt.generate(count);
   }

   @Override
   public boolean test(PartialItem item, PartialCompoundNbt nbt) {
      return this.nbt.isSubsetOf(nbt) && ModConfigs.ITEM_GROUPS.isInGroup(this.id, item, nbt);
   }

   public PartialItemGroup copy() {
      return new PartialItemGroup(this.id, this.nbt.copy());
   }

   public static Optional<PartialItemGroup> parse(String string, boolean logErrors) {
      try {
         return Optional.of(parse(new StringReader(string)));
      } catch (IllegalArgumentException | CommandSyntaxException var3) {
         if (logErrors) {
            var3.printStackTrace();
         }

         return Optional.empty();
      }
   }

   public static PartialItemGroup parse(String string) throws CommandSyntaxException {
      return parse(new StringReader(string));
   }

   public static PartialItemGroup parse(StringReader reader) throws CommandSyntaxException {
      if (reader.peek() != '@') {
         throw new IllegalArgumentException("Invalid item group '" + reader.getString() + "' does not start with @");
      } else {
         reader.skip();
         int cursor = reader.getCursor();

         while (reader.canRead() && isCharValid(reader.peek())) {
            reader.skip();
         }

         String string = reader.getString().substring(cursor, reader.getCursor());

         try {
            return of(new ResourceLocation(string), PartialCompoundNbt.parse(reader));
         } catch (ResourceLocationException var4) {
            reader.setCursor(cursor);
            throw new IllegalArgumentException("Invalid item identifier '" + string + "' in item group '" + reader.getString() + "'");
         }
      }
   }

   protected static boolean isCharValid(char c) {
      return c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c == '_' || c == ':' || c == '/' || c == '.' || c == '-';
   }
}
