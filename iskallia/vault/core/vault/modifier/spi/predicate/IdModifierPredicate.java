package iskallia.vault.core.vault.modifier.spi.predicate;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import java.util.Optional;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;

public class IdModifierPredicate implements ModifierPredicate {
   private final ResourceLocation id;

   public IdModifierPredicate(ResourceLocation id) {
      this.id = id;
   }

   @Override
   public boolean test(VaultModifier<?> modifier) {
      return modifier.getId().equals(this.id);
   }

   @Override
   public String toString() {
      return this.id.toString();
   }

   public static Optional<IdModifierPredicate> parse(String string, boolean logErrors) {
      try {
         return Optional.of(parse(new StringReader(string)));
      } catch (IllegalArgumentException | CommandSyntaxException var3) {
         if (logErrors) {
            var3.printStackTrace();
         }

         return Optional.empty();
      }
   }

   public static IdModifierPredicate parse(String string) throws CommandSyntaxException {
      return parse(new StringReader(string));
   }

   public static IdModifierPredicate parse(StringReader reader) throws CommandSyntaxException {
      int cursor = reader.getCursor();

      while (reader.canRead() && isCharValid(reader.peek())) {
         reader.skip();
      }

      String string = reader.getString().substring(cursor, reader.getCursor());

      try {
         return new IdModifierPredicate(new ResourceLocation(string));
      } catch (ResourceLocationException var4) {
         reader.setCursor(cursor);
         throw new IllegalArgumentException("Invalid identifier '" + string + "' in modifier '" + reader.getString() + "'");
      }
   }

   protected static boolean isCharValid(char c) {
      return c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c == '_' || c == ':' || c == '/' || c == '.' || c == '-';
   }
}
