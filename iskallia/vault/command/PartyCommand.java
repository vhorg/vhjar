package iskallia.vault.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.world.data.VaultPartyData;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import net.minecraft.ChatFormatting;
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
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.player.Player;

public class PartyCommand extends Command {
   @Override
   public void registerCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
      LiteralArgumentBuilder<CommandSourceStack> builder = Commands.literal(this.getName());
      builder.requires(sender -> sender.hasPermission(this.getRequiredPermissionLevel()));
      this.build(builder);
      dispatcher.register(builder);
   }

   @Override
   public String getName() {
      return "party";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 0;
   }

   @Override
   public void build(LiteralArgumentBuilder<CommandSourceStack> builder) {
      builder.then(Commands.literal("create").executes(this::create));
      builder.then(Commands.literal("invite").then(Commands.argument("target", EntityArgument.player()).executes(this::invite)));
      builder.then(Commands.literal("accept_invite").then(Commands.argument("target", EntityArgument.player()).executes(this::accept)));
      builder.then(Commands.literal("remove").then(Commands.argument("target", EntityArgument.player()).executes(this::remove)));
      builder.then(Commands.literal("leave").executes(this::leave));
      builder.then(Commands.literal("disband").executes(this::disband));
      builder.then(Commands.literal("list").executes(this::list));
   }

   private int list(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      VaultPartyData data = VaultPartyData.get(((CommandSourceStack)ctx.getSource()).getLevel());
      ServerPlayer player = ((CommandSourceStack)ctx.getSource()).getPlayerOrException();
      Optional<VaultPartyData.Party> party = data.getParty(player.getUUID());
      if (!party.isPresent()) {
         player.sendMessage(new TextComponent("You are not in a party!").withStyle(ChatFormatting.RED), player.getUUID());
         return 0;
      } else {
         PlayerList players = player.getServer().getPlayerList();
         MutableComponent members = new TextComponent("Members: ").withStyle(ChatFormatting.GREEN);
         List<Component> playerNames = party.get()
            .getMembers()
            .stream()
            .<ServerPlayer>map(players::getPlayer)
            .filter(Objects::nonNull)
            .<Component>map(Player::getName)
            .collect(Collectors.toList());

         for (int i = 0; i < playerNames.size(); i++) {
            if (i != 0) {
               members.append(", ");
            }

            members.append(playerNames.get(i));
         }

         player.sendMessage(members, player.getUUID());
         return 0;
      }
   }

   private int invite(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      VaultPartyData data = VaultPartyData.get(((CommandSourceStack)ctx.getSource()).getLevel());
      ServerPlayer player = ((CommandSourceStack)ctx.getSource()).getPlayerOrException();
      ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
      Optional<VaultPartyData.Party> party = data.getParty(player.getUUID());
      if (!party.isPresent()) {
         this.create(ctx);
         party = data.getParty(player.getUUID());
         if (!party.isPresent()) {
            return 0;
         }
      }

      if (data.getParty(target.getUUID()).isPresent()) {
         player.sendMessage(new TextComponent("This player is already in another party.").withStyle(ChatFormatting.RED), player.getUUID());
      } else {
         party.get()
            .getMembers()
            .forEach(
               uuid -> {
                  ServerPlayer player2 = ((CommandSourceStack)ctx.getSource()).getServer().getPlayerList().getPlayer(uuid);
                  if (player2 != null) {
                     player2.sendMessage(
                        new TextComponent("Inviting " + target.getName().getString() + " to the party.").withStyle(ChatFormatting.GREEN), player.getUUID()
                     );
                  }
               }
            );
         String partyAccept = "/party accept_invite " + player.getName().getString();
         MutableComponent acceptTxt = new TextComponent(partyAccept).withStyle(ChatFormatting.AQUA);
         acceptTxt.withStyle(
            style -> style.withHoverEvent(new HoverEvent(Action.SHOW_TEXT, new TextComponent("Click to accept!")))
               .withClickEvent(new ClickEvent(net.minecraft.network.chat.ClickEvent.Action.RUN_COMMAND, partyAccept))
         );
         Component acceptMessage = new TextComponent("")
            .append(new TextComponent("Run '").withStyle(ChatFormatting.GREEN))
            .append(acceptTxt)
            .append(new TextComponent("' to accept their invite!").withStyle(ChatFormatting.GREEN));
         party.get().invite(target.getUUID());
         target.sendMessage(
            new TextComponent(player.getName().getString() + " has invited you to their party.").withStyle(ChatFormatting.GREEN), player.getUUID()
         );
         target.sendMessage(acceptMessage, player.getUUID());
      }

      return 0;
   }

   private int accept(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      VaultPartyData data = VaultPartyData.get(((CommandSourceStack)ctx.getSource()).getLevel());
      ServerPlayer player = ((CommandSourceStack)ctx.getSource()).getPlayerOrException();
      ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
      Optional<VaultPartyData.Party> party = data.getParty(player.getUUID());
      if (party.isPresent()) {
         player.sendMessage(new TextComponent("You already are in a party!").withStyle(ChatFormatting.RED), player.getUUID());
         return 0;
      } else {
         if (!data.getParty(target.getUUID()).isPresent()) {
            player.sendMessage(new TextComponent("This player has left their party.").withStyle(ChatFormatting.RED), player.getUUID());
         } else {
            data.getParty(target.getUUID())
               .get()
               .getMembers()
               .forEach(
                  uuid -> {
                     ServerPlayer player2 = ((CommandSourceStack)ctx.getSource()).getServer().getPlayerList().getPlayer(uuid);
                     if (player2 != null) {
                        player2.sendMessage(
                           new TextComponent("Successfully added " + player.getName().getString() + " to the party.").withStyle(ChatFormatting.GREEN),
                           player.getUUID()
                        );
                     }
                  }
               );
            if (data.getParty(target.getUUID()).get().confirmInvite(player.getUUID())) {
               VaultPartyData.broadcastPartyData(player.getLevel());
               player.sendMessage(
                  new TextComponent("You have been added to " + target.getName().getString() + "'s party.").withStyle(ChatFormatting.GREEN), player.getUUID()
               );
            } else {
               player.sendMessage(
                  new TextComponent("You are not invited to " + target.getName().getString() + "'s party.").withStyle(ChatFormatting.RED), player.getUUID()
               );
            }
         }

         return 0;
      }
   }

   private int remove(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      VaultPartyData data = VaultPartyData.get(((CommandSourceStack)ctx.getSource()).getLevel());
      ServerPlayer player = ((CommandSourceStack)ctx.getSource()).getPlayerOrException();
      ServerPlayer target = EntityArgument.getPlayer(ctx, "target");
      Optional<VaultPartyData.Party> party = data.getParty(player.getUUID());
      if (!party.isPresent()) {
         player.sendMessage(new TextComponent("You are not in a party!").withStyle(ChatFormatting.RED), player.getUUID());
         return 0;
      } else {
         Optional<VaultPartyData.Party> other = data.getParty(target.getUUID());
         if (other.isPresent() && other.get() != party.get()) {
            player.sendMessage(new TextComponent("This player is in another party.").withStyle(ChatFormatting.RED), player.getUUID());
         } else if (party.get().remove(target.getUUID())) {
            party.get()
               .getMembers()
               .forEach(
                  uuid -> {
                     ServerPlayer player2 = ((CommandSourceStack)ctx.getSource()).getServer().getPlayerList().getPlayer(uuid);
                     if (player2 != null) {
                        player2.sendMessage(
                           new TextComponent(target.getName().getString() + " was removed from the party.").withStyle(ChatFormatting.GREEN), player.getUUID()
                        );
                     }
                  }
               );
            target.sendMessage(
               new TextComponent("You have been removed from " + player.getName().getString() + "'s party.").withStyle(ChatFormatting.GREEN), player.getUUID()
            );
            VaultPartyData.broadcastPartyData(player.getLevel());
         } else {
            player.sendMessage(new TextComponent("This player not in your party.").withStyle(ChatFormatting.RED), player.getUUID());
         }

         return 0;
      }
   }

   private int create(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      VaultPartyData data = VaultPartyData.get(((CommandSourceStack)ctx.getSource()).getLevel());
      ServerPlayer player = ((CommandSourceStack)ctx.getSource()).getPlayerOrException();
      if (data.createParty(player.getUUID())) {
         player.sendMessage(new TextComponent("Successfully created a party.").withStyle(ChatFormatting.GREEN), player.getUUID());
         VaultPartyData.broadcastPartyData(player.getLevel());
      } else {
         player.sendMessage(new TextComponent("You are already in a party! Please leave or disband it first.").withStyle(ChatFormatting.RED), player.getUUID());
      }

      return 0;
   }

   private int leave(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      VaultPartyData data = VaultPartyData.get(((CommandSourceStack)ctx.getSource()).getLevel());
      ServerPlayer player = ((CommandSourceStack)ctx.getSource()).getPlayerOrException();
      Optional<VaultPartyData.Party> party = data.getParty(player.getUUID());
      if (party.isPresent()) {
         if (party.get().remove(player.getUUID())) {
            party.get()
               .getMembers()
               .forEach(
                  uuid -> {
                     ServerPlayer player2 = ((CommandSourceStack)ctx.getSource()).getServer().getPlayerList().getPlayer(uuid);
                     if (player2 != null) {
                        player2.sendMessage(
                           new TextComponent(player.getName().getString() + " has left the party.").withStyle(ChatFormatting.GREEN), player.getUUID()
                        );
                     }
                  }
               );
            player.sendMessage(new TextComponent("Successfully left the party.").withStyle(ChatFormatting.GREEN), player.getUUID());
            VaultPartyData.broadcastPartyData(player.getLevel());
         } else {
            player.sendMessage(new TextComponent("You are not in a party!").withStyle(ChatFormatting.RED), player.getUUID());
         }
      } else {
         player.sendMessage(new TextComponent("You are not in a party!").withStyle(ChatFormatting.RED), player.getUUID());
      }

      return 0;
   }

   private int disband(CommandContext<CommandSourceStack> ctx) throws CommandSyntaxException {
      VaultPartyData data = VaultPartyData.get(((CommandSourceStack)ctx.getSource()).getLevel());
      ServerPlayer player = ((CommandSourceStack)ctx.getSource()).getPlayerOrException();
      VaultPartyData.Party party = data.getParty(player.getUUID()).orElse(null);
      if (party != null && data.disbandParty(player.getUUID())) {
         party.getMembers().forEach(uuid -> {
            ServerPlayer player2 = ((CommandSourceStack)ctx.getSource()).getServer().getPlayerList().getPlayer(uuid);
            if (player2 != null) {
               player2.sendMessage(new TextComponent("The party was disbanded.").withStyle(ChatFormatting.GREEN), player.getUUID());
            }
         });
         VaultPartyData.broadcastPartyData(player.getLevel());
      } else {
         player.sendMessage(new TextComponent("You are not in a party!").withStyle(ChatFormatting.RED), player.getUUID());
      }

      return 0;
   }

   @Override
   public boolean isDedicatedServerOnly() {
      return false;
   }
}
