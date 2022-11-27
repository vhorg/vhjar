package iskallia.vault.backup;

import com.mojang.brigadier.StringReader;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import java.util.UUID;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.commands.arguments.UuidArgument;

public abstract class BackupListArgument implements ArgumentType<String> {
   protected abstract UUID getPlayerRef(CommandContext<CommandSourceStack> var1);

   public String parse(StringReader reader) throws CommandSyntaxException {
      return reader.readUnquotedString();
   }

   public static class Player extends BackupListArgument {
      @Override
      protected UUID getPlayerRef(CommandContext<CommandSourceStack> ctx) {
         try {
            return EntityArgument.getPlayer(ctx, "player").getUUID();
         } catch (CommandSyntaxException var3) {
            throw new RuntimeException(var3);
         }
      }
   }

   public static class UUIDRef extends BackupListArgument {
      @Override
      protected UUID getPlayerRef(CommandContext<CommandSourceStack> ctx) {
         return UuidArgument.getUuid(ctx, "player");
      }
   }
}
