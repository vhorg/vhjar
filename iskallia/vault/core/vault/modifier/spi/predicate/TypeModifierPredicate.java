package iskallia.vault.core.vault.modifier.spi.predicate;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.core.vault.modifier.registry.VaultModifierTypeRegistry;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import java.util.Optional;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;

public class TypeModifierPredicate implements ModifierPredicate {
   private final ResourceLocation id;

   public TypeModifierPredicate(ResourceLocation id) {
      this.id = id;
   }

   @Override
   public boolean test(VaultModifier<?> modifier) {
      return this.id.equals(VaultModifierTypeRegistry.getIdFor(modifier.getClass()).orElse(null));
   }

   @Override
   public String toString() {
      return "#" + this.id.toString();
   }

   public static Optional<TypeModifierPredicate> parse(String string, boolean logErrors) {
      try {
         return Optional.of(parse(new StringReader(string)));
      } catch (IllegalArgumentException | CommandSyntaxException var3) {
         if (logErrors) {
            var3.printStackTrace();
         }

         return Optional.empty();
      }
   }

   public static TypeModifierPredicate parse(String string) throws CommandSyntaxException {
      return parse(new StringReader(string));
   }

   public static TypeModifierPredicate parse(StringReader reader) throws CommandSyntaxException {
      if (reader.peek() != '#') {
         throw new IllegalArgumentException("Invalid modifier group '" + reader.getString() + "' does not start with #");
      } else {
         reader.skip();
         int cursor = reader.getCursor();

         while (reader.canRead() && isCharValid(reader.peek())) {
            reader.skip();
         }

         String string = reader.getString().substring(cursor, reader.getCursor());

         try {
            return new TypeModifierPredicate(new ResourceLocation(string));
         } catch (ResourceLocationException var4) {
            reader.setCursor(cursor);
            throw new IllegalArgumentException("Invalid type identifier '" + string + "' in modifier type '" + reader.getString() + "'");
         }
      }
   }

   protected static boolean isCharValid(char c) {
      return c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c == '_' || c == ':' || c == '/' || c == '.' || c == '-';
   }
}
