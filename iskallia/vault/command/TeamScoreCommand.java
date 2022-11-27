package iskallia.vault.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.world.data.EventTeamData;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import joptsimple.internal.Strings;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerPlayer;

public class TeamScoreCommand extends Command {
   @Override
   public String getName() {
      return "scores";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 0;
   }

   @Override
   public void build(LiteralArgumentBuilder<CommandSourceStack> builder) {
      builder.executes(this::showScores);
   }

   private int showScores(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      ServerPlayer player = ((CommandSourceStack)context.getSource()).getPlayerOrException();
      List<EventTeamData.Team> teams = EventTeamData.get(player.getLevel()).getTeams();
      teams.sort(Comparator.comparing(EventTeamData.Team::getScore).reversed());
      List<Integer> scores = new ArrayList<>();
      teams.forEach(teamx -> {
         if (!scores.contains(teamx.getScore())) {
            scores.add(teamx.getScore());
         }
      });
      scores.sort(Comparator.reverseOrder());

      for (int i = 0; i < teams.size(); i++) {
         EventTeamData.Team team = teams.get(i);
         if (i > 0) {
            player.sendMessage(new TextComponent("----------------").withStyle(ChatFormatting.DARK_GRAY), Util.NIL_UUID);
         }

         Component ct = new TextComponent("Team: ").append(new TextComponent(team.getName()).withStyle(team.getColor()));
         player.sendMessage(ct, Util.NIL_UUID);
         int rankIndex = scores.indexOf(team.getScore()) + 1;
         ChatFormatting rankColor = rankIndex == 1 ? ChatFormatting.GOLD : (rankIndex == 2 ? ChatFormatting.GRAY : ChatFormatting.RED);
         Component rankTxt = new TextComponent("Rank: ")
            .append(new TextComponent(String.valueOf(rankIndex)).withStyle(rankColor))
            .append(" ")
            .append(new TextComponent("Score: "))
            .append(new TextComponent(String.valueOf(team.getScore())).withStyle(rankColor));
         player.sendMessage(rankTxt, Util.NIL_UUID);
         player.sendMessage(new TextComponent("Members:"), Util.NIL_UUID);
         String memberNames = Strings.join(team.getMembers(), ", ");
         player.sendMessage(new TextComponent(memberNames).withStyle(team.getColor()), Util.NIL_UUID);
      }

      return 0;
   }

   @Override
   public boolean isDedicatedServerOnly() {
      return false;
   }
}
