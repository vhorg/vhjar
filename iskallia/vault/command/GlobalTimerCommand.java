package iskallia.vault.command;

import com.mojang.brigadier.arguments.LongArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.world.data.GlobalTimeData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;

public class GlobalTimerCommand extends Command {
   @Override
   public String getName() {
      return "global_timer";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public void build(LiteralArgumentBuilder<CommandSourceStack> builder) {
      builder.then(
         ((LiteralArgumentBuilder)Commands.literal("reset").executes(context -> this.resetTimer(context, 7776000L)))
            .then(
               Commands.argument("seconds", LongArgumentType.longArg())
                  .executes(context -> this.resetTimer(context, LongArgumentType.getLong(context, "seconds")))
            )
      );
   }

   private int resetTimer(CommandContext<CommandSourceStack> context, long seconds) throws CommandSyntaxException {
      CommandSourceStack source = (CommandSourceStack)context.getSource();
      ServerLevel world = source.getLevel();
      GlobalTimeData globalTimeData = GlobalTimeData.get(world);
      globalTimeData.reset(seconds);
      return 0;
   }

   @Override
   public boolean isDedicatedServerOnly() {
      return false;
   }
}
