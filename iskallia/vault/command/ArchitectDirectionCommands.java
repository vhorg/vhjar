package iskallia.vault.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.world.data.VaultRaidData;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.objective.architect.ArchitectObjective;
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.Util;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

public class ArchitectDirectionCommands {
   public static void register(CommandDispatcher<CommandSource> dispatcher) {
      dispatcher.register((LiteralArgumentBuilder)Commands.func_197057_a("north").executes(cmd -> voteFor((CommandSource)cmd.getSource(), Direction.NORTH)));
      dispatcher.register((LiteralArgumentBuilder)Commands.func_197057_a("east").executes(cmd -> voteFor((CommandSource)cmd.getSource(), Direction.EAST)));
      dispatcher.register((LiteralArgumentBuilder)Commands.func_197057_a("south").executes(cmd -> voteFor((CommandSource)cmd.getSource(), Direction.SOUTH)));
      dispatcher.register((LiteralArgumentBuilder)Commands.func_197057_a("west").executes(cmd -> voteFor((CommandSource)cmd.getSource(), Direction.WEST)));
   }

   private static int voteFor(CommandSource src, Direction direction) throws CommandSyntaxException {
      ServerPlayerEntity sPlayer = src.func_197035_h();
      VaultRaid vault = VaultRaidData.get(sPlayer.func_71121_q()).getActiveFor(sPlayer);
      if (vault == null) {
         sPlayer.func_145747_a(new StringTextComponent("Not in an Architect Vault!").func_240699_a_(TextFormatting.RED), Util.field_240973_b_);
         return 0;
      } else if (!vault.getActiveObjective(ArchitectObjective.class)
         .map(objective -> objective.handleVote(sPlayer.func_200200_C_().getString(), direction))
         .orElse(false)) {
         sPlayer.func_145747_a(new StringTextComponent("No vote active or already voted!").func_240699_a_(TextFormatting.RED), Util.field_240973_b_);
         return 0;
      } else {
         return 1;
      }
   }
}
