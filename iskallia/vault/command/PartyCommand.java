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
import net.minecraft.command.CommandSource;
import net.minecraft.command.Commands;
import net.minecraft.command.arguments.EntityArgument;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.server.management.PlayerList;
import net.minecraft.util.text.IFormattableTextComponent;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.HoverEvent;
import net.minecraft.util.text.event.HoverEvent.Action;

public class PartyCommand extends Command {
   @Override
   public void registerCommand(CommandDispatcher<CommandSource> dispatcher) {
      LiteralArgumentBuilder<CommandSource> builder = Commands.func_197057_a(this.getName());
      builder.requires(sender -> sender.func_197034_c(this.getRequiredPermissionLevel()));
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
   public void build(LiteralArgumentBuilder<CommandSource> builder) {
      builder.then(Commands.func_197057_a("create").executes(this::create));
      builder.then(Commands.func_197057_a("invite").then(Commands.func_197056_a("target", EntityArgument.func_197096_c()).executes(this::invite)));
      builder.then(Commands.func_197057_a("accept_invite").then(Commands.func_197056_a("target", EntityArgument.func_197096_c()).executes(this::accept)));
      builder.then(Commands.func_197057_a("remove").then(Commands.func_197056_a("target", EntityArgument.func_197096_c()).executes(this::remove)));
      builder.then(Commands.func_197057_a("leave").executes(this::leave));
      builder.then(Commands.func_197057_a("disband").executes(this::disband));
      builder.then(Commands.func_197057_a("list").executes(this::list));
   }

   private int list(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
      VaultPartyData data = VaultPartyData.get(((CommandSource)ctx.getSource()).func_197023_e());
      ServerPlayerEntity player = ((CommandSource)ctx.getSource()).func_197035_h();
      Optional<VaultPartyData.Party> party = data.getParty(player.func_110124_au());
      if (!party.isPresent()) {
         player.func_145747_a(new StringTextComponent("You are not in a party!").func_240699_a_(TextFormatting.RED), player.func_110124_au());
         return 0;
      } else {
         PlayerList players = player.func_184102_h().func_184103_al();
         IFormattableTextComponent members = new StringTextComponent("Members: ").func_240699_a_(TextFormatting.GREEN);
         List<ITextComponent> playerNames = party.get()
            .getMembers()
            .stream()
            .<ServerPlayerEntity>map(players::func_177451_a)
            .filter(Objects::nonNull)
            .<ITextComponent>map(PlayerEntity::func_200200_C_)
            .collect(Collectors.toList());

         for (int i = 0; i < playerNames.size(); i++) {
            if (i != 0) {
               members.func_240702_b_(", ");
            }

            members.func_230529_a_(playerNames.get(i));
         }

         player.func_145747_a(members, player.func_110124_au());
         return 0;
      }
   }

   private int invite(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
      VaultPartyData data = VaultPartyData.get(((CommandSource)ctx.getSource()).func_197023_e());
      ServerPlayerEntity player = ((CommandSource)ctx.getSource()).func_197035_h();
      ServerPlayerEntity target = EntityArgument.func_197089_d(ctx, "target");
      Optional<VaultPartyData.Party> party = data.getParty(player.func_110124_au());
      if (!party.isPresent()) {
         player.func_145747_a(new StringTextComponent("You are not in a party!").func_240699_a_(TextFormatting.RED), player.func_110124_au());
         return 0;
      } else {
         if (data.getParty(target.func_110124_au()).isPresent()) {
            player.func_145747_a(
               new StringTextComponent("This player is already in another party.").func_240699_a_(TextFormatting.RED), player.func_110124_au()
            );
         } else {
            party.get()
               .getMembers()
               .forEach(
                  uuid -> {
                     ServerPlayerEntity player2 = ((CommandSource)ctx.getSource()).func_197028_i().func_184103_al().func_177451_a(uuid);
                     if (player2 != null) {
                        player2.func_145747_a(
                           new StringTextComponent("Inviting " + target.func_200200_C_().getString() + " to the party.").func_240699_a_(TextFormatting.GREEN),
                           player.func_110124_au()
                        );
                     }
                  }
               );
            String partyAccept = "/party accept_invite " + player.func_200200_C_().getString();
            IFormattableTextComponent acceptTxt = new StringTextComponent(partyAccept).func_240699_a_(TextFormatting.AQUA);
            acceptTxt.func_240700_a_(
               style -> style.func_240716_a_(new HoverEvent(Action.field_230550_a_, new StringTextComponent("Click to accept!")))
                  .func_240715_a_(new ClickEvent(net.minecraft.util.text.event.ClickEvent.Action.RUN_COMMAND, partyAccept))
            );
            ITextComponent acceptMessage = new StringTextComponent("")
               .func_230529_a_(new StringTextComponent("Run '").func_240699_a_(TextFormatting.GREEN))
               .func_230529_a_(acceptTxt)
               .func_230529_a_(new StringTextComponent("' to accept their invite!").func_240699_a_(TextFormatting.GREEN));
            party.get().invite(target.func_110124_au());
            target.func_145747_a(
               new StringTextComponent(player.func_200200_C_().getString() + " has invited you to their party.").func_240699_a_(TextFormatting.GREEN),
               player.func_110124_au()
            );
            target.func_145747_a(acceptMessage, player.func_110124_au());
         }

         return 0;
      }
   }

   private int accept(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
      VaultPartyData data = VaultPartyData.get(((CommandSource)ctx.getSource()).func_197023_e());
      ServerPlayerEntity player = ((CommandSource)ctx.getSource()).func_197035_h();
      ServerPlayerEntity target = EntityArgument.func_197089_d(ctx, "target");
      Optional<VaultPartyData.Party> party = data.getParty(player.func_110124_au());
      if (party.isPresent()) {
         player.func_145747_a(new StringTextComponent("You already are in a party!").func_240699_a_(TextFormatting.RED), player.func_110124_au());
         return 0;
      } else {
         if (!data.getParty(target.func_110124_au()).isPresent()) {
            player.func_145747_a(new StringTextComponent("This player has left their party.").func_240699_a_(TextFormatting.RED), player.func_110124_au());
         } else {
            data.getParty(target.func_110124_au())
               .get()
               .getMembers()
               .forEach(
                  uuid -> {
                     ServerPlayerEntity player2 = ((CommandSource)ctx.getSource()).func_197028_i().func_184103_al().func_177451_a(uuid);
                     if (player2 != null) {
                        player2.func_145747_a(
                           new StringTextComponent("Successfully added " + player.func_200200_C_().getString() + " to the party.")
                              .func_240699_a_(TextFormatting.GREEN),
                           player.func_110124_au()
                        );
                     }
                  }
               );
            if (data.getParty(target.func_110124_au()).get().confirmInvite(player.func_110124_au())) {
               VaultPartyData.broadcastPartyData(player.func_71121_q());
               player.func_145747_a(
                  new StringTextComponent("You have been added to " + target.func_200200_C_().getString() + "'s party.").func_240699_a_(TextFormatting.GREEN),
                  player.func_110124_au()
               );
            } else {
               player.func_145747_a(
                  new StringTextComponent("You are not invited to " + target.func_200200_C_().getString() + "'s party.").func_240699_a_(TextFormatting.RED),
                  player.func_110124_au()
               );
            }
         }

         return 0;
      }
   }

   private int remove(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
      VaultPartyData data = VaultPartyData.get(((CommandSource)ctx.getSource()).func_197023_e());
      ServerPlayerEntity player = ((CommandSource)ctx.getSource()).func_197035_h();
      ServerPlayerEntity target = EntityArgument.func_197089_d(ctx, "target");
      Optional<VaultPartyData.Party> party = data.getParty(player.func_110124_au());
      if (!party.isPresent()) {
         player.func_145747_a(new StringTextComponent("You are not in a party!").func_240699_a_(TextFormatting.RED), player.func_110124_au());
         return 0;
      } else {
         Optional<VaultPartyData.Party> other = data.getParty(target.func_110124_au());
         if (other.isPresent() && other.get() != party.get()) {
            player.func_145747_a(new StringTextComponent("This player is in another party.").func_240699_a_(TextFormatting.RED), player.func_110124_au());
         } else if (party.get().remove(target.func_110124_au())) {
            party.get()
               .getMembers()
               .forEach(
                  uuid -> {
                     ServerPlayerEntity player2 = ((CommandSource)ctx.getSource()).func_197028_i().func_184103_al().func_177451_a(uuid);
                     if (player2 != null) {
                        player2.func_145747_a(
                           new StringTextComponent(target.func_200200_C_().getString() + " was removed from the party.").func_240699_a_(TextFormatting.GREEN),
                           player.func_110124_au()
                        );
                     }
                  }
               );
            target.func_145747_a(
               new StringTextComponent("You have been removed from " + player.func_200200_C_().getString() + "'s party.").func_240699_a_(TextFormatting.GREEN),
               player.func_110124_au()
            );
            VaultPartyData.broadcastPartyData(player.func_71121_q());
         } else {
            player.func_145747_a(new StringTextComponent("This player not in your party.").func_240699_a_(TextFormatting.RED), player.func_110124_au());
         }

         return 0;
      }
   }

   private int create(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
      VaultPartyData data = VaultPartyData.get(((CommandSource)ctx.getSource()).func_197023_e());
      ServerPlayerEntity player = ((CommandSource)ctx.getSource()).func_197035_h();
      if (data.createParty(player.func_110124_au())) {
         player.func_145747_a(new StringTextComponent("Successfully created a party.").func_240699_a_(TextFormatting.GREEN), player.func_110124_au());
         VaultPartyData.broadcastPartyData(player.func_71121_q());
      } else {
         player.func_145747_a(
            new StringTextComponent("You are already in a party! Please leave or disband it first.").func_240699_a_(TextFormatting.RED),
            player.func_110124_au()
         );
      }

      return 0;
   }

   private int leave(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
      VaultPartyData data = VaultPartyData.get(((CommandSource)ctx.getSource()).func_197023_e());
      ServerPlayerEntity player = ((CommandSource)ctx.getSource()).func_197035_h();
      Optional<VaultPartyData.Party> party = data.getParty(player.func_110124_au());
      if (party.isPresent()) {
         if (party.get().remove(player.func_110124_au())) {
            party.get()
               .getMembers()
               .forEach(
                  uuid -> {
                     ServerPlayerEntity player2 = ((CommandSource)ctx.getSource()).func_197028_i().func_184103_al().func_177451_a(uuid);
                     if (player2 != null) {
                        player2.func_145747_a(
                           new StringTextComponent(player.func_200200_C_().getString() + " has left the party.").func_240699_a_(TextFormatting.GREEN),
                           player.func_110124_au()
                        );
                     }
                  }
               );
            player.func_145747_a(new StringTextComponent("Successfully left the party.").func_240699_a_(TextFormatting.GREEN), player.func_110124_au());
            VaultPartyData.broadcastPartyData(player.func_71121_q());
         } else {
            player.func_145747_a(new StringTextComponent("You are not in a party!").func_240699_a_(TextFormatting.RED), player.func_110124_au());
         }
      } else {
         player.func_145747_a(new StringTextComponent("You are not in a party!").func_240699_a_(TextFormatting.RED), player.func_110124_au());
      }

      return 0;
   }

   private int disband(CommandContext<CommandSource> ctx) throws CommandSyntaxException {
      VaultPartyData data = VaultPartyData.get(((CommandSource)ctx.getSource()).func_197023_e());
      ServerPlayerEntity player = ((CommandSource)ctx.getSource()).func_197035_h();
      VaultPartyData.Party party = data.getParty(player.func_110124_au()).orElse(null);
      if (party != null && data.disbandParty(player.func_110124_au())) {
         party.getMembers().forEach(uuid -> {
            ServerPlayerEntity player2 = ((CommandSource)ctx.getSource()).func_197028_i().func_184103_al().func_177451_a(uuid);
            if (player2 != null) {
               player2.func_145747_a(new StringTextComponent("The party was disbanded.").func_240699_a_(TextFormatting.GREEN), player.func_110124_au());
            }
         });
         VaultPartyData.broadcastPartyData(player.func_71121_q());
      } else {
         player.func_145747_a(new StringTextComponent("You are not in a party!").func_240699_a_(TextFormatting.RED), player.func_110124_au());
      }

      return 0;
   }

   @Override
   public boolean isDedicatedServerOnly() {
      return false;
   }
}
