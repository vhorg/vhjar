package iskallia.vault.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.world.data.SoulShardTraderData;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.objective.architect.ArchitectObjective;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Direction;

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
      builder.then(
         Commands.func_197057_a("player_vote")
            .then(
               Commands.func_197056_a("a", StringArgumentType.word())
                  .then(
                     Commands.func_197056_a("b", StringArgumentType.word())
                        .executes(
                           ctx -> this.voteFor(
                              (CommandSource)ctx.getSource(),
                              StringArgumentType.getString(ctx, "a"),
                              Direction.func_176739_a(StringArgumentType.getString(ctx, "b"))
                           )
                        )
                  )
            )
      );
   }

   private int resetShardTrader(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
      SoulShardTraderData.get(((CommandSource)ctx.getSource()).func_197028_i()).resetTrades();
      return 0;
   }

   private int voteFor(CommandSource src, String voter, Direction direction) throws CommandSyntaxException {
      ServerPlayerEntity sPlayer = src.func_197035_h();
      VaultRaid vault = VaultRaidData.get(sPlayer.func_71121_q()).getActiveFor(sPlayer);
      if (direction == null) {
         return 0;
      } else if (vault == null) {
         return 0;
      } else {
         return !vault.getActiveObjective(ArchitectObjective.class).map(objective -> objective.handleVote(voter, direction)).orElse(false) ? 0 : 1;
      }
   }

   @Override
   public boolean isDedicatedServerOnly() {
      return false;
   }
}
