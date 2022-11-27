package iskallia.vault.command;

import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import iskallia.vault.init.ModConfigs;
import iskallia.vault.world.vault.VaultRaid;
import iskallia.vault.world.vault.logic.objective.architect.ArchitectObjective;
import iskallia.vault.world.vault.modifier.spi.VaultModifier;
import net.minecraft.ChatFormatting;
import net.minecraft.Util;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;

public class ArchitectCommand extends Command {
   @Override
   public String getName() {
      return "architect";
   }

   @Override
   public int getRequiredPermissionLevel() {
      return 2;
   }

   @Override
   public void build(LiteralArgumentBuilder<CommandSourceStack> builder) {
      builder.then(
         Commands.literal("player_vote")
            .then(
               Commands.argument("sender", StringArgumentType.word())
                  .then(Commands.argument("direction", StringArgumentType.word()).executes(this::receiveArchitectVote))
            )
      );
      builder.then(
         Commands.literal("temporary_modifier")
            .then(
               Commands.argument("sender", StringArgumentType.word())
                  .then(Commands.argument("type", StringArgumentType.word()).executes(this::addRandomModifier))
            )
      );
   }

   private int addRandomModifier(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      ServerPlayer player = ((CommandSourceStack)context.getSource()).getPlayerOrException();
      ServerLevel sWorld = player.getLevel();
      VaultRaid vault = null;
      if (vault == null) {
         return 0;
      } else if (vault.getActiveObjective(ArchitectObjective.class).isEmpty()) {
         return 0;
      } else {
         String sender = StringArgumentType.getString(context, "sender");
         boolean isPositive = StringArgumentType.getString(context, "type").equalsIgnoreCase("positive");
         VaultModifier<?> modifier = isPositive
            ? ModConfigs.ARCHITECT_EVENT.getRandomPositiveModifier()
            : ModConfigs.ARCHITECT_EVENT.getRandomNegativeModifier();
         int minutes = ModConfigs.ARCHITECT_EVENT.getTemporaryModifierMinutes();
         Component ct = new TextComponent(sender)
            .withStyle(ChatFormatting.GOLD)
            .append(new TextComponent(" added ").withStyle(ChatFormatting.GRAY))
            .append(modifier.getNameComponent())
            .append(new TextComponent(" for ").withStyle(ChatFormatting.GRAY))
            .append(new TextComponent(minutes + " minutes!").withStyle(ChatFormatting.GOLD));
         vault.getModifiers().addTemporaryModifier(modifier, 1, minutes * 60 * 20);
         vault.getPlayers().forEach(vPlayer -> vPlayer.runIfPresent(player.getServer(), sPlayer -> sPlayer.sendMessage(ct, Util.NIL_UUID)));
         return 0;
      }
   }

   private int receiveArchitectVote(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
      ServerPlayer player = ((CommandSourceStack)context.getSource()).getPlayerOrException();
      String sender = StringArgumentType.getString(context, "sender");
      String directionString = StringArgumentType.getString(context, "direction");
      Direction dir = Direction.byName(directionString);
      if (dir == null) {
         return 0;
      } else {
         VaultRaid vault = null;
         if (vault == null) {
            return 0;
         } else {
            vault.getActiveObjective(ArchitectObjective.class).ifPresent(objective -> objective.handleVote(sender, dir));
            return 0;
         }
      }
   }

   @Override
   public boolean isDedicatedServerOnly() {
      return false;
   }
}
