package iskallia.vault.core.world.data.item;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.core.world.data.entity.PartialCompoundNbt;
import java.util.Optional;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

public class PartialItemTag implements ItemPlacement<PartialItemTag> {
   private ResourceLocation id;
   private PartialCompoundNbt nbt;

   public PartialItemTag(ResourceLocation id, PartialCompoundNbt nbt) {
      this.id = id;
      this.nbt = nbt;
   }

   public static PartialItemTag of(ResourceLocation id, PartialCompoundNbt entity) {
      return new PartialItemTag(id, entity);
   }

   public boolean isSubsetOf(PartialItemTag other) {
      return (this.id == null || this.id.equals(other.id)) && this.nbt.isSubsetOf(other.nbt);
   }

   @Override
   public boolean isSubsetOf(ItemStack stack) {
      throw new UnsupportedOperationException();
   }

   public void fillInto(PartialItemTag other) {
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
      return this.nbt.isSubsetOf(nbt)
         && item.asWhole().map(other -> other.builtInRegistryHolder().tags().anyMatch(tag -> tag.location().equals(this.id))).orElse(false);
   }

   public PartialItemTag copy() {
      return new PartialItemTag(this.id, this.nbt.copy());
   }

   public static Optional<PartialItemTag> parse(String string, boolean logErrors) {
      try {
         return Optional.of(parse(new StringReader(string)));
      } catch (IllegalArgumentException | CommandSyntaxException var3) {
         if (logErrors) {
            var3.printStackTrace();
         }

         return Optional.empty();
      }
   }

   public static PartialItemTag parse(String string) throws CommandSyntaxException {
      return parse(new StringReader(string));
   }

   public static PartialItemTag parse(StringReader reader) throws CommandSyntaxException {
      if (reader.peek() != '#') {
         throw new IllegalArgumentException("Invalid item tag '" + reader.getString() + "' does not start with #");
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
            throw new IllegalArgumentException("Invalid tag identifier '" + string + "' in item tag '" + reader.getString() + "'");
         }
      }
   }

   protected static boolean isCharValid(char c) {
      return c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c == '_' || c == ':' || c == '/' || c == '.' || c == '-';
   }
}
