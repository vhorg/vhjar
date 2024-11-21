package iskallia.vault.core.vault.modifier.spi.predicate;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.core.vault.modifier.spi.VaultModifier;
import iskallia.vault.init.ModConfigs;
import java.util.Optional;
import net.minecraft.ResourceLocationException;
import net.minecraft.resources.ResourceLocation;

public class GroupModifierPredicate implements ModifierPredicate {
   private final ResourceLocation id;

   public GroupModifierPredicate(ResourceLocation id) {
      this.id = id;
   }

   @Override
   public boolean test(VaultModifier<?> modifier) {
      return ModConfigs.MODIFIER_GROUPS.isInGroup(this.id, modifier);
   }

   @Override
   public String toString() {
      return "@" + this.id.toString();
   }

   public static Optional<GroupModifierPredicate> parse(String string, boolean logErrors) {
      try {
         return Optional.of(parse(new StringReader(string)));
      } catch (IllegalArgumentException | CommandSyntaxException var3) {
         if (logErrors) {
            var3.printStackTrace();
         }

         return Optional.empty();
      }
   }

   public static GroupModifierPredicate parse(String string) throws CommandSyntaxException {
      return parse(new StringReader(string));
   }

   public static GroupModifierPredicate parse(StringReader reader) throws CommandSyntaxException {
      if (reader.peek() != '@') {
         throw new IllegalArgumentException("Invalid modifier group '" + reader.getString() + "' does not start with @");
      } else {
         reader.skip();
         int cursor = reader.getCursor();

         while (reader.canRead() && isCharValid(reader.peek())) {
            reader.skip();
         }

         String string = reader.getString().substring(cursor, reader.getCursor());

         try {
            return new GroupModifierPredicate(new ResourceLocation(string));
         } catch (ResourceLocationException var4) {
            reader.setCursor(cursor);
            throw new IllegalArgumentException("Invalid group identifier '" + string + "' in modifier group '" + reader.getString() + "'");
         }
      }
   }

   protected static boolean isCharValid(char c) {
      return c >= '0' && c <= '9' || c >= 'a' && c <= 'z' || c == '_' || c == ':' || c == '/' || c == '.' || c == '-';
   }
}
