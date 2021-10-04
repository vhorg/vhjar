package iskallia.vault.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.world.data.SoulShardTraderData;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;

public class InternalCommand extends Command {
   @Override
   public String getName() {
      return "internal";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public void build(LiteralArgumentBuilder<CommandSource> builder) {
      builder.then(Commands.func_197057_a("reset_shard_trades").executes(this::resetShardTrader));
   }

   private int resetShardTrader(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
      SoulShardTraderData.get(((CommandSource)ctx.getSource()).func_197028_i()).resetTrades();
      return 0;
   }

   @Override
   public boolean isDedicatedServerOnly() {
      return false;
   }
}
