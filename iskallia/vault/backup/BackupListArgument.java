package iskallia.vault.backup;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.UUID;
import net.minecraft.command.CommandSource;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.command.arguments.UUIDArgument;

public abstract class BackupListArgument implements ArgumentType<String> {
   protected abstract UUID getPlayerRef(CommandContext<CommandSource> var1);

   public String parse(StringReader reader) throws CommandSyntaxException {
      return reader.readUnquotedString();
   }

   public static class Player extends BackupListArgument {
      @Override
      protected UUID getPlayerRef(CommandContext<CommandSource> ctx) {
         try {
            return EntityArgument.func_197089_d(ctx, "player").func_110124_au();
         } catch (CommandSyntaxException var3) {
            throw new RuntimeException(var3);
         }
      }
   }

   public static class UUIDRef extends BackupListArgument {
      @Override
      protected UUID getPlayerRef(CommandContext<CommandSource> ctx) {
         return UUIDArgument.func_239195_a_(ctx, "player");
      }
   }
}
