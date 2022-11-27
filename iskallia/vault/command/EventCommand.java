package iskallia.vault.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.objective.raid.RaidChallengeObjective;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class EventCommand extends Command {
   @Override
   public String getName() {
      return "event";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public void build(LiteralArgumentBuilder<CommandSourceStack> builder) {
      builder.then(
         Commands.literal("vote")
            .then(
               Commands.argument("sender", StringArgumentType.string())
                  .then(Commands.argument("modifier", StringArgumentType.string()).executes(this::voteModifier))
            )
      );
   }

   private int voteModifier(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      ServerPlayer player = ((CommandSourceStack)context.getSource()).getPlayerOrException();
      ServerLevel sWorld = player.getLevel();
      if (!ModConfigs.RAID_EVENT_CONFIG.isEnabled()) {
         return 0;
      } else {
         VaultRaid vault = null;
         if (vault == null) {
            return 0;
         } else {
            RaidChallengeObjective objective = vault.getActiveObjective(RaidChallengeObjective.class).orElse(null);
            if (objective != null && objective.getVotingSession() != null) {
               String sender = StringArgumentType.getString(context, "sender");
               String modifier = StringArgumentType.getString(context, "modifier");
               objective.getVotingSession().addVote(sender, modifier);
               return 0;
            } else {
               return 0;
            }
         }
      }
   }

   @Override
   public boolean isDedicatedServerOnly() {
      return false;
   }
}
