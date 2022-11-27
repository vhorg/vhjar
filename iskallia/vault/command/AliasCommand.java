package iskallia.vault.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.world.data.PlayerAliasData;
import net.minecraft.ChatFormatting;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;

public class AliasCommand extends Command {
   @Override
   public String getName() {
      return "alias";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 0;
   }

   @Override
   public void build(LiteralArgumentBuilder<CommandSourceStack> builder) {
      builder.then(Commands.argument("from", StringArgumentType.word()).then(Commands.argument("to", StringArgumentType.word()).executes(this::createAlias)));
   }

   private int createAlias(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      CommandSourceStack sender = (CommandSourceStack)ctx.getSource();
      ServerLevel world = sender.getLevel();
      String from = StringArgumentType.getString(ctx, "from");
      String to = StringArgumentType.getString(ctx, "to");
      PlayerAliasData.get(world).putAlias(from, to);
      sender.sendSuccess(new TextComponent(String.format("Alias \"%s\" -> \"%s\" created.", from, to)).withStyle(ChatFormatting.GRAY), true);
      return 0;
   }

   @Override
   public boolean isDedicatedServerOnly() {
      return false;
   }
}
