package iskallia.vault.command;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.research.ResearchTree;
import iskallia.vault.util.NetcodeUtils;
import iskallia.vault.util.PlayerReference;
import iskallia.vault.world.data.PlayerResearchesData;
import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.ClickEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.HoverEvent;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.HoverEvent.Action;
import net.minecraft.server.level.ServerPlayer;

public class ResearchTeamCommand extends Command {
   @Override
   public String getName() {
      return "research_team";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 0;
   }

   @Override
   public void build(LiteralArgumentBuilder<CommandSourceStack> builder) {
      builder.then(Commands.literal("invite").then(Commands.argument("target", EntityArgument.player()).executes(this::invite)));
      builder.then(Commands.literal("accept_invite").then(Commands.argument("inviter", EntityArgument.player()).executes(this::accept)));
      builder.then(Commands.literal("leave").executes(this::leave));
      builder.then(Commands.literal("list").executes(this::list));
   }

   private int invite(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      ServerPlayer player = ((CommandSourceStack)ctx.getSource()).getPlayerOrException();
      ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
      if (player.getUUID().equals(target.getUUID())) {
         player.sendMessage(new TextComponent("You can't invite yourself!").withStyle(ChatFormatting.RED), Util.NIL_UUID);
         return 0;
      } else {
         String playerName = player.getGameProfile().getName();
         String targetName = target.getGameProfile().getName();
         PlayerResearchesData data = PlayerResearchesData.get(player.getLevel());
         if (!data.isInTeam(target.getUUID()) && data.createInvite(player, target)) {
            player.sendMessage(new TextComponent("Invited " + targetName + " to the research-sharing team.").withStyle(ChatFormatting.GREEN), Util.NIL_UUID);
            data.getTeamMembers(player.getUUID())
               .forEach(
                  plReference -> NetcodeUtils.runIfPresent(
                     player.server,
                     plReference.getId(),
                     otherPlayer -> otherPlayer.sendMessage(
                        new TextComponent(playerName + " invited " + targetName + " to the research-sharing team.").withStyle(ChatFormatting.GRAY),
                        Util.NIL_UUID
                     )
                  )
               );
            target.sendMessage(
               new TextComponent(player.getGameProfile().getName() + " invited you to a research-sharing team.").withStyle(ChatFormatting.GREEN), Util.NIL_UUID
            );
            String teamAccept = "/the_vault research_team accept_invite " + playerName;
            MutableComponent acceptTxt = new TextComponent(teamAccept).withStyle(ChatFormatting.AQUA);
            acceptTxt.withStyle(
               style -> style.withHoverEvent(new HoverEvent(Action.SHOW_TEXT, new TextComponent("Click to accept!")))
                  .withClickEvent(new ClickEvent(net.minecraft.network.chat.ClickEvent.Action.RUN_COMMAND, teamAccept))
            );
            Component acceptMessage = new TextComponent("")
               .append(new TextComponent("Run '").withStyle(ChatFormatting.GREEN))
               .append(acceptTxt)
               .append(new TextComponent("' to accept their invite!").withStyle(ChatFormatting.GREEN));
            target.sendMessage(acceptMessage, Util.NIL_UUID);
            return 0;
         } else {
            boolean inPlayerTeam = data.getTeamMembers(player.getUUID()).stream().map(PlayerReference::getId).anyMatch(id -> id.equals(target.getUUID()));
            String teamMsg = " is already part of " + (inPlayerTeam ? "your" : "a") + " team!";
            player.sendMessage(new TextComponent(targetName + teamMsg).withStyle(ChatFormatting.RED), Util.NIL_UUID);
            return 0;
         }
      }
   }

   private int accept(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      ServerPlayer player = ((CommandSourceStack)ctx.getSource()).getPlayerOrException();
      ServerPlayer inviter = EntityArgument.getPlayer(ctx, "inviter");
      PlayerResearchesData data = PlayerResearchesData.get(player.getLevel());
      if (data.isInTeam(player.getUUID())) {
         player.sendMessage(
            new TextComponent("Unable to accept a invite! You are already in a research-sharing team!").withStyle(ChatFormatting.RED), Util.NIL_UUID
         );
         return 0;
      } else {
         String playerName = player.getGameProfile().getName();
         if (!data.acceptInvite(player, inviter.getUUID())) {
            player.sendMessage(
               new TextComponent("Unable to accept a invite! You were not invited by " + playerName + "!").withStyle(ChatFormatting.RED), Util.NIL_UUID
            );
            return 0;
         } else {
            player.sendMessage(new TextComponent("Successfully joined a research-sharing team!").withStyle(ChatFormatting.GREEN), Util.NIL_UUID);
            this.list(ctx);
            data.getTeamMembers(player.getUUID()).forEach(plReference -> NetcodeUtils.runIfPresent(player.server, plReference.getId(), otherPlayer -> {
               if (!otherPlayer.getUUID().equals(player.getUUID())) {
                  otherPlayer.sendMessage(new TextComponent(playerName + " joined the research-sharing team.").withStyle(ChatFormatting.GRAY), Util.NIL_UUID);
               }
            }));
            return 0;
         }
      }
   }

   private int leave(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      ServerPlayer player = ((CommandSourceStack)ctx.getSource()).getPlayerOrException();
      PlayerResearchesData data = PlayerResearchesData.get(player.getLevel());
      if (data.isInTeam(player.getUUID()) && data.leaveCurrentTeam(player)) {
         player.sendMessage(new TextComponent("Successfully left your research-sharing team!").withStyle(ChatFormatting.GREEN), Util.NIL_UUID);
         return 0;
      } else {
         player.sendMessage(new TextComponent("You are not in a research-sharing team!").withStyle(ChatFormatting.RED), Util.NIL_UUID);
         return 0;
      }
   }

   private int list(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      ServerPlayer player = ((CommandSourceStack)ctx.getSource()).getPlayerOrException();
      ResearchTree tree = PlayerResearchesData.get(player.getLevel()).getResearches(player);
      List<PlayerReference> teamMembers = tree.getResearchShares();
      if (teamMembers.isEmpty()) {
         player.sendMessage(new TextComponent("You are not in a research-sharing team!").withStyle(ChatFormatting.RED), Util.NIL_UUID);
         return 0;
      } else {
         player.sendMessage(new TextComponent("You are sharing new researches with:"), Util.NIL_UUID);
         teamMembers.stream().map(PlayerReference::getName).map(name -> new TextComponent("- " + name)).forEach(cmp -> player.sendMessage(cmp, Util.NIL_UUID));
         return 0;
      }
   }

   @Override
   public boolean isDedicatedServerOnly() {
      return false;
   }
}
